package it.crystalnest.fancy_entity_renderer.api.entity.player;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.math.Axis;
import it.crystalnest.fancy_entity_renderer.Constants;
import it.crystalnest.fancy_entity_renderer.api.Rotation;
import it.crystalnest.fancy_entity_renderer.api.entity.RenderMode;
import it.crystalnest.fancy_entity_renderer.api.entity.player.mock.FancyPlayerMock;
import it.crystalnest.fancy_entity_renderer.imixin.SpecialDrawer;
import it.crystalnest.fancy_entity_renderer.platform.Services;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.commands.arguments.item.ItemParser;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * Custom player widget.
 */
public class FancyPlayerWidget extends AbstractWidget {
  /**
   * Player render height.
   */
  public static final float PLAYER_RENDER_HEIGHT = 1.875F;

  /**
   * Player eye height when crouching.
   */
  public static final float PLAYER_CROUCHING_EYE_HEIGHT = Player.POSES.get(Pose.CROUCHING).eyeHeight();

  /**
   * Ratio of a player's height to its width.
   */
  public static final float PLAYER_SIZE_RATIO = Player.DEFAULT_BB_HEIGHT / Player.DEFAULT_BB_WIDTH;

  /**
   * Global render state.
   */
  private final FancyPlayerMock renderState = new FancyPlayerMock(new GameProfile(Util.NIL_UUID, "FancyMock"));

  /**
   * Renderer for the wide player model.
   */
//  private final FancyPlayerRenderer wideRenderer = new FancyPlayerRenderer(false);
//
//  /**
//   * Renderer for the slim player model.
//   */
//  private final FancyPlayerRenderer slimRenderer = new FancyPlayerRenderer(true);

  /**
   * Current player renderer.
   */
  private FancyPlayerRenderer renderer = renderState.isSlim ? FancyPlayerRenderer.SLIM_RENDERER : FancyPlayerRenderer.WIDE_RENDERER;

  /**
   * Memory for overridable render state properties.
   */
  private final OverridableProperties properties = new OverridableProperties(renderState.name);

  /**
   * Random source.
   */
  private final Random random = new Random();

  /**
   * @param x x coordinate on the screen.
   * @param y y coordinate on the screen.
   * @param width widget width.
   * @param height widget height.
   */
  public FancyPlayerWidget(int x, int y, int width, int height) {
    super(x, y, width, height, CommonComponents.EMPTY);
  }

  /**
   * Handles errors happening when trying to copy user profiles.
   *
   * @param source user source (name or UUID).
   */
  private static void handlePlayerCopyError(String source) {
    Constants.LOGGER.error("Failed to copy player \"{}\"", source);
  }

  /**
   * Handles errors happening when trying to fetch user profiles.
   *
   * @param error error.
   * @param <T> expected return value type.
   * @return {@link Optional#empty()} to delegate value handling to the caller.
   */
  private static <T> Optional<T> handlePlayerCopyError(Throwable error) {
    Constants.LOGGER.error("Copy of player failed with error!", error);
    return Optional.empty();
  }

  /**
   * Renders the widget.
   *
   * @param gfx GUI graphics.
   * @param mouseX mouse x coordinate.
   * @param mouseY mouse y coordinate.
   * @param partialTick partial tick.
   */
  @Override
  protected void renderWidget(GuiGraphics gfx, int mouseX, int mouseY, float partialTick) {
    updateRenderState(getX(), getY(), getWidth(), getHeight(), mouseX, mouseY, partialTick);
    gfx.pose().pushPose();
    float offsetX = 0;
    float offsetY = (float) renderer.getRenderOffset(renderState, 0).y;
    if (renderState.pose == Pose.SLEEPING) {
      offsetX += PLAYER_RENDER_HEIGHT * renderState.scale / 2;
      offsetY -= 0.25F * renderState.scale;
      if (renderState.isBaby) {
        // TODO: Why these values?
        offsetX /= 1.75F;
        offsetY /= 1.5F;
      }
    }
    gfx.pose().translate(getX() + getWidth() / 2F + offsetX, getY() + getHeight() + offsetY, 100);
    gfx.flush();
    gfx.pose().scale(1, -1, 1);
    Lighting.setupForEntityInInventory(Axis.XP.rotationDegrees(renderState.bodyRot.getX()));
    gfx.pose().rotateAround(new Quaternionf().rotateXYZ(renderState.bodyRot.getX(), -renderState.bodyRot.getY(), renderState.bodyRot.getZ()), 0, 0, 0);
    ((SpecialDrawer) gfx).drawSpecial(bufferSource -> renderer.render(renderState, gfx.pose(), bufferSource, LightTexture.FULL_BRIGHT));
    gfx.flush();
    gfx.pose().popPose();
  }

  /**
   * Plays a sound when the widget is pressed.<br>
   * Here no sound is played.
   *
   * @param soundManager sound manager.
   */
  @Override
  public void playDownSound(@NotNull SoundManager soundManager) {
    // Disable playing any sound when clicked.
  }

  /**
   * Updates the narrator narration for this widget.
   *
   * @param output narration element output.
   */
  @Override
  protected void updateWidgetNarration(@NotNull NarrationElementOutput output) {
    // No narration.
  }

