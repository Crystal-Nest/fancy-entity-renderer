package it.crystalnest.fancy_entity_renderer;

import it.crystalnest.fancy_entity_renderer.api.entity.player.FancyProfileFetcher;
import it.crystalnest.fancy_entity_renderer.platform.Services;
import org.jetbrains.annotations.ApiStatus;

/**
 * Common mod loader.
 */
@ApiStatus.Internal
public final class CommonModLoader {
  private CommonModLoader() {}

  /**
   * Initialize common operations across loaders.
   */
  public static void init() {
    if (Services.PLATFORM.isClient()) {
      FancyProfileFetcher.setup();
    }
  }
}