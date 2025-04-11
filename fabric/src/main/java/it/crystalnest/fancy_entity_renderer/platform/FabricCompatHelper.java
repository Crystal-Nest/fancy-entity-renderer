package it.crystalnest.fancy_entity_renderer.platform;

import it.crystalnest.fancy_entity_renderer.compat.SoulFired;
import it.crystalnest.fancy_entity_renderer.platform.services.CompatHelper;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.resources.ResourceLocation;

/**
 * Fabric compatibility helper.
 */
public final class FabricCompatHelper implements CompatHelper {
  @Override
  public void setOnFire(EntityRenderState state, ResourceLocation fireType) {
    SoulFired.setOnFire(state, fireType);
  }
}
