package it.crystalnest.fancy_entity_renderer.api.entity.player.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.crystalnest.fancy_entity_renderer.api.entity.player.model.FancyPlayerCapeModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.client.resources.model.EquipmentAssetManager;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.Equippable;
import org.jetbrains.annotations.NotNull;

public class FancyCapeLayer extends RenderLayer<PlayerRenderState, PlayerModel> {
  private final HumanoidModel<PlayerRenderState> adultModel;

  private final HumanoidModel<PlayerRenderState> babyModel;

  private final EquipmentAssetManager equipmentAssets;

  public FancyCapeLayer(RenderLayerParent<PlayerRenderState, PlayerModel> renderer, EntityModelSet modelSet, EquipmentAssetManager equipmentAssets) {
    super(renderer);
    this.adultModel = new FancyPlayerCapeModel(modelSet, false);
    this.babyModel = new FancyPlayerCapeModel(modelSet, true);
    this.equipmentAssets = equipmentAssets;
  }

  private boolean hasLayer(ItemStack stack, EquipmentClientInfo.LayerType layer) {
    Equippable equippable = stack.get(DataComponents.EQUIPPABLE);
    return equippable != null && equippable.assetId().isPresent() && !equipmentAssets.get(equippable.assetId().get()).getLayers(layer).isEmpty();
  }

  @Override
  public void render(@NotNull PoseStack poseStack, @NotNull MultiBufferSource multiBufferSource, int packedLight, PlayerRenderState renderState, float f, float g) {
    if (!renderState.isInvisible && renderState.showCape) {
      PlayerSkin playerskin = renderState.skin;
      if (playerskin.capeTexture() != null) {
        if (!hasLayer(renderState.chestEquipment, EquipmentClientInfo.LayerType.WINGS)) {
          poseStack.pushPose();
          if (hasLayer(renderState.chestEquipment, EquipmentClientInfo.LayerType.HUMANOID)) {
            poseStack.translate(0, -0.053125F, 0.06875F);
          }
          VertexConsumer vertexconsumer = multiBufferSource.getBuffer(RenderType.entitySolid(playerskin.capeTexture()));
          HumanoidModel<PlayerRenderState> model = renderState.isBaby ? babyModel : adultModel;
          getParentModel().copyPropertiesTo(model);
          model.setupAnim(renderState);
          model.renderToBuffer(poseStack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY);
          poseStack.popPose();
        }
      }
    }
  }
}
