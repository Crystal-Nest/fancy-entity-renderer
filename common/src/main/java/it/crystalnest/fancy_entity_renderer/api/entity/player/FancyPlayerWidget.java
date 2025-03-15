package it.crystalnest.fancy_entity_renderer.api.entity.player;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.math.Axis;
import it.crystalnest.fancy_entity_renderer.Constants;
import it.crystalnest.fancy_entity_renderer.api.Rotation;
import it.crystalnest.fancy_entity_renderer.api.entity.player.state.FancyPlayerRenderState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Custom player widget.
 */
public class FancyPlayerWidget extends AbstractWidget {
  /**
   * Player render height.
   */
  public static final float PLAYER_RENDER_HEIGHT = 1.875F;

  /**
   * Global render state.
   */
  private final FancyPlayerRenderState renderState = new FancyPlayerRenderState();

  /**
   * Renderer for the wide player model.
   */
  private final FancyPlayerRenderer wideRenderer = new FancyPlayerRenderer(renderState, false);

  /**
   * Renderer for the slim player model.
   */
  private final FancyPlayerRenderer slimRenderer = new FancyPlayerRenderer(renderState, true);

  /**
   * Current player renderer.
   */
  private FancyPlayerRenderer renderer = renderState.isSlim ? slimRenderer : wideRenderer;

