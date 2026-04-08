package it.crystalnest.fancy_entity_renderer.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import it.crystalnest.fancy_entity_renderer.api.entity.player.state.FancyPlayerRenderState;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 *
 */
@Mixin(ItemInHandLayer.class)
public abstract class ItemInHandLayerMixin {
  /**
   *
   *
   * @param original
   * @param state
   * @return
   */
  @ModifyReturnValue(method = "useBabyOffset", at = @At(value = "RETURN"))
  private boolean modifyBabyCheck(boolean original, ArmedEntityRenderState state) {
    return original && !(state instanceof FancyPlayerRenderState);
  }
}
