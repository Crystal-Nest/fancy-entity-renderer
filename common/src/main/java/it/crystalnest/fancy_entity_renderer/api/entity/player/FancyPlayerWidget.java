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

  public FancyPlayerWidget setBodyFollowsMouse(boolean followsMouse) {
    renderState.bodyFollowsMouse = followsMouse;
    if (followsMouse) {
      properties.bodyRot.copy(renderState.bodyRot);
    } else {
      renderState.bodyRot.copy(properties.bodyRot);
    }
    return this;
  }

  public FancyPlayerWidget setHeadFollowsMouse(boolean followsMouse) {
    renderState.headFollowsMouse = followsMouse;
    if (followsMouse) {
      properties.headRot.copy(renderState.headRot);
    } else {
      renderState.headRot.copy(properties.headRot);
    }
    return this;
  }

  public FancyPlayerWidget setHeadRotation(Rotation rotation) {
    properties.headRot.copy(rotation);
    renderState.headRot.copy(rotation);
    return this;
  }

  public FancyPlayerWidget setHeadRotation(float x, float y, float z) {
    properties.headRot.updateDeg(x, y, z);
    renderState.headRot.updateDeg(x, y, z);
    return this;
  }

  public FancyPlayerWidget setBodyRotation(Rotation rotation) {
    properties.bodyRot.copy(rotation);
    renderState.bodyRot.copy(rotation);
    return this;
  }

  public FancyPlayerWidget setBodyRotation(float x, float y, float z) {
    properties.bodyRot.updateDeg(x, y, z);
    renderState.bodyRot.updateDeg(x, y, z);
    return this;
  }

  public FancyPlayerWidget setLeftArmRotation(Rotation rotation) {
    renderState.leftArmRot.copy(rotation);
    return this;
  }

  public FancyPlayerWidget setLeftArmRotation(float x, float y, float z) {
    renderState.leftArmRot.updateDeg(x, y, z);
    return this;
  }

  public FancyPlayerWidget setRightArmRotation(Rotation rotation) {
    renderState.rightArmRot.copy(rotation);
    return this;
  }

  public FancyPlayerWidget setRightArmRotation(float x, float y, float z) {
    renderState.rightArmRot.updateDeg(x, y, z);
    return this;
  }

  public FancyPlayerWidget setLeftLegRotation(Rotation rotation) {
    renderState.leftLegRot.copy(rotation);
    return this;
  }

  public FancyPlayerWidget setLeftLegRotation(float x, float y, float z) {
    renderState.leftLegRot.updateDeg(x, y, z);
    return this;
  }

  public FancyPlayerWidget setRightLegRotation(Rotation rotation) {
    renderState.rightLegRot.copy(rotation);
    return this;
  }

  public FancyPlayerWidget setRightLegRotation(float x, float y, float z) {
    renderState.rightLegRot.updateDeg(x, y, z);
    return this;
  }

  /**
   * Makes the player slim or wide.
   *
   * @param isSlim whether the player should be slim.
   */
  public FancyPlayerWidget setSlim(boolean isSlim) {
    properties.isSlim = isSlim;
    if (!renderState.copyLocalPlayer && properties.skin == null) {
      updateIsSlim(properties.isSlim);
      renderer = isSlim ? slimRenderer : wideRenderer;
    }
    return this;
  }

  /**
   * Sets a custom skin for the player.
   *
   * @param skin {@link PlayerSkin}.
   */
  public FancyPlayerWidget setSkin(@Nullable PlayerSkin skin) {
    properties.skin = skin;
    if (!renderState.copyLocalPlayer) {
      updateSkin(skin);
    }
    return this;
  }

  /**
   * Makes the player copy the local player or not.
   *
   * @param copyLocalPlayer whether to copy the local player.
   */
  public FancyPlayerWidget setCopyLocalPlayer(boolean copyLocalPlayer) {
    renderState.copyLocalPlayer = copyLocalPlayer;
    if (copyLocalPlayer) {
      copyPlayer(Minecraft.getInstance().getGameProfile());
    } else {
      updateSkin(properties.skin);
      renderState.name = properties.name;
    }
    return this;
  }

  public FancyPlayerWidget setName(String name) {
    properties.name = name;
    if (!renderState.copyLocalPlayer) {
      renderState.name = name;
    }
    return this;
  }

  public FancyPlayerWidget setShowName(boolean showName) {
    renderState.showPlayerName = showName;
    return this;
  }

  public FancyPlayerWidget setUpsideDown(boolean isUpsideDown) {
    renderState.isUpsideDown = isUpsideDown;
    return this;
  }

  public FancyPlayerWidget setSpectator(boolean isSpectator) {
    renderState.isSpectator = isSpectator;
    return this;
  }

  public FancyPlayerWidget setGlowing(boolean isGlowing) {
    renderState.appearsGlowing = isGlowing;
    return this;
  }

  public FancyPlayerWidget setMoving(boolean isMoving) {
    renderState.isMoving = isMoving;
    return this;
  }

  @ApiStatus.Experimental
  public FancyPlayerWidget setOnFire(boolean onFire) {
    // TODO: Flames are too wide, tall, and "in front".
    renderState.displayFireAnimation = onFire;
    return this;
  }

  public FancyPlayerWidget setBaby(boolean isBaby) {
    renderState.isBaby = isBaby;
    return this;
  }

  public FancyPlayerWidget setCrouching(boolean isCrouching) {
    renderState.isCrouching = isCrouching;
    return this;
  }

  public FancyPlayerWidget setRightHandItem(@Nullable Item item) {
    renderState.rightHandHeldItem = item;
    return this;
  }

  public FancyPlayerWidget setLeftHandItem(@Nullable Item item) {
    renderState.leftHandHeldItem = item;
    return this;
  }

  public FancyPlayerWidget setHeadWearable(@Nullable Item item) {
    renderState.headEquipment = item == null ? ItemStack.EMPTY : item.getDefaultInstance();
    return this;
  }

  public FancyPlayerWidget setChestWearable(@Nullable Item item) {
    renderState.chestEquipment = item == null ? ItemStack.EMPTY : item.getDefaultInstance();
    return this;
  }

  public FancyPlayerWidget setLegsWearable(@Nullable Item item) {
    renderState.legsEquipment = item == null ? ItemStack.EMPTY : item.getDefaultInstance();
    return this;
  }

  public FancyPlayerWidget setFeetWearable(@Nullable Item item) {
    renderState.feetEquipment = item == null ? ItemStack.EMPTY : item.getDefaultInstance();
    return this;
  }

  public FancyPlayerWidget setHeadWearable(@Nullable ItemStack item) {
    renderState.headEquipment = item == null ? ItemStack.EMPTY : item;
    return this;
  }

  public FancyPlayerWidget setChestWearable(@Nullable ItemStack item) {
    renderState.chestEquipment = item == null ? ItemStack.EMPTY : item;
    return this;
  }

  public FancyPlayerWidget setLegsWearable(@Nullable ItemStack item) {
    renderState.legsEquipment = item == null ? ItemStack.EMPTY : item;
    return this;
  }

  public FancyPlayerWidget setFeetWearable(@Nullable ItemStack item) {
    renderState.feetEquipment = item == null ? ItemStack.EMPTY : item;
    return this;
  }

  private void updateSkin(@Nullable PlayerSkin skin) {
    if (skin != null) {
      renderState.isSlim = skin.model() == PlayerSkin.Model.SLIM;
      renderState.skin = skin;
    } else {
      updateIsSlim(properties.isSlim);
    }
    renderer = renderState.isSlim ? slimRenderer : wideRenderer;
  }

  private void updateIsSlim(boolean isSlim) {
    renderState.isSlim = isSlim;
    renderState.skin = DefaultPlayerSkin.DEFAULT_SKINS[(int) (Math.random() * 9) + (isSlim ? 0 : 9)];
  }

  // TODO: Handle properties correctly when copying a player that is not the local one.

  /**
   * Copies a player from its profile name.
   *
   * @param profileName profile name.
   */
  public void copyPlayer(String profileName) {
    copyPlayer(FancyProfileFetcher.fetchProfile(profileName), profileName);
  }

  /**
   * Copies a player from its UUID.
   *
   * @param profileId profile UUID.
   */
  public void copyPlayer(UUID profileId) {
    copyPlayer(FancyProfileFetcher.fetchProfile(profileId), profileId.toString());
  }

  /**
   * Copies the player from the given profile result.
   *
   * @param result profile result.
   * @param source player identifier.
   */
  private void copyPlayer(CompletableFuture<Optional<GameProfile>> result, String source) {
    result.exceptionally(FancyPlayerWidget::handlePlayerCopyError).thenAccept(profile -> profile.ifPresentOrElse(this::copyPlayer, () -> handlePlayerCopyError(source)));
  }

  /**
   * Copies a player from the specified {@link GameProfile}.
   *
   * @param profile game profile.
   */
  private void copyPlayer(GameProfile profile) {
    Minecraft.getInstance().getSkinManager().getOrLoad(profile).exceptionally(FancyPlayerWidget::handlePlayerCopyError).thenAccept(skin -> {
      renderState.name = profile.getName();
      skin.ifPresentOrElse(value -> renderState.skin = value, () -> handlePlayerCopyError(renderState.name));
      renderState.isSlim = renderState.skin.model() == PlayerSkin.Model.SLIM;
      renderer = renderState.isSlim ? slimRenderer : wideRenderer;
    });
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
    final Rotation headRot = new Rotation();

    final Rotation bodyRot = new Rotation();

    boolean isSlim;

    @NotNull
    String name;

    @Nullable
    PlayerSkin skin;

    // TODO: Handle correctly when player is baby, and choose whether to add flags to show the parrots or use their nullability instead.
    @Nullable
    Parrot.Variant parrotOnLeftShoulder;

    @Nullable
    Parrot.Variant parrotOnRightShoulder;

    private OverridableProperties(@NotNull String name) {
      this.name = name;
    }
  }
}
