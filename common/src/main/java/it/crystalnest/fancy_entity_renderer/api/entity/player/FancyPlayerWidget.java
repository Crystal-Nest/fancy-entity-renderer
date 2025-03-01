package it.crystalnest.fancy_entity_renderer.api.entity.player;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.yggdrasil.ProfileResult;
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
import net.minecraft.world.item.Items;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Optional;
import java.util.UUID;

public class FancyPlayerWidget extends AbstractWidget {
  private final FancyPlayerRenderState renderState = new FancyPlayerRenderState();

  private final FancyPlayerRenderer wideRenderer = new FancyPlayerRenderer(renderState, false);

  private final FancyPlayerRenderer slimRenderer = new FancyPlayerRenderer(renderState, true);

  private FancyPlayerRenderer renderer;

  public FancyPlayerWidget(int x, int y, int width, int height) {
    super(x, y, width, height, CommonComponents.EMPTY);
    renderer = renderState.isSlim ? slimRenderer : wideRenderer;
    // TODO: Flames are too wide, tall, and "in front".
//    renderState.displayFireAnimation = true;
    renderState.headFollowsMouse = true;
    renderState.bodyFollowsMouse = true;
    renderState.showPlayerName = true;
    renderState.isBaby = true;
    copyPlayer(Minecraft.getInstance().getGameProfile());
    copyPlayer("Crystal_Spider_");
//    copyPlayer(UUID.fromString("6be8d691-9635-4468-ace3-69a05a4440b6"));

//    loadSkinTexture(b64Skin);
//    Constants.LOGGER.info(Minecraft.getInstance().gameDirectory.getPath());
  }

  private static FancySessionService fancySessionService() {
    return (FancySessionService) Minecraft.getInstance().getMinecraftSessionService();
  }

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
        renderState.name = profile.getName();
        renderState.skin = skin.orElse(renderState.skin);
        renderState.isSlim = renderState.skin.model() == PlayerSkin.Model.SLIM;
        renderer = renderState.isSlim ? slimRenderer : wideRenderer;
      });
  }

  public void copyPlayer(String profileName) {
    copyPlayer(fancySessionService().fetchProfile(profileName, false), profileName);
  }

  public void copyPlayer(UUID profileId) {
    copyPlayer(fancySessionService().fetchProfile(profileId, false), profileId.toString());
  }

  public void copyPlayer(@Nullable ProfileResult result, String source) {
    if (result != null) {
      copyPlayer(result.profile());
    } else {
      Constants.LOGGER.error("Copy of player {} failed!", source);
    }
  }

  public static File defaultCacheDir;

  public static File getCacheDir() {
    return defaultCacheDir == null ? new File(Minecraft.getInstance().gameDirectory, "assets/skins") : defaultCacheDir;
  }

  public void loadSkinTexture(String skinBase64) {
    String fileUrl = parseBase64SkinTexture(b64Skin);

    Constants.LOGGER.info(fileUrl);

//    Minecraft.getInstance().getSkinManager().getInsecureSkin(new GameProfile())
//      .exceptionally(error -> {
//        Constants.LOGGER.error("Copy of player \"{}\" failed!", profile.getName(), error);
//        return Optional.of(renderState.skin);
//      })
//      .thenAccept(skin -> {
//        renderState.name = profile.getName();
//        renderState.skin = skin.orElse(renderState.skin);
//        renderState.isSlim = renderState.skin.model() == PlayerSkin.Model.SLIM;
//        renderer = renderState.isSlim ? slimRenderer : wideRenderer;
//      });

  }

  String b64Skin = "ewogICJ0aW1lc3RhbXAiIDogMTYzODk4MDIzNzMzMywKICAicHJvZmlsZUlkIiA6ICJiN2ZkYmU2N2NkMDA0NjgzYjlmYTllM2UxNzczODI1NCIsCiAgInByb2ZpbGVOYW1lIiA6ICJDVUNGTDE0IiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzlhOWFjNGFjNDUwNTRiZTk0ZTMzZDFhYzI2ZjE5NDg5ZDIwZTA5ZjQ5MjgwMzljOWFhYWIxNmI2ZGU0YTdiOGYiCiAgICB9CiAgfQp9";


  public static String parseBase64SkinTexture(String base64) {
    byte[] skinBytes = Base64.decodeBase64(base64);
    String hash = DigestUtils.sha256Hex(skinBytes);
    File cacheFile =  new File(new File(getCacheDir(), hash.length() > 2 ? hash.substring(0, 2) : "xx"), hash);
    //Save base64 image to cache file
    try {
      FileUtils.writeByteArrayToFile(cacheFile, skinBytes);
      return hash;
    } catch (Exception e) {
      return null;
    }
  }

  public void updateRenderState(int x, int y, int width, int height, int mouseX, int mouseY, float partialTick) {
    renderState.boundingBoxWidth = width;
    renderState.boundingBoxHeight = height;
    // 1.875 is the rendered height (1.8 is the hitbox height).
    renderState.scale = height / 1.875F;
    if (renderState.bodyFollowsMouse || renderState.headFollowsMouse) {
      // Must rotate around Y axis when mouse moves along X axis and vice versa.

//      float adultHeight = 1.875F; // Height of an adult player
//      float adultEyeHeight = 1.62F; // Eye level for an adult
//      float babyHeight = 0.9375F; // Height of a baby player
//      float babyEyeHeight = 0.81F; // Eye level for a baby

      float modelHeight = 1.875F;
      float modelEye = 0.255F; // 1.875F - 0.255F = 1.62F it works (internet says eyes are at 1.62m and player height 1.875m)
      // When player is tall
      // 0.81 = 1.62F - 0.81 eye
      float eyeY = renderState.isBaby ? y + (height / 2F) + (0.1275F * height / 2 / 0.9375F) : y + 0.255F * height / 1.875F;

      // this is always the middle of the width, so is invariant to resizing
      float eyeX = (x + width / 2F);
      double mouseXRelative = mouseX - eyeX;
      double mouseYRelative = mouseY - eyeY;
      double xRot = Math.atan(mouseYRelative / 40F) * 20;
      double yRot = -Math.atan(mouseXRelative / 40F) * 20;

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
    renderState.rightHandHeldItem = Items.NETHERITE_SWORD;
    renderState.leftHandHeldItem = Items.OAK_TRAPDOOR;
//    renderState.headEquipment = Items.NETHERITE_HELMET.getDefaultInstance();
//    renderState.chestEquipment = Items.ELYTRA.getDefaultInstance();
//    renderState.legsEquipment = Items.LEATHER_LEGGINGS.getDefaultInstance();
//    renderState.feetEquipment = Items.GOLDEN_BOOTS.getDefaultInstance();
  }
}
