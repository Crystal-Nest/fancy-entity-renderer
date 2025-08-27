package it.crystalnest.fancy_entity_renderer.api.entity.player;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import it.crystalnest.fancy_entity_renderer.api.entity.player.layer.FancyCapeLayer;
import it.crystalnest.fancy_entity_renderer.api.entity.player.mock.FancyPlayerMock;
import it.crystalnest.fancy_entity_renderer.api.entity.player.model.FancyPlayerModel;
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
  public static final FancyPlayerRenderer SLIM_RENDERER = new FancyPlayerRenderer(true);

  public static final FancyPlayerRenderer WIDE_RENDERER = new FancyPlayerRenderer(false);

  /**
   * Render context.
   */
  private static final EntityRendererProvider.Context RENDER_CONTEXT = new EntityRendererProvider.Context(
    Minecraft.getInstance().getEntityRenderDispatcher(),
    Minecraft.getInstance().getItemRenderer(),
    Minecraft.getInstance().getBlockRenderer(),
    Minecraft.getInstance().getEntityRenderDispatcher().getItemInHandRenderer(),
    Minecraft.getInstance().getResourceManager(),
    Minecraft.getInstance().getEntityModels(),
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
   * @param isSlim whether the player is slim.
   */
  public FancyPlayerRenderer(boolean isSlim) {
    super(RENDER_CONTEXT, isSlim);
    entityRenderDispatcher.overrideCameraOrientation(new Quaternionf());
    entityRenderDispatcher.setRenderShadow(false);
    entityRenderDispatcher.setRenderHitBoxes(false);
    adultModel = new FancyPlayerModel(RENDER_CONTEXT.getModelSet(), isSlim, false);
    babyModel = new FancyPlayerModel(RENDER_CONTEXT.getModelSet(), isSlim, true);
    model = adultModel;
    layers.replaceAll(layer -> switch (layer) {
      case HumanoidArmorLayer<?, ?, ?> l -> new HumanoidArmorLayer<>(
        this,
        new HumanoidArmorModel<>(RENDER_CONTEXT.bakeLayer(isSlim ? ModelLayers.PLAYER_SLIM_INNER_ARMOR : ModelLayers.PLAYER_INNER_ARMOR)),
        new HumanoidArmorModel<>(RENDER_CONTEXT.bakeLayer(isSlim ? ModelLayers.PLAYER_SLIM_OUTER_ARMOR : ModelLayers.PLAYER_OUTER_ARMOR)),
//        new HumanoidArmorModel<>(FancyPlayerModel.getBabyArmorModel(true)),
//        new HumanoidArmorModel<>(FancyPlayerModel.getBabyArmorModel(false)),
        RENDER_CONTEXT.getModelManager()
      );
      case CapeLayer l -> new FancyCapeLayer(this, RENDER_CONTEXT.getModelSet(), RENDER_CONTEXT.getModelManager());
      default -> layer;
    });
  }

  /**
   * Renders the player model.<br>
   * Called after {@link #render(FancyPlayerMock, PoseStack, MultiBufferSource, int)}.
   *
   * @param entity entity to render.
   * @param entityYaw entity yaw.
   * @param partialTicks partial ticks.
   * @param poseStack pose stack.
   * @param bufferSource buffer source.
   * @param packedLight packed light.
   */
  @Override
  public void render(@NotNull AbstractClientPlayer entity, float entityYaw, float partialTicks, @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight) {
    model = ((FancyPlayerMock) entity).isBaby ? babyModel : adultModel;
    extractRenderState();
    super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
  }

  /**
   * Renders the player model.
   *
   * @param poseStack pose stack.
   * @param bufferSource buffer source.
   * @param packedLight packed light.
   */
  public void render(@NotNull FancyPlayerMock entity, @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight) {
    entityRenderDispatcher.overrideCameraOrientation(new Quaternionf().rotateXYZ(-entity.bodyRot.getX(), entity.bodyRot.getY(), -entity.bodyRot.getZ()));
    entityRenderDispatcher.render(entity, 0, 0, 0, 0, 0, poseStack, bufferSource, packedLight);
  }

  /**
   * Renders the player name tag.
   *
   * @param entity entity to render.
   * @param nameTag name tag.
   * @param poseStack pose stack.
   * @param bufferSource buffer source.
   * @param packedLight packed light.
   * @param partialTick partial tick.
   */
  @Override
  protected void renderNameTag(@NotNull AbstractClientPlayer entity, @NotNull Component nameTag, @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight, float partialTick) {
    FancyPlayerMock player = (FancyPlayerMock) entity;
    if (player.showPlayerName && !player.isInvisibleTo(null)) {
      float scale = player.scale * NAMETAG_SCALE;
      Font font = getFont();
      poseStack.pushPose();
      float height = player.isBaby && player.pose != Pose.SPIN_ATTACK ? player.boundingBoxHeight * Player.DEFAULT_BABY_SCALE : player.boundingBoxHeight;
      float offsetY = (player.pose == Pose.SLEEPING || player.pose == Pose.SWIMMING ? player.boundingBoxWidth : height) / scale + (float) player.nameTagAttachment.y;
      float offsetX = player.pose == Pose.SLEEPING ? -(float) player.nameTagAttachment.x : font.width(nameTag) / 2F;
      poseStack.scale(scale, -scale, scale);
      poseStack.translate(-offsetX, -offsetY, 0);
      if (player.isUpsideDown) {
        poseStack.scale(1, -1, 1);
      }
      if (player.deathTime > 1) {
        poseStack.rotateAround(Axis.ZP.rotationDegrees(Math.min(Mth.sqrt((player.deathTime - 1) / 20F * 1.6F), 1) * getFlipDegrees(player)), offsetX, offsetY, 0);
      }
      font.drawInBatch(nameTag, 0, 0, 553648127, false, poseStack.last().pose(), bufferSource, Font.DisplayMode.SEE_THROUGH, (int) (Minecraft.getInstance().options.getBackgroundOpacity(0.25F) * 255) << 24, packedLight);
      font.drawInBatch(nameTag, 0, 0, player.isDiscrete ? 553648127 : -1, false, poseStack.last().pose(), bufferSource, Font.DisplayMode.NORMAL, 0, packedLight);
      poseStack.popPose();
    }
  }

  /**
   * Updates the given render state with data from the given player.<br>
   * Since there is no player entity for this renderer, the render state is updated from the global render state passed in the constructor and retrieved with {@link #state()}.
   *
   * @param entity player entity.
   * @param renderState render state to update.
   * @param partialTick partial tick.
   */
  public void extractRenderState(@Nullable AbstractClientPlayer entity, @NotNull FancyPlayerMock renderState, float partialTick) {
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
      Minecraft.getInstance().getItemModelResolver().updateForTopItem(state.rightHandItem, state.rightHandHeldItem, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, false, null, null, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND.ordinal());
    }
    if (state.leftHandHeldItem != null) {
      Minecraft.getInstance().getItemModelResolver().updateForTopItem(state.leftHandItem, state.leftHandHeldItem, ItemDisplayContext.THIRD_PERSON_LEFT_HAND, true, null, null, ItemDisplayContext.THIRD_PERSON_LEFT_HAND.ordinal());
    }
    renderState.elytraRotX = (float) (Math.PI / 16);
    renderState.elytraRotZ = (float) (Math.PI / 10);
  }

  /**
   * Sets up the model rotations depending on the pose.
   *
   * @param entity entity to render.
   * @param poseStack pose stack.
   * @param bob bob.
   * @param yBodyRot body rotation around the Y axis.
   * @param partialTick partial tick.
   * @param scale render scale.
   */
  @Override
  protected void setupRotations(@NotNull AbstractClientPlayer entity, @NotNull PoseStack poseStack, float bob, float yBodyRot, float partialTick, float scale) {
    FancyPlayerMock player = (FancyPlayerMock) entity;
    if (player.pose == Pose.SPIN_ATTACK) {
      poseStack.mulPose(Axis.XN.rotationDegrees(90));
    }
    super.setupRotations(entity, poseStack, bob, yBodyRot, partialTick, scale);
    if (player.isUpsideDown) {
      if (player.pose == Pose.DYING || player.pose == Pose.SPIN_ATTACK) {
        poseStack.translate(0.0F, (player.boundingBoxHeight + 0.1F) / scale, 0.0F);
        poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
      } else if (player.pose == Pose.SLEEPING) {
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
      }
    }
  }
}
