package it.crystalnest.fancy_entity_renderer.api.entity.player.mock;

import com.mojang.authlib.GameProfile;
import it.crystalnest.fancy_entity_renderer.api.Rotation;
import it.crystalnest.fancy_entity_renderer.api.entity.player.FancyPlayerWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelPart;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Mock for client player.
 */
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
  public final Rotation modelRot = new Rotation();

  /**
   * Whether the player is baby.
   */
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
   * Whether to copy the appearance a player.
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
   * Model scale.
   */
  public float scale;

  /**
   * Model bounding box height.
   */
  public float boundingBoxHeight;

  /**
   * Model bounding box width.
   */
  public float boundingBoxWidth;

  /**
   * Whether the model is upside down.
   */
  public boolean isUpsideDown;

  /**
   * Player name.
   */
  public String name = getGameProfile().getName();

  /**
   * Whether the player is in spectator mode.
   */
  public boolean isSpectator;

  /**
   * Whether the player is invisible.
   */
  public boolean isInvisible;

  /**
   * Whether the player is invisible to other players.
   */
  public boolean isInvisibleToPlayer;

  /**
   * Whether the player is glowing.
   */
  public boolean appearsGlowing;

  /**
   * Parrot variant for the left shoulder.
   */
  @Nullable
  public Parrot.Variant parrotOnLeftShoulder;

  /**
   * Parrot variant for the right shoulder.
   */
  @Nullable
  public Parrot.Variant parrotOnRightShoulder;

  /**
   * Whether to display the fire animation.
   */
  public boolean displayFireAnimation;

  /**
   * Whether the player is making a spin attack.
   */
  public boolean isAutoSpinAttack;

  /**
   * Whether the player is swimming.
   */
  public boolean isVisuallySwimming;

  /**
   * Movement speed.
   */
  public float speedValue = 1;

  /**
   * Walk speed.
   */
  public float walkSpeed;

  /**
   * Cumulative partial tick counter.
   */
  public float partialTick;

  /**
   * Player skin.
   */
  public PlayerSkin skin = DefaultPlayerSkin.get(getUUID());

  /**
   * Whether to show the player's cape.
   */
  public boolean showCape = true;

  /**
   * Whether to show the player's left arm.
   */
  public boolean showLeftArm = true;

  /**
   * Whether to show the player's left sleeve.
   */
  public boolean showLeftSleeve = true;

  /**
   * Whether to show the player's right arm.
   */
  public boolean showRightArm = true;

  /**
   * Whether to show the player's right sleeve.
   */
  public boolean showRightSleeve = true;

  /**
   * Whether to show the player's left leg.
   */
  public boolean showLeftLeg = true;

  /**
   * Whether to show the player's left pants.
   */
  public boolean showLeftPants = true;

  /**
   * Whether to show the player's right leg.
   */
  public boolean showRightLeg = true;

  /**
   * Whether to show the player's right pants.
   */
  public boolean showRightPants = true;

  /**
   * Whether to show the player's head.
   */
  public boolean showHead = true;

  /**
   * Whether to show the player's hat.
   */
  public boolean showHat = true;

  /**
   * Whether to show the player's torso.
   */
  public boolean showBody = true;

  /**
   * Whether to show the player's jacket.
   */
  public boolean showJacket = true;

  /**
   * @param gameProfile mock game profile.
   */
  public FancyPlayerMock(GameProfile gameProfile) {
    super(level(gameProfile), gameProfile);
    xo = 0;
    yo = 0;
    zo = 0;
    elytraRotX = (float) (Math.PI / 16);
    elytraRotY = 0;
    elytraRotZ = (float) (Math.PI / 10);
    yHeadRot = 0;
  }

  /**
   * @param gameProfile mock game profile.
   * @return current client level, or a mock level when no world is loaded.
   */
  private static ClientLevel level(GameProfile gameProfile) {
    ClientLevel level = Minecraft.getInstance().level;
    return level == null ? new FancyLevelMock(gameProfile) : level;
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
    return Component.literal(name == null ? "FancyMock" : name);
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

  /**
   * Returns the max health for this entity.<p>
   * Overridden for compatibility with <b>Pehkui</b>:<br>
   *  Pehkui mixins into {@link LivingEntity#getMaxHealth()} at {@code RETURN} and tries to read a value from its config;
   *  however, this method is called inside {@link LivingEntity#LivingEntity LivingEntity constructor} and, when a {@link FancyPlayerMock} instance is loaded in the main menu, it's too early, making the game crash because the config is not ready.
   *
   * @return max health.
   */
  @Override
  public float getMaxHealth() {
    return 1;
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
