package it.crystalnest.fancy_entity_renderer.platform;

import it.crystalnest.fancy_entity_renderer.api.entity.player.mock.FancyRegistryMock;
import it.crystalnest.fancy_entity_renderer.platform.services.RegistryHelper;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

/**
 * Forge registry helper.
 */
public class ForgeRegistryHelper implements RegistryHelper {
  @Override
  public <T> FancyRegistryMock<T> mockRegistry(ResourceKey<? extends Registry<? extends T>> key) {
    return new FancyRegistryMock<>(key) {};
  }
}
