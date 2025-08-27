package it.crystalnest.fancy_entity_renderer.platform.services;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

/**
 * Compatibility helper.
 */
public interface CompatHelper {
  /**
   * Updates the given state with the given fire type.
   *
   * @param entity entity to render.
   * @param fireType fire type.
   */
  void setOnFire(Entity entity, ResourceLocation fireType);
}
