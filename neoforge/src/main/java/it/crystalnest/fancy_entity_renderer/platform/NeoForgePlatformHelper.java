package it.crystalnest.fancy_entity_renderer.platform;

import it.crystalnest.fancy_entity_renderer.platform.model.Platform;
import it.crystalnest.fancy_entity_renderer.platform.services.PlatformHelper;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;

/**
 * NeoForge platform helper.
 */
public final class NeoForgePlatformHelper implements PlatformHelper {
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
    return !FMLLoader.getCurrent().isProduction();
  }

  @Override
  public boolean isClient() {
    return FMLLoader.getCurrent().getDist().isClient();
  }

  @Override
  public boolean isServer() {
    return FMLLoader.getCurrent().getDist().isDedicatedServer();
  }
}
