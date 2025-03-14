package it.crystalnest.fancy_entity_renderer.platform;

import it.crystalnest.fancy_entity_renderer.platform.model.Platform;
import it.crystalnest.fancy_entity_renderer.platform.services.PlatformHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

/**
 * Fabric platform helper.
 */
public final class FabricPlatformHelper implements PlatformHelper {
  @Override
  public Platform getPlatformName() {
    return Platform.FABRIC;
  }

  @Override
  public boolean isModLoaded(String modId) {
    return FabricLoader.getInstance().isModLoaded(modId);
  }

  @Override
  public boolean isDevEnv() {
    return FabricLoader.getInstance().isDevelopmentEnvironment();
  }

  @Override
  public boolean isClient() {
    return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
  }

  @Override
  public boolean isServer() {
    return FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER;
  }
}
