package it.crystalnest.fancy_entity_renderer.api.entity.player;

import com.mojang.blaze3d.vertex.PoseStack;
import it.crystalnest.fancy_entity_renderer.api.entity.player.layer.FancyCapeLayer;
import it.crystalnest.fancy_entity_renderer.api.entity.player.model.FancyPlayerModel;
import it.crystalnest.fancy_entity_renderer.api.entity.player.state.FancyPlayerRenderState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.HumanoidArmorModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.util.CommonColors;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

/**
 * Custom player renderer.
 */
public class FancyPlayerRenderer extends PlayerRenderer {
  /**
   * Render context.
   */
  private static final EntityRendererProvider.Context RENDER_CONTEXT = new EntityRendererProvider.Context(
    Minecraft.getInstance().getEntityRenderDispatcher(),
    Minecraft.getInstance().getItemModelResolver(),
    Minecraft.getInstance().getMapRenderer(),
    Minecraft.getInstance().getBlockRenderer(),
    Minecraft.getInstance().getResourceManager(),
    Minecraft.getInstance().getEntityModels(),
    Minecraft.getInstance().getEntityRenderDispatcher().equipmentAssets,
    Minecraft.getInstance().font
  );

  /**
   * Adult player model.
   */
  private final FancyPlayerModel adultModel;

  /**
   * Baby player model.
   */
  private final FancyPlayerModel babyModel;

  /**
   * @param state global render state.
   * @param isSlim whether the player is slim.
   */
  public FancyPlayerRenderer(FancyPlayerRenderState state, boolean isSlim) {
    super(RENDER_CONTEXT, isSlim);
    entityRenderDispatcher.overrideCameraOrientation(new Quaternionf());
    entityRenderDispatcher.setRenderShadow(false);
    entityRenderDispatcher.setRenderHitBoxes(false);
    adultModel = new FancyPlayerModel(RENDER_CONTEXT.getModelSet(), isSlim, false);
    babyModel = new FancyPlayerModel(RENDER_CONTEXT.getModelSet(), isSlim, true);
    model = adultModel;
    reusedState = state;
    layers.replaceAll(layer -> switch (layer) {
      case HumanoidArmorLayer<?, ?, ?> l -> new HumanoidArmorLayer<>(
        this,
        new HumanoidArmorModel<>(RENDER_CONTEXT.bakeLayer(isSlim ? ModelLayers.PLAYER_SLIM_INNER_ARMOR : ModelLayers.PLAYER_INNER_ARMOR)),
        new HumanoidArmorModel<>(RENDER_CONTEXT.bakeLayer(isSlim ? ModelLayers.PLAYER_SLIM_OUTER_ARMOR : ModelLayers.PLAYER_OUTER_ARMOR)),
        new HumanoidArmorModel<>(FancyPlayerModel.getBabyArmorModel(true)),
        new HumanoidArmorModel<>(FancyPlayerModel.getBabyArmorModel(false)),
        RENDER_CONTEXT.getEquipmentRenderer()
      );
      case CapeLayer l -> new FancyCapeLayer(this, RENDER_CONTEXT.getModelSet(), RENDER_CONTEXT.getEquipmentAssets());
      default -> layer;
    });
  }

  /**
   * Returns the current state as a {@link FancyPlayerRenderState}.
   *
   * @return current render state.
   */
  private FancyPlayerRenderState state() {
    return (FancyPlayerRenderState) reusedState;
  }

  /**
   * Renders the player model.<br>
   * Called after {@link #render(PoseStack, MultiBufferSource, int)}.
   *
   * @param state render state.
   * @param poseStack pose stack.
   * @param bufferSource buffer source.
   * @param packedLight packed light.
   */
  @Override
  public void render(@NotNull PlayerRenderState state, @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight) {
    model = state.isBaby ? babyModel : adultModel;
    super.render(state, poseStack, bufferSource, packedLight);
  }

  /**
   * Renders the player model.
   *
   * @param poseStack pose stack.
   * @param bufferSource buffer source.
   * @param packedLight packed light.
   */
  public void render(@NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight) {
    poseStack.rotateAround(new Quaternionf().rotateX(state().bodyRot.getX()).rotateY(-state().bodyRot.getY()).rotateZ(state().bodyRot.getZ()), 0, 0, 0);
    // Entity is null, but it won't get used anyway because extractRenderState was overridden.
    // noinspection DataFlowIssue
    entityRenderDispatcher.render(null, 0, 0, 0, 0, poseStack, bufferSource, packedLight, this);
  }

