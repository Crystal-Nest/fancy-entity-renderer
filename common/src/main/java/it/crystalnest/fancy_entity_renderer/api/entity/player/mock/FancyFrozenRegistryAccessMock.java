package it.crystalnest.fancy_entity_renderer.api.entity.player.mock;

import it.crystalnest.fancy_entity_renderer.platform.Services;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
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
   * Registry access backed by vanilla's static registries.
   */
  private static final RegistryAccess.Frozen BUILTIN_REGISTRIES = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);

  /**
   * Real registry access to delegate to.
   */
  private final RegistryAccess delegate;

  /**
   * Registries computed so far.
   */
  private final Map<ResourceKey<? extends Registry<?>>, Registry<?>> registries = new ConcurrentHashMap<>();

  /**
   * Creates a registry access that delegates to the active client registry access when available.
   */
  public FancyFrozenRegistryAccessMock() {
    this(currentRegistryAccess());
  }

  /**
   * @param delegate real registry access to delegate to.
   */
  public FancyFrozenRegistryAccessMock(RegistryAccess delegate) {
    this.delegate = delegate;
  }

  /**
   * @return real client registry access when connected, otherwise vanilla's static registries.
   */
  private static RegistryAccess currentRegistryAccess() {
    Minecraft minecraft = Minecraft.getInstance();
    ClientPacketListener connection = minecraft.getConnection();

    if (connection != null) {
      return connection.registryAccess();
    }

    if (minecraft.level != null) {
      return minecraft.level.registryAccess();
    }

    return BUILTIN_REGISTRIES;
  }

  /**
   * @param entry registry entry.
   * @return typed registry access entry.
   * @param <T> registry type.
   */
  private static <T> RegistryEntry<T> registryEntry(Map.Entry<ResourceKey<? extends Registry<?>>, Registry<?>> entry) {
    return new RegistryEntry<>((ResourceKey<? extends Registry<T>>) entry.getKey(), (Registry<T>) entry.getValue());
  }

  @Override
  public <E> @NotNull Optional<Registry<E>> registry(@NotNull ResourceKey<? extends Registry<? extends E>> resourceKey) {
    return delegate.registry(resourceKey).or(() -> Optional.of((Registry<E>) registries.computeIfAbsent(resourceKey, k -> Services.REGISTRY.mockRegistry(resourceKey))));
  }

  @Override
  public <E> @NotNull Registry<E> registryOrThrow(@NotNull ResourceKey<? extends Registry<? extends E>> registryKey) {
    return registry(registryKey).orElseThrow(() -> new IllegalStateException("Missing registry: " + registryKey));
  }

  @Override
  public @NotNull Stream<RegistryEntry<?>> registries() {
    return Stream.concat(delegate.registries(), registries.entrySet().stream().map(FancyFrozenRegistryAccessMock::registryEntry));
  }

  @Override
  public <T> HolderLookup.@NotNull RegistryLookup<T> lookupOrThrow(@NotNull ResourceKey<? extends Registry<? extends T>> registryKey) {
    return registryOrThrow(registryKey).asLookup();
  }
}
