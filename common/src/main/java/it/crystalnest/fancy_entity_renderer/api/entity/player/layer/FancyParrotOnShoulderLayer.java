package it.crystalnest.fancy_entity_renderer.api.entity.player.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.crystalnest.fancy_entity_renderer.api.entity.player.mock.FancyPlayerMock;
import net.minecraft.client.model.ParrotModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ParrotRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.animal.Parrot;
import org.jetbrains.annotations.NotNull;

/**
 * Custom parrot on shoulder layer.
 */
public class FancyParrotOnShoulderLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
  /**
   * Parrot model.
   */
  protected final ParrotModel model;

  /**
   * @param renderer player renderer.
   * @param modelSet entity model set.
   */
  public FancyParrotOnShoulderLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> renderer, EntityModelSet modelSet) {
    super(renderer);
    model = new ParrotModel(modelSet.bakeLayer(ModelLayers.PARROT));
  }

  @Override
  public void render(@NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight, @NotNull AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
    renderParrot(poseStack, buffer, packedLight, player, limbSwing, limbSwingAmount, netHeadYaw, headPitch, true);
    renderParrot(poseStack, buffer, packedLight, player, limbSwing, limbSwingAmount, netHeadYaw, headPitch, false);
  }

  /**
   * Renders a parrot.
   *
   * @param poseStack pose stack.
   * @param buffer buffer source.
   * @param packedLight packed light.
   * @param player player.
   * @param limbSwing limb swing.
   * @param limbSwingAmount limb swing amount.
   * @param netHeadYaw head yaw.
   * @param headPitch head pitch.
   * @param leftShoulder whether it's the left shoulder.
   */
  protected void renderParrot(PoseStack poseStack, MultiBufferSource buffer, int packedLight, AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float netHeadYaw, float headPitch, boolean leftShoulder) {
    if (player instanceof FancyPlayerMock playerMock) {
      Parrot.Variant variant = leftShoulder ? playerMock.parrotOnLeftShoulder : playerMock.parrotOnRightShoulder;
      if (variant != null) {
        poseStack.pushPose();
        poseStack.translate(leftShoulder ? 0.4F : -0.4F, playerMock.isCrouching() ? -1.3F : -1.5F, 0);
        VertexConsumer vertexconsumer = buffer.getBuffer(model.renderType(ParrotRenderer.getVariantTexture(variant)));
        model.renderOnShoulder(poseStack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY, limbSwing, limbSwingAmount, netHeadYaw, headPitch, playerMock.tickCount);
        poseStack.popPose();
      }
    }
  }
}