  /**
   * Sets whether the player's model should follow the mouse cursor.<br>
   * Overrides any manual body rotation set previously if {@code true}, otherwise restores the previous body rotation, if any.
   *
   * @param followsMouse whether to follow the mouse cursor.
   * @return {@code this}.
   */
  public FancyPlayerWidget setBodyFollowsMouse(boolean followsMouse) {
    if (renderState.pose == Pose.STANDING || renderState.pose == Pose.CROUCHING || renderState.pose == Pose.SPIN_ATTACK) {
      renderState.bodyFollowsMouse = followsMouse;
      if (followsMouse) {
        properties.bodyRot.copy(renderState.bodyRot);
      } else {
        renderState.bodyRot.copy(properties.bodyRot);
      }
    } else {
      properties.bodyFollowsMouse = followsMouse;
    }
    return this;
  }

  /**
   * Sets whether the player's head should follow the mouse cursor.<br>
   * Overrides any manual head rotation set previously if {@code true}, otherwise restores the previous head rotation, if any.
   *
   * @param followsMouse whether to follow the mouse cursor.
   * @return {@code this}.
   */
  public FancyPlayerWidget setHeadFollowsMouse(boolean followsMouse) {
    if (renderState.pose == Pose.STANDING || renderState.pose == Pose.CROUCHING || renderState.pose == Pose.SPIN_ATTACK) {
      renderState.headFollowsMouse = followsMouse;
      if (followsMouse) {
        properties.headRot.copy(renderState.headRot);
      } else {
        renderState.headRot.copy(properties.headRot);
      }
    } else {
      properties.headFollowsMouse = followsMouse;
    }
    return this;
  }

  /**
   * Sets a manual head rotation.<br>
   * If you want to set the rotation based on degree values, it's suggested to use {@link #setHeadRotation(float, float, float)} instead.
   *
   * @param rotation {@link Rotation} to set.
   * @return {@code this}.
   */
  public FancyPlayerWidget setHeadRotation(Rotation rotation) {
    properties.headRot.copy(rotation);
    renderState.headRot.copy(rotation);
    return this;
  }

  /**
   * Sets a manual head rotation.<br>
   * The values passed as parameters are assumed in degrees. If you want to use radians, use {@link #setHeadRotation(Rotation)} instead.
   *
   * @param x rotation around the X axis (horizontal).
   * @param y rotation around the Y axis (vertical).
   * @param z rotation around the Z axis (depth).
   * @return {@code this}.
   */
  public FancyPlayerWidget setHeadRotation(float x, float y, float z) {
    properties.headRot.setDeg(x, y, z);
    renderState.headRot.setDeg(x, y, z);
    return this;
  }

  /**
   * Sets a manual rotation of the whole model.<br>
   * If you want to set the rotation based on degree values, it's suggested to use {@link #setBodyRotation(float, float, float)} instead.
   *
   * @param rotation {@link Rotation} to set.
   * @return {@code this}.
   */
  public FancyPlayerWidget setBodyRotation(Rotation rotation) {
    properties.bodyRot.copy(rotation);
    renderState.bodyRot.copy(rotation);
    return this;
  }

  /**
   * Sets a manual rotation of the whole model.<br>
   * The values passed as parameters are assumed in degrees. If you want to use radians, use {@link #setBodyRotation(Rotation)} instead.
   *
   * @param x rotation around the X axis (horizontal).
   * @param y rotation around the Y axis (vertical).
   * @param z rotation around the Z axis (depth).
   * @return {@code this}.
   */
  public FancyPlayerWidget setBodyRotation(float x, float y, float z) {
    properties.bodyRot.setDeg(x, y, z);
    renderState.bodyRot.setDeg(x, y, z);
    return this;
  }

  /**
   * Sets a manual left arm rotation.<br>
   * If you want to set the rotation based on degree values, it's suggested to use {@link #setLeftArmRotation(float, float, float)} instead.
   *
   * @param rotation {@link Rotation} to set.
   * @return {@code this}.
   */
  public FancyPlayerWidget setLeftArmRotation(Rotation rotation) {
    renderState.leftArmRot.copy(rotation);
    return this;
  }

  /**
   * Sets a manual left arm rotation.<br>
   * The values passed as parameters are assumed in degrees. If you want to use radians, use {@link #setLeftArmRotation(Rotation)} instead.
   *
   * @param x rotation around the X axis (horizontal).
   * @param y rotation around the Y axis (vertical).
   * @param z rotation around the Z axis (depth).
   * @return {@code this}.
   */
  public FancyPlayerWidget setLeftArmRotation(float x, float y, float z) {
    renderState.leftArmRot.setDeg(x, y, z);
    return this;
  }

  /**
   * Sets a manual right arm rotation.<br>
   * If you want to set the rotation based on degree values, it's suggested to use {@link #setRightArmRotation(float, float, float)} instead.
   *
   * @param rotation {@link Rotation} to set.
   * @return {@code this}.
   */
  public FancyPlayerWidget setRightArmRotation(Rotation rotation) {
    renderState.rightArmRot.copy(rotation);
    return this;
  }

  /**
   * Sets a manual right arm rotation.<br>
   * The values passed as parameters are assumed in degrees. If you want to use radians, use {@link #setRightArmRotation(Rotation)} instead.
   *
   * @param x rotation around the X axis (horizontal).
   * @param y rotation around the Y axis (vertical).
   * @param z rotation around the Z axis (depth).
   * @return {@code this}.
   */
  public FancyPlayerWidget setRightArmRotation(float x, float y, float z) {
    renderState.rightArmRot.setDeg(x, y, z);
    return this;
  }

