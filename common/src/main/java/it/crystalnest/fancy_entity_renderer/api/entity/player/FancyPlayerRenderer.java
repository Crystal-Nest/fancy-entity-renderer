package it.crystalnest.fancy_entity_renderer.api.entity.player;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import it.crystalnest.fancy_entity_renderer.api.entity.player.layer.FancyCapeLayer;
import it.crystalnest.fancy_entity_renderer.api.entity.player.model.FancyPlayerModel;
import it.crystalnest.fancy_entity_renderer.api.entity.player.state.FancyPlayerRenderState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.ArmorModelSet;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

/**
 * Custom player renderer.
 */
public class FancyPlayerRenderer extends AvatarRenderer<AbstractClientPlayer> {
  /**
   * Adult player model.
   */
  private final FancyPlayerModel adultModel;

  /**
   * Baby player model.
   */
  private final FancyPlayerModel babyModel;

  /**
   * @param context render context.
   * @param isSlim whether the player is slim.
   */
  public FancyPlayerRenderer(EntityRendererProvider.Context context, boolean isSlim) {
    super(context, isSlim);
    adultModel = new FancyPlayerModel(context.getModelSet(), isSlim, false);
    babyModel = new FancyPlayerModel(context.getModelSet(), isSlim, true);
    model = adultModel;
    layers.replaceAll(layer -> switch (layer) {
      case HumanoidArmorLayer<?, ?, ?> l -> new HumanoidArmorLayer<>(
        this,
        ArmorModelSet.bake(
          isSlim ? ModelLayers.PLAYER_SLIM_ARMOR : ModelLayers.PLAYER_ARMOR,
          context.getModelSet(),
          part -> new PlayerModel(part, isSlim)
        ),
        FancyPlayerModel.getBabyArmorModel(isSlim),
        context.getEquipmentRenderer()
      );
      case CapeLayer l -> new FancyCapeLayer(this, context.getModelSet(), context.getEquipmentAssets());
      default -> layer;
    });
  }

  /**
   * Submits the texts for the name tag.
   *
   * @param renderState render state.
   * @param poseStack pose stack.
   * @param submitNodeCollector submit node collector.
   * @param camera camera state.
   */
  @Override
  protected void submitNameTag(@NotNull AvatarRenderState renderState, @NotNull PoseStack poseStack, @NotNull SubmitNodeCollector submitNodeCollector, @NotNull CameraRenderState camera) {
    if (renderState instanceof FancyPlayerRenderState state && state.nameTag != null && state.nameTagAttachment != null) {
      FormattedCharSequence text = state.nameTag.getVisualOrderText();
      Minecraft minecraft = Minecraft.getInstance();
      poseStack.pushPose();
      float scale = state.scale * NAMETAG_SCALE;
      float height = state.isBaby && state.pose != Pose.SPIN_ATTACK ? state.boundingBoxHeight * LivingEntity.DEFAULT_BABY_SCALE : state.boundingBoxHeight;
      float offsetY = (state.pose == Pose.SLEEPING || state.pose == Pose.SWIMMING ? state.boundingBoxWidth : height) / scale + (float) state.nameTagAttachment.y;
      poseStack.scale(scale, -scale, scale);
      if (state.pinName) {
        poseStack.rotateAround(new Quaternionf().rotateY(state.modelRot.getY()), 0, 0, 0);
      }
      poseStack.translate(0, -offsetY, 0);
      if (state.deathTime > 1) {
        poseStack.rotateAround(Axis.ZP.rotationDegrees(Math.min(Mth.sqrt((state.deathTime - 1) / 20F * 1.6F), 1) * 90), 0, offsetY, 0);
      }
      float x = -minecraft.font.width(text) / 2F;
      submitNodeCollector.submitText(poseStack, x, 0, text, false, Font.DisplayMode.SEE_THROUGH, state.lightCoords, -2130706433, (int) (minecraft.options.getBackgroundOpacity(0.25F) * 255F) << 24, 0);
      submitNodeCollector.submitText(poseStack, x, 0, text, false, Font.DisplayMode.NORMAL, LightTexture.lightCoordsWithEmission(state.lightCoords, 2), state.isDiscrete ? -2130706433 : -1, 0, 0);
      poseStack.popPose();
    }
  }

  @Override
  public @NotNull FancyPlayerRenderState createRenderState() {
    return new FancyPlayerRenderState();
  }

