package it.crystalnest.fancy_entity_renderer.compat;

import it.crystalnest.prometheus.api.type.FireTypeChanger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

/**
 * Prometheus compatibility.
 */
public final class Prometheus {
  private Prometheus() {}

  /**
   * Updates the given state with the given fire type.
   *
   * @param entity entity to render.
   * @param fireType fire type.
   */
  public static void setOnFire(Entity entity, ResourceLocation fireType) {
    ((FireTypeChanger) entity).setFireType(fireType);
  }
}