  /**
   * Sets a manual left leg rotation.<br>
   * If you want to set the rotation based on degree values, it's suggested to use {@link #setLeftLegRotation(float, float, float)} instead.
   *
   * @param rotation {@link Rotation} to set.
   * @return {@code this}.
   */
  public FancyPlayerWidget setLeftLegRotation(Rotation rotation) {
    renderState.leftLegRot.copy(rotation);
    return this;
  }

  /**
   * Sets a manual left leg rotation.<br>
   * The values passed as parameters are assumed in degrees. If you want to use radians, use {@link #setLeftLegRotation(Rotation)} instead.
   *
   * @param x rotation around the X axis (horizontal).
   * @param y rotation around the Y axis (vertical).
   * @param z rotation around the Z axis (depth).
   * @return {@code this}.
   */
  public FancyPlayerWidget setLeftLegRotation(float x, float y, float z) {
    renderState.leftLegRot.setDeg(x, y, z);
    return this;
  }

  /**
   * Sets a manual right leg rotation.<br>
   * If you want to set the rotation based on degree values, it's suggested to use {@link #setRightLegRotation(float, float, float)} instead.
   *
   * @param rotation {@link Rotation} to set.
   * @return {@code this}.
   */
  public FancyPlayerWidget setRightLegRotation(Rotation rotation) {
    renderState.rightLegRot.copy(rotation);
    return this;
  }

  /**
   * Sets a manual right leg rotation.<br>
   * The values passed as parameters are assumed in degrees. If you want to use radians, use {@link #setRightLegRotation(Rotation)} instead.
   *
   * @param x rotation around the X axis (horizontal).
   * @param y rotation around the Y axis (vertical).
   * @param z rotation around the Z axis (depth).
   * @return {@code this}.
   */
  public FancyPlayerWidget setRightLegRotation(float x, float y, float z) {
    renderState.rightLegRot.setDeg(x, y, z);
    return this;
  }

  /**
   * Makes the player slim or wide.<br>
   * If no skin is set (either manually or by copying a player), a random base skin is selected.
   *
   * @param isSlim whether the player should be slim.
   * @return {@code this}.
   */
  public FancyPlayerWidget setSlim(boolean isSlim) {
    properties.isSlim = isSlim;
    if (!renderState.copyingPlayer && properties.skin == null) {
      updateIsSlim(properties.isSlim);
      renderer = isSlim ? FancyPlayerRenderer.SLIM_RENDERER : FancyPlayerRenderer.WIDE_RENDERER;
    }
    return this;
  }

  /**
   * Sets a custom skin for the player.<br>
   * Overrides the slim property if a valid skin. If {@code null}, restores the previous value for the slim property.
   *
   * @param skin {@link PlayerSkin}.
   * @return {@code this}.
   */
  public FancyPlayerWidget setSkin(@Nullable PlayerSkin skin) {
    properties.skin = skin;
    if (!renderState.copyingPlayer) {
      updateSkin(skin);
    }
    return this;
  }

  /**
   * Makes the model copy the local player.<br>
   * If you want to undo the copy, use {@link #uncopyPlayer()}.<br>
   * Overrides the slim and the skin properties.
   *
   * @return {@code this}.
   */
  public FancyPlayerWidget copyLocalPlayer() {
    renderState.copyingPlayer = true;
    copyPlayer(Minecraft.getInstance().getGameProfile());
    return this;
  }

  /**
   * Sets the player's name.<br>
   * If you want to change the name's visibility, use {@link #setShowName(boolean)}.
   *
   * @param name player's name.
   * @return {@code this}.
   */
  public FancyPlayerWidget setName(String name) {
    properties.name = name;
    if (!renderState.copyingPlayer) {
      renderState.name = name;
    }
    return this;
  }

  /**
   * Sets whether to show the player's name.
   *
   * @param showName whether to show the player's name.
   * @return {@code this}.
   */
  public FancyPlayerWidget setShowName(boolean showName) {
    renderState.showPlayerName = showName;
    return this;
  }

  /**
   * Sets whether to pin the player's name at the top of the bounding box.<p>
   * <b>WARNING: Experimental!</b><br>
   * Currently, it has no effect.
   *
   * @param pinName whether to pin the player's name at the top of the bounding box.
   * @return {@code this}.
   */
  @ApiStatus.Experimental
  public FancyPlayerWidget setPinName(boolean pinName) {
    renderState.pinName = pinName;
    return this;
  }

  /**
   * Sets whether the player is rendered upside-down.
   *
   * @param isUpsideDown whether to render the player's upside-down.
   * @return {@code this}.
   */
  public FancyPlayerWidget setUpsideDown(boolean isUpsideDown) {
    renderState.isUpsideDown = isUpsideDown;
    return this;
  }

  /**
   * Sets the player render mode.
   *
   * @param mode {@link RenderMode}.
   * @return {@code this}.
   */
  public FancyPlayerWidget setRenderMode(RenderMode mode) {
    renderState.isSpectator = RenderMode.SPECTATOR == mode;
    renderState.isInvisible = RenderMode.NORMAL != mode;
    renderState.isInvisibleToPlayer = RenderMode.INVISIBLE == mode;
    return this;
  }

