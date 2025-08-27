package it.crystalnest.fancy_entity_renderer.platform;

import it.crystalnest.fancy_entity_renderer.platform.services.CompatHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

/**
 * Forge compatibility helper.
 */
public final class ForgeCompatHelper implements CompatHelper {
  @Override
  public void setOnFire(Entity entity, ResourceLocation fireType) {
    // Soul Fire'd is not available for Forge after 1.20.4.
  }
}
