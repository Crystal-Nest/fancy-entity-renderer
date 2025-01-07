package it.crystalnest.fancy_entity_renderer;

import net.fabricmc.api.ModInitializer;
import org.jetbrains.annotations.ApiStatus;

/**
 * Mod loader.
 */
@ApiStatus.Internal
public final class ModLoader implements ModInitializer {
  @Override
  public void onInitialize() {
    CommonModLoader.init();
  }
}
