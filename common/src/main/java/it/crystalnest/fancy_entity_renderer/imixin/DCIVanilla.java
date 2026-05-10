package it.crystalnest.fancy_entity_renderer.imixin;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentInitializers;

import java.util.List;

/**
 * Data Component Initializers for Vanilla-only.
 */
public interface DCIVanilla {
  /**
   * Builds Vanilla-only data component registries.
   *
   * @param context data provider.
   * @return list of pending data components.
   */
  List<DataComponentInitializers.PendingComponents<?>> buildVanilla(HolderLookup.Provider context);
}