  /**
   * Sets whether the player should glow.<p>
   * <b>WARNING: Experimental!</b><br>
   * Currently, it has no effect.
   *
   * @param isGlowing whether the player should glow.
   * @return {@code this}.
   */
  @ApiStatus.Experimental
  public FancyPlayerWidget setGlowing(boolean isGlowing) {
    renderState.appearsGlowing = isGlowing;
    return this;
  }

  /**
   * Sets whether the player should be moving.
   *
   * @param isMoving whether the player should be moving.
   * @return {@code this}.
   */
  public FancyPlayerWidget setMoving(boolean isMoving) {
    renderState.isMoving = isMoving;
    return this;
  }

  /**
   * Sets whether the player is on fire.<br>
   * If Soul Fire'd is installed, you can use {@link #setOnFire(boolean, ResourceLocation)} to specify the kind of fire.
   *
   * @param onFire whether the player is on fire.
   * @return {@code this}.
   */
  public FancyPlayerWidget setOnFire(boolean onFire) {
    if (renderState.pose == Pose.STANDING || renderState.pose == Pose.CROUCHING) {
      renderState.displayFireAnimation = onFire;
    } else {
      properties.displayFireAnimation = onFire;
    }
    return this;
  }

  /**
   * Sets whether the player is on fire and what kind of fire it is.<br>
   * Effective only when Soul Fire'd is installed too.
   *
   * @param onFire whether the player is on fire.
   * @param fireType Soul Fire'd fire type.
   * @return {@code this}.
   */
  public FancyPlayerWidget setOnFire(boolean onFire, ResourceLocation fireType) {
    if (Services.PLATFORM.isModLoaded("soul_fire_d")) {
      Services.COMPAT.setOnFire(renderState, fireType);
    }
    return setOnFire(onFire);
  }

  /**
   * Sets the amount of arrows stuck into the player's body.<br>
   * Arrow positions are randomly generated.
   *
   * @param count amount of arrows.
   * @return {@code this}.
   */
  public FancyPlayerWidget setArrowCount(int count) {
    renderState.arrowCount = count;
    return this;
  }

  /**
   * Sets the amount of stingers stuck into the player's body.<br>
   * Arrow positions are randomly generated.
   *
   * @param count amount of stingers.
   * @return {@code this}.
   */
  public FancyPlayerWidget setStingerCount(int count) {
    renderState.stingerCount = count;
    return this;
  }

  /**
   * Sets the parrot on the left shoulder.<br>
   * Set to {@code null} to remove.
   *
   * @param parrot {@link Parrot.Variant parrot variant}.
   * @return {@code this}.
   */
  public FancyPlayerWidget setLeftParrot(@Nullable Parrot.Variant parrot) {
    if (renderState.isBaby || renderState.pose == Pose.SWIMMING) {
      properties.parrotOnLeftShoulder = parrot;
    } else {
      renderState.parrotOnLeftShoulder = parrot;
    }
    return this;
  }

  /**
   * Sets the parrot on the right shoulder.<br>
   * Set to {@code null} to remove.
   *
   * @param parrot {@link Parrot.Variant parrot variant}.
   * @return {@code this}.
   */
  public FancyPlayerWidget setRightParrot(@Nullable Parrot.Variant parrot) {
    if (renderState.isBaby || renderState.pose == Pose.SWIMMING) {
      properties.parrotOnRightShoulder = parrot;
    } else {
      renderState.parrotOnRightShoulder = parrot;
    }
    return this;
  }

  /**
   * Sets the parrots on each shoulder.<br>
   * Set to {@code null} to remove one.
   *
   * @param left left shoulder {@link Parrot.Variant parrot variant}.
   * @param right right shoulder {@link Parrot.Variant parrot variant}.
   * @return {@code this}.
   */
  public FancyPlayerWidget setParrots(@Nullable Parrot.Variant left, @Nullable Parrot.Variant right) {
    if (renderState.isBaby || renderState.pose == Pose.SWIMMING) {
      properties.parrotOnLeftShoulder = left;
      properties.parrotOnRightShoulder = right;
    } else {
      renderState.parrotOnLeftShoulder = left;
      renderState.parrotOnRightShoulder = right;
    }
    return this;
  }

  /**
   * Sets whether the player is a baby.<br>
   * Overrides the visibility of the left and right parrots. If {@code true}, the parrots will be hidden. If {@code false}, any previously hidden parrots will be restored.
   *
   * @param isBaby whether the player is a baby.
   * @return {@code this}.
   */
  public FancyPlayerWidget setBaby(boolean isBaby) {
    renderState.isBaby = isBaby;
    if (isBaby) {
      properties.parrotOnLeftShoulder = renderState.parrotOnLeftShoulder;
      properties.parrotOnRightShoulder = renderState.parrotOnRightShoulder;
    } else {
      renderState.parrotOnLeftShoulder = properties.parrotOnLeftShoulder;
      renderState.parrotOnRightShoulder = properties.parrotOnRightShoulder;
    }
    return this;
  }

