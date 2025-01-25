package it.crystalnest.fancy_entity_renderer.api.entity.player;

import com.mojang.blaze3d.vertex.PoseStack;
import it.crystalnest.fancy_entity_renderer.api.entity.player.model.FancyPlayerModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.client.resources.model.EquipmentAssetManager;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

public class FancyPlayerRenderer extends PlayerRenderer {
  private static final EntityRendererProvider.Context RENDER_CONTEXT = new EntityRendererProvider.Context(
    Minecraft.getInstance().getEntityRenderDispatcher(),
    Minecraft.getInstance().getItemModelResolver(),
    Minecraft.getInstance().getMapRenderer(),
    Minecraft.getInstance().getBlockRenderer(),
    Minecraft.getInstance().getResourceManager(),
    Minecraft.getInstance().getEntityModels(),
    new EquipmentAssetManager(),
    Minecraft.getInstance().font
  );

  private final FancyPlayerModel adultModel;

  private final FancyPlayerModel babyModel;

  public FancyPlayerRenderer(boolean slim) {
    super(RENDER_CONTEXT, slim);
    entityRenderDispatcher.overrideCameraOrientation(new Quaternionf());
    adultModel = new FancyPlayerModel(slim, false);
    babyModel = new FancyPlayerModel(slim, true);
    model = adultModel;
  }

  @Override
  @ApiStatus.Internal
  public void render(@NotNull PlayerRenderState state, @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight) {
    model = state.isBaby ? babyModel : adultModel;
    super.render(state, poseStack, bufferSource, packedLight);
  }
}
