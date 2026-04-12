package it.crystalnest.fancy_entity_renderer.mixin.client;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import it.crystalnest.fancy_entity_renderer.api.entity.player.state.FancyPlayerRenderState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.render.pip.GuiEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import net.minecraft.client.renderer.state.gui.pip.GuiEntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import org.joml.Quaternionf;
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
public abstract class GuiEntityRendererMixin {
  /**
   * Shadowed {@link GuiEntityRenderer#entityRenderDispatcher}.
   */
  @Final
  @Shadow
  private EntityRenderDispatcher entityRenderDispatcher;

  /**
   * Injects at the start of the method {@link GuiEntityRenderer#renderToTexture(GuiEntityRenderState, PoseStack)}.<br>
   * Checks if the entity render state is that of a fancy entity widget and, if so, handles it properly and cancels the original method.
   *
   * @param entityState submitted {@link GuiEntityRenderState} with the entity render state.
   * @param poseStack {@link PoseStack}.
   * @param ci {@link CallbackInfo}.
   */
  @Inject(method = "renderToTexture(Lnet/minecraft/client/renderer/state/gui/pip/GuiEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;)V", at = @At(value = "HEAD"), cancellable = true)
  private void onRenderToTexture(GuiEntityRenderState entityState, PoseStack poseStack, CallbackInfo ci) {
    if (entityState.renderState() instanceof FancyPlayerRenderState renderState) {
      Minecraft.getInstance().gameRenderer.getLighting().setupFor(Lighting.Entry.ENTITY_IN_UI);
      poseStack.translate(entityState.translation().x(), entityState.translation().y(), entityState.translation().z());
      poseStack.scale(1, -1, -1);
      poseStack.mulPose(entityState.rotation());
      FeatureRenderDispatcher dispatcher = Minecraft.getInstance().gameRenderer.getFeatureRenderDispatcher();
      CameraRenderState camera = new CameraRenderState();
      if (entityState.overrideCameraAngle() != null) {
        camera.orientation = entityState.overrideCameraAngle().get(new Quaternionf());
      }
      entityRenderDispatcher.submit(renderState, camera, 0, 0, 0, poseStack, dispatcher.getSubmitNodeStorage());
      dispatcher.renderAllFeatures();
      ci.cancel();
    }
  }
}
