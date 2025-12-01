package it.crystalnest.fancy_entity_renderer.mixin.client;

import it.crystalnest.fancy_entity_renderer.api.entity.player.state.FancyPlayerRenderState;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRenderDispatcherMixin {
  @Inject(method = "getRenderer(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;)Lnet/minecraft/client/renderer/entity/EntityRenderer;", at = @At(value = "HEAD"), cancellable = true)
  private <S extends EntityRenderState> void onGetRenderer(S renderState, CallbackInfoReturnable<EntityRenderer<?, ? super S>> cir) {
    if (renderState instanceof FancyPlayerRenderState state) {
      state.renderer.extractRenderState(null, state, 0);
      cir.setReturnValue((EntityRenderer<?, ? super S>) state.renderer);
    }
  }
}
