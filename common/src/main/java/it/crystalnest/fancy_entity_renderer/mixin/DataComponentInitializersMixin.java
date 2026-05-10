package it.crystalnest.fancy_entity_renderer.mixin;

import it.crystalnest.fancy_entity_renderer.imixin.DCIVanilla;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentInitializers;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Injects into {@link DataComponentInitializers} to alter build behavior for GUI contexts.
 */
@Mixin(DataComponentInitializers.class)
public abstract class DataComponentInitializersMixin implements DCIVanilla {
  /**
   * Shadowed {@link DataComponentInitializers#initializers}.
   */
  @Final
  @Shadow
  private List<DataComponentInitializers.InitializerEntry<?>> initializers;

  /**
   * Shadowed {@link DataComponentInitializers#registryEmpty(Map, ResourceKey)}.
   */
  @Shadow
  private static <T> void registryEmpty(Map<ResourceKey<? extends Registry<?>>, DataComponentInitializers.PendingComponentBuilders<?>> buildersByRegistry, ResourceKey<? extends Registry<? extends T>> registryKey) {
    throw new IllegalStateException("Calling shadowed method!");
  }

  /**
   * Shadowed {@link DataComponentInitializers#addBuilder(Map, ResourceKey, DataComponentMap.Builder)}.
   */
  @Shadow
  private static <T> void addBuilder(Map<ResourceKey<? extends Registry<?>>, DataComponentInitializers.PendingComponentBuilders<?>> buildersByRegistry, ResourceKey<T> key, DataComponentMap.Builder builder) {
    throw new IllegalStateException("Calling shadowed method!");
  }

  /**
   * Shadowed {@link DataComponentInitializers#createInitializerForRegistry(HolderLookup.Provider, DataComponentInitializers.PendingComponentBuilders)}.
   */
  @Shadow
  private static <T> DataComponentInitializers.PendingComponents<T> createInitializerForRegistry(HolderLookup.Provider context, DataComponentInitializers.PendingComponentBuilders<T> elementBuilders) {
    throw new IllegalStateException("Calling shadowed method!");
  }

  @Override
  public List<DataComponentInitializers.PendingComponents<?>> buildVanilla(HolderLookup.Provider context) {
    Map<ResourceKey<? extends Registry<?>>, DataComponentInitializers.PendingComponentBuilders<?>> buildersByRegistry = new HashMap<>();
    context.listRegistryKeys().filter(Registries.ITEM::equals).forEach((registryKey) -> registryEmpty(buildersByRegistry, registryKey));
    runInitializers(context).forEach((key, builder) -> addBuilder(buildersByRegistry, key, builder));
    return buildersByRegistry.values().stream().map(elementBuilders -> createInitializerForRegistry(context, elementBuilders)).collect(Collectors.toUnmodifiableList());
  }

  /**
   * Runs all Vanilla-only item initializers.
   *
   * @param context data provider.
   * @return map of keys and builders.
   */
  @Unique
  private Map<ResourceKey<?>, DataComponentMap.Builder> runInitializers(HolderLookup.Provider context) {
    Map<ResourceKey<?>, DataComponentMap.Builder> results = new HashMap<>();
    for (DataComponentInitializers.InitializerEntry<?> initializer : initializers) {
      if (Registries.ITEM.equals(initializer.key().registryKey()) && Identifier.DEFAULT_NAMESPACE.equals(initializer.key().identifier().getNamespace())) {
        DataComponentMap.Builder builder = results.computeIfAbsent(initializer.key(), k -> DataComponentMap.builder());
        initializer.run(builder, context);
      }
    }
    return results;
  }
}
