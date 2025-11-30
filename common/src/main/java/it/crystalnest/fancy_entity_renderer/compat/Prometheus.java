package it.crystalnest.fancy_entity_renderer.compat;

import it.crystalnest.prometheus.api.FireManager;
import it.crystalnest.prometheus.api.type.FireTypeChanger;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.resources.ResourceLocation;

/**
 * Prometheus compatibility.
 */
public final class Prometheus {
  private Prometheus() {}

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
