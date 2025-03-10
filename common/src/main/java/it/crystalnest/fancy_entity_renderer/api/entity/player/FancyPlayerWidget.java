package it.crystalnest.fancy_entity_renderer.api.entity.player;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.math.Axis;
import it.crystalnest.fancy_entity_renderer.Constants;
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
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

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
  private FancyPlayerRenderer renderer;

  /**
   * @param x x coordinate on the screen.
   * @param y y coordinate on the screen.
   * @param width widget width.
   * @param height widget height.
   */
  public FancyPlayerWidget(int x, int y, int width, int height) {
    super(x, y, width, height, CommonComponents.EMPTY);
    renderer = renderState.isSlim ? slimRenderer : wideRenderer;
    // TODO: Flames are too wide, tall, and "in front".
//    renderState.displayFireAnimation = true;
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

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int button) {
    // TODO: Remove.
    if (button == 0) {
      setSlim(!renderState.isSlim);
    } else {
      renderState.isBaby = !renderState.isBaby;
    }
    return super.mouseClicked(mouseX, mouseY, button);
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
   * Returns whether the widget is active.
   *
   * @return whether the widget is active.
   */
  @Override
  public boolean isActive() {
    return false;
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
   * Makes the player slim or wide.
   *
   * @param isSlim whether the player should be slim.
   */
  public void setSlim(boolean isSlim) {
    if (!renderState.copyLocalPlayer) {
      renderState.isSlim = isSlim;
      renderState.skin = DefaultPlayerSkin.DEFAULT_SKINS[(int) (Math.random() * 9) + (renderState.isSlim ? 0 : 9)];
      renderer = renderState.isSlim ? slimRenderer : wideRenderer;
    }
  }

  /**
   * Sets a custom skin for the player.
   *
   * @param skin {@link PlayerSkin}.
   */
  public void setSkin(PlayerSkin skin) {
    if (!renderState.copyLocalPlayer) {
      renderState.isSlim = skin.model() == PlayerSkin.Model.SLIM;
      renderState.skin = skin;
      renderer = renderState.isSlim ? slimRenderer : wideRenderer;
    }
  }

  /**
   * Makes the player copy the local player or not.
   *
   * @param copyLocalPlayer whether to copy the local player.
   */
  public void setCopyLocalPlayer(boolean copyLocalPlayer) {
    renderState.copyLocalPlayer = copyLocalPlayer;
    if (copyLocalPlayer) {
      copyPlayer(Minecraft.getInstance().getGameProfile());
    }
  }

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
    result.exceptionally(this::handlePlayerCopyError).thenAccept(profile -> profile.ifPresentOrElse(this::copyPlayer, () -> handlePlayerCopyError(source)));
  }

  /**
   * Copies a player from the specified {@link GameProfile}.
   *
   * @param profile game profile.
   */
  private void copyPlayer(GameProfile profile) {
    Minecraft.getInstance().getSkinManager().getOrLoad(profile).exceptionally(this::handlePlayerCopyError).thenAccept(skin -> {
      renderState.name = profile.getName();
      skin.ifPresentOrElse(value -> renderState.skin = value, () -> handlePlayerCopyError(renderState.name));
      renderState.isSlim = renderState.skin.model() == PlayerSkin.Model.SLIM;
      renderer = renderState.isSlim ? slimRenderer : wideRenderer;
    });
  }

  /**
   * Handles errors happening when trying to fetch user profiles.
   *
   * @param error error.
   * @return {@link Optional#empty()} to delegate value handling to the caller.
   * @param <T> expected return value type.
   */
  private <T> Optional<T> handlePlayerCopyError(Throwable error) {
    Constants.LOGGER.error("Copy of player failed with error!", error);
    return Optional.empty();
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
      } else {
//      renderState.bodyRot.setXDeg(RotationDegreesSetByTheUser);
//      renderState.bodyRot.setYDeg(RotationDegreesSetByTheUser);
//      renderState.bodyRot.setZDeg(RotationDegreesSetByTheUser);
      }
      if (renderState.headFollowsMouse) {
        renderState.headRot.setXDeg(xRot);
        renderState.headRot.setYDeg(yRot);
        renderState.headRot.setZ(0);
      } else {
//      renderState.headRot.setXDeg(RotationDegreesSetByTheUser);
//      renderState.headRot.setYDeg(RotationDegreesSetByTheUser);
//      renderState.headRot.setZDeg(RotationDegreesSetByTheUser);
      }
    }
//    renderState.rightHandHeldItem = Items.NETHERITE_SWORD;
//    renderState.leftHandHeldItem = Items.OAK_TRAPDOOR;
//    renderState.headEquipment = Items.NETHERITE_HELMET.getDefaultInstance();
//    renderState.chestEquipment = Items.ELYTRA.getDefaultInstance();
//    renderState.legsEquipment = Items.LEATHER_LEGGINGS.getDefaultInstance();
//    renderState.feetEquipment = Items.GOLDEN_BOOTS.getDefaultInstance();
  }
}
