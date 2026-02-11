package it.crystalnest.fancy_entity_renderer.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.gui.render.pip.GuiEntityRenderer;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.gui.render.state.GuiRenderState;
import net.minecraft.client.gui.render.state.pip.GuiEntityRenderState;
import net.minecraft.client.gui.render.state.pip.PictureInPictureRenderState;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Injects into {@link GuiRenderer} to allow the rendering of multiple entities.
 */
@Mixin(GuiRenderer.class)
public abstract class GuiRendererMixin {
  /**
   * Set of already prepared {@code GuiEntityRenderState}s in the current frame.
   */
  @Unique
  private final Set<GuiEntityRenderState> preparedGuiEntityRenderStates = new ReferenceOpenHashSet<>();

  /**
   * Shadowed {@link GuiRenderer#renderState}.
   */
  @Final
  @Shadow
  GuiRenderState renderState;

  /**
   * Reference to the main {@link GuiEntityRenderer}.
   */
  @Unique
  GuiEntityRenderer guiEntityRenderer;

  /**
   * {@code GuiEntityRenderer}s used in the last frame which could be reused in the current frame.
   */
  @Unique
  private Object2ObjectMap<GuiEntityRenderState, GuiEntityRenderer> renderersLastFrame = new Object2ObjectOpenHashMap<>();

  /**
   * {@code GuiEntityRenderer}s already used in the current frame which could be reused in the next one.
   */
  @Unique
  private Object2ObjectMap<GuiEntityRenderState, GuiEntityRenderer> renderersThisFrame = new Object2ObjectOpenHashMap<>();

  /**
   * Shadowed {@link GuiRenderer#pictureInPictureRenderers}.
   */
  @Final
  @Shadow
  private Map<Class<? extends PictureInPictureRenderState>, PictureInPictureRenderer<?>> pictureInPictureRenderers;

  private GuiRendererMixin() {}

  /**
   * Checks whether the given renderer can be reused for the given state, texture width, and texture height.
   *
   * @param renderer {@link GuiEntityRenderer}.
   * @param state {@link GuiEntityRenderState}.
   * @param width texture width.
   * @param height texture height.
   * @return Whether the renderer can be reused.
   */
  @Unique
  private static boolean canBeReusedFor(GuiEntityRenderer renderer, GuiEntityRenderState state, int width, int height) {
    return renderer.texture == null || (renderer.texture.getWidth(0) == width && renderer.texture.getHeight(0) == height);
  }

  /**
   * Shadowed {@link GuiRenderer#preparePictureInPictureState(PictureInPictureRenderState, int)}.
   *
   * @param state PiP state.
   * @param guiScale GUI scale.
   * @param <T> state type.
   */
  @Shadow
  protected abstract <T extends PictureInPictureRenderState> void preparePictureInPictureState(T state, int guiScale);

  /**
   * Injects at the end of the constructor to save the reference to the main {@link GuiEntityRenderer}.
   *
   * @param renderState {@link GuiRenderState}.
   * @param bufferSource buffer source.
   * @param submitNodeCollector submit node collector.
   * @param featureRenderDispatcher render dispatcher.
   * @param renderers list of PiP renderers.
   * @param ci {@link CallbackInfo}.
   */
  @Inject(method = "<init>", at = @At(value = "TAIL"))
  private void onInit(GuiRenderState renderState, MultiBufferSource.BufferSource bufferSource, SubmitNodeCollector submitNodeCollector, FeatureRenderDispatcher featureRenderDispatcher, List<PictureInPictureRenderer<?>> renderers, CallbackInfo ci) {
    guiEntityRenderer = (GuiEntityRenderer) pictureInPictureRenderers.get(GuiEntityRenderState.class);
  }

  /**
   * Injects at the end of the method {@link GuiRenderer#render(GpuBufferSlice)}.<br>
   * Clears the renderers from last frame and then swaps the per-frame renderer lists.
   *
   * @param ci {@link CallbackInfo}.
   */
  @Inject(method = "render", at = @At(value = "TAIL"))
  private void onRender(CallbackInfo ci) {
    renderersLastFrame.values().forEach(PictureInPictureRenderer::close);
    renderersLastFrame.clear();
    Object2ObjectMap<GuiEntityRenderState, GuiEntityRenderer> tmp = renderersLastFrame;
    renderersLastFrame = renderersThisFrame;
    renderersThisFrame = tmp;
  }