  /**
   * Sets the player pose.
   *
   * @param pose {@link Pose}.
   * @return {@code this}.
   */
  public FancyPlayerWidget setPose(Pose pose) {
    if (Player.POSES.containsKey(pose) && pose != Pose.FALL_FLYING) {
      renderState.pose = pose;
      renderState.isAutoSpinAttack = pose == Pose.SPIN_ATTACK;
      renderState.isCrouching = pose == Pose.CROUCHING;
      renderState.isVisuallySwimming = pose == Pose.SWIMMING;
      renderState.swimAmount = renderState.isVisuallySwimming ? 1 : 0;
      renderState.hasRedOverlay = pose == Pose.DYING;
      renderState.deathTime = renderState.hasRedOverlay ? 5 : 0;
      if (pose == Pose.STANDING || pose == Pose.CROUCHING) {
        renderState.displayFireAnimation = properties.displayFireAnimation;
      } else {
        properties.displayFireAnimation = renderState.displayFireAnimation;
        renderState.displayFireAnimation = false;
      }
      if (pose == Pose.STANDING || pose == Pose.CROUCHING || pose == Pose.SPIN_ATTACK) {
        renderState.headFollowsMouse = properties.headFollowsMouse;
        renderState.bodyFollowsMouse = properties.bodyFollowsMouse;
      } else {
        properties.headFollowsMouse = renderState.headFollowsMouse;
        properties.bodyFollowsMouse = renderState.bodyFollowsMouse;
        renderState.headFollowsMouse = false;
        renderState.bodyFollowsMouse = false;
      }
      if (pose == Pose.SWIMMING) {
        properties.parrotOnLeftShoulder = renderState.parrotOnLeftShoulder;
        properties.parrotOnRightShoulder = renderState.parrotOnRightShoulder;
        renderState.parrotOnLeftShoulder = null;
        renderState.parrotOnRightShoulder = null;
      } else {
        renderState.parrotOnLeftShoulder = properties.parrotOnLeftShoulder;
        renderState.parrotOnRightShoulder = properties.parrotOnRightShoulder;
      }
    } else {
      Constants.LOGGER.warn("Pose {} is not supported for Player entity!", pose);
    }
    return this;
  }

  /**
   * Sets the right arm pose.<br>
   * {@link HumanoidModel.ArmPose#EMPTY} to remove.
   *
   * @param pose {@link HumanoidModel.ArmPose}.
   * @return {@code this}.
   */
  public FancyPlayerWidget setRightArmPose(HumanoidModel.ArmPose pose) {
    renderState.rightArmPose = pose;
    return this;
  }

  /**
   * Sets the left arm pose.<br>
   * {@link HumanoidModel.ArmPose#EMPTY} to remove.
   *
   * @param pose {@link HumanoidModel.ArmPose}.
   * @return {@code this}.
   */
  public FancyPlayerWidget setLeftArmPose(HumanoidModel.ArmPose pose) {
    renderState.leftArmPose = pose;
    return this;
  }

  /**
   * Sets the movement speed.<br>
   * Effective only when the player is moving (see {@link #setMoving(boolean)}.
   *
   * @param speed speed value.
   * @return {@code this}.
   */
  public FancyPlayerWidget setMovementSpeed(float speed) {
    renderState.speedValue = speed;
    return this;
  }

  /**
   * Sets the item the player is holding in its right hand.<br>
   * Pass a valid item to set it, pass {@code null} to empty the hand.
   *
   * @param item item string, in the same format as for the command {@code /give}.
   * @param provider {@link HolderLookup.Provider} for registry access, for example from {@link Level#registryAccess()}.
   * @return {@code this}.
   */
  public FancyPlayerWidget setRightHandItem(@Nullable String item, HolderLookup.Provider provider) {
    renderState.rightHandHeldItem = getNullableItem(item, i -> parseItem(i, provider));
    return this;
  }

  /**
   * Sets the item the player is holding in its left hand.<br>
   * Pass a valid item to set it, pass {@code null} to empty the hand.
   *
   * @param item item string, in the same format as for the command {@code /give}.
   * @param provider {@link HolderLookup.Provider} for registry access, for example from {@link Level#registryAccess()}.
   * @return {@code this}.
   */
  public FancyPlayerWidget setLeftHandItem(@Nullable String item, HolderLookup.Provider provider) {
    renderState.leftHandHeldItem = getNullableItem(item, i -> parseItem(i, provider));
    return this;
  }

  /**
   * Sets the item the player is holding in its right hand.<br>
   * Pass a valid item to set it, pass {@code null} to empty the hand.
   *
   * @param item item to set or {@code null}.
   * @return {@code this}.
   */
  public FancyPlayerWidget setRightHandItem(@Nullable Item item) {
    renderState.rightHandHeldItem = getNullableItem(item, Item::getDefaultInstance);
    return this;
  }

  /**
   * Sets the item the player is holding in its left hand.<br>
   * Pass a valid item to set it, pass {@code null} to empty the hand.
   *
   * @param item item to set or {@code null}.
   * @return {@code this}.
   */
  public FancyPlayerWidget setLeftHandItem(@Nullable Item item) {
    renderState.leftHandHeldItem = getNullableItem(item, Item::getDefaultInstance);
    return this;
  }

  /**
   * Sets the item the player is holding in its right hand.<br>
   * Pass a valid item to set it, pass {@code null} to empty the hand.
   *
   * @param item item to set or {@code null}.
   * @return {@code this}.
   */
  public FancyPlayerWidget setRightHandItem(@Nullable ItemStack item) {
    renderState.rightHandHeldItem = getNullableItem(item, i -> i);
    return this;
  }

  /**
   * Sets the item the player is holding in its left hand.<br>
   * Pass a valid item to set it, pass {@code null} to empty the hand.
   *
   * @param item item to set or {@code null}.
   * @return {@code this}.
   */
  public FancyPlayerWidget setLeftHandItem(@Nullable ItemStack item) {
    renderState.leftHandHeldItem = getNullableItem(item, i -> i);
    return this;
  }

