package it.crystalnest.fancy_entity_renderer.api.entity.player.mock;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Lifecycle;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Mock for registry.
 *
 * @param <T> registry type.
 */
@SuppressWarnings("unchecked")
public abstract class FancyRegistryMock<T> implements Registry<T> {
  /**
   * Registry key.
   */
  private final ResourceKey<? extends Registry<T>> key;

  /**
   * Lookup.
   */
  private final HolderLookup.RegistryLookup<T> lookup;

  /**
   * @param key registry key.
   */
  public FancyRegistryMock(ResourceKey<? extends Registry<? extends T>> key) {
    this.key = (ResourceKey<? extends Registry<T>>) key;
    this.lookup = new HolderLookup.RegistryLookup<>() {
      public @NotNull ResourceKey<? extends Registry<? extends T>> key() {
        return FancyRegistryMock.this.key;
      }

      public @NotNull Lifecycle registryLifecycle() {
        return FancyRegistryMock.this.registryLifecycle();
      }

      public @NotNull Optional<Holder.Reference<T>> get(@NotNull ResourceKey<T> key) {
        return FancyRegistryMock.this.getHolder(key);
      }

      public @NotNull Optional<HolderSet.Named<T>> get(@NotNull TagKey<T> key) {
        return FancyRegistryMock.this.getTag(key);
      }

      public @NotNull Stream<Holder.Reference<T>> listElements() {
        return FancyRegistryMock.this.holders();
      }

      public @NotNull Stream<HolderSet.Named<T>> listTags() {
        return FancyRegistryMock.this.getTags().map(Pair::getSecond);
      }
    };
  }

  @NotNull
  @Override
  public Iterator<T> iterator() {
    return Collections.emptyIterator();
  }

  @Override
  public @NotNull ResourceKey<? extends Registry<T>> key() {
    return key;
  }

  @Nullable
  @Override
  public ResourceLocation getKey(@NotNull T key) {
    return null;
  }

  @Override
  public @NotNull Optional<ResourceKey<T>> getResourceKey(@NotNull T key) {
    return Optional.empty();
  }

  @Override
  public int getId(@Nullable T key) {
    return 0;
  }

  @Nullable
  @Override
  public T get(@Nullable ResourceKey<T> resourceKey) {
    return null;
  }

  @Nullable
  @Override
  public T get(@Nullable ResourceLocation resourceLocation) {
    return null;
  }

  @Override
  public @NotNull Lifecycle lifecycle(@NotNull T key) {
    return Lifecycle.stable();
  }

  @Override
  public @NotNull Lifecycle registryLifecycle() {
    return Lifecycle.stable();
  }

  @Override
  public @NotNull Set<ResourceLocation> keySet() {
    return Set.of();
  }

  @Override
  public @NotNull Set<Map.Entry<ResourceKey<T>, T>> entrySet() {
    return Set.of();
  }

  @Override
  public @NotNull Set<ResourceKey<T>> registryKeySet() {
    return Set.of();
  }

  @Override
  public @NotNull Optional<Holder.Reference<T>> getRandom(@NotNull RandomSource randomSource) {
    return Optional.empty();
  }

  @Override
  public boolean containsKey(@NotNull ResourceLocation resourceLocation) {
    return false;
  }

  @Override
  public boolean containsKey(@NotNull ResourceKey<T> resourceKey) {
    return false;
  }

  @Override
  public @NotNull Registry<T> freeze() {
    return this;
  }

  @Override
  public Holder.@NotNull Reference<T> createIntrusiveHolder(@NotNull T key) {
    throw new IllegalStateException("This registry can't create intrusive holders");
  }

  @Override
  public @NotNull Optional<Holder.Reference<T>> getHolder(int i) {
    return Optional.empty();
  }

  @Override
  public @NotNull Optional<Holder.Reference<T>> getHolder(@NotNull ResourceKey<T> resourceKey) {
    return Optional.empty();
  }

  @Override
  public @NotNull Holder<T> wrapAsHolder(@NotNull T key) {
    return Holder.direct(key);
  }

  @Override
  public Holder.@NotNull Reference<T> getHolderOrThrow(@NotNull ResourceKey<T> key) {
    return Holder.Reference.createStandAlone(holderOwner(), key);
  }

  @Override
  public @NotNull Stream<Holder.Reference<T>> holders() {
    return Stream.empty();
  }

  @Override
  public @NotNull Optional<HolderSet.Named<T>> getTag(@NotNull TagKey<T> tagKey) {
    return Optional.empty();
  }

  @Override
  @SuppressWarnings("deprecation")
  public HolderSet.@NotNull Named<T> getOrCreateTag(@NotNull TagKey<T> tagKey) {
    return HolderSet.emptyNamed(holderOwner(), tagKey);
  }

  @Override
  public @NotNull Stream<Pair<TagKey<T>, HolderSet.Named<T>>> getTags() {
    return Stream.empty();
  }

  @Override
  public @NotNull Stream<TagKey<T>> getTagNames() {
    return Stream.empty();
  }

  @Override
  public void resetTags() {}

  @Override
  public void bindTags(@NotNull Map<TagKey<T>, List<Holder<T>>> map) {}

  @Override
  public @NotNull HolderOwner<T> holderOwner() {
    return lookup;
  }

  @Override
  public HolderLookup.@NotNull RegistryLookup<T> asLookup() {
    return lookup;
  }

  @Nullable
  @Override
  public T byId(int i) {
    return null;
  }

  @Override
  public int size() {
    return 0;
  }
}
