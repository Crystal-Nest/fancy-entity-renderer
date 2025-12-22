package it.crystalnest.fancy_entity_renderer.api.entity.player.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import it.crystalnest.fancy_entity_renderer.api.entity.player.model.FancyPlayerCapeModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.EquipmentAssetManager;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.core.ClientAsset;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.Equippable;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Custom cape layer.
 */
public class FancyCapeLayer extends RenderLayer<@NotNull AvatarRenderState, @NotNull PlayerModel> {
  /**
   * Model for an adult player.
   */
  private final HumanoidModel<@NotNull AvatarRenderState> adultModel;

  /**
   * Model for a baby player.
   */
  private final HumanoidModel<@NotNull AvatarRenderState> babyModel;

  /**
   * Manager to retrieve infos about the equipment assets.
   */
  private final EquipmentAssetManager equipmentAssets;

  /**
   * @param renderer parent renderer.
   * @param modelSet entity model set.
   * @param equipmentAssets {@link #equipmentAssets}.
   */
  public FancyCapeLayer(RenderLayerParent<@NotNull AvatarRenderState, @NotNull PlayerModel> renderer, EntityModelSet modelSet, EquipmentAssetManager equipmentAssets) {
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
    if (equippable != null) {
      Optional<ResourceKey<EquipmentAsset>> assetId = equippable.assetId();
      return assetId.isPresent() && !equipmentAssets.get(assetId.get()).getLayers(layer).isEmpty();
    }
    return false;
  }

  @Override
  public void submit(@NotNull PoseStack poseStack, @NotNull SubmitNodeCollector submitNodeCollector, int i, AvatarRenderState renderState, float v, float v1) {
    if (!renderState.isInvisible && renderState.showCape) {
      ClientAsset.Texture capeTexture = renderState.skin.cape();
      if (capeTexture != null && !hasLayer(renderState.chestEquipment, EquipmentClientInfo.LayerType.WINGS)) {
        poseStack.pushPose();
        if (hasLayer(renderState.chestEquipment, EquipmentClientInfo.LayerType.HUMANOID)) {
          poseStack.translate(0, -0.053125F, 0.06875F);
        }
        HumanoidModel<@NotNull AvatarRenderState> model = renderState.isBaby ? babyModel : adultModel;
        model.setupAnim(renderState);
        submitNodeCollector.submitModel(model, renderState, poseStack, RenderTypes.entitySolid(capeTexture.texturePath()), i, OverlayTexture.NO_OVERLAY, renderState.outlineColor, null);
        poseStack.popPose();
      }
    }
  }
}
