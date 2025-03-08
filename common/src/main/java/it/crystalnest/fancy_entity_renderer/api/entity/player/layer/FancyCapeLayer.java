package it.crystalnest.fancy_entity_renderer.api.entity.player.layer;

import com.mojang.blaze3d.vertex.PoseStack;
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
import net.minecraft.client.resources.model.EquipmentAssetManager;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.Equippable;
import org.jetbrains.annotations.NotNull;

/**
 * Custom cape layer.
 */
public class FancyCapeLayer extends RenderLayer<PlayerRenderState, PlayerModel> {
  /**
   * Model for an adult player.
   */
  private final HumanoidModel<PlayerRenderState> adultModel;

  /**
   * Model for a baby player.
   */
  private final HumanoidModel<PlayerRenderState> babyModel;

  /**
   * Manager to retrieve infos about the equipment assets.
   */
  private final EquipmentAssetManager equipmentAssets;

  /**
   * @param renderer parent renderer.
   * @param modelSet entity model set.
   * @param equipmentAssets {@link #equipmentAssets}.
   */
  public FancyCapeLayer(RenderLayerParent<PlayerRenderState, PlayerModel> renderer, EntityModelSet modelSet, EquipmentAssetManager equipmentAssets) {
    super(renderer);
    this.adultModel = new FancyPlayerCapeModel(modelSet, false);
    this.babyModel = new FancyPlayerCapeModel(modelSet, true);
    this.equipmentAssets = equipmentAssets;
  }

  /**
   * Whether the given {@link ItemStack} allows for the specified {@link EquipmentClientInfo.LayerType LayerType}.
   *
   * @param stack item stack.
   * @param layer layer type.
   * @return whether the item stack allows for the layer type.
   */
  private boolean hasLayer(ItemStack stack, EquipmentClientInfo.LayerType layer) {
    Equippable equippable = stack.get(DataComponents.EQUIPPABLE);
    return equippable != null && equippable.assetId().isPresent() && !equipmentAssets.get(equippable.assetId().get()).getLayers(layer).isEmpty();
  }

  /**
   * Renders this layer.
   *
   * @param poseStack pose stack.
   * @param multiBufferSource buffer source.
   * @param packedLight packed light.
   * @param renderState render state.
   * @param yRot rotation around the Y axis.
   * @param xRot rotation around the X axis.
   */
  @Override
  public void render(@NotNull PoseStack poseStack, @NotNull MultiBufferSource multiBufferSource, int packedLight, PlayerRenderState renderState, float yRot, float xRot) {
    if (!renderState.isInvisible && renderState.showCape) {
      ResourceLocation capeTexture = renderState.skin.capeTexture();
      if (capeTexture != null && !hasLayer(renderState.chestEquipment, EquipmentClientInfo.LayerType.WINGS)) {
        poseStack.pushPose();
        if (hasLayer(renderState.chestEquipment, EquipmentClientInfo.LayerType.HUMANOID)) {
          poseStack.translate(0, -0.053125F, 0.06875F);
        }
        HumanoidModel<PlayerRenderState> model = renderState.isBaby ? babyModel : adultModel;
        getParentModel().copyPropertiesTo(model);
        model.setupAnim(renderState);
        model.renderToBuffer(poseStack, multiBufferSource.getBuffer(RenderType.entitySolid(capeTexture)), packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
      }
    }
  }
}
