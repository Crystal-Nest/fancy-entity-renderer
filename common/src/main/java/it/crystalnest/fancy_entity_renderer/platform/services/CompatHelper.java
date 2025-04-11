package it.crystalnest.fancy_entity_renderer.platform.services;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.resources.ResourceLocation;

/**
 * Compatibility helper.
 */
public interface CompatHelper {
  /**
   * Updates the given state with the given fire type.
   *
   * @param state render state.
   * @param fireType fire type.
   */
  void setOnFire(EntityRenderState state, ResourceLocation fireType);
}
