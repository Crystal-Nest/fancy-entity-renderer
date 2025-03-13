package it.crystalnest.fancy_entity_renderer;

import it.crystalnest.fancy_entity_renderer.api.entity.player.FancyProfileFetcher;
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
    // TODO: Check if the widget works only on screen or on GUIs too.
    // TODO: Avoid crashes when the mod is installed server-side.
    FancyProfileFetcher.setup();
  }
}