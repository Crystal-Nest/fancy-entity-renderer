package it.crystalnest.fancy_entity_renderer.api.entity.player;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import it.crystalnest.fancy_entity_renderer.Constants;
import it.crystalnest.fancy_entity_renderer.api.Rotation;
import it.crystalnest.fancy_entity_renderer.api.entity.RenderMode;
import it.crystalnest.fancy_entity_renderer.api.entity.player.mock.FancyPlayerMock;
import it.crystalnest.fancy_entity_renderer.compat.SoulFireD;
import it.crystalnest.fancy_entity_renderer.platform.Services;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.commands.arguments.item.ItemParser;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
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

import java.util.ArrayList;
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
   * Skin model name used by Minecraft for slim player models.
   */
  private static final String SLIM_MODEL = "slim";

  /**
   * Player render height.
   */
  public static final float PLAYER_RENDER_HEIGHT = 1.875F;

  /**
   * Player eye height when crouching.
   */
  public static final float PLAYER_CROUCHING_EYE_HEIGHT = Player.CROUCH_BB_HEIGHT * 0.85F;

  /**
   * Player baby scale.
   */
  public static final float PLAYER_BABY_SCALE = 0.5F;

  /**
   * Ratio of a player's height to its width.
   */
  public static final float PLAYER_SIZE_RATIO = Player.DEFAULT_BB_HEIGHT / Player.DEFAULT_BB_WIDTH;

  /**
   * Global render state.
   */
  protected final FancyPlayerMock player = new FancyPlayerMock(new GameProfile(Util.NIL_UUID, "FancyMock"));

  /**
   * Random source.
   */
  protected final Random random = new Random();

  /**
   * Memory for overridable render state properties.
   */
  private final OverridableProperties properties = new OverridableProperties(player.name);

  /**
   * Allowed poses when mimicking a player.
   */
  public List<Pose> allowedPoses = new ArrayList<>();

  /**
   * Current player renderer.
   */
  protected FancyPlayerRenderer renderer = player.isSlim ? FancyPlayerRenderer.SLIM_RENDERER : FancyPlayerRenderer.WIDE_RENDERER;

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
    float offsetY = (float) renderer.getRenderOffset(player, 0).y;
    if (player.getPose() == Pose.SLEEPING) {
      offsetX += PLAYER_RENDER_HEIGHT * player.scale / 2;
      offsetY -= 0.25F * player.scale;
      if (player.isBaby) {
        // TODO: Why these values?
        offsetX /= 1.75F;
        offsetY /= 1.5F;
      }
    }
    gfx.pose().translate(getX() + getWidth() / 2F + offsetX, getY() + getHeight() + offsetY, 100);
    gfx.flush();
    gfx.pose().scale(1, -1, 1);
    Lighting.setupForEntityInInventory();
    gfx.pose().rotateAround(new Quaternionf().rotateXYZ(player.modelRot.getX(), -player.modelRot.getY(), player.modelRot.getZ()), 0, 0, 0);
    renderer.render(player, gfx.pose(), gfx.bufferSource(), LightTexture.FULL_BRIGHT);
    gfx.bufferSource().endBatch();
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
    if (player.getPose() == Pose.STANDING || player.getPose() == Pose.CROUCHING || player.getPose() == Pose.SPIN_ATTACK) {
      player.bodyFollowsMouse = followsMouse;
      if (followsMouse) {
        properties.bodyRot.copy(player.modelRot);
      } else {
        player.modelRot.copy(properties.bodyRot);
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
    if (player.getPose() == Pose.STANDING || player.getPose() == Pose.CROUCHING || player.getPose() == Pose.SPIN_ATTACK) {
      player.headFollowsMouse = followsMouse;
      if (followsMouse) {
        properties.headRot.copy(player.headRot);
      } else {
        player.headRot.copy(properties.headRot);
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
    player.headRot.copy(rotation);
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
    player.headRot.setDeg(x, y, z);
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
    player.modelRot.copy(rotation);
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
    player.modelRot.setDeg(x, y, z);
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
    player.leftArmRot.copy(rotation);
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
    player.leftArmRot.setDeg(x, y, z);
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
    player.rightArmRot.copy(rotation);
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
    player.rightArmRot.setDeg(x, y, z);
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
    player.leftLegRot.copy(rotation);
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
    player.leftLegRot.setDeg(x, y, z);
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
    player.rightLegRot.copy(rotation);
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
    player.rightLegRot.setDeg(x, y, z);
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
    if (!player.copyingPlayer && properties.skin == null) {
      updateIsSlim(properties.isSlim);
      renderer = isSlim ? FancyPlayerRenderer.SLIM_RENDERER : FancyPlayerRenderer.WIDE_RENDERER;
    }
    return this;
  }

  /**
   * Sets a custom skin for the player.<br>
   * Uses the current slim property. If {@code null}, restores the previous default skin.
   *
   * @param skin skin texture.
   * @return {@code this}.
   */
  public FancyPlayerWidget setSkin(@Nullable ResourceLocation skin) {
    return setSkin(skin, properties.isSlim);
  }

  /**
   * Sets a custom skin for the player.<br>
   * If {@code null}, restores the previous default skin.
   *
   * @param skin skin texture.
   * @param isSlim whether the skin should use the slim model.
   * @return {@code this}.
   */
  public FancyPlayerWidget setSkin(@Nullable ResourceLocation skin, boolean isSlim) {
    properties.skin = skin;
    properties.isSlim = isSlim;
    if (!player.copyingPlayer) {
      updateSkin(skin, isSlim);
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
    player.copyingPlayer = true;
    copyPlayer(Minecraft.getInstance().getUser().getGameProfile());
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
    if (!player.copyingPlayer) {
      player.name = name;
    }
    return this;
  }

  /**
   * Sets whether to pin the player's name at the top of the bounding box.<br>
   * If you want to change the name's visibility, use {@link #setShowName(boolean)}.
   *
   * @param pinName whether to pin the player's name at the top of the bounding box.
   * @return {@code this}.
   */
  public FancyPlayerWidget setPinName(boolean pinName) {
    player.pinName = pinName;
    return this;
  }

  /**
   * Sets whether to show the player's name.
   *
   * @param showName whether to show the player's name.
   * @return {@code this}.
   */
  public FancyPlayerWidget setShowName(boolean showName) {
    player.showPlayerName = showName;
    return this;
  }

  /**
   * Sets whether to show the player's cape.<br>
   * Note: to show a cape, the player's skin must include the cape.
   *
   * @param showCape whether to show the player's cape.
   * @return {@code this}.
   */
  public FancyPlayerWidget setShowCape(boolean showCape) {
    player.showCape = showCape;
    return this;
  }

  /**
   * Sets whether to show the player's left arm.
   *
   * @param showLeftArm whether to show the player's left arm.
   * @return {@code this}.
   */
  public FancyPlayerWidget setShowLeftArm(boolean showLeftArm) {
    player.showLeftArm = showLeftArm;
    return this;
  }

  /**
   * Sets whether to show the player's left sleeve.<br>
   * Note: the player's skin must include an outer layer for the left sleeve to be visible.
   *
   * @param showLeftSleeve whether to show the player's left sleeve.
   * @return {@code this}.
   */
  public FancyPlayerWidget setShowLeftSleeve(boolean showLeftSleeve) {
    player.showLeftSleeve = showLeftSleeve;
    return this;
  }

  /**
   * Sets whether to show the player's right arm.
   *
   * @param showRightArm whether to show the player's right arm.
   * @return {@code this}.
   */
  public FancyPlayerWidget setShowRightArm(boolean showRightArm) {
    player.showRightArm = showRightArm;
    return this;
  }

  /**
   * Sets whether to show the player's right sleeve.<br>
   * Note: the player's skin must include an outer layer for the right sleeve to be visible.
   *
   * @param showRightSleeve whether to show the player's right sleeve.
   * @return {@code this}.
   */
  public FancyPlayerWidget setShowRightSleeve(boolean showRightSleeve) {
    player.showRightSleeve = showRightSleeve;
    return this;
  }

  /**
   * Sets whether to show the player's left leg.
   *
   * @param showLeftLeg whether to show the player's left leg.
   * @return {@code this}.
   */
  public FancyPlayerWidget setShowLeftLeg(boolean showLeftLeg) {
    player.showLeftLeg = showLeftLeg;
    return this;
  }

  /**
   * Sets whether to show the player's left pants.<br>
   * Note: the player's skin must include an outer layer for the left pants to be visible.
   *
   * @param showLeftPants whether to show the player's left pants.
   * @return {@code this}.
   */
  public FancyPlayerWidget setShowLeftPants(boolean showLeftPants) {
    player.showLeftPants = showLeftPants;
    return this;
  }

  /**
   * Sets whether to show the player's right leg.
   *
   * @param showRightLeg whether to show the player's right leg.
   * @return {@code this}.
   */
  public FancyPlayerWidget setShowRightLeg(boolean showRightLeg) {
    player.showRightLeg = showRightLeg;
    return this;
  }

  /**
   * Sets whether to show the player's right pants.<br>
   * Note: the player's skin must include an outer layer for the right pants to be visible.
   *
   * @param showRightPants whether to show the player's right pants.
   * @return {@code this}.
   */
  public FancyPlayerWidget setShowRightPants(boolean showRightPants) {
    player.showRightPants = showRightPants;
    return this;
  }

  /**
   * Sets whether to show the player's head.
   *
   * @param showHead whether to show the player's head.
   * @return {@code this}.
   */
  public FancyPlayerWidget setShowHead(boolean showHead) {
    player.showHead = showHead;
    return this;
  }

  /**
   * Sets whether to show the player's hat.<br>
   * Note: the player's skin must include an outer layer for the hat to be visible.
   *
   * @param showHat whether to show the player's hat.
   * @return {@code this}.
   */
  public FancyPlayerWidget setShowHat(boolean showHat) {
    player.showHat = showHat;
    return this;
  }

  /**
   * Sets whether to show the player's torso.
   *
   * @param showBody whether to show the player's torso.
   * @return {@code this}.
   */
  public FancyPlayerWidget setShowBody(boolean showBody) {
    player.showBody = showBody;
    return this;
  }

  /**
   * Sets whether to show the player's jacket.<br>
   * Note: the player's skin must include an outer layer for the jacket to be visible.
   *
   * @param showJacket whether to show the player's jacket.
   * @return {@code this}.
   */
  public FancyPlayerWidget setShowJacket(boolean showJacket) {
    player.showJacket = showJacket;
    return this;
  }

  /**
   * Sets whether to show the player's outer layer.<br>
   * Note: the player's skin must include an outer layer for it to be visible.
   *
   * @param showOuterLayer whether to show the player's outer layer.
   * @return {@code this}.
   */
  public FancyPlayerWidget setShowOuterLayer(boolean showOuterLayer) {
    setShowLeftSleeve(showOuterLayer);
    setShowRightSleeve(showOuterLayer);
    setShowLeftPants(showOuterLayer);
    setShowRightPants(showOuterLayer);
    setShowHat(showOuterLayer);
    setShowJacket(showOuterLayer);
    return this;
  }

  /**
   * Sets whether to show the player's inner layer.
   *
   * @param showInnerLayer whether to show the player's inner layer.
   * @return {@code this}.
   */
  public FancyPlayerWidget setShowInnerLayer(boolean showInnerLayer) {
    setShowLeftArm(showInnerLayer);
    setShowRightArm(showInnerLayer);
    setShowLeftLeg(showInnerLayer);
    setShowRightLeg(showInnerLayer);
    setShowHead(showInnerLayer);
    setShowBody(showInnerLayer);
    return this;
  }

  /**
   * Sets whether the player is rendered upside-down.
   *
   * @param isUpsideDown whether to render the player's upside-down.
   * @return {@code this}.
   */
  public FancyPlayerWidget setUpsideDown(boolean isUpsideDown) {
    player.isUpsideDown = isUpsideDown;
    return this;
  }

  /**
   * Sets the player render mode.
   *
   * @param mode {@link RenderMode}.
   * @return {@code this}.
   */
  public FancyPlayerWidget setRenderMode(RenderMode mode) {
    player.isSpectator = RenderMode.SPECTATOR == mode;
    player.isInvisible = RenderMode.NORMAL != mode;
    player.isInvisibleToPlayer = RenderMode.INVISIBLE == mode;
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
    player.appearsGlowing = isGlowing;
    return this;
  }

  /**
   * Sets whether the player should be moving.
   *
   * @param isMoving whether the player should be moving.
   * @return {@code this}.
   */
  public FancyPlayerWidget setMoving(boolean isMoving) {
    player.isMoving = isMoving;
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
    if (player.getPose() == Pose.STANDING || player.getPose() == Pose.CROUCHING) {
      player.displayFireAnimation = onFire;
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
      SoulFireD.setOnFire(player, fireType);
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
    player.setArrowCount(count);
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
    player.setStingerCount(count);
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
    if (player.isBaby || player.getPose() == Pose.SWIMMING) {
      properties.parrotOnLeftShoulder = parrot;
    } else {
      player.parrotOnLeftShoulder = parrot;
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
    if (player.isBaby || player.getPose() == Pose.SWIMMING) {
      properties.parrotOnRightShoulder = parrot;
    } else {
      player.parrotOnRightShoulder = parrot;
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
    if (player.isBaby || player.getPose() == Pose.SWIMMING) {
      properties.parrotOnLeftShoulder = left;
      properties.parrotOnRightShoulder = right;
    } else {
      player.parrotOnLeftShoulder = left;
      player.parrotOnRightShoulder = right;
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
    player.isBaby = isBaby;
    if (isBaby) {
      properties.parrotOnLeftShoulder = player.parrotOnLeftShoulder;
      properties.parrotOnRightShoulder = player.parrotOnRightShoulder;
    } else {
      player.parrotOnLeftShoulder = properties.parrotOnLeftShoulder;
      player.parrotOnRightShoulder = properties.parrotOnRightShoulder;
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
      player.setPose(pose);
      player.isAutoSpinAttack = pose == Pose.SPIN_ATTACK;
      player.isVisuallySwimming = pose == Pose.SWIMMING;
      player.deathTime = pose == Pose.DYING ? 5 : 0;
      if (pose == Pose.STANDING || pose == Pose.CROUCHING) {
        player.displayFireAnimation = properties.displayFireAnimation;
      } else {
        properties.displayFireAnimation = player.displayFireAnimation;
        player.displayFireAnimation = false;
      }
      if (pose == Pose.STANDING || pose == Pose.CROUCHING || pose == Pose.SPIN_ATTACK) {
        player.headFollowsMouse = properties.headFollowsMouse;
        player.bodyFollowsMouse = properties.bodyFollowsMouse;
      } else {
        properties.headFollowsMouse = player.headFollowsMouse;
        properties.bodyFollowsMouse = player.bodyFollowsMouse;
        player.headFollowsMouse = false;
        player.bodyFollowsMouse = false;
      }
      if (pose == Pose.SWIMMING) {
        properties.parrotOnLeftShoulder = player.parrotOnLeftShoulder;
        properties.parrotOnRightShoulder = player.parrotOnRightShoulder;
        player.parrotOnLeftShoulder = null;
        player.parrotOnRightShoulder = null;
      } else {
        player.parrotOnLeftShoulder = properties.parrotOnLeftShoulder;
        player.parrotOnRightShoulder = properties.parrotOnRightShoulder;
      }
    } else {
      Constants.LOGGER.warn("Pose {} is not supported for Player entity!", pose);
    }
    return this;
  }

  /**
   * Sets the movement speed.<br>
   * Effective only when the player is moving (see {@link #setMoving(boolean)}).
   *
   * @param speed speed value.
   * @return {@code this}.
   */
  public FancyPlayerWidget setMovementSpeed(float speed) {
    player.speedValue = speed;
    return this;
  }

  /**
   * Sets the walking speed and amplitude.<br>
   * Effective only when the player is moving (see {@link #setMoving(boolean)}).
   *
   * @param speed speed value.
   * @return {@code this}.
   */
  public FancyPlayerWidget setWalkingSpeed(float speed) {
    player.walkSpeed = speed;
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
    player.setItemInHand(InteractionHand.MAIN_HAND, getNullableItem(item, i -> parseItem(i, provider)));
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
    player.setItemInHand(InteractionHand.OFF_HAND, getNullableItem(item, i -> parseItem(i, provider)));
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
    player.setItemInHand(InteractionHand.MAIN_HAND, getNullableItem(item, Item::getDefaultInstance));
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
    player.setItemInHand(InteractionHand.OFF_HAND, getNullableItem(item, Item::getDefaultInstance));
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
    player.setItemInHand(InteractionHand.MAIN_HAND, getNullableItem(item, i -> i));
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
    player.setItemInHand(InteractionHand.OFF_HAND, getNullableItem(item, i -> i));
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
    player.setItemSlot(EquipmentSlot.HEAD, getNullableItem(item, i -> parseItem(i, provider)));
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
    player.setItemSlot(EquipmentSlot.CHEST, getNullableItem(item, i -> parseItem(i, provider)));
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
    player.setItemSlot(EquipmentSlot.LEGS, getNullableItem(item, i -> parseItem(i, provider)));
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
    player.setItemSlot(EquipmentSlot.FEET, getNullableItem(item, i -> parseItem(i, provider)));
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
    player.setItemSlot(EquipmentSlot.HEAD, getNullableItem(item, Item::getDefaultInstance));
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
    player.setItemSlot(EquipmentSlot.CHEST, getNullableItem(item, Item::getDefaultInstance));
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
    player.setItemSlot(EquipmentSlot.LEGS, getNullableItem(item, Item::getDefaultInstance));
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
    player.setItemSlot(EquipmentSlot.FEET, getNullableItem(item, Item::getDefaultInstance));
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
    player.setItemSlot(EquipmentSlot.HEAD, getNullableItem(item, i -> i));
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
    player.setItemSlot(EquipmentSlot.CHEST, getNullableItem(item, i -> i));
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
    player.setItemSlot(EquipmentSlot.LEGS, getNullableItem(item, i -> i));
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
    player.setItemSlot(EquipmentSlot.FEET, getNullableItem(item, i -> i));
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
      ItemParser.ItemResult result = ItemParser.parseForItem(provider.lookupOrThrow(Registries.ITEM), new StringReader(item));
      return new ItemInput(result.item(), result.nbt()).createItemStack(1, false);
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
    player.copyingPlayer = false;
    updateSkin(properties.skin, properties.isSlim);
    player.name = properties.name;
    return this;
  }

  /**
   * Returns whether the widget is currently copying a player (either local or remote).
   *
   * @return whether the widget is copying a player.
   */
  public boolean isCopyingPlayer() {
    return player.copyingPlayer;
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
    player.copyingPlayer = true;
    properties.name = player.name;
    UUID id = profile.getId() == null ? Util.NIL_UUID : profile.getId();
    player.name = profile.getName() == null ? id.toString() : profile.getName();
    updateSkin(DefaultPlayerSkin.getDefaultSkin(id), isSlimModel(DefaultPlayerSkin.getSkinModelName(id)));
    Minecraft.getInstance().getSkinManager().registerSkins(profile, (type, location, texture) -> {
      if (type == MinecraftProfileTexture.Type.SKIN) {
        updateSkin(location, isSlimModel(texture.getMetadata("model")));
      } else if (type == MinecraftProfileTexture.Type.CAPE) {
        player.cape = location;
      } else if (type == MinecraftProfileTexture.Type.ELYTRA) {
        player.elytra = location;
      }
    }, true);
    return this;
  }

  /**
   * Updates the render state based on the skin property.
   *
   * @param skin Player's skin.
   */
  private void updateSkin(@Nullable ResourceLocation skin, boolean isSlim) {
    if (skin != null) {
      player.isSlim = isSlim;
      player.skin = skin;
      player.cape = null;
      player.elytra = null;
    } else {
      updateIsSlim(properties.isSlim);
    }
    renderer = player.isSlim ? FancyPlayerRenderer.SLIM_RENDERER : FancyPlayerRenderer.WIDE_RENDERER;
  }

  /**
   * Updates the render state based on the slim property.
   *
   * @param isSlim whether the player is slim or wide.
   */
  private void updateIsSlim(boolean isSlim) {
    player.isSlim = isSlim;
    player.skin = getRandomDefaultSkin(isSlim);
    player.cape = null;
    player.elytra = null;
  }

  /**
   * Returns a random default skin matching the selected model type.
   *
   * @param isSlim whether the skin should be slim.
   * @return skin texture.
   */
  private ResourceLocation getRandomDefaultSkin(boolean isSlim) {
    UUID id;
    do {
      id = new UUID(random.nextLong(), random.nextLong());
    } while (isSlimModel(DefaultPlayerSkin.getSkinModelName(id)) != isSlim);
    return DefaultPlayerSkin.getDefaultSkin(id);
  }

  /**
   * Checks whether a model name is the slim player model.
   *
   * @param modelName model name.
   * @return whether the model is slim.
   */
  private static boolean isSlimModel(@Nullable String modelName) {
    return SLIM_MODEL.equals(modelName);
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
    player.boundingBoxWidth = height / PLAYER_SIZE_RATIO;
    player.boundingBoxHeight = height;
    player.scale = height / PLAYER_RENDER_HEIGHT;
    if (player.bodyFollowsMouse || player.headFollowsMouse) {
      float renderHeight = player.getPose() == Pose.CROUCHING ? Player.CROUCH_BB_HEIGHT : PLAYER_RENDER_HEIGHT;
      float eyeHeight = player.getPose() == Pose.CROUCHING ? PLAYER_CROUCHING_EYE_HEIGHT : Player.DEFAULT_EYE_HEIGHT;
      float adultEyeY = (renderHeight - eyeHeight) * height / renderHeight;
      float eyeY = y + (player.isBaby ? (height + adultEyeY) * PLAYER_BABY_SCALE : adultEyeY);
      float eyeX = (x + width / 2F);
      double mouseXRelative = mouseX - eyeX;
      double mouseYRelative = mouseY - eyeY;
      double xRot = Math.atan(mouseYRelative / 40F) * 20;
      double yRot = -Math.atan(mouseXRelative / 40F) * 20;
      if (player.isUpsideDown) {
        xRot = -xRot;
        yRot = -yRot;
      }
      if (player.bodyFollowsMouse) {
        player.modelRot.setXDeg(xRot);
        player.modelRot.setYDeg(yRot);
        player.modelRot.setZ(0);
      }
      if (player.headFollowsMouse) {
        player.headRot.setXDeg(xRot);
        player.headRot.setYDeg(yRot);
        player.headRot.setZ(0);
      }
    }
  }

  /**
   * Small dataclass to handle persistence of render state properties that would otherwise be irreversibly overridden by other properties.
   */
  private static final class OverridableProperties {
    /**
     * Head rotation.<br>
     * Overridable by {@link FancyPlayerMock#headFollowsMouse headFollowsMouse}.
     */
    final Rotation headRot = new Rotation();

    /**
     * Whole model rotation.<br>
     * Overridable by {@link FancyPlayerMock#bodyFollowsMouse bodyFollowsMouse}.
     */
    final Rotation bodyRot = new Rotation();

    /**
     * Whether the whole model should rotate to follow the mouse.<br>
     * Overridable by {@link FancyPlayerMock#getPose()}.
     */
    public boolean bodyFollowsMouse;

    /**
     * Whether the head should rotate to follow the mouse.<br>
     * Overridable by {@link FancyPlayerMock#getPose()}.
     */
    public boolean headFollowsMouse;

    /**
     * Whether the model is slim or wide.<br>
     * Overridable by {@link FancyPlayerMock#skin skin} or when copying a player.
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
    ResourceLocation skin;

    /**
     * Parrot variant on the left shoulder.<br>
     * Overridable by {@link FancyPlayerMock#isBaby isBaby}.
     */
    @Nullable
    Parrot.Variant parrotOnLeftShoulder;

    /**
     * Parrot variant on the left shoulder.<br>
     * Overridable by {@link FancyPlayerMock#isBaby isBaby}.
     */
    @Nullable
    Parrot.Variant parrotOnRightShoulder;

    /**
     * Whether to display the fire animation.<br>
     * Overridable by {@link FancyPlayerMock#getPose()}.
     */
    boolean displayFireAnimation;

    /**
     * @param name {@link FancyPlayerMock#name name}.
     */
    private OverridableProperties(@NotNull String name) {
      this.name = name;
    }
  }
}
