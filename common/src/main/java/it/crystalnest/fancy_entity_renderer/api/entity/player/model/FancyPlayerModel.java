package it.crystalnest.fancy_entity_renderer.api.entity.player.model;

import it.crystalnest.fancy_entity_renderer.api.entity.player.state.FancyPlayerRenderState;
import net.minecraft.client.model.HumanoidArmorModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.LayerDefinitions;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import org.jetbrains.annotations.NotNull;

public class FancyPlayerModel extends PlayerModel {
  private static final LayerDefinition FANCY_PLAYER = LayerDefinition.create(createMesh(CubeDeformation.NONE, false), 64, 64);

  private static final LayerDefinition FANCY_PLAYER_SLIM = LayerDefinition.create(createMesh(CubeDeformation.NONE, true), 64, 64);

  public FancyPlayerModel(boolean isSlim, boolean isBaby) {
    super(getModelPart(isSlim, isBaby), isSlim);
  }

  private static ModelPart getModelPart(boolean isSlim, boolean isBaby) {
    LayerDefinition layerDefinition = isSlim ? FANCY_PLAYER_SLIM : FANCY_PLAYER;
    if (isBaby) {
      layerDefinition = layerDefinition.apply(BABY_TRANSFORMER);
    }
    return layerDefinition.bakeRoot();
  }

  public static ModelPart getBabyArmorModel(boolean isInner) {
    return LayerDefinition.create(HumanoidArmorModel.createBodyLayer(isInner ? LayerDefinitions.INNER_ARMOR_DEFORMATION : LayerDefinitions.OUTER_ARMOR_DEFORMATION), 64, 32).apply(HumanoidModel.BABY_TRANSFORMER).bakeRoot();
  }

  @Override
  public void setupAnim(@NotNull PlayerRenderState state) {
    super.setupAnim(state);
    update((FancyPlayerRenderState) state);
  }

  private void update(@NotNull FancyPlayerRenderState state) {
    leftArm.xRot += state.leftArmRot.getX();
    leftArm.yRot += state.leftArmRot.getY();
    leftArm.zRot += state.leftArmRot.getZ();

    rightArm.xRot += state.rightArmRot.getX();
    rightArm.yRot += state.rightArmRot.getY();
    rightArm.zRot += state.rightArmRot.getZ();

    leftLeg.xRot += state.leftLegRot.getX();
    leftLeg.yRot += state.leftLegRot.getY();
    leftLeg.zRot += state.leftLegRot.getZ();

    rightLeg.xRot += state.rightLegRot.getX();
    rightLeg.yRot += state.rightLegRot.getY();
    rightLeg.zRot += state.rightLegRot.getZ();

    root().xRot += state.bodyRot.getX();
    root().yRot += state.bodyRot.getY();
    root().zRot += state.bodyRot.getZ();

    head.xRot += state.headRot.getX();
    head.yRot += state.headRot.getY();
    head.zRot += state.headRot.getZ();
  }
}
