package it.crystalnest.fancy_entity_renderer.platform;

import it.crystalnest.fancy_entity_renderer.api.entity.player.mock.FancyRegistryMock;
import it.crystalnest.fancy_entity_renderer.platform.services.RegistryHelper;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.callback.RegistryCallback;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * NeoForge registry helper.
 */
public class NeoForgeRegistryHelper implements RegistryHelper {
  @Override
  public <T> FancyRegistryMock<T> mockRegistry(ResourceKey<? extends Registry<? extends T>> key) {
    return new FancyRegistryMock<>(key) {
      @Override
      public boolean doesSync() {
        return false;
      }

      @Override
      public int getMaxId() {
        return 0;
      }

      @Override
      public void addCallback(@NotNull RegistryCallback<T> registryCallback) {}

      @Override
      public void addAlias(@NotNull ResourceLocation from, @NotNull ResourceLocation to) {}

      @Override
      public @NotNull ResourceLocation resolve(@NotNull ResourceLocation name) {
        return name;
      }

      @Override
      public @NotNull ResourceKey<T> resolve(@NotNull ResourceKey<T> key) {
        return key;
      }

      @Override
      public int getId(@NotNull ResourceKey<T> resourceKey) {
        return 0;
      }

      @Override
      public int getId(@NotNull ResourceLocation resourceLocation) {
        return 0;
      }

      @Override
      public boolean containsValue(@NotNull T t) {
        return false;
      }

      @Override
      public <A> @Nullable A getData(@NotNull DataMapType<T, A> dataMapType, @NotNull ResourceKey<T> resourceKey) {
        return null;
      }

      @Override
      public <A> @NotNull Map<ResourceKey<T>, A> getDataMap(@NotNull DataMapType<T, A> dataMapType) {
        return Map.of();
      }
    };
  }
}