  /**
   * Wraps the call to {@link GuiRenderState#forEachPictureInPicture(Consumer)} inside the method {@link GuiRenderer#preparePictureInPicture()}.<br>
   * Prepares multiple {@link GuiEntityRenderState}s per frame with a double pass.
   *
   * @param instance {@link GuiRenderState} invoking (owning) the wrapped method.
   * @param action the consumer passed to the wrapped method.
   * @param original the original (wrapped) method.
   * @param i local variable storing the GUI scale at the injection point.
   */
  @WrapOperation(method = "preparePictureInPicture", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/render/state/GuiRenderState;forEachPictureInPicture(Ljava/util/function/Consumer;)V"))
  private void redirectForEachPictureInPicture(GuiRenderState instance, Consumer<PictureInPictureRenderState> action, Operation<Void> original, @Local int i) {
    // Empty the render states prepared this frame.
    preparedGuiEntityRenderStates.clear();
    // Make the first prepare pass for GuiEntityRenderStates and prepare all others.
    original.call(instance, (Consumer<PictureInPictureRenderState>) state -> {
      if (state instanceof GuiEntityRenderState guiEntityRenderState) {
        if (prepareGuiEntityRenderState(guiEntityRenderState, i, true)) {
          preparedGuiEntityRenderStates.add(guiEntityRenderState);
        }
      } else {
        action.accept(state);
      }
    });
    // Make the second prepare pass for GuiEntityRenderStates.
    original.call(instance, (Consumer<PictureInPictureRenderState>) state -> {
      if (state instanceof GuiEntityRenderState guiEntityRenderState && preparedGuiEntityRenderStates.add(guiEntityRenderState)) {
        prepareGuiEntityRenderState(guiEntityRenderState, i, false);
      }
    });
    // Empty the render states prepared this frame.
    preparedGuiEntityRenderStates.clear();
  }

  /**
   * Tries to prepare the given state.
   *
   * @param state {@link GuiEntityRenderState}.
   * @param guiScale GUI scale.
   * @param first whether it's the first or second prepare pass.
   * @return whether the state was prepared.
   */
  @Unique
  private boolean prepareGuiEntityRenderState(GuiEntityRenderState state, int guiScale, boolean first) {
    GuiEntityRenderer renderer = getGuiEntityRenderer(state, guiScale, first);
    if (renderer != null) {
      renderer.prepare(state, renderState, guiScale);
      return true;
    }
    return false;
  }

  /**
   * Injects in the method {@link GuiRenderer#close()} after the first call to {@link Collection#forEach(Consumer)} that closes all PiP renderers.<br>
   * Also closes all renderers saved in {@link GuiRendererMixin#renderersLastFrame} and {@link GuiRendererMixin#renderersLastFrame}.
   *
   * @param ci {@link CallbackInfo}.
   */
  @Inject(method = "close", at = @At(value = "INVOKE", target = "Ljava/util/Collection;forEach(Ljava/util/function/Consumer;)V", ordinal = 0))
  private void onClose(CallbackInfo ci) {
    renderersThisFrame.values().forEach(PictureInPictureRenderer::close);
    renderersLastFrame.values().forEach(PictureInPictureRenderer::close);
  }

  /**
   * Returns the most suitable {@link GuiEntityRenderer} for the given state.
   *
   * @param state {@link GuiEntityRenderState}.
   * @param guiScale GUI scale.
   * @param first whether it's the first or second prepare pass.
   * @return the most suitable {@link GuiEntityRenderer}.
   */
  @Unique
  @Nullable
  private GuiEntityRenderer getGuiEntityRenderer(GuiEntityRenderState state, int guiScale, boolean first) {
    int width = (state.x1() - state.x0()) * guiScale;
    int height = (state.y1() - state.y0()) * guiScale;
    // First prepare pass: try to reuse existing renderers by state equality.
    if (first) {
      GuiEntityRenderer renderer = renderersLastFrame.get(state);
      if (renderer != null && canBeReusedFor(renderer, state, width, height)) {
        renderersLastFrame.remove(state);
        renderersThisFrame.put(state, renderer);
        return renderer;
      }
      return null;
    }
    // Second prepare pass: try to find a renderer of matching texture size.
    ObjectIterator<GuiEntityRenderer> it = renderersLastFrame.values().iterator();
    while (it.hasNext()) {
      GuiEntityRenderer renderer = it.next();
      if (canBeReusedFor(renderer, state, width, height)) {
        it.remove();
        renderersThisFrame.put(state, renderer);
        return renderer;
      }
    }
    // No suitable renderer was found -> create a new one.
    GuiEntityRenderer renderer = new GuiEntityRenderer(guiEntityRenderer.bufferSource, guiEntityRenderer.entityRenderDispatcher);
    renderersThisFrame.put(state, renderer);
    return renderer;
  }
}