  /**
   * Sets the item the player is wearing on its head.<br>
   * Pass a valid item string to set it, pass {@code null} to remove it.
   *
   * @param item item string, in the same format as for the command {@code /give}.
   * @param provider {@link HolderLookup.Provider} for registry access, for example from {@link Level#registryAccess()}.
   * @return {@code this}.
   */
  public FancyPlayerWidget setHeadWearable(@Nullable String item, HolderLookup.Provider provider) {
    renderState.headEquipment = getNullableItem(item, i -> parseItem(i, provider));
    return this;
  }

  /**
   * Sets the item the player is wearing on its chest.<br>
   * Pass a valid item to set it, pass {@code null} to remove it.
   *
   * @param item item string, in the same format as for the command {@code /give}.
   * @param provider {@link HolderLookup.Provider} for registry access, for example from {@link Level#registryAccess()}.
   * @return {@code this}.
   */
  public FancyPlayerWidget setChestWearable(@Nullable String item, HolderLookup.Provider provider) {
    renderState.chestEquipment = getNullableItem(item, i -> parseItem(i, provider));
    return this;
  }

  /**
   * Sets the item the player is wearing on its legs.<br>
   * Pass a valid item to set it, pass {@code null} to remove it.
   *
   * @param item item string, in the same format as for the command {@code /give}.
   * @param provider {@link HolderLookup.Provider} for registry access, for example from {@link Level#registryAccess()}.
   * @return {@code this}.
   */
  public FancyPlayerWidget setLegsWearable(@Nullable String item, HolderLookup.Provider provider) {
    renderState.legsEquipment = getNullableItem(item, i -> parseItem(i, provider));
    return this;
  }

  /**
   * Sets the item the player is wearing on its feet.<br>
   * Pass a valid item to set it, pass {@code null} to remove it.
   *
   * @param item item string, in the same format as for the command {@code /give}.
   * @param provider {@link HolderLookup.Provider} for registry access, for example from {@link Level#registryAccess()}.
   * @return {@code this}.
   */
  public FancyPlayerWidget setFeetWearable(@Nullable String item, HolderLookup.Provider provider) {
    renderState.feetEquipment = getNullableItem(item, i -> parseItem(i, provider));
    return this;
  }

  /**
   * Sets the item the player is wearing on its head.<br>
   * Pass a valid item to set it, pass {@code null} to remove it.
   *
   * @param item item to wear on the head.
   * @return {@code this}.
   */
  public FancyPlayerWidget setHeadWearable(@Nullable Item item) {
    renderState.headEquipment = getNullableItem(item, Item::getDefaultInstance);
    return this;
  }

  /**
   * Sets the item the player is wearing on its chest.<br>
   * Pass a valid item to set it, pass {@code null} to remove it.
   *
   * @param item item to wear on the chest.
   * @return {@code this}.
   */
  public FancyPlayerWidget setChestWearable(@Nullable Item item) {
    renderState.chestEquipment = getNullableItem(item, Item::getDefaultInstance);
    return this;
  }

  /**
   * Sets the item the player is wearing on its legs.<br>
   * Pass a valid item to set it, pass {@code null} to remove it.
   *
   * @param item item to wear on the legs.
   * @return {@code this}.
   */
  public FancyPlayerWidget setLegsWearable(@Nullable Item item) {
    renderState.legsEquipment = getNullableItem(item, Item::getDefaultInstance);
    return this;
  }

  /**
   * Sets the item the player is wearing on its feet.<br>
   * Pass a valid item to set it, pass {@code null} to remove it.
   *
   * @param item item to wear on the feet.
   * @return {@code this}.
   */
  public FancyPlayerWidget setFeetWearable(@Nullable Item item) {
    renderState.feetEquipment = getNullableItem(item, Item::getDefaultInstance);
    return this;
  }

  /**
   * Sets the item the player is wearing on its head.<br>
   * Pass a valid item to set it, pass {@code null} to remove it.
   *
   * @param item item to wear on the head.
   * @return {@code this}.
   */
  public FancyPlayerWidget setHeadWearable(@Nullable ItemStack item) {
    renderState.headEquipment = getNullableItem(item, i -> i);
    return this;
  }

  /**
   * Sets the item the player is wearing on its chest.<br>
   * Pass a valid item to set it, pass {@code null} to remove it.
   *
   * @param item item to wear on the chest.
   * @return {@code this}.
   */
  public FancyPlayerWidget setChestWearable(@Nullable ItemStack item) {
    renderState.chestEquipment = getNullableItem(item, i -> i);
    return this;
  }

  /**
   * Sets the item the player is wearing on its legs.<br>
   * Pass a valid item to set it, pass {@code null} to remove it.
   *
   * @param item item to wear on the legs.
   * @return {@code this}.
   */
  public FancyPlayerWidget setLegsWearable(@Nullable ItemStack item) {
    renderState.legsEquipment = getNullableItem(item, i -> i);
    return this;
  }

  /**
   * Sets the item the player is wearing on its feet.<br>
   * Pass a valid item to set it, pass {@code null} to remove it.
   *
   * @param item item to wear on the feet.
   * @return {@code this}.
   */
  public FancyPlayerWidget setFeetWearable(@Nullable ItemStack item) {
    renderState.feetEquipment = getNullableItem(item, i -> i);
    return this;
  }

