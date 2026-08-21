package it.crystalnest.fancy_entity_renderer.api.entity.player;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import it.crystalnest.fancy_entity_renderer.api.entity.player.layer.FancyParrotOnShoulderLayer;
import it.crystalnest.fancy_entity_renderer.api.entity.player.mock.FancyPlayerMock;
import it.crystalnest.fancy_entity_renderer.api.entity.player.model.FancyPlayerModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.HumanoidArmorModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.ParrotOnShoulderLayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
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
    Minecraft.getInstance().getItemRenderer(),
    Minecraft.getInstance().getBlockRenderer(),
    Minecraft.getInstance().getEntityRenderDispatcher().getItemInHandRenderer(),
    Minecraft.getInstance().getResourceManager(),
    Minecraft.getInstance().getEntityModels(),
    Minecraft.getInstance().font
  );

  /**
   * Player renderer for slim models.
   */
  public static final FancyPlayerRenderer SLIM_RENDERER = new FancyPlayerRenderer(true);

  /**
   * Player renderer for wide models.
   */
  public static final FancyPlayerRenderer WIDE_RENDERER = new FancyPlayerRenderer(false);

  /**
   * @param isSlim whether the player is slim.
   */
  public FancyPlayerRenderer(boolean isSlim) {
    super(RENDER_CONTEXT, isSlim);
    entityRenderDispatcher.overrideCameraOrientation(new Quaternionf());
    model = new FancyPlayerModel(RENDER_CONTEXT.getModelSet(), isSlim);
    layers.replaceAll(layer -> switch (layer) {
      case HumanoidArmorLayer<?, ?, ?> l -> new HumanoidArmorLayer<>(
        this,
        new HumanoidArmorModel<>(RENDER_CONTEXT.bakeLayer(isSlim ? ModelLayers.PLAYER_SLIM_INNER_ARMOR : ModelLayers.PLAYER_INNER_ARMOR)),
        new HumanoidArmorModel<>(RENDER_CONTEXT.bakeLayer(isSlim ? ModelLayers.PLAYER_SLIM_OUTER_ARMOR : ModelLayers.PLAYER_OUTER_ARMOR)),
        RENDER_CONTEXT.getModelManager()
      );
      case ParrotOnShoulderLayer<AbstractClientPlayer> l -> new FancyParrotOnShoulderLayer(this, RENDER_CONTEXT.getModelSet());
      default -> layer;
    });
  }

  /**
   * Renders the player model.<br>
   * Called after {@link #render(FancyPlayerMock, PoseStack, MultiBufferSource, int)}.
   *
   * @param entity entity to render.
   * @param entityYaw entity yaw.
   * @param partialTick partial tick.
   * @param poseStack pose stack.
   * @param bufferSource buffer source.
   * @param packedLight packed light.
   */
  @Override
  public void render(@NotNull AbstractClientPlayer entity, float entityYaw, float partialTick, @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight) {
    updatePlayer((FancyPlayerMock) entity, partialTick);
    super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
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
    if (player.showPlayerName && !player.isInvisibleToPlayer) {
      float scale = player.scale * NAMETAG_SCALE;
      Font font = getFont();
      poseStack.pushPose();
      Vec3 nameTagAttachment;
      if (player.getPose() == Pose.SLEEPING) {
        nameTagAttachment = new Vec3(Player.POSES.get(player.getPose()).eyeHeight() * player.scale - player.boundingBoxHeight / 1.35F, 0, 0);
      } else {
        nameTagAttachment = new Vec3(0, 0.25F * player.scale, 0);
      }
      float height = player.isBaby && player.getPose() != Pose.SPIN_ATTACK ? player.boundingBoxHeight * Player.DEFAULT_BABY_SCALE : player.boundingBoxHeight;
      float offsetY = (player.getPose() == Pose.SLEEPING || player.getPose() == Pose.SWIMMING ? player.boundingBoxWidth : height) / scale + (float) nameTagAttachment.y;
      float offsetX = player.getPose() == Pose.SLEEPING ? -(float) nameTagAttachment.x : font.width(nameTag) / 2F;
      poseStack.scale(scale, -scale, scale);
      if (player.pinName) {
        poseStack.rotateAround(new Quaternionf().rotateY(player.modelRot.getY()), 0, 0, 0);
      }
      poseStack.translate(-offsetX, -offsetY, 0);
      if (player.isUpsideDown) {
        poseStack.scale(1, -1, 1);
      }
      if (player.deathTime > 1) {
        poseStack.rotateAround(Axis.ZP.rotationDegrees(Math.min(Mth.sqrt((player.deathTime - 1) / 20F * 1.6F), 1) * getFlipDegrees(player)), offsetX, offsetY, 0);
      }
      font.drawInBatch(nameTag, 0, 0, 553648127, false, poseStack.last().pose(), bufferSource, Font.DisplayMode.SEE_THROUGH, (int) (Minecraft.getInstance().options.getBackgroundOpacity(0.25F) * 255) << 24, packedLight);
      font.drawInBatch(nameTag, 0, 0, player.isCrouching() || player.isInvisible() ? 553648127 : -1, false, poseStack.last().pose(), bufferSource, Font.DisplayMode.NORMAL, 0, packedLight);
      poseStack.popPose();
    }
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
    if (player.getPose() == Pose.SPIN_ATTACK) {
      poseStack.mulPose(Axis.XN.rotationDegrees(90));
    }
    super.setupRotations(entity, poseStack, bob, yBodyRot, partialTick, scale);
    if (player.isUpsideDown) {
      if (player.getPose() == Pose.DYING || player.getPose() == Pose.SPIN_ATTACK) {
        poseStack.translate(0.0F, (player.boundingBoxHeight + 0.1F) / scale, 0.0F);
        poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
      } else if (player.getPose() == Pose.SLEEPING) {
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
      }
    }
  }

  /**
   * Renders the player model.
   *
   * @param poseStack pose stack.
   * @param bufferSource buffer source.
   * @param packedLight packed light.
   */
  public void render(@NotNull FancyPlayerMock entity, @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight) {
    if (entityRenderDispatcher.camera == null) {
      entityRenderDispatcher.camera = Minecraft.getInstance().gameRenderer.getMainCamera();
    }
    entityRenderDispatcher.overrideCameraOrientation(new Quaternionf().rotateXYZ(-entity.modelRot.getX(), entity.modelRot.getY(), -entity.modelRot.getZ()));
    entityRenderDispatcher.render(entity, 0, 0, 0, 0, entity.isMoving && !entity.hasPose(Pose.DYING) ? entity.partialTick + entity.speedValue * 0.33F : 0, poseStack, bufferSource, packedLight);
  }

  /**
   * Updates the given player.
   *
   * @param player player to update.
   * @param partialTick partial tick.
   */
  public void updatePlayer(@NotNull FancyPlayerMock player, float partialTick) {
    if (player.isMoving && !player.hasPose(Pose.DYING)) {
      player.partialTick = partialTick;
      if (!(player.hasPose(Pose.SPIN_ATTACK) || player.hasPose(Pose.SLEEPING))) {
        player.walkAnimation.setSpeed(player.walkSpeed);
      } else {
        player.walkAnimation.setSpeed(0);
      }
    } else {
      player.tickCount = 3000;
      player.partialTick = 0;
      player.walkAnimation.setSpeed(0);
    }
  }

  @Override
  protected boolean shouldShowName(@NotNull AbstractClientPlayer entity) {
    return true;
  }
}
