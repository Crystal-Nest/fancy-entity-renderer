package it.crystalnest.fancy_entity_renderer.api.entity.player.model;

import it.crystalnest.fancy_entity_renderer.api.entity.player.state.FancyPlayerRenderState;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.LayerDefinitions;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.entity.ArmorModelSet;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import org.jetbrains.annotations.NotNull;

/**
 * Custom player model.
 */
public class FancyPlayerModel extends PlayerModel {
  /**
   * @param modelSet entity model set.
   * @param isSlim whether the player is slim.
   * @param isBaby whether the player is baby.
   */
  public FancyPlayerModel(EntityModelSet modelSet, boolean isSlim, boolean isBaby) {
    super(getModelPart(modelSet, isSlim, isBaby), isSlim);
  }

  /**
   * @param part model part.
   * @param isSlim whether the player is slim.
   */
  public FancyPlayerModel(ModelPart part, boolean isSlim) {
    super(part, isSlim);
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
   * @param isSlim whether the armor layer is slim.
   * @return correct armor model part.
   */
  public static ArmorModelSet<PlayerModel> getBabyArmorModel(boolean isSlim) {
    return PlayerModel
      .createArmorMeshSet(LayerDefinitions.INNER_ARMOR_DEFORMATION, LayerDefinitions.OUTER_ARMOR_DEFORMATION)
      .map(mesh -> new FancyPlayerModel(LayerDefinition.create(mesh, 64, 32).apply(BABY_TRANSFORMER).bakeRoot(), isSlim));
  }

  /**
   * Sets up the model animation pose.
   *
   * @param state render state.
   */
  @Override
  public void setupAnim(@NotNull AvatarRenderState state) {
    super.setupAnim(state);
    update((FancyPlayerRenderState) state);
  }

  /**
   * Updates the animation pose with the render state rotations for single body parts.
   *
   * @param state render state.
   */
  private void update(@NotNull FancyPlayerRenderState state) {
    leftArm.offsetRotation(state.leftArmRot.getOffset());
    rightArm.offsetRotation(state.rightArmRot.getOffset());
    leftLeg.offsetRotation(state.leftLegRot.getOffset());
    rightLeg.offsetRotation(state.rightLegRot.getOffset());
    head.offsetRotation(state.headRot.getOffset());
    leftArm.skipDraw = !state.showLeftArm;
    rightArm.skipDraw = !state.showRightArm;
    leftLeg.skipDraw = !state.showLeftLeg;
    rightLeg.skipDraw = !state.showRightLeg;
    head.skipDraw = !state.showHead;
    body.skipDraw = !state.showBody;
  }
}
