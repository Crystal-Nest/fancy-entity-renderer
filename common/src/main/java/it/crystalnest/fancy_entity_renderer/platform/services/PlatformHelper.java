package it.crystalnest.fancy_entity_renderer.platform.services;

import it.crystalnest.fancy_entity_renderer.platform.model.Environment;
import it.crystalnest.fancy_entity_renderer.platform.model.Platform;

/**
 * Platform specific helper.
 */
public interface PlatformHelper {
  /**
   * Gets the name of the current platform
   *
   * @return The name of the current platform.
   */
  Platform getPlatformName();

  /**
   * Checks if a mod with the given id is loaded.
   *
   * @param modId The mod to check if it is loaded.
   * @return True if the mod is loaded, false otherwise.
   */
  boolean isModLoaded(String modId);

  /**
   * Checks if the game is currently in a development environment.
   *
   * @return True if in a development environment, false otherwise.
   */
  boolean isDevEnv();

  /**
   * Checks if the mod is loaded in a client environment.
   *
   * @return True if in a client environment, false otherwise.
   */
  boolean isClient();

  /**
   * Checks if the mod is loaded in a server environment.
   *
   * @return True if in a server environment, false otherwise.
   */
  boolean isServer();

  /**
   * Gets the name of the environment type as a string.
   *
   * @return The name of the environment type.
   */
  default Environment getEnvironment() {
    return isDevEnv() ? Environment.DEVELOPMENT : Environment.PRODUCTION;
  }
}