  /**
   * Sets the attack time for the attack animation.<br>
   * Value must be {@code >= 0}.
   *
   * @param attackTime attack time animation.
   * @return {@code this}.
   */
  public FancyPlayerWidget setAttackTime(float attackTime) {
    if (attackTime >= 0) {
      renderState.attackTime = attackTime % 1;
    }
    return this;
  }

  /**
   * Sets the attack arm for the attack animation.
   *
   * @param attackArm attack arm.
   * @return {@code this}.
   */
  public FancyPlayerWidget setAttackArm(HumanoidArm attackArm) {
    renderState.attackArm = attackArm;
    return this;
  }

  /**
   * Fully mimics the local player.<br>
   * If you want to undo the mimicking, use {@link #unmimicPlayer()}.<br>
   * Overrides almost every other property.
   *
   * @return {@code this}.
   */
  public FancyPlayerWidget mimicLocalPlayer() {
    return mimicPlayer(Minecraft.getInstance().player);
  }

  /**
   * Fully mimics the given client player.<br>
   * If you want to undo the mimicking, use {@link #unmimicPlayer()}.<br>
   * Overrides almost every other property.
   *
   * @param player player to mimic.
   * @return {@code this}.
   */
  public FancyPlayerWidget mimicPlayer(@Nullable AbstractClientPlayer player) {
    renderState.mimickedPlayer = player;
    return this;
  }

  /**
   * Stops the widget from currently mimicking a player.
   *
   * @return {@code this}.
   */
  public FancyPlayerWidget unmimicPlayer() {
    renderState.mimickedPlayer = null;
    return this;
  }

  /**
   * Returns whether the widget is currently mimicking a player.
   *
   * @return whether the widget is mimicking a player
   */
  public boolean isMimickingPlayer() {
    return renderState.mimickedPlayer != null;
  }

  /**
   * Sets the allowed poses for when mimicking a player.<br>
   * Effective only when mimicking a player.
   *
   * @param poses list of allowed poses.
   * @return {@code this}.
   */
  public FancyPlayerWidget setAllowedPoses(List<Pose> poses) {
    renderState.allowedPoses = poses;
    return this;
  }

  /**
   * Safely checks and returns the {@link ItemStack} to use as wearable.
   *
   * @param item item data.
   * @param getter item data parser.
   * @param <T> type of the item data.
   * @return {@link ItemStack} to use as wearable.
   */
  private <T> ItemStack getNullableItem(T item, Function<T, ItemStack> getter) {
    return item == null ? ItemStack.EMPTY : getter.apply(item);
  }

  /**
   * Parses an item string into an {@link ItemStack} using the given provider.
   *
   * @param item item string, in the same format as for the command {@code /give}.
   * @param provider {@link HolderLookup.Provider} for registry access, for example from {@link Level#registryAccess()}.
   * @return {@link ItemStack} to use as wearable.
   */
  private ItemStack parseItem(String item, HolderLookup.Provider provider) {
    try {
      ItemParser.ItemResult result = new ItemParser(provider).parse(new StringReader(item));
      return new ItemInput(result.item(), result.components()).createItemStack(1, false);
    } catch (CommandSyntaxException e) {
      Constants.LOGGER.error("Error parsing {}", item, e);
      return ItemStack.EMPTY;
    }
  }

  /**
   * Copies a player from its profile name.<br>
   * Verify that the copy was successful by calling {@link #isCopyingPlayer()}.
   *
   * @param profileName profile name.
   * @return {@code this}.
   */
  public FancyPlayerWidget copyPlayer(String profileName) {
    return copyPlayer(FancyProfileFetcher.fetchProfile(profileName), profileName);
  }

  /**
   * Copies a player from its UUID.<br>
   * Verify that the copy was successful by calling {@link #isCopyingPlayer()}.
   *
   * @param profileId profile UUID.
   * @return {@code this}.
   */
  public FancyPlayerWidget copyPlayer(UUID profileId) {
    return copyPlayer(FancyProfileFetcher.fetchProfile(profileId), profileId.toString());
  }

  /**
   * Stops the widget from currently copying a player.
   *
   * @return {@code this}.
   */
  public FancyPlayerWidget uncopyPlayer() {
    renderState.copyingPlayer = false;
    updateSkin(properties.skin);
    renderState.name = properties.name;
    return this;
  }

  /**
   * Returns whether the widget is currently copying a player (either local or remote).
   *
   * @return whether the widget is copying a player.
   */
  public boolean isCopyingPlayer() {
    return renderState.copyingPlayer;
  }

  /**
   * Copies the player from the given profile result.
   *
   * @param result profile result.
   * @param source player identifier.
   * @return {@code this}.
   */
  private FancyPlayerWidget copyPlayer(CompletableFuture<Optional<GameProfile>> result, String source) {
    result.exceptionally(FancyPlayerWidget::handlePlayerCopyError).thenAccept(profile -> profile.ifPresentOrElse(this::copyPlayer, () -> handlePlayerCopyError(source)));
    return this;
  }

  /**
   * Copies a player from the specified {@link GameProfile}.
   *
   * @param profile game profile.
   * @return {@code this}.
   */
  private FancyPlayerWidget copyPlayer(GameProfile profile) {
    Minecraft.getInstance().getSkinManager().getOrLoad(profile).exceptionally(throwable -> (PlayerSkin) FancyPlayerWidget.handlePlayerCopyError(throwable).orElse(null)).thenAccept(skin -> {
      renderState.copyingPlayer = true;
      properties.name = renderState.name;
      renderState.name = profile.getName();
      if (skin != null) {
        this.updateSkin(skin);
      } else {
        handlePlayerCopyError(renderState.name);
      }
    });
    return this;
  }

