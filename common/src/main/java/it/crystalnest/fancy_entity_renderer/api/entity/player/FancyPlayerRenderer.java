package it.crystalnest.fancy_entity_renderer.api.entity.player;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import it.crystalnest.fancy_entity_renderer.api.entity.player.layer.FancyCapeLayer;
import it.crystalnest.fancy_entity_renderer.api.entity.player.model.FancyPlayerModel;
import it.crystalnest.fancy_entity_renderer.api.entity.player.state.FancyPlayerRenderState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.HumanoidArmorModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
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
    entityRenderDispatcher.overrideCameraOrientation(new Quaternionf().rotateXYZ(-state().bodyRot.getX(), state().bodyRot.getY(), -state().bodyRot.getZ()));
    // noinspection DataFlowIssue: Entity is null, but it won't get used anyway because extractRenderState was overridden.
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
    if (state.showPlayerName && !state.isInvisibleToPlayer) {
      float scale = state.scale * NAMETAG_SCALE;
      Font font = getFont();
      poseStack.pushPose();
      float height = renderState.isBaby && renderState.pose != Pose.SPIN_ATTACK ? state.boundingBoxHeight * Player.DEFAULT_BABY_SCALE : state.boundingBoxHeight;
      // noinspection DataFlowIssue: nameTagAttachment can't be null, its value is always updated in extractRenderState.
      float offsetY = (state.pose == Pose.SLEEPING || state.pose == Pose.SWIMMING ? state.boundingBoxWidth : height) / scale + (float) state.nameTagAttachment.y;
      float offsetX = state.pose == Pose.SLEEPING ? -(float) state.nameTagAttachment.x : font.width(nameTag) / 2F;
      poseStack.scale(scale, -scale, scale);
      poseStack.translate(-offsetX, -offsetY, 0);
      if (renderState.isUpsideDown) {
        poseStack.scale(1, -1, 1);
      }
      if (renderState.deathTime > 1) {
        poseStack.rotateAround(Axis.ZP.rotationDegrees(Math.min(Mth.sqrt((renderState.deathTime - 1) / 20F * 1.6F), 1) * getFlipDegrees()), offsetX, offsetY, 0);
      }
      font.drawInBatch(nameTag, 0, 0, -2130706433, false, poseStack.last().pose(), bufferSource, Font.DisplayMode.SEE_THROUGH, (int) (Minecraft.getInstance().options.getBackgroundOpacity(0.25F) * 255) << 24, packedLight);
      font.drawInBatch(nameTag, 0, 0, renderState.isDiscrete ? -2130706433 : -1, false, poseStack.last().pose(), bufferSource, Font.DisplayMode.NORMAL, 0, LightTexture.lightCoordsWithEmission(packedLight, 2));
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
    renderState.eyeHeight = Player.POSES.get(state.pose).eyeHeight();
    renderState.isDiscrete = state.isCrouching || state.isInvisible;
    if (state.isMoving && state.pose != Pose.DYING) {
      float step = renderState.speedValue * 0.33F;
      renderState.ageInTicks += step;
      renderState.walkAnimationPos += step;
    } else {
      renderState.ageInTicks = 3000;
      renderState.walkAnimationPos = 0;
    }
    renderState.nameTag = Component.literal(state.name);
    if (state.pose == Pose.SLEEPING) {
      renderState.nameTagAttachment = new Vec3(Player.POSES.get(state.pose).eyeHeight() * state.scale - state.boundingBoxHeight / 1.35F, 0, 0);
    } else {
      renderState.nameTagAttachment = new Vec3(0, 0.25F * state.scale, 0);
    }
    if (state.rightHandHeldItem != null) {
      itemModelResolver.updateForTopItem(state.rightHandItem, state.rightHandHeldItem, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, null, null, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND.ordinal());
    }
    if (state.leftHandHeldItem != null) {
      itemModelResolver.updateForTopItem(state.leftHandItem, state.leftHandHeldItem, ItemDisplayContext.THIRD_PERSON_LEFT_HAND, null, null, ItemDisplayContext.THIRD_PERSON_LEFT_HAND.ordinal());
    }
    renderState.elytraRotX = (float) (Math.PI / 16);
    renderState.elytraRotZ = (float) (Math.PI / 10);
  }

  /**
   * Sets up the model rotations depending on the pose.
   *
   * @param state render state.
   * @param poseStack pose stack.
   * @param bodyRot body rotation around the Y axis.
   * @param scale render scale.
   */
  @Override
  protected void setupRotations(@NotNull PlayerRenderState state, @NotNull PoseStack poseStack, float bodyRot, float scale) {
    if (state.pose == Pose.SPIN_ATTACK) {
      poseStack.mulPose(Axis.XN.rotationDegrees(90));
    }
    super.setupRotations(state, poseStack, bodyRot, scale);
    if (state.isUpsideDown) {
      if (state.pose == Pose.DYING || state.pose == Pose.SPIN_ATTACK) {
        poseStack.translate(0.0F, (state.boundingBoxHeight + 0.1F) / scale, 0.0F);
        poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
      } else if (state.pose == Pose.SLEEPING) {
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
      }
    }
  }
}
