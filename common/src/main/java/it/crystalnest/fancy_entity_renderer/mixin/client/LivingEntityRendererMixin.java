package it.crystalnest.fancy_entity_renderer.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import it.crystalnest.fancy_entity_renderer.api.entity.player.mock.FancyPlayerMock;
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
}
