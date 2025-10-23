package it.crystalnest.fancy_entity_renderer.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import it.crystalnest.fancy_entity_renderer.api.entity.player.mock.FancyPlayerMock;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Injects into {@link Entity} to handle custom render properties.
 */
@Mixin(Entity.class)
public abstract class EntityMixin {
  private EntityMixin() {}

  /**
   * Modifies the return value of {@link Entity#getBbWidth()}.
   *
   * @param original original return value.
   * @return bounding box width.
   */
  @ModifyReturnValue(method = "getBbWidth", at = @At(value = "RETURN"))
  private float modifyGetBbWidth(float original) {
    return ((Object) this) instanceof FancyPlayerMock playerMock ? playerMock.boundingBoxWidth : original;
  }

  /**
   * Modifies the return value of {@link Entity#getBbHeight()}.
   *
   * @param original original return value.
   * @return bounding box height.
   */
  @ModifyReturnValue(method = "getBbHeight", at = @At(value = "RETURN"))
  private float modifyGetBbHeight(float original) {
    return ((Object) this) instanceof FancyPlayerMock playerMock ? playerMock.boundingBoxHeight : original;
  }
}
