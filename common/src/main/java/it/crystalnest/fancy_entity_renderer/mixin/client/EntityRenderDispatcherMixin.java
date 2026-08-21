package it.crystalnest.fancy_entity_renderer.mixin.client;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.crystalnest.fancy_entity_renderer.api.entity.player.FancyPlayerRenderer;
import it.crystalnest.fancy_entity_renderer.api.entity.player.mock.FancyPlayerMock;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelReader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Injects into {@link EntityRenderDispatcher} to handle custom render properties.
 */
@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRenderDispatcherMixin {
  private EntityRenderDispatcherMixin() {}

  /**
   * Injects into the method {@link EntityRenderDispatcher#getRenderer(Entity)} at the end.<br>
   * If the player is a {@link FancyPlayerMock}, returns the correct renderer.
   *
   * @param entity entity to render.
   * @param cir {@link CallbackInfoReturnable}.
   * @param <T> entity type.
   */
  @SuppressWarnings("unchecked")
  @Inject(method = "getRenderer", at = @At(value = "HEAD"), cancellable = true)
  private <T extends Entity> void getRenderer(final Entity entity, final CallbackInfoReturnable<EntityRenderer<? super T>> cir) {
    if (entity instanceof FancyPlayerMock player) {
      cir.setReturnValue((EntityRenderer<? super T>) (player.isSlim ? FancyPlayerRenderer.SLIM_RENDERER : FancyPlayerRenderer.WIDE_RENDERER));
    }
  }

  /**
   * Prevents world shadows from being rendered for GUI player mocks.
   *
   * @param poseStack pose stack.
   * @param buffer buffer source.
   * @param entity entity to render.
   * @param weight weight.
   * @param partialTicks partial ticks.
   * @param level level.
   * @param size size.
   * @return {@code false} if the entity is a {@link FancyPlayerMock}, {@code true} otherwise.
   */
  @WrapWithCondition(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;renderShadow(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/entity/Entity;FFLnet/minecraft/world/level/LevelReader;F)V"))
  private boolean wrapRenderShadow(PoseStack poseStack, MultiBufferSource buffer, Entity entity, float weight, float partialTicks, LevelReader level, float size) {
    return !(entity instanceof FancyPlayerMock);
  }

  /**
   * Prevents hitboxes from being rendered for GUI player mocks.
   *
   * @param poseStack pose stack.
   * @param buffer buffer source.
   * @param entity entity to render.
   * @param red red channel value.
   * @param green green channel value.
   * @param blue blue channel value.
   * @param alpha alpha channel value.
   * @return {@code false} if the entity is a {@link FancyPlayerMock}, {@code true} otherwise.
   */
  @WrapWithCondition(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;renderHitbox(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/world/entity/Entity;FFFF)V"))
  private boolean wrapRenderHitbox(PoseStack poseStack, VertexConsumer buffer, Entity entity, float red, float green, float blue, float alpha) {
    return !(entity instanceof FancyPlayerMock);
  }
}