  /**
   * Memory for overridable render state properties.
   */
  private final OverridableProperties properties = new OverridableProperties(renderState.name);

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
    gfx.pose().translate(getX() + getWidth() / 2F, getY() + getHeight(), 100);
    gfx.flush();
    gfx.pose().scale(1, -1, 1);
    Lighting.setupForEntityInInventory(Axis.XP.rotationDegrees(renderState.bodyRot.getX()));
    gfx.drawSpecial(bufferSource -> renderer.render(gfx.pose(), bufferSource, LightTexture.FULL_BRIGHT));
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
    // TODO: Maybe add narration for when the player name is visible (what about when the name is visible and the player is crouching?).
  }

  /**
   * Sets whether the player's model should follow the mouse cursor.<br>
   * Overrides any manual body rotation set previously if {@code true}, otherwise restores the previous body rotation, if any.
   *
   * @param followsMouse whether to follow the mouse cursor.
   * @return {@code this}.
   */
  public FancyPlayerWidget setBodyFollowsMouse(boolean followsMouse) {
    renderState.bodyFollowsMouse = followsMouse;
    if (followsMouse) {
      properties.bodyRot.copy(renderState.bodyRot);
    } else {
      renderState.bodyRot.copy(properties.bodyRot);
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
    renderState.headFollowsMouse = followsMouse;
    if (followsMouse) {
      properties.headRot.copy(renderState.headRot);
    } else {
      renderState.headRot.copy(properties.headRot);
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
    properties.headRot.updateDeg(x, y, z);
    renderState.headRot.updateDeg(x, y, z);
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
    properties.bodyRot.updateDeg(x, y, z);
    renderState.bodyRot.updateDeg(x, y, z);
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
    renderState.leftArmRot.updateDeg(x, y, z);
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
    renderState.rightArmRot.updateDeg(x, y, z);
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
    renderState.leftLegRot.updateDeg(x, y, z);
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
    renderState.rightLegRot.updateDeg(x, y, z);
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
      renderer = isSlim ? slimRenderer : wideRenderer;
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
   * Sets whether the player is rendered as in spectator mode.<p>
   * <b>WARNING: Experimental!</b><br>
   * Currently, it only makes the player a floating head.
   *
   * @param isSpectator whether the player is rendered as in spectator mode.
   * @return {@code this}.
   */
  @ApiStatus.Experimental
  public FancyPlayerWidget setSpectator(boolean isSpectator) {
    renderState.isSpectator = isSpectator;
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
   * Sets whether the player should be moving.<p>
   * <b>WARNING: Experimental!</b><br>
   * Currently, it just makes the player's arms move idly and has not been tested with custom arm rotations.
   *
   * @param isMoving whether the player should be moving.
   * @return {@code this}.
   */
  @ApiStatus.Experimental
  public FancyPlayerWidget setMoving(boolean isMoving) {
    renderState.isMoving = isMoving;
    return this;
  }

  /**
   * Sets whether the player is on fire.<p>
   * <b>WARNING: Experimental!</b><br>
   * Currently, it works, but doesn't look that good.
   *
   * @param onFire whether the player is on fire.
   * @return {@code this}.
   */
  @ApiStatus.Experimental
  public FancyPlayerWidget setOnFire(boolean onFire) {
    // TODO: Flames are too wide, tall, and "in front".
    renderState.displayFireAnimation = onFire;
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
   * Sets whether the player is crouching.<br>
   * Will probably be removed in the future in favor of a more general method to set default player poses.
   *
   * @param isCrouching whether the player is crouching.
   * @return {@code this}.
   */
  @Deprecated(since = "0.1.0", forRemoval = true)
  public FancyPlayerWidget setCrouching(boolean isCrouching) {
    renderState.isCrouching = isCrouching;
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
    renderState.rightHandHeldItem = item;
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
    renderState.leftHandHeldItem = item;
    return this;
  }

  /**
   * Sets the item the player is wearing on its head.<br>
   * Pass a valid item to set it, pass {@code null} to remove it.<br>
   * If you want more customization on the item properties (e.g., armor trim), use {@link #setHeadWearable(ItemStack)} instead.
   *
   * @param item item to wear on the head.
   * @return {@code this}.
   */
  public FancyPlayerWidget setHeadWearable(@Nullable Item item) {
    renderState.headEquipment = item == null ? ItemStack.EMPTY : item.getDefaultInstance();
    return this;
  }

  /**
   * Sets the item the player is wearing on its chest.<br>
   * Pass a valid item to set it, pass {@code null} to remove it.<br>
   * If you want more customization on the item properties (e.g., armor trim), use {@link #setChestWearable(ItemStack)} instead.
   *
   * @param item item to wear on the chest.
   * @return {@code this}.
   */
  public FancyPlayerWidget setChestWearable(@Nullable Item item) {
    renderState.chestEquipment = item == null ? ItemStack.EMPTY : item.getDefaultInstance();
    return this;
  }

  /**
   * Sets the item the player is wearing on its legs.<br>
   * Pass a valid item to set it, pass {@code null} to remove it.<br>
   * If you want more customization on the item properties (e.g., armor trim), use {@link #setLegsWearable(ItemStack)} instead.
   *
   * @param item item to wear on the legs.
   * @return {@code this}.
   */
  public FancyPlayerWidget setLegsWearable(@Nullable Item item) {
    renderState.legsEquipment = item == null ? ItemStack.EMPTY : item.getDefaultInstance();
    return this;
  }

  /**
   * Sets the item the player is wearing on its feet.<br>
   * Pass a valid item to set it, pass {@code null} to remove it.<br>
   * If you want more customization on the item properties (e.g., armor trim), use {@link #setFeetWearable(ItemStack)} instead.
   *
   * @param item item to wear on the feet.
   * @return {@code this}.
   */
  public FancyPlayerWidget setFeetWearable(@Nullable Item item) {
    renderState.feetEquipment = item == null ? ItemStack.EMPTY : item.getDefaultInstance();
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
    renderState.headEquipment = item == null ? ItemStack.EMPTY : item;
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
    renderState.chestEquipment = item == null ? ItemStack.EMPTY : item;
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
    renderState.legsEquipment = item == null ? ItemStack.EMPTY : item;
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
    renderState.feetEquipment = item == null ? ItemStack.EMPTY : item;
    return this;
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
    Minecraft.getInstance().getSkinManager().getOrLoad(profile).exceptionally(FancyPlayerWidget::handlePlayerCopyError).thenAccept(skin -> {
      renderState.copyingPlayer = true;
      properties.name = renderState.name;
      renderState.name = profile.getName();
      skin.ifPresentOrElse(this::updateSkin, () -> handlePlayerCopyError(renderState.name));
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
    renderer = renderState.isSlim ? slimRenderer : wideRenderer;
  }

  /**
   * Updates the render state based on the slim property.
   *
   * @param isSlim whether the player is slim or wide.
   */
  private void updateIsSlim(boolean isSlim) {
    renderState.isSlim = isSlim;
    renderState.skin = DefaultPlayerSkin.DEFAULT_SKINS[(int) (Math.random() * 9) + (isSlim ? 0 : 9)];
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
    renderState.boundingBoxWidth = width;
    renderState.boundingBoxHeight = height;
    renderState.scale = height / PLAYER_RENDER_HEIGHT;
    if (renderState.bodyFollowsMouse || renderState.headFollowsMouse) {
      // float adultHeight = PLAYER_RENDER_HEIGHT; // Height of an adult player
      // float adultEyeHeight = Player.DEFAULT_EYE_HEIGHT; // Eye level for an adult (when standing)
      // baby values are simply halved.

      // 1.62 = Player.DEFAULT_EYE_HEIGHT;
      // 0.6 = Player.SWIMMING_BB_HEIGHT;
      // 1.5 = Player.CROUCH_BB_HEIGHT;
      // 0.6 = Player.SWIMMING_BB_WIDTH;
      // 1.8 = Entity.DEFAULT_BB_HEIGHT;
      // 0.6 = Entity.DEFAULT_BB_WIDTH;
      // Player.POSES; // From poses we can get the eye level for each different pose.

      // modelEye = PLAYER_RENDER_HEIGHT - Player.DEFAULT_EYE_HEIGHT
      // If baby, both the render height and the eye height are halved
      float eyeY = renderState.isBaby ? y + (height / 2F) + ((PLAYER_RENDER_HEIGHT - Player.DEFAULT_EYE_HEIGHT) * height / PLAYER_RENDER_HEIGHT) / 2 : y + (PLAYER_RENDER_HEIGHT - Player.DEFAULT_EYE_HEIGHT) * height / PLAYER_RENDER_HEIGHT;
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
     * @param name {@link FancyPlayerRenderState#name name}.
     */
    private OverridableProperties(@NotNull String name) {
      this.name = name;
    }
  }
}
