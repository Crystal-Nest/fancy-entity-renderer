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
import net.minecraft.world.entity.Pose;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

public class FancyPlayerRenderer extends PlayerRenderer {
  public static final EntityRendererProvider.Context RENDER_CONTEXT = new EntityRendererProvider.Context(
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

  public FancyPlayerRenderer(FancyPlayerRenderState state, boolean slim) {
    super(RENDER_CONTEXT, slim);
    entityRenderDispatcher.overrideCameraOrientation(new Quaternionf());
    entityRenderDispatcher.setRenderShadow(false);
    entityRenderDispatcher.setRenderHitBoxes(false);
    adultModel = new FancyPlayerModel(slim, false);
    babyModel = new FancyPlayerModel(slim, true);
    model = adultModel;
    reusedState = state;
    // TODO: Define armor model layers correctly (slim, wide, baby, adult). Check out the Zombie renderer.
  }

  @Override
  public void extractRenderState(@Nullable AbstractClientPlayer player, @NotNull PlayerRenderState renderState, float partialTick) {
    FancyPlayerRenderState state = (FancyPlayerRenderState) reusedState;
    // Update fixed properties.
    renderState.customName = null;
//    renderState.ageInTicks += 1; // To use if we implement dynamic player movements. TODO: Check if it stops after some time.
    renderState.ageInTicks = 3000;
    renderState.walkAnimationPos = 0;
    renderState.walkAnimationSpeed = 0;
    renderState.isDiscrete = state.isCrouching;
    // Update properties changed externally.
    renderState.boundingBoxWidth = state.boundingBoxWidth;
    renderState.boundingBoxHeight = state.boundingBoxHeight;
    renderState.scale = state.scale;
    renderState.nameTagAttachment = state.nameTagAttachment;
    // TODO:
    //  STANDING is fine.
    //  FALL_FLYING is to be blacklisted.
    //  SLEEPING needs to be adjusted to center the body, and probably scale depending on width rather than height.
    //  SWIMMING is not doing anything (to be blacklisted if we won't support dynamic player movements).
    //  SPIN_ATTACK is to be blacklisted (if we won't support dynamic player movements).
    //  CROUCHING is fine.
    //  LONG_JUMPING is for Frog, Goat, and Breeze only.
    //  DYING is not doing anything (to be blacklisted).
    //  CROAKING is for Frog only.
    //  USING_TONGUE is for Frog only.
    //  SITTING is for Camel only.
    //  ROARING is for Warden only.
    //  SNIFFING is for Warden and Sniffer only.
    //  EMERGING is for Warden only.
    //  DIGGING is for Warden only.
    //  SLIDING is for Breeze only.
    //  SHOOTING is for Breeze only.
    //  INHALING is for Breeze only.
    renderState.pose = Pose.STANDING;
    // TODO: Camera orientation should be used to move the name tag along with the player body (maybe).
//    entityRenderDispatcher.overrideCameraOrientation(new Quaternionf(-state.bodyRot.getX(), -state.bodyRot.getY(), -state.bodyRot.getZ(), 1));
    renderState.nameTag = Component.literal(state.copyLocalPlayer ? Minecraft.getInstance().getGameProfile().getName() : "Name Tag Test");
    // TODO: Implement copying the local player (texture, showCape/showHat/show..., cape texture)
    // TODO: Implement choosing local texture files (both skin and cape), as well as choosing the texture from a player's name/uuid.
    renderState.skin = state.skin;
    renderState.parrotOnLeftShoulder = state.parrotOnLeftShoulder;
    renderState.parrotOnRightShoulder = state.parrotOnRightShoulder;
    renderState.isBaby = state.isBaby;
    // TODO: Works almost fine, but the item model is kind of transparent to itself.
//    Minecraft.getInstance().getItemModelResolver().updateForTopItem(state.rightHandItem, Items.NETHERITE_SWORD.getDefaultInstance(), ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, false, null, null, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND.ordinal());
    // TODO: Why is armor not rendered?
    renderState.headEquipment = state.headEquipment;
    renderState.chestEquipment = state.chestEquipment;
    // TODO: Render elytra (if cape has a texture, elytra should be renderer with that texture too).
    // TODO: Only makes the body disappear, but maybe it should also make the head transparent. It might be nice to have a flag to choose between "no body, solid head" and "no body, transparent head".
    renderState.isSpectator = state.isSpectator;
    // TODO: Flame is not rendered.
    renderState.displayFireAnimation = state.displayFireAnimation;
    // TODO: Glowing effect doesn't work. The entity renders the same regardless. Could ignore this, since it was not in the original FancyManu, but it would be nice to have (not even sure this is the right property).
    renderState.appearsGlowing = state.appearsGlowing;
  }

  @Override
  public void render(@NotNull PlayerRenderState state, @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight) {
    model = state.isBaby ? babyModel : adultModel;
//    poseStack.mulPose(Axis.ZP.rotationDegrees(180));
    super.render(state, poseStack, bufferSource, packedLight);
  }

  public void render(@NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight) {
    // noinspection DataFlowIssue
    entityRenderDispatcher.render(null, 0, 0, 0, 0, poseStack, bufferSource, packedLight, this);
  }
}
