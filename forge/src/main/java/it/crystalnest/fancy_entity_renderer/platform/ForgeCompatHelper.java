package it.crystalnest.fancy_entity_renderer.platform;

import it.crystalnest.fancy_entity_renderer.platform.services.CompatHelper;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.resources.ResourceLocation;

/**
 * Forge compatibility helper.
 */
public final class ForgeCompatHelper implements CompatHelper {
  @Override
  public void setOnFire(EntityRenderState state, ResourceLocation fireType) {
    // Soul Fire'd is not available for Forge after 1.20.4.
  }
}
