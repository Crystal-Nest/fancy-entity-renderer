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
    return Platform.FORGE;
  }

  @Override
  public boolean isModLoaded(String modId) {
    return ModList.get().isLoaded(modId);
  }

  @Override
  public boolean isDevEnv() {
    return !FMLLoader.isProduction();
  }

  @Override
  public boolean isClient() {
    return FMLLoader.getDist().isClient();
  }

  @Override
  public boolean isServer() {
    return FMLLoader.getDist().isDedicatedServer();
  }
}
