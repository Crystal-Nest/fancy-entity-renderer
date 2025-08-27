package it.crystalnest.fancy_entity_renderer.api.entity.player.mock;

import com.mojang.authlib.GameProfile;
import it.crystalnest.fancy_entity_renderer.api.Rotation;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FancyPlayerMock extends AbstractClientPlayer {
  public FancyPlayerMock(GameProfile gameProfile) {
    super(new FancyLevelMock(), gameProfile);
  }

  public boolean isBaby;

  @Override
  public boolean isBaby() {
    return isBaby;
  }

  /**
   * Left arm rotation.
   */
  public final Rotation leftArmRot = new Rotation();

  /**
   * Right arm rotation.
   */
  public final Rotation rightArmRot = new Rotation();

  /**
   * Left leg rotation.
   */
  public final Rotation leftLegRot = new Rotation();

  /**
   * Right leg rotation.
   */
  public final Rotation rightLegRot = new Rotation();

  /**
   * Head rotation.
   */
  public final Rotation headRot = new Rotation();

  /**
   * Whole model rotation.
   */
  public final Rotation bodyRot = new Rotation();

  /**
   * Whether the whole model should rotate to follow the mouse.
   */
  public boolean bodyFollowsMouse;

  /**
   * Whether the head should rotate to follow the mouse.
   */
  public boolean headFollowsMouse;

  /**
   * Whether to mimic a player.
   */
  public boolean copyingPlayer;

  /**
   * Whether the model is slim or wide.
   */
  public boolean isSlim = true;

  /**
   * Whether the player should move.
   */
  public boolean isMoving;

  /**
   * Whether to show the player's name.
   */
  public boolean showPlayerName;

  /**
   * Whether to pin the player's name at the top of the bounding box.
   */
  public boolean pinName;

  /**
   * Item held in the right hand.<br>
   * {@code null} if none.
   */
  @Nullable
  public ItemStack rightHandHeldItem;

  /**
   * Item held in the left hand.<br>
   * {@code null} if none.
   */
  @Nullable
  public ItemStack leftHandHeldItem;

  @NotNull
  public Pose pose = Pose.STANDING;

  public float scale;

  public float boundingBoxHeight;

  public float boundingBoxWidth;

  @NotNull
  public Vec3 nameTagAttachment = Vec3.ZERO;

  public boolean isUpsideDown;

  public boolean isDiscrete;

  public String name;

  public boolean isSpectator;
  public boolean isInvisible;
  public boolean isInvisibleToPlayer;
  public boolean appearsGlowing;

  @Nullable
  public Parrot.Variant parrotOnLeftShoulder;
  @Nullable
  public Parrot.Variant parrotOnRightShoulder;

  public boolean displayFireAnimation;

  public boolean isAutoSpinAttack;
  public boolean isCrouching;
  public boolean isVisuallySwimming;
  public float swimAmount;
  public boolean hasRedOverlay;

  public HumanoidModel.ArmPose rightArmPose;
  public HumanoidModel.ArmPose leftArmPose;
  public float speedValue;

  @NotNull
  public ItemStack headEquipment = ItemStack.EMPTY;
  @NotNull
  public ItemStack chestEquipment = ItemStack.EMPTY;
  @NotNull
  public ItemStack legsEquipment = ItemStack.EMPTY;
  @NotNull
  public ItemStack feetEquipment = ItemStack.EMPTY;

  public PlayerSkin skin;

  public boolean showCape;
}
