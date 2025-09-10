package it.crystalnest.fancy_entity_renderer.mixin.client;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import it.crystalnest.fancy_entity_renderer.api.entity.player.state.FancyPlayerRenderState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.render.pip.GuiEntityRenderer;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.gui.render.state.pip.GuiEntityRenderState;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Injects into {@link GuiEntityRenderer} to handle Fancy Entity Widgets.
 */
@Mixin(GuiEntityRenderer.class)
public abstract class GuiEntityRendererMixin extends PictureInPictureRenderer<GuiEntityRenderState> {
  /**
   * Shadowed {@link GuiEntityRenderer#entityRenderDispatcher}.
   */
  @Final
  @Shadow
  private EntityRenderDispatcher entityRenderDispatcher;

  /**
   * Useless constructor to make the compiler happy.
   */
  private GuiEntityRendererMixin(MultiBufferSource.BufferSource bufferSource) {
    super(bufferSource);
  }

  /**
   * Injects at the start of the method {@link GuiEntityRenderer#renderToTexture(GuiEntityRenderState, PoseStack)}.<br>
   * Checks if the entity render state is that of a fancy entity widget and, if so, handles it properly and cancels the original method.
   *
   * @param guiState submitted {@link GuiEntityRenderState} with the entity render state.
   * @param poseStack {@link PoseStack}.
   * @param ci {@link CallbackInfo}.
   */
  @Inject(method = "renderToTexture(Lnet/minecraft/client/gui/render/state/pip/GuiEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;)V", at = @At(value = "HEAD"), cancellable = true)
  private void onRenderToTexture(GuiEntityRenderState guiState, PoseStack poseStack, CallbackInfo ci) {
    if (guiState.renderState() instanceof FancyPlayerRenderState renderState) {
      Minecraft.getInstance().gameRenderer.getLighting().setupFor(Lighting.Entry.ENTITY_IN_UI);
      poseStack.translate(guiState.translation().x, guiState.translation().y, guiState.translation().z);
      poseStack.scale(1, -1, -1);
      poseStack.mulPose(guiState.rotation());
      if (guiState.overrideCameraAngle() != null) {
        entityRenderDispatcher.overrideCameraOrientation(guiState.overrideCameraAngle());
      }
      this.entityRenderDispatcher.setRenderShadow(false);
      // noinspection DataFlowIssue: Entity is null, but it won't get used anyway because extractRenderState was overridden.
      entityRenderDispatcher.render(null, 0, 0, 0, 0, poseStack, bufferSource, LightTexture.FULL_BRIGHT, renderState.renderer);
      this.entityRenderDispatcher.setRenderShadow(true);
      ci.cancel();
    }
  }
}
