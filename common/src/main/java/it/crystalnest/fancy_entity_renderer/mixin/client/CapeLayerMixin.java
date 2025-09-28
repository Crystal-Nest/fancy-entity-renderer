package it.crystalnest.fancy_entity_renderer.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.blaze3d.vertex.PoseStack;
import it.crystalnest.fancy_entity_renderer.api.entity.player.mock.FancyPlayerMock;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import net.minecraft.world.entity.player.PlayerModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Injects into {@link CapeLayer} to handle custom render properties.
 */
@Mixin(CapeLayer.class)
public abstract class CapeLayerMixin {
  private CapeLayerMixin() {}

  /**
   * Modifies the result of {@link AbstractClientPlayer#isModelPartShown(PlayerModelPart)} inside the method {@link CapeLayer#render(PoseStack, MultiBufferSource, int, AbstractClientPlayer, float, float, float, float, float, float)}.<br>
   * If the player is a {@link FancyPlayerMock}, uses the custom render property.
   *
   * @param original original return value.
   * @param poseStack pose stack.
   * @param buffer buffer source.
   * @param packedLight packet light.
   * @param livingEntity player.
   * @param limbSwing limb swing.
   * @param limbSwingAmount limb swing amount.
   * @param partialTick partial tick.
   * @param ageInTicks age in ticks.
   * @param netHeadYaw head yaw.
   * @param headPitch head pitch.
   * @return whether to render the cape.
   */
  @ModifyExpressionValue(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/player/AbstractClientPlayer;FFFFFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/AbstractClientPlayer;isModelPartShown(Lnet/minecraft/world/entity/player/PlayerModelPart;)Z"))
  private boolean renderIfCapeShown(boolean original, PoseStack poseStack, MultiBufferSource buffer, int packedLight, AbstractClientPlayer livingEntity, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
    return livingEntity instanceof FancyPlayerMock playerMock ? playerMock.showCape : original;
  }

  /**
   * Injects into the method {@link CapeLayer#render(PoseStack, MultiBufferSource, int, AbstractClientPlayer, float, float, float, float, float, float)} after the call to {@link PoseStack#pushPose()}.<br>
   * If the player is a {@link FancyPlayerMock} and is baby, corrects the cape size and position.
   *
   * @param poseStack
   * @param buffer
   * @param packedLight
   * @param livingEntity
   * @param limbSwing
   * @param limbSwingAmount
   * @param partialTick
   * @param ageInTicks
   * @param netHeadYaw
   * @param headPitch
   * @param ci
   */
  @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/player/AbstractClientPlayer;FFFFFF)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;pushPose()V"))
  private void onRender(PoseStack poseStack, MultiBufferSource buffer, int packedLight, AbstractClientPlayer livingEntity, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
    if (livingEntity instanceof FancyPlayerMock playerMock && playerMock.isBaby) {
      poseStack.scale(0.5F, 0.5F, 0.5F);
      poseStack.translate(0, 1.5F, 0);
    }
  }
}
