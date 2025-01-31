package it.crystalnest.fancy_entity_renderer.api.entity.player.model;

import it.crystalnest.fancy_entity_renderer.api.entity.player.state.FancyPlayerRenderState;
import net.minecraft.client.model.HumanoidArmorModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import org.jetbrains.annotations.NotNull;

public class FancyPlayerModel extends PlayerModel {

  public static final class Definitions {

    private static final ModelPart FANCY_PLAYER;

    private static final ModelPart FANCY_PLAYER_SLIM;

    private static final ModelPart FANCY_PLAYER_BABY;

    private static final ModelPart FANCY_PLAYER_SLIM_BABY;

    public static final ModelPart FANCY_PLAYER_INNER_ARMOR;

    public static final ModelPart FANCY_PLAYER_OUTER_ARMOR;

    public static final ModelPart FANCY_PLAYER_BABY_INNER_ARMOR;

    public static final ModelPart FANCY_PLAYER_BABY_OUTER_ARMOR;

    static {
      LayerDefinition innerArmorModel = LayerDefinition.create(HumanoidArmorModel.createBodyLayer(new CubeDeformation(0.5F)), 64, 32);
      LayerDefinition outerArmorModel = LayerDefinition.create(HumanoidArmorModel.createBodyLayer(new CubeDeformation(1.0F)), 64, 32);

      LayerDefinition playerModel = LayerDefinition.create(createMesh(CubeDeformation.NONE, false), 64, 64);
      LayerDefinition playerSlimModel = LayerDefinition.create(createMesh(CubeDeformation.NONE, true), 64, 64);

      FANCY_PLAYER = playerModel.bakeRoot();
      FANCY_PLAYER_SLIM = playerSlimModel.bakeRoot();
      FANCY_PLAYER_BABY = playerModel.apply(BABY_TRANSFORMER).bakeRoot();
      FANCY_PLAYER_SLIM_BABY = playerSlimModel.apply(BABY_TRANSFORMER).bakeRoot();
      FANCY_PLAYER_INNER_ARMOR = innerArmorModel.bakeRoot();
      FANCY_PLAYER_OUTER_ARMOR = outerArmorModel.bakeRoot();
      FANCY_PLAYER_BABY_INNER_ARMOR = innerArmorModel.apply(BABY_TRANSFORMER).bakeRoot();
      FANCY_PLAYER_BABY_OUTER_ARMOR = outerArmorModel.apply(BABY_TRANSFORMER).bakeRoot();
    }
  }


  public FancyPlayerModel(boolean slim, boolean baby) {
    super(getModelPart(slim, baby), slim);
  }

  private static ModelPart getModelPart(boolean slim, boolean baby) {
    if (baby) {
      return slim ? Definitions.FANCY_PLAYER_SLIM_BABY : Definitions.FANCY_PLAYER_BABY;
    }
    return slim ? Definitions.FANCY_PLAYER_SLIM : Definitions.FANCY_PLAYER;
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

    // TODO: Use quaternions with body rot already in LivingEntityState bodyRot
    root().xRot += state.bodyRot.getX();
    root().yRot += state.bodyRot.getY();
    root().zRot += state.bodyRot.getZ();

    head.xRot += state.headRot.getX();
    head.yRot += state.headRot.getY();
    head.zRot += state.headRot.getZ();
  }
}
