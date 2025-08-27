package it.crystalnest.fancy_entity_renderer.compat;

import it.crystalnest.soul_fire_d.api.FireManager;
import it.crystalnest.soul_fire_d.api.type.FireTypeChanger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

/**
 * Soul Fire'd compatibility.
 */
public final class SoulFired {
  private SoulFired() {}

  /**
   * Updates the given state with the given fire type.
   *
   * @param entity entity to render.
   * @param fireType fire type.
   */
  public static void setOnFire(Entity entity, ResourceLocation fireType) {
    ((FireTypeChanger) entity).setFireType(FireManager.ensure(fireType));
  }
}