  @Override
  public void extractRenderState(@Nullable AbstractClientPlayer player, @NotNull AvatarRenderState renderState, float partialTick) {
    if (renderState instanceof FancyPlayerRenderState state) {
      if (state.mimickedPlayer != null) {
        float height = state.boundingBoxHeight;
        boolean isBaby = state.isBaby;
        boolean isUpsideDown = state.isUpsideDown;
        super.extractRenderState(state.mimickedPlayer, renderState, partialTick);
        mimicRenderState(state, height, isBaby, isUpsideDown);
      } else {
        updateRenderState(state);
      }
    }
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
  protected void setupRotations(@NotNull AvatarRenderState state, @NotNull PoseStack poseStack, float bodyRot, float scale) {
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

  /**
   * Submits the player model for rendering.
   *
   * @param state render state.
   * @param poseStack pose stack.
   * @param submitNodeCollector submit node collector.
   * @param camera camera state.
   */
  @Override
  public void submit(@NotNull AvatarRenderState state, @NotNull PoseStack poseStack, @NotNull SubmitNodeCollector submitNodeCollector, @NotNull CameraRenderState camera) {
    model = state.isBaby ? babyModel : adultModel;
    super.submit(state, poseStack, submitNodeCollector, camera);
  }

  /**
   * Updates the given render state when mimicking a player.<br>
   *
   * @param state render state.
   * @param height render height.
   * @param isBaby whether the player was originally baby.
   * @param isUpsideDown whether the player was originally upside-down.
   */
  protected void mimicRenderState(@NotNull FancyPlayerRenderState state, float height, boolean isBaby, boolean isUpsideDown) {
    state.updateScale(height);
    state.bodyRot = 0;
    if (!state.allowedPoses.contains(state.pose)) {
      state.pose = Pose.STANDING;
    }
    state.isFallFlying = state.pose == Pose.FALL_FLYING;
    state.isAutoSpinAttack = state.pose == Pose.SPIN_ATTACK;
    state.isCrouching = state.pose == Pose.CROUCHING;
    state.isVisuallySwimming = state.pose == Pose.SWIMMING;
    if (!state.isVisuallySwimming) {
      state.swimAmount = 0;
    }
    state.hasRedOverlay = state.pose == Pose.DYING;
    if (!state.hasRedOverlay) {
      state.deathTime = 0;
    }
    if (state.pose != Pose.STANDING && state.pose != Pose.CROUCHING) {
      state.displayFireAnimation = false;
    }
    if (state.pose == Pose.SWIMMING) {
      state.parrotOnLeftShoulder = null;
      state.parrotOnRightShoulder = null;
    }
    state.isBaby = isBaby;
    state.isUpsideDown = isUpsideDown;
  }

  /**
   * Updates the given render state.
   *
   * @param state render state.
   */
  protected void updateRenderState(@NotNull FancyPlayerRenderState state) {
    state.eyeHeight = Avatar.POSES.get(state.pose).eyeHeight();
    state.isDiscrete = state.isCrouching || state.isInvisible;
    if (state.isMoving && state.pose != Pose.DYING) {
      float step = state.speedValue * 0.33F;
      state.ageInTicks += step;
      state.walkAnimationPos += step;
      if (!(state.pose == Pose.SPIN_ATTACK || state.pose == Pose.SLEEPING)) {
        state.walkAnimationSpeed = state.walkSpeed;
      } else {
        state.walkAnimationSpeed = 0;
      }
    } else {
      state.ageInTicks = 3000;
      state.walkAnimationPos = 0;
      state.walkAnimationSpeed = 0;
    }
    state.nameTag = state.showPlayerName && !state.isInvisibleToPlayer ? Component.literal(state.name) : null;
    if (state.pose == Pose.SLEEPING) {
      state.nameTagAttachment = new Vec3(Avatar.POSES.get(state.pose).eyeHeight() * state.scale - state.boundingBoxHeight / 1.35F, 0, 0);
    } else {
      state.nameTagAttachment = new Vec3(0, 0.25F * state.scale, 0);
    }
    if (state.rightHandHeldItem != null) {
      itemModelResolver.updateForTopItem(state.rightHandItem, state.rightHandHeldItem, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, null, null, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND.ordinal());
    }
    if (state.leftHandHeldItem != null) {
      itemModelResolver.updateForTopItem(state.leftHandItem, state.leftHandHeldItem, ItemDisplayContext.THIRD_PERSON_LEFT_HAND, null, null, ItemDisplayContext.THIRD_PERSON_LEFT_HAND.ordinal());
    }
    state.elytraRotX = (float) (Math.PI / 16);
    state.elytraRotZ = (float) (Math.PI / 10);
  }
}