  /**
   * Renders the player name tag.
   *
   * @param renderState render state.
   * @param nameTag name tag.
   * @param poseStack pose stack.
   * @param bufferSource buffer source.
   * @param packedLight packed light.
   */
  @Override
  protected void renderNameTag(@NotNull PlayerRenderState renderState, @NotNull Component nameTag, @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight) {
    FancyPlayerRenderState state = state();
    if (state.showPlayerName) {
      float scale = state.scale * NAMETAG_SCALE;
      Font font = getFont();
      poseStack.pushPose();
      // nameTagAttachment can't be null, its value is always update in extractRenderState.
      // noinspection DataFlowIssue
      poseStack.translate(state.nameTagAttachment);
      poseStack.scale(scale, -scale, scale);
      font.drawInBatch(
        nameTag,
        -font.width(nameTag) / 2F,
        -state.boundingBoxHeight / scale,
        state.isDiscrete ? -2130706433 : CommonColors.WHITE,
        false,
        poseStack.last().pose(),
        bufferSource,
        Font.DisplayMode.NORMAL,
        (int) (Minecraft.getInstance().options.getBackgroundOpacity(0.25F) * 255) << 24,
        packedLight
      );
      poseStack.popPose();
    }
  }

  /**
   * Updates the given render state with data from the given player.<br>
   * Since there is no player entity for this renderer, the render state is updated from the global render state passed in the constructor and retrieved with {@link #state()}.
   *
   * @param player player entity (always {@code null}).
   * @param renderState render state to update.
   * @param partialTick partial tick.
   */
  @Override
  public void extractRenderState(@Nullable AbstractClientPlayer player, @NotNull PlayerRenderState renderState, float partialTick) {
    FancyPlayerRenderState state = state();
    // Update fixed properties.
//    renderState.ageInTicks += 1; // To use if we implement dynamic player movements.
    renderState.ageInTicks = 3000;
    renderState.walkAnimationPos = 0;
    renderState.walkAnimationSpeed = 0;
    renderState.eyeHeight = Player.DEFAULT_EYE_HEIGHT;
    renderState.isDiscrete = state.isCrouching;
    // Update properties changed externally.
    renderState.boundingBoxWidth = state.boundingBoxWidth;
    renderState.boundingBoxHeight = state.boundingBoxHeight;
    renderState.scale = state.scale;
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
    renderState.nameTag = Component.literal(state.name);
    renderState.nameTagAttachment = new Vec3(0, 0.5F * state.scale, 0);
    // TODO: Implement choosing local texture files (both skin and cape), as well as choosing the texture from a player's name/uuid.
    renderState.skin = state.skin;
    renderState.parrotOnLeftShoulder = state.parrotOnLeftShoulder;
    renderState.parrotOnRightShoulder = state.parrotOnRightShoulder;
    renderState.isBaby = state.isBaby;
    if (state.rightHandHeldItem != null) {
      Minecraft.getInstance().getItemModelResolver().updateForTopItem(state.rightHandItem, state.rightHandHeldItem.getDefaultInstance(), ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, false, null, null, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND.ordinal());
    }
    if (state.leftHandHeldItem != null) {
      Minecraft.getInstance().getItemModelResolver().updateForTopItem(state.leftHandItem, state.leftHandHeldItem.getDefaultInstance(), ItemDisplayContext.THIRD_PERSON_LEFT_HAND, true, null, null, ItemDisplayContext.THIRD_PERSON_LEFT_HAND.ordinal());
    }
    renderState.headEquipment = state.headEquipment;
    renderState.chestEquipment = state.chestEquipment;
    renderState.legsEquipment = state.legsEquipment;
    renderState.feetEquipment = state.feetEquipment;
    // TODO: Only makes the body disappear, but maybe it should also make the head transparent. It might be nice to have a flag to choose between "no body, solid head" and "no body, transparent head".
    renderState.isSpectator = state.isSpectator;
    // TODO: Glowing effect doesn't work. The entity renders the same regardless. Could ignore this, since it was not in the original FancyManu, but it would be nice to have (not even sure this is the right property).
    renderState.appearsGlowing = state.appearsGlowing;
    renderState.displayFireAnimation = state.displayFireAnimation;
    renderState.elytraRotX = (float) (Math.PI / 16);
//    renderState.elytraRotY = (float) (Math.PI / 2);
    renderState.elytraRotZ = (float) (Math.PI / 10);
  }
}
