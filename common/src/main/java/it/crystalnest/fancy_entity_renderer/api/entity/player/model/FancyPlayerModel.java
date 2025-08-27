package it.crystalnest.fancy_entity_renderer.api.entity.player.model;

import it.crystalnest.fancy_entity_renderer.api.entity.player.mock.FancyPlayerMock;
import net.minecraft.client.model.HumanoidArmorModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.LayerDefinitions;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.player.AbstractClientPlayer;
import org.jetbrains.annotations.NotNull;

/**
 * Custom player model.
 */
public class FancyPlayerModel extends PlayerModel<AbstractClientPlayer> {
  /**
   * @param modelSet entity model set.
   * @param isSlim whether the player is slim.
   * @param isBaby whether the player is baby.
   */
  public FancyPlayerModel(EntityModelSet modelSet, boolean isSlim, boolean isBaby) {
    super(getModelPart(modelSet, isSlim, isBaby), isSlim);
  }

  /**
   * Returns the correct {@link ModelPart} depending on whether the player is slim and/or baby.
   *
   * @param modelSet entity model set.
   * @param isSlim whether the player is slim.
   * @param isBaby whether the player is baby.
   * @return correct model part.
   */
  private static ModelPart getModelPart(EntityModelSet modelSet, boolean isSlim, boolean isBaby) {
    LayerDefinition layerDefinition = modelSet.roots.get(isSlim ? ModelLayers.PLAYER_SLIM : ModelLayers.PLAYER);
    if (isBaby) {
      layerDefinition = layerDefinition.apply(BABY_TRANSFORMER);
    }
    return layerDefinition.bakeRoot();
  }

  /**
   * Returns the {@link ModelPart} for a baby player armor model.
   *
   * @param isInner whether the armor layer is inner.
   * @return correct armor model part.
   */
  public static ModelPart getBabyArmorModel(boolean isInner) {
    return LayerDefinition.create(HumanoidArmorModel.createBodyLayer(isInner ? LayerDefinitions.INNER_ARMOR_DEFORMATION : LayerDefinitions.OUTER_ARMOR_DEFORMATION), 64, 32).apply(HumanoidModel.BABY_TRANSFORMER).bakeRoot();
  }

  /**
   * Sets up the model animation pose.
   *
   * @param entity entity to render.
   * @param limbSwing
   * @param limbSwingAmount
   * @param ageInTicks
   * @param netHeadYaw
   * @param headPitch
   */
  @Override
  public void setupAnim(@NotNull AbstractClientPlayer entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
    update((FancyPlayerMock) entity);
  }

  /**
   * Updates the animation pose with the render state rotations for single body parts.
   *
   * @param player player to render.
   */
  private void update(@NotNull FancyPlayerMock player) {
    leftArm.offsetRotation(player.leftArmRot.getOffset());
    rightArm.offsetRotation(player.rightArmRot.getOffset());
    leftLeg.offsetRotation(player.leftLegRot.getOffset());
    rightLeg.offsetRotation(player.rightLegRot.getOffset());
    head.offsetRotation(player.headRot.getOffset());
  }
}
