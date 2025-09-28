package it.crystalnest.fancy_entity_renderer.api.entity.player.mock;

import com.mojang.authlib.GameProfile;
import it.crystalnest.fancy_entity_renderer.api.Rotation;
import it.crystalnest.fancy_entity_renderer.api.entity.player.FancyPlayerWidget;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelPart;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FancyPlayerMock extends AbstractClientPlayer {
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

  public boolean isBaby;

  /**
   * Whether the whole model should rotate to follow the mouse.
   */
  public boolean bodyFollowsMouse;

  /**
   * Whether the head should rotate to follow the mouse.
   */
  public boolean headFollowsMouse;

  /**
   * Whether to copy a player.
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

  public float scale;

  public float boundingBoxHeight;

  public float boundingBoxWidth;

  public boolean isUpsideDown;

  public String name = getGameProfile().getName();

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

  public boolean isVisuallySwimming;

  public float speedValue = 1;

  public float walkSpeed;

  public float partialTick;

  public PlayerSkin skin = DefaultPlayerSkin.get(getUUID());

  public boolean showCape = true;

  public boolean showLeftArm = true;

  public boolean showLeftSleeve = true;

  public boolean showRightArm = true;

  public boolean showRightSleeve = true;

  public boolean showLeftLeg = true;

  public boolean showLeftPants = true;

  public boolean showRightLeg = true;

  public boolean showRightPants = true;

  public boolean showHead = true;

  public boolean showHat = true;

  public boolean showBody = true;

  public boolean showJacket = true;

  public FancyPlayerMock(GameProfile gameProfile) {
    super(new FancyLevelMock(gameProfile), gameProfile);
    xo = 0;
    yo = 0;
    zo = 0;
    elytraRotX = (float) (Math.PI / 16);
    elytraRotY = 0;
    elytraRotZ = (float) (Math.PI / 10);
    yHeadRot = 0;
  }

  @Override
  public boolean isSpectator() {
    return isSpectator;
  }

  @Nullable
  @Override
  protected PlayerInfo getPlayerInfo() {
    return null;
  }

  @NotNull
  @Override
  public PlayerSkin getSkin() {
    return skin;
  }

  @Override
  public boolean isInvisible() {
    return isInvisible;
  }

  @Override
  public boolean isInvisibleTo(@NotNull Player player) {
    return isInvisibleToPlayer;
  }

  @Override
  public boolean displayFireAnimation() {
    return displayFireAnimation;
  }

  @NotNull
  @Override
  public Component getName() {
    return Component.literal(name);
  }

  @Override
  public boolean isModelPartShown(@NotNull PlayerModelPart part) {
    return true;
  }

  @Override
  public float getSwimAmount(float partialTicks) {
    return isVisuallySwimming ? 1 : 0;
  }

  @Override
  public boolean isBaby() {
    return isBaby;
  }

  @Override
  public float getScale() {
    return scale;
  }

  @Override
  public boolean isAutoSpinAttack() {
    return isAutoSpinAttack;
  }

  @Override
  public boolean isUsingItem() {
    return true;
  }

  /**
   * Updates the scale and bounding box properties from the given height value.
   *
   * @param height height.
   */
  public void updateScale(float height) {
    boundingBoxHeight = height;
    boundingBoxWidth = height / FancyPlayerWidget.PLAYER_SIZE_RATIO;
    scale = height / FancyPlayerWidget.PLAYER_RENDER_HEIGHT;
  }
}
