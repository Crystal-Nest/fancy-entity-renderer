package it.crystalnest.fancy_entity_renderer.handler;

import it.crystalnest.fancy_entity_renderer.Constants;
import it.crystalnest.fancy_entity_renderer.api.entity.player.FancyProfileFetcher;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

/**
 * Handles client events.
 */
@EventBusSubscriber(modid = Constants.MOD_ID, value = Dist.CLIENT)
public final class ClientEventsHandler {
  private ClientEventsHandler() {}

  /**
   * Handles the {@link FMLClientSetupEvent} event.
   * 
   * @param event {@link FMLClientSetupEvent}.
   */
  @SubscribeEvent
  private static void handle(FMLClientSetupEvent event) {
    FancyProfileFetcher.setup();
  }
}
