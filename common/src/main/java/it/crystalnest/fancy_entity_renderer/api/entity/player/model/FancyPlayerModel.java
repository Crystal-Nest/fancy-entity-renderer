package it.crystalnest.fancy_entity_renderer.api.entity.player.model;

import it.crystalnest.fancy_entity_renderer.api.entity.player.mock.FancyPlayerMock;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.player.AbstractClientPlayer;
import org.jetbrains.annotations.NotNull;

/**
 * Custom player model.
 */
public class FancyPlayerModel extends PlayerModel<AbstractClientPlayer> {
  /**
   * @param modelSet entity model set.
   * @param isSlim whether the player is slim.
   */
  public FancyPlayerModel(EntityModelSet modelSet, boolean isSlim) {
    super(modelSet.roots.get(isSlim ? ModelLayers.PLAYER_SLIM : ModelLayers.PLAYER).bakeRoot(), isSlim);
  }

  /**
   * Sets up the model animation pose.
   *
   * @param entity entity to render.
   * @param limbSwing limb swing.
   * @param limbSwingAmount limb swing amount.
   * @param ageInTicks age in ticks.
   * @param netHeadYaw head yaw.
   * @param headPitch head pitch.
   */
  @Override
  public void setupAnim(@NotNull AbstractClientPlayer entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    head.zRot = 0;
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
    leftSleeve.offsetRotation(player.leftArmRot.getOffset());
    rightArm.offsetRotation(player.rightArmRot.getOffset());
    rightSleeve.offsetRotation(player.rightArmRot.getOffset());
    leftLeg.offsetRotation(player.leftLegRot.getOffset());
    leftPants.offsetRotation(player.leftLegRot.getOffset());
    rightLeg.offsetRotation(player.rightLegRot.getOffset());
    rightPants.offsetRotation(player.rightLegRot.getOffset());
    head.offsetRotation(player.headRot.getOffset());
    hat.offsetRotation(player.headRot.getOffset());
    if (player.isBaby) {
      // Don't know why, but it works.
      hat.xScale *= 1.5F;
      hat.yScale *= 1.5F;
      hat.zScale *= 1.5F;
    }
    leftArm.skipDraw = !player.showLeftArm;
    leftSleeve.skipDraw = !player.showLeftSleeve;
    rightArm.skipDraw = !player.showRightArm;
    rightSleeve.skipDraw = !player.showRightSleeve;
    leftLeg.skipDraw = !player.showLeftLeg;
    leftPants.skipDraw = !player.showLeftPants;
    rightLeg.skipDraw = !player.showRightLeg;
    rightPants.skipDraw = !player.showRightPants;
    head.skipDraw = !player.showHead;
    hat.skipDraw = !player.showHat;
    body.skipDraw = !player.showBody;
    jacket.skipDraw = !player.showJacket;
  }
}
