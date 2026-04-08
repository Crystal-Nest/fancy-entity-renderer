package it.crystalnest.fancy_entity_renderer.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import it.crystalnest.fancy_entity_renderer.api.entity.player.state.FancyPlayerRenderState;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Injects into {@link ItemInHandLayer} to handle baby player models.
 */
@Mixin(ItemInHandLayer.class)
public abstract class ItemInHandLayerMixin {
  /**
   * Modifies the return value of {@link ItemInHandLayer#useBabyOffset(ArmedEntityRenderState)}.<br>
   * Returns the appropriate result considering fancy player renders.
   *
   * @param original original check value.
   * @param state render state.
   * @return whether to consider valid the original baby check value.
   */
  @ModifyReturnValue(method = "useBabyOffset", at = @At(value = "RETURN"))
  private boolean modifyBabyCheck(boolean original, ArmedEntityRenderState state) {
    return original && !(state instanceof FancyPlayerRenderState);
  }
}
