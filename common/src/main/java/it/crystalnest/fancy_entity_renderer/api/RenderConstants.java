package it.crystalnest.fancy_entity_renderer.api;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public final class RenderConstants {
  /**
   * Render context.
   */
  public static final EntityRendererProvider.Context RENDER_CONTEXT = new EntityRendererProvider.Context(
    Minecraft.getInstance().getEntityRenderDispatcher(),
    Minecraft.getInstance().getItemModelResolver(),
    Minecraft.getInstance().getMapRenderer(),
    Minecraft.getInstance().getBlockRenderer(),
    Minecraft.getInstance().getResourceManager(),
    Minecraft.getInstance().getEntityModels(),
    Minecraft.getInstance().getEntityRenderDispatcher().equipmentAssets,
    Minecraft.getInstance().font
  );
}
