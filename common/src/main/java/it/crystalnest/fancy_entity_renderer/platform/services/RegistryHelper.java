package it.crystalnest.fancy_entity_renderer.platform.services;

import it.crystalnest.fancy_entity_renderer.api.entity.player.mock.FancyRegistryMock;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

/**
 * Registry helper.
 */
public interface RegistryHelper {
  /**
   * Provides a suitable registry mock.
   *
   * @param key registry key.
   * @return registry mock.
   * @param <T> registry type.
   */
  <T> FancyRegistryMock<T> mockRegistry(ResourceKey<? extends Registry<? extends T>> key);
}
