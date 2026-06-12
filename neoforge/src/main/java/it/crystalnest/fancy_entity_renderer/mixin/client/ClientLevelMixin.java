package it.crystalnest.fancy_entity_renderer.mixin.client;

import it.crystalnest.fancy_entity_renderer.api.entity.player.mock.FancyLevelMock;
import net.minecraft.client.multiplayer.ClientLevel;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.level.LevelEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Suppresses NeoForge's level load event for render-only mock levels.
 */
@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin {
  private ClientLevelMixin() {}

  @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/neoforged/bus/api/IEventBus;post(Lnet/neoforged/bus/api/Event;)Lnet/neoforged/bus/api/Event;"))
  private Event onInit(IEventBus eventBus, Event event) {
    if (event instanceof LevelEvent.Load && FancyLevelMock.isConstructing()) {
      return event;
    }
    return eventBus.post(event);
  }
}
