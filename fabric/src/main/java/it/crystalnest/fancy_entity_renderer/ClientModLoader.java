package it.crystalnest.fancy_entity_renderer;

import it.crystalnest.fancy_entity_renderer.api.entity.player.FancyProfileFetcher;
import net.fabricmc.api.ClientModInitializer;
import org.jetbrains.annotations.ApiStatus;

/**
 * Client mod loader.
 */
@ApiStatus.Internal
public final class ClientModLoader implements ClientModInitializer {
  @Override
  public void onInitializeClient() {
    FancyProfileFetcher.setup();
  }
}
