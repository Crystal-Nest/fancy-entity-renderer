package it.crystalnest.fancy_entity_renderer.api.entity.player;

import com.mojang.blaze3d.vertex.PoseStack;
import it.crystalnest.fancy_entity_renderer.api.entity.player.model.FancyPlayerModel;
import it.crystalnest.fancy_entity_renderer.api.entity.player.state.FancyPlayerRenderState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.client.resources.model.EquipmentAssetManager;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

public class FancyPlayerRenderer extends PlayerRenderer {
  private static final EntityRendererProvider.Context RENDER_CONTEXT = new EntityRendererProvider.Context(
    Minecraft.getInstance().getEntityRenderDispatcher(),
    Minecraft.getInstance().getItemModelResolver(),
    Minecraft.getInstance().getMapRenderer(),
    Minecraft.getInstance().getBlockRenderer(),
    Minecraft.getInstance().getResourceManager(),
    Minecraft.getInstance().getEntityModels(),
    new EquipmentAssetManager(),
    Minecraft.getInstance().font
  );

  private final FancyPlayerModel adultModel;

  private final FancyPlayerModel babyModel;

  public FancyPlayerRenderState renderState;

  public FancyPlayerRenderer(boolean slim) {
    super(RENDER_CONTEXT, slim);
    entityRenderDispatcher.overrideCameraOrientation(new Quaternionf());
    entityRenderDispatcher.setRenderShadow(false);
    entityRenderDispatcher.setRenderHitBoxes(false);
    adultModel = new FancyPlayerModel(slim, false);
    babyModel = new FancyPlayerModel(slim, true);
    model = adultModel;
    // TODO: Define armor model layers correctly (slim, wide, baby, adult). Check out the Zombie renderer.
  }

  @NotNull
  @Override
  public FancyPlayerRenderState createRenderState() {
    renderState = new FancyPlayerRenderState();
    return renderState;
  }

  @Override
  public void extractRenderState(@Nullable AbstractClientPlayer player, @NotNull PlayerRenderState renderState, float partialTick) {
    // Prevent changing the inner render state.
    // TODO: Maybe we should update the renderState here?
  }

  @Override
  public void render(@NotNull PlayerRenderState state, @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight) {
    model = state.isBaby ? babyModel : adultModel;
    super.render(state, poseStack, bufferSource, packedLight);
  }

  public void render(@NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight) {
    entityRenderDispatcher.render(null, 0, 0, 0, 0, poseStack, bufferSource, packedLight, this);
  }

  public void updateRenderState(int x, int y, int width, int height, int mouseX, int mouseY, float partialTick) {
//    renderState.boundingBoxWidth = width;
//    renderState.boundingBoxHeight = -1;
//    renderState.ageInTicks += 1;
    renderState.scale = Math.min(width / 0.875F, height / 1.875F);
    // TODO: The divisors below are probably due to the entity proportions, might be better to derive them from something rather than using magic numbers.
    if (renderState.bodyFollowsMouse || renderState.headFollowsMouse) {
      // Must rotate around Y axis when mouse moves along X axis and vice versa.
      double xRot = -Math.atan(((y + y + height) / 2F - mouseY) / 40) * 20;
      double yRot = -Math.atan(((x + x + width) / 2F - mouseX) / 40) * 20;
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

    renderState.nameTag = Component.literal(renderState.copyLocalPlayer ? Minecraft.getInstance().getGameProfile().getName() : "Name Tag Test");
    // TODO: Offset from height should scale depending on the size value.
    renderState.nameTagAttachment = new Vec3(0, -(height + 20.5), 0);

    // TODO: Implement copying the local player (texture, showCape/showHat/show..., cape texture)
    // TODO: Implement choosing local texture files (both skin and cape), as well as choosing the texture from a player's name/uuid.
//    renderState.skin = skin;
//    renderState.isCrouching = isCrouching;
//    renderState.parrotOnLeftShoulder = leftShoulderParrot;
//    renderState.parrotOnRightShoulder = rightShoulderParrot;
    // TODO: Fix name tag. It doesn't render currently.
//    renderState.customName = null;
//    renderState.isBaby = isBaby;
    // TODO: Glowing effect doesn't work. The entity renders the same regardless. Could ignore this, since it was not in the original FancyManu, but it would be nice to have (not even sure this is the right property).
//    renderState.appearsGlowing = isGlowing;
//    renderState.isSpectator = false;
//    renderState.pose = isCrouching ? Pose.CROUCHING : pose;
//    renderState.isDiscrete = isCrouching;
    // TODO: Works almost fine, but the item model is kind of transparent to itself.
//    Minecraft.getInstance().getItemModelResolver().updateForTopItem(renderState.rightHandItem, Items.NETHERITE_SWORD.getDefaultInstance(), ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, false, null, null, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND.ordinal());
    // TODO: Why is armor not rendered?
//    renderState.headEquipment = Items.NETHERITE_HELMET.getDefaultInstance();
//    renderState.chestEquipment = Items.NETHERITE_CHESTPLATE.getDefaultInstance();
    // TODO: Render elytra (if cape has a texture, elytra should be renderer with that texture too).
    // TODO: Only makes the body disappear, but maybe it should also make the head transparent. It might be nice to have a flag to choose between "no body, solid head" and "no body, transparent head".
//    renderState.isSpectator = true;
    // TODO: Flame is not rendered.
    renderState.displayFireAnimation = false;
  }
}
