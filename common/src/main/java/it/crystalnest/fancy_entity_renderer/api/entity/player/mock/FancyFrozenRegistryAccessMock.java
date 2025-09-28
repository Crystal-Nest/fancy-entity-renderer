package it.crystalnest.fancy_entity_renderer.api.entity.player.mock;

import it.crystalnest.fancy_entity_renderer.platform.Services;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

/**
 * Mock for frozen registry access.
 */
@SuppressWarnings("unchecked")
public class FancyFrozenRegistryAccessMock implements RegistryAccess.Frozen {
  /**
   * Registries computed so far.
   */
  private final Map<ResourceKey<? extends Registry<?>>, Registry<?>> registries = new ConcurrentHashMap<>();

  @Override
  public <E> @NotNull Optional<Registry<E>> registry(@NotNull ResourceKey<? extends Registry<? extends E>> resourceKey) {
    return Optional.of((Registry<E>) registries.computeIfAbsent(resourceKey, k -> Services.REGISTRY.mockRegistry(resourceKey)));
  }

  @Override
  public <E> @NotNull Registry<E> registryOrThrow(@NotNull ResourceKey<? extends Registry<? extends E>> registryKey) {
    return (Registry<E>) registries.computeIfAbsent(registryKey, k -> Services.REGISTRY.mockRegistry(registryKey));
  }

  @Override
  public @NotNull Stream<RegistryEntry<?>> registries() {
    return Stream.empty();
  }
}
