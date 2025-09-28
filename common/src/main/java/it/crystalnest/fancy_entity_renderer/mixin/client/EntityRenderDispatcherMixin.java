package it.crystalnest.fancy_entity_renderer.mixin.client;

import it.crystalnest.fancy_entity_renderer.api.entity.player.FancyPlayerRenderer;
import it.crystalnest.fancy_entity_renderer.api.entity.player.mock.FancyPlayerMock;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
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
}
