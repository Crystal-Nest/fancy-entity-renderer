package it.crystalnest.fancy_entity_renderer.api.entity.player;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
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
import net.minecraft.world.entity.Pose;
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

  public FancyPlayerRenderState state;

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
    state = new FancyPlayerRenderState();
    return state;
  }

  @Override
  public void extractRenderState(@Nullable AbstractClientPlayer player, @NotNull PlayerRenderState state, float partialTick) {
    // Prevent changing the inner render state.
    // TODO: Maybe we should update the state here?
  }

  @Override
  public void render(@NotNull PlayerRenderState state, @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight) {
    model = state.isBaby ? babyModel : adultModel;
    poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
    super.render(state, poseStack, bufferSource, packedLight);
  }

  public void render(@NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight) {
    entityRenderDispatcher.render(null, 0, 0, 0, 0, poseStack, bufferSource, packedLight, this);
  }

  public void updateRenderState(int x, int y, int width, int height, int mouseX, int mouseY, float partialTick) {
    state.boundingBoxWidth = width;
    state.boundingBoxHeight = height;
    state.ageInTicks = 3000;
    state.walkAnimationPos = 0;
    state.walkAnimationSpeed = 0;
    state.isCrouching = false;
    state.isDiscrete = false;
    state.pose = Pose.STANDING;
    state.appearsGlowing = true;
    // TODO: The divisors below are probably due to the entity proportions, might be better to derive them from something rather than using magic numbers.
    state.scale = Math.min(width / 0.875F, height / 1.875F);
    if (state.bodyFollowsMouse || state.headFollowsMouse) {
      // Must rotate around Y axis when mouse moves along X axis and vice versa.
      double xRot = -Math.atan(((y + y + height) / 2F - mouseY) / 40) * 20;
      double yRot = -Math.atan(((x + x + width) / 2F - mouseX) / 40) * 20;
      if (state.isUpsideDown) {
        xRot = -xRot;
        yRot = -yRot;
      }
      // TODO: The rotations above are calculated based on the size of the bounding rectangle, meaning the adult head Y center is lower than it should be, and both baby body and head Y centers are higher than they should be.
      //       Rather than on the bounding rectangle, the rotations should be calculated separately for head and body depending on their actual sizes and positions (what happens with Poses other than Pose.STANDING?).
      if (state.bodyFollowsMouse) {
        state.bodyRot.setXDeg(xRot);
        state.bodyRot.setYDeg(yRot);
        state.bodyRot.setZ(0);
      } else {
//      state.bodyRot.setXDeg(RotationDegreesSetByTheUser);
//      state.bodyRot.setYDeg(RotationDegreesSetByTheUser);
//      state.bodyRot.setZDeg(RotationDegreesSetByTheUser);
      }
      if (state.headFollowsMouse) {
        state.headRot.setXDeg(xRot);
        state.headRot.setYDeg(yRot);
        state.headRot.setZ(0);
      } else {
//      state.headRot.setXDeg(RotationDegreesSetByTheUser);
//      state.headRot.setYDeg(RotationDegreesSetByTheUser);
//      state.headRot.setZDeg(RotationDegreesSetByTheUser);
      }
    }

    // TODO: Camera orientation should be used to move the name tag along with the player body (maybe).
//    entityRenderDispatcher.overrideCameraOrientation(new Quaternionf(-state.bodyRot.getX(), -state.bodyRot.getY(), -state.bodyRot.getZ(), 1));
    state.nameTag = Component.literal(state.copyLocalPlayer ? Minecraft.getInstance().getGameProfile().getName() : "Name Tag Test");
    state.nameTagAttachment = new Vec3(0, -(height + (20.5 * height / 120)), 0);

    // TODO: Implement copying the local player (texture, showCape/showHat/show..., cape texture)
    // TODO: Implement choosing local texture files (both skin and cape), as well as choosing the texture from a player's name/uuid.
//    state.skin = skin;
//    state.isCrouching = isCrouching;
//    state.parrotOnLeftShoulder = leftShoulderParrot;
//    state.parrotOnRightShoulder = rightShoulderParrot;
    // TODO: Fix name tag. It doesn't render currently.
//    state.customName = null;
//    state.isBaby = isBaby;
    // TODO: Glowing effect doesn't work. The entity renders the same regardless. Could ignore this, since it was not in the original FancyManu, but it would be nice to have (not even sure this is the right property).
//    state.appearsGlowing = isGlowing;
//    state.isSpectator = false;
//    state.pose = isCrouching ? Pose.CROUCHING : pose;
//    state.isDiscrete = isCrouching;
    // TODO: Works almost fine, but the item model is kind of transparent to itself.
//    Minecraft.getInstance().getItemModelResolver().updateForTopItem(state.rightHandItem, Items.NETHERITE_SWORD.getDefaultInstance(), ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, false, null, null, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND.ordinal());
    // TODO: Why is armor not rendered?
//    state.headEquipment = Items.NETHERITE_HELMET.getDefaultInstance();
//    state.chestEquipment = Items.NETHERITE_CHESTPLATE.getDefaultInstance();
    // TODO: Render elytra (if cape has a texture, elytra should be renderer with that texture too).
    // TODO: Only makes the body disappear, but maybe it should also make the head transparent. It might be nice to have a flag to choose between "no body, solid head" and "no body, transparent head".
//    state.isSpectator = true;
    // TODO: Flame is not rendered.
    state.displayFireAnimation = true;
  }
}
