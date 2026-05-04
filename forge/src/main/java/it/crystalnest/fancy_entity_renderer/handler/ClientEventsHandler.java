package it.crystalnest.fancy_entity_renderer.handler;

import it.crystalnest.fancy_entity_renderer.Constants;
import it.crystalnest.fancy_entity_renderer.api.entity.player.FancyProfileFetcher;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

/**
 * Handles client events.
 */
@EventBusSubscriber(modid = Constants.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class ClientEventsHandler {
  private ClientEventsHandler() {}

  /**
   * Handles the {@link FMLClientSetupEvent} event.
   *
   * @param event {@link FMLClientSetupEvent}.
   */
  @SubscribeEvent
  public static void handle(FMLClientSetupEvent event) {
    FancyProfileFetcher.setup();
  }
}
