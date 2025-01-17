package it.crystalnest.fancy_entity_renderer.handler;

import it.crystalnest.fancy_entity_renderer.Constants;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = Constants.MOD_ID)
public final class EntityRenderersEventHandler {
  private EntityRenderersEventHandler() {}

  @SubscribeEvent
  private static void handle(EntityRenderersEvent.RegisterLayerDefinitions event) {
    event.registerLayerDefinition( Mode);
  }
}
