package it.crystalnest.fancy_entity_renderer.compat;

import it.crystalnest.soul_fire_d.api.FireManager;
import it.crystalnest.soul_fire_d.api.type.FireTypeChanger;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.resources.ResourceLocation;

/**
 * Soul Fire'd compatibility.
 */
public final class SoulFired {
  private SoulFired() {}

  /**
   * Updates the given state with the given fire type.
   *
   * @param state render state.
   * @param fireType fire type.
   */
  public static void setOnFire(EntityRenderState state, ResourceLocation fireType) {
    ((FireTypeChanger) state).setFireType(FireManager.ensure(fireType));
  }
}
