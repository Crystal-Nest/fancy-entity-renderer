package it.crystalnest.fancy_entity_renderer.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import it.crystalnest.fancy_entity_renderer.api.entity.player.mock.FancyPlayerMock;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Injects into {@link LivingEntityRenderer} to handle custom render properties.
 */
@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin {
  private LivingEntityRendererMixin() {}

  /**
   * Modifies the return value of the method {@link LivingEntityRenderer#isEntityUpsideDown(LivingEntity)}.<br>
   * Checks also whether the player is a {@link FancyPlayerMock} and is set to be upside down.
   *
   * @param original original return value.
   * @param entity entity.
   * @return whether the entity is upside down.
   */
  @ModifyReturnValue(method = "isEntityUpsideDown", at = @At(value = "RETURN"))
  private static boolean onIsUpsideDown(boolean original, LivingEntity entity) {
    return original || (entity instanceof FancyPlayerMock playerMock && playerMock.isUpsideDown);
  }

  /**
   * Wraps the call to {@link PoseStack#scale(float, float, float)} inside the method {@link LivingEntityRenderer#render(LivingEntity, float, float, PoseStack, MultiBufferSource, int)}.<br>
   * Prevents other mods (e.g., Additional Entity Attributes) from forcefully overriding the player mock scale.
   *
   * @param instance pose stack.
   * @param x x scaling.
   * @param y y scaling.
   * @param z z scaling.
   * @param original original scale call.
   * @param entity entity to scale.
   * @param entityYaw entity yaw.
   * @param partialTicks partial ticks.
   * @param poseStack pose stack.
   * @param buffer buffer source.
   * @param packedLight packed light.
   */
  @WrapOperation(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;scale(FFF)V", ordinal = 0))
  private void test(PoseStack instance, float x, float y, float z, Operation<Void> original, LivingEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
    if (entity instanceof FancyPlayerMock playerMock) {
      float scale = playerMock.getScale();
      instance.scale(scale, scale, scale);
    } else {
      original.call(instance, x, y, z);
    }
  }
}
