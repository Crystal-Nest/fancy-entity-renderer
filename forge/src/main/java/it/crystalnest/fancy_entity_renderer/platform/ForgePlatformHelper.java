package it.crystalnest.fancy_entity_renderer.platform;

import it.crystalnest.fancy_entity_renderer.platform.model.Platform;
import it.crystalnest.fancy_entity_renderer.platform.services.PlatformHelper;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;

/**
 * Forge platform helper.
 */
public final class ForgePlatformHelper implements PlatformHelper {
  @Override
  public Platform getPlatformName() {
    return Platform.NEOFORGE;
  }

  @Override
  public boolean isModLoaded(String modId) {
    return ModList.get().isLoaded(modId);
  }

  @Override
  public boolean isDevEnv() {
    return !FMLLoader.isProduction();
  }
}
