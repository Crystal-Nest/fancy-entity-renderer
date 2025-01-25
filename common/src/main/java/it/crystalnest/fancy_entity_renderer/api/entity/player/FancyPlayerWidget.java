package it.crystalnest.fancy_entity_renderer.api.entity.player;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.math.Axis;
import it.crystalnest.fancy_entity_renderer.api.entity.player.state.FancyPlayerRenderState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class FancyPlayerWidget extends AbstractWidget {
  private final FancyPlayerRenderer renderer;

  private static final FancyPlayerRenderer WIDE_RENDERER = new FancyPlayerRenderer(false);

  private static final FancyPlayerRenderer SLIM_RENDERER = new FancyPlayerRenderer(true);

  private final FancyPlayerRenderState renderState = new FancyPlayerRenderState();

  public FancyPlayerWidget(int x, int y, int width, int height) {
    super(x, y, width, height, CommonComponents.EMPTY);
    renderer = DefaultPlayerSkin.get(Minecraft.getInstance().getGameProfile()).model() == PlayerSkin.Model.SLIM ? SLIM_RENDERER : WIDE_RENDERER;
  }

  @Override
  protected void renderWidget(GuiGraphics gfx, int mouseX, int mouseY, float partialTick) {
    updateRenderState(mouseX, mouseY);
    gfx.pose().pushPose();
    gfx.pose().translate(getX() + getWidth() / 2F, getY() + getHeight(), 100);
    float f = getHeight() / 2.125F;
    gfx.pose().scale(f, f, f);
    gfx.pose().translate(0, -0.0625F, 0);
    gfx.pose().rotateAround(Axis.XP.rotationDegrees(renderState.bodyRot.getX()), 0, -1.0625F, 0);
    gfx.pose().mulPose(Axis.YP.rotationDegrees(renderState.bodyRot.getY()));
    gfx.flush();
    Lighting.setupForEntityInInventory(Axis.XP.rotationDegrees(renderState.bodyRot.getX()));
    gfx.drawSpecial(src -> renderer.render(renderState, gfx.pose(), src, 15728880));
    Lighting.setupFor3DItems();
    gfx.pose().popPose();
  }

  public void updateRenderState(int mouseX, int mouseY) {
    if (renderState.bodyFollowsMouse || renderState.headFollowsMouse) {
      // Must rotate around Y axis when mouse moves along X axis and vice versa.
      double yRot = -Math.atan(((getX() + (getX() + getWidth())) / 2.0F - mouseX) / 40) * 20;
      double xRot = -Math.atan(((getY() + (getY() + getHeight())) / 2.0F - mouseY) / 40) * 20;
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

//  public void updateRenderState(@NotNull PlayerRenderState state) {
//    // TODO: Implement copying the local player (name, texture, showCape/showHat/show..., cape texture)
//    // TODO: Implement choosing local texture files (both skin and cape), as well as choosing the texture from a player's name/uuid.
//    state.skin = skin;
//    state.isCrouching = isCrouching;
//    state.parrotOnLeftShoulder = leftShoulderParrot;
//    state.parrotOnRightShoulder = rightShoulderParrot;
////    reusedState.nameTag = this.getNameTag(p_entity);
////    reusedState.nameTagAttachment = new Player().getAttachments().getNullable(EntityAttachment.NAME_TAG, 0, p_entity.getYRot(partialTick));
//    /**
//     * Values below copied from {@link EntityType.Builder#nameTagOffset(float)}
//     */
//    state.nameTag = Component.literal("TEST");
//    state.nameTagAttachment = new Vec3(0.0F, 2.05F, 0.0F);
//    // TODO: Fix name tag. It doesn't render currently.
//    state.customName = null;
//    state.isBaby = isBaby;
//    // TODO: Glowing effect doesn't work. The entity renders the same regardless. Could ignore this, since it was not in the original FancyManu, but it would be nice to have (not even sure this is the right property).
//    state.appearsGlowing = isGlowing;
//    state.isSpectator = false;
//    state.pose = isCrouching ? Pose.CROUCHING : pose;
//    state.isDiscrete = isCrouching;
//    // TODO: Works almost fine, but the item model is kind of transparent to itself.
////    Minecraft.getInstance().getItemModelResolver().updateForTopItem(state.rightHandItem, Items.NETHERITE_SWORD.getDefaultInstance(), ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, false, null, null, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND.ordinal());
//    // TODO: Why is armor not rendered?
////    state.headEquipment = Items.NETHERITE_HELMET.getDefaultInstance();
////    state.chestEquipment = Items.NETHERITE_CHESTPLATE.getDefaultInstance();
//    // TODO: The reason why the flame doesn't render might be the same reason why the name tag doesn't render.
////    state.displayFireAnimation = true;
//    // TODO: Render elytra (if cape has a texture, elytra should be renderer with that texture too).
//  }

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
}