  /**
   * Updates the render state based on the skin property.
   *
   * @param skin Player's skin.
   */
  private void updateSkin(@Nullable PlayerSkin skin) {
    if (skin != null) {
      renderState.isSlim = skin.model() == PlayerSkin.Model.SLIM;
      renderState.skin = skin;
    } else {
      updateIsSlim(properties.isSlim);
    }
    renderer = renderState.isSlim ? FancyPlayerRenderer.SLIM_RENDERER : FancyPlayerRenderer.WIDE_RENDERER;
  }

  /**
   * Updates the render state based on the slim property.
   *
   * @param isSlim whether the player is slim or wide.
   */
  private void updateIsSlim(boolean isSlim) {
    renderState.isSlim = isSlim;
    renderState.skin = DefaultPlayerSkin.DEFAULT_SKINS[random.nextInt(9) + (isSlim ? 0 : 9)];
  }

  /**
   * Updates the global render state.
   *
   * @param x x coordinate of the widget.
   * @param y y coordinate of the widget.
   * @param width widget width.
   * @param height widget height.
   * @param mouseX mouse x coordinate.
   * @param mouseY mouse y coordinate.
   * @param partialTick partial tick.
   */
  private void updateRenderState(int x, int y, int width, int height, int mouseX, int mouseY, float partialTick) {
    renderState.boundingBoxWidth = height / PLAYER_SIZE_RATIO;
    renderState.boundingBoxHeight = height;
    renderState.scale = height / PLAYER_RENDER_HEIGHT;
    if (renderState.bodyFollowsMouse || renderState.headFollowsMouse) {
      float renderHeight = renderState.pose == Pose.CROUCHING ? Player.CROUCH_BB_HEIGHT : PLAYER_RENDER_HEIGHT;
      float eyeHeight = renderState.pose == Pose.CROUCHING ? PLAYER_CROUCHING_EYE_HEIGHT : Player.DEFAULT_EYE_HEIGHT;
      float adultEyeY = (renderHeight - eyeHeight) * height / renderHeight;
      float eyeY = y + (renderState.isBaby ? (height + adultEyeY) * Player.DEFAULT_BABY_SCALE : adultEyeY);
      float eyeX = (x + width / 2F);
      double mouseXRelative = mouseX - eyeX;
      double mouseYRelative = mouseY - eyeY;
      double xRot = Math.atan(mouseYRelative / 40F) * 20;
      double yRot = -Math.atan(mouseXRelative / 40F) * 20;
      if (renderState.isUpsideDown) {
        xRot = -xRot;
        yRot = -yRot;
      }
      if (renderState.bodyFollowsMouse) {
        renderState.bodyRot.setXDeg(xRot);
        renderState.bodyRot.setYDeg(yRot);
        renderState.bodyRot.setZ(0);
      }
      if (renderState.headFollowsMouse) {
        renderState.headRot.setXDeg(xRot);
        renderState.headRot.setYDeg(yRot);
        renderState.headRot.setZ(0);
      }
    }
  }

  /**
   * Small dataclass to handle persistence of render state properties that would otherwise be irreversibly overridden by other properties.
   */
  private static final class OverridableProperties {
    /**
     * Head rotation.<br>
     * Overridable by {@link FancyPlayerRenderState#headFollowsMouse headFollowsMouse}.
     */
    final Rotation headRot = new Rotation();

    /**
     * Whole model rotation.<br>
     * Overridable by {@link FancyPlayerRenderState#bodyFollowsMouse bodyFollowsMouse}.
     */
    final Rotation bodyRot = new Rotation();

    /**
     * Whether the whole model should rotate to follow the mouse.<br>
     * Overridable by {@link FancyPlayerRenderState#pose}.
     */
    public boolean bodyFollowsMouse;

    /**
     * Whether the head should rotate to follow the mouse.<br>
     * Overridable by {@link FancyPlayerRenderState#pose}.
     */
    public boolean headFollowsMouse;

    /**
     * Whether the model is slim or wide.<br>
     * Overridable by {@link FancyPlayerRenderState#skin skin} or when copying a player.
     */
    boolean isSlim;

    /**
     * Player's name.<br>
     * Overridable by copying a player.
     */
    @NotNull
    String name;

    /**
     * Player's skin.<br>
     * Overridable by copying a player.
     */
    @Nullable
    PlayerSkin skin;

    /**
     * Parrot variant on the left shoulder.<br>
     * Overridable by {@link FancyPlayerRenderState#isBaby isBaby}.
     */
    @Nullable
    Parrot.Variant parrotOnLeftShoulder;

    /**
     * Parrot variant on the left shoulder.<br>
     * Overridable by {@link FancyPlayerRenderState#isBaby isBaby}.
     */
    @Nullable
    Parrot.Variant parrotOnRightShoulder;

    /**
     * Whether to display the fire animation.<br>
     * Overridable by {@link FancyPlayerRenderState#pose}.
     */
    boolean displayFireAnimation;

    /**
     * @param name {@link FancyPlayerRenderState#name name}.
     */
    private OverridableProperties(@NotNull String name) {
      this.name = name;
    }
  }
}
