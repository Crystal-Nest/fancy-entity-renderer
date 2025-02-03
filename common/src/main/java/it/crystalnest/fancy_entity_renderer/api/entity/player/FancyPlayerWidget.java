package it.crystalnest.fancy_entity_renderer.api.entity.player;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.math.Axis;
import it.crystalnest.fancy_entity_renderer.Constants;
import it.crystalnest.fancy_entity_renderer.api.FancySessionService;
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
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class FancyPlayerWidget extends AbstractWidget {
  private final FancyPlayerRenderState renderState = new FancyPlayerRenderState();

  private final FancyPlayerRenderer wideRenderer = new FancyPlayerRenderer(renderState, false);

  private final FancyPlayerRenderer slimRenderer = new FancyPlayerRenderer(renderState, true);

  private FancyPlayerRenderer renderer;

  private static FancySessionService fancySessionService() {
    return (FancySessionService) Minecraft.getInstance().getMinecraftSessionService();
  }

  public FancyPlayerWidget(int x, int y, int width, int height) {
    super(x, y, width, height, CommonComponents.EMPTY);
//    setSlim(renderState.isSlim);
//    renderState.displayFireAnimation = true;
    // TODO: Rotations aren't working correctly, and the cape doesn't rotate (also, how come the cape is visible the cape property is not explicitly set?).
    renderState.headFollowsMouse = true;
    renderState.bodyFollowsMouse = true;
//    copyPlayer(fancySessionService().fetchProfile(UUID.fromString("6be8d691-9635-4468-ace3-69a05a4440b6"), false).profile());
    copyPlayer(fancySessionService().fetchProfile("Crystal_Spider_", false).profile());
  }

  @Override
  protected void renderWidget(GuiGraphics gfx, int mouseX, int mouseY, float partialTick) {
    updateRenderState(getX(), getY(), getWidth(), getHeight(), mouseX, mouseY, partialTick);
    gfx.pose().pushPose();
    gfx.pose().translate(getX() + getWidth() / 2F, getY() + getHeight(), 100);
    gfx.flush();
    gfx.pose().scale(1 , -1, 1); // For some reason this renders the flame overlay.
    Lighting.setupForEntityInInventory(Axis.XP.rotationDegrees(renderState.bodyRot.getX()));
    gfx.drawSpecial(src -> renderer.render(gfx.pose(), src, LightTexture.FULL_BRIGHT));
    gfx.flush();
    gfx.pose().popPose();
  }

  @Override
  public void playDownSound(@NotNull SoundManager soundManager) {
    // Disable playing any sound when clicked.
  }

  @Override
  public boolean isActive() {
    return false;
  }

  @Override
  protected void updateWidgetNarration(@NotNull NarrationElementOutput output) {
    // TODO: Maybe add narration for when the player name is visible (what about when the name is visible and the player is crouching?).
  }

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int button) {
    if (button == 0) {
        setSlim(!renderState.isSlim);
        Constants.LOGGER.error("SLIM");
    } else {
      setCopyLocalPlayer(!renderState.copyLocalPlayer);
      Constants.LOGGER.error("COPY");
    }
    return super.mouseClicked(mouseX, mouseY, button);
  }

  public void setSlim(boolean isSlim) {
    if (!renderState.copyLocalPlayer) {
      renderState.isSlim = isSlim;
      renderState.skin = DefaultPlayerSkin.DEFAULT_SKINS[(int) (Math.random() * 9) + (renderState.isSlim ? 0 : 9)];
      renderer = renderState.isSlim ? slimRenderer : wideRenderer;
    }
  }

  public void setCopyLocalPlayer(boolean copyLocalPlayer) {
    renderState.copyLocalPlayer = copyLocalPlayer;
    if (copyLocalPlayer) {
      copyPlayer(Minecraft.getInstance().getGameProfile());
    }
  }

  public void copyPlayer(GameProfile profile) {
    Minecraft.getInstance().getSkinManager().getOrLoad(profile)
      .exceptionally(error -> {
        Constants.LOGGER.error("Copy of player \"{}\" failed!", profile.getName(), error);
        return Optional.of(renderState.skin);
      })
      .thenAccept(skin -> {
        renderState.skin = skin.orElse(renderState.skin);
        renderState.isSlim = renderState.skin.model() == PlayerSkin.Model.SLIM;
        renderer = renderState.isSlim ? slimRenderer : wideRenderer;
      });
  }

  public void updateRenderState(int x, int y, int width, int height, int mouseX, int mouseY, float partialTick) {
    renderState.boundingBoxWidth = width;
    renderState.boundingBoxHeight = height;
    // 1.875 is the rendered height (1.8 is the hitbox height).
    renderState.scale = height / 1.875F;
    if (renderState.bodyFollowsMouse || renderState.headFollowsMouse) {
      // Must rotate around Y axis when mouse moves along X axis and vice versa.
      double xRot = -Math.atan(((y + y + height) / 2F - mouseY) / 40) * 20;
      double yRot = -Math.atan(((x + x + width) / 2F - mouseX) / 40) * 20;
      if (renderState.isUpsideDown) {
        xRot = -xRot;
        yRot = -yRot;
      }
      // TODO: The rotations above are calculated based on the size of the bounding rectangle, meaning the adult head Y center is lower than it should be, and both baby body and head Y centers are higher than they should be.
      //       Rather than on the bounding rectangle, the rotations should be calculated separately for head and body depending on their actual sizes and positions (what happens with Poses other than Pose.STANDING?).
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
    renderState.nameTagAttachment = new Vec3(0, (height + (20.5 * height / 120)), 0);
  }
}
