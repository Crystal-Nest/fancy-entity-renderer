package it.crystalnest.fancy_entity_renderer.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import it.crystalnest.fancy_entity_renderer.api.entity.player.FancyPlayerRenderer;
import it.crystalnest.fancy_entity_renderer.api.entity.player.state.FancyPlayerRenderState;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.player.PlayerModelType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

/**
 * Injects into {@link EntityRenderDispatcher} to use the correct renderers.
 */
@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRenderDispatcherMixin {
  /**
   * Map of {@link FancyPlayerRenderer}s for the model type.
   */
  @Unique
  private Map<PlayerModelType, FancyPlayerRenderer> fancyPlayerRenderers = Map.of();

  /**
   * Injects at the start of the method {@link EntityRenderDispatcher#getRenderer(EntityRenderState)}.<br>
   * If the render state is a {@link FancyPlayerRenderState}, returns the correct {@link FancyPlayerRenderer}.
   *
   * @param renderState render state.
   * @param cir {@link CallbackInfoReturnable}.
   * @param <S> type of render state.
   */
  @SuppressWarnings("unchecked")
  @Inject(method = "getRenderer(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;)Lnet/minecraft/client/renderer/entity/EntityRenderer;", at = @At(value = "HEAD"), cancellable = true)
  private <S extends EntityRenderState> void onGetRenderer(S renderState, CallbackInfoReturnable<EntityRenderer<?, ? super S>> cir) {
    if (renderState instanceof FancyPlayerRenderState state) {
      FancyPlayerRenderer renderer = fancyPlayerRenderers.getOrDefault(state.skin.model(), fancyPlayerRenderers.get(PlayerModelType.SLIM));
      renderer.extractRenderState(null, state, 0);
      cir.setReturnValue((EntityRenderer<?, ? super S>) renderer);
    }
  }

  /**
   * Injects at the end of the method {@link EntityRenderDispatcher#onResourceManagerReload(ResourceManager)}.<br>
   * Updates the map of {@link FancyPlayerRenderer}s.
   *
   * @param manager resource manager.
   * @param ci {@link CallbackInfo}.
   * @param context render context.
   */
  @Inject(method = "onResourceManagerReload", at = @At(value = "TAIL"))
  private void onOnResourceManagerReload(ResourceManager manager, CallbackInfo ci, @Local(ordinal = 0) EntityRendererProvider.Context context) {
    fancyPlayerRenderers = Map.of(PlayerModelType.WIDE, new FancyPlayerRenderer(context, false), PlayerModelType.SLIM, new FancyPlayerRenderer(context, true));
  }
}
