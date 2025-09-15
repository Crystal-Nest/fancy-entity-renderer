//package it.crystalnest.fancy_entity_renderer.api.entity.player.layer;
//
//import com.mojang.blaze3d.vertex.PoseStack;
//import it.crystalnest.fancy_entity_renderer.api.entity.player.mock.FancyPlayerMock;
//import it.crystalnest.fancy_entity_renderer.api.entity.player.model.FancyPlayerCapeModel;
//import net.minecraft.client.model.HumanoidModel;
//import net.minecraft.client.model.PlayerModel;
//import net.minecraft.client.model.geom.EntityModelSet;
//import net.minecraft.client.player.AbstractClientPlayer;
//import net.minecraft.client.renderer.MultiBufferSource;
//import net.minecraft.client.renderer.RenderType;
//import net.minecraft.client.renderer.entity.RenderLayerParent;
//import net.minecraft.client.renderer.entity.layers.RenderLayer;
//import net.minecraft.client.renderer.texture.OverlayTexture;
//import net.minecraft.client.resources.model.EquipmentClientInfo;
//import net.minecraft.core.component.DataComponents;
//import net.minecraft.resources.ResourceKey;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.item.equipment.EquipmentAsset;
//import net.minecraft.world.item.equipment.Equippable;
//import org.jetbrains.annotations.NotNull;
//
//import java.util.Optional;
//
///**
// * Custom cape layer.
// */
//public class FancyCapeLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
//  /**
//   * Model for an adult player.
//   */
//  private final HumanoidModel<AbstractClientPlayer> adultModel;
//
//  /**
//   * Model for a baby player.
//   */
//  private final HumanoidModel<AbstractClientPlayer> babyModel;
//
//  /**
//   * @param renderer parent renderer.
//   * @param modelSet entity model set.
//   */
//  public FancyCapeLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> renderer, EntityModelSet modelSet) {
//    super(renderer);
//    this.adultModel = new FancyPlayerCapeModel(modelSet, false);
//    this.babyModel = new FancyPlayerCapeModel(modelSet, true);
//  }
//
//  /**
//   * Whether the given {@link ItemStack} allows for the specified {@link EquipmentClientInfo.LayerType LayerType}.
//   *
//   * @param stack item stack.
//   * @param layer layer type.
//   * @return whether the item stack allows for the layer type.
//   */
//  private boolean hasLayer(ItemStack stack, EquipmentClientInfo.LayerType layer) {
//    Equippable equippable = stack.get(DataComponents.EQUIPPABLE);
//    if (equippable != null) {
//      Optional<ResourceKey<EquipmentAsset>> assetId = equippable.assetId();
//      return assetId.isPresent() && !equipmentAssets.get(assetId.get()).getLayers(layer).isEmpty();
//    }
//    return false;
//  }
//
//  /**
//   * Renders this layer.
//   *
//   * @param poseStack pose stack.
//   * @param multiBufferSource buffer source.
//   * @param packedLight packed light.
//   * @param entity entity to render.
//   * @param limbSwing limb swing.
//   * @param limbSwingAmount limb swing amount.
//   * @param partialTicks partial ticks.
//   * @param ageInTicks age in ticks.
//   * @param netHeadYaw net head yaw.
//   * @param headPitch head pitch.
//   */
//  @Override
//  public void render(@NotNull PoseStack poseStack, @NotNull MultiBufferSource multiBufferSource, int packedLight, @NotNull AbstractClientPlayer entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
//    FancyPlayerMock player = (FancyPlayerMock) entity;
//    if (!player.isInvisible && player.showCape) {
//      ResourceLocation capeTexture = player.skin.capeTexture();
//      if (capeTexture != null && !hasLayer(player.chestEquipment, EquipmentClientInfo.LayerType.WINGS)) {
//        poseStack.pushPose();
//        if (hasLayer(player.chestEquipment, EquipmentClientInfo.LayerType.HUMANOID)) {
//          poseStack.translate(0, -0.053125F, 0.06875F);
//        }
//        HumanoidModel<AbstractClientPlayer> model = player.isBaby ? babyModel : adultModel;
//        getParentModel().copyPropertiesTo(model);
//        model.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
//        model.renderToBuffer(poseStack, multiBufferSource.getBuffer(RenderType.entitySolid(capeTexture)), packedLight, OverlayTexture.NO_OVERLAY);
//        poseStack.popPose();
//      }
//    }
//  }
//}
