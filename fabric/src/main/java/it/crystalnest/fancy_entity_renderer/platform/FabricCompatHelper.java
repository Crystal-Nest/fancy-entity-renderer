package it.crystalnest.fancy_entity_renderer.platform;

import it.crystalnest.fancy_entity_renderer.compat.SoulFired;
import it.crystalnest.fancy_entity_renderer.platform.services.CompatHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

/**
 * Fabric compatibility helper.
 */
public final class FabricCompatHelper implements CompatHelper {
  @Override
  public void setOnFire(Entity entity, ResourceLocation fireType) {
    SoulFired.setOnFire(entity, fireType);
  }
}
