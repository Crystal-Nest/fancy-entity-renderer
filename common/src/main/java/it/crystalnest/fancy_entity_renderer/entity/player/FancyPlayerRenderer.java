package it.crystalnest.fancy_entity_renderer.entity.player;

import com.mojang.blaze3d.vertex.PoseStack;
import it.crystalnest.fancy_entity_renderer.entity.player.model.FancyPlayerModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.client.resources.model.EquipmentAssetManager;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.animal.Parrot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FancyPlayerRenderer extends PlayerRenderer {

  @NotNull
  public volatile PlayerSkin skin = DefaultPlayerSkin.getDefaultSkin();
  public boolean isCrouching = false;
  public boolean isBaby = false;
  public boolean isGlowing = false;
  @Nullable
  public Parrot.Variant leftShoulderParrot = null;
  @Nullable
  public Parrot.Variant rightShoulderParrot = null;

  public float leftArmXRot = 0F;
  public float leftArmYRot = 0F;
  public float leftArmZRot = 0F;

  public float rightArmXRot = 0F;
  public float rightArmYRot = 0F;
  public float rightArmZRot = 0F;

  public float leftLegXRot = 0F;
  public float leftLegYRot = 0F;
  public float leftLegZRot = 0F;

  public float rightLegXRot = 0F;
  public float rightLegYRot = 0F;
  public float rightLegZRot = 0F;

  public float headXRot = 0F;
  public float headYRot = 0F;
  public float headZRot = 0F;

  public float bodyXRot = 0F;
  public float bodyYRot = 0F;

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
  public FancyPlayerRenderer(boolean isSlim, boolean isBaby) {
    super(RENDER_CONTEXT, false);
    model = new FancyPlayerModel(isSlim, isBaby);
  }

  public void updatePlayerProperties(@NotNull PlayerRenderState state) {

    state.skin = this.skin;
    state.isCrouching = this.isCrouching;
    state.parrotOnLeftShoulder = this.leftShoulderParrot;
    state.parrotOnRightShoulder = this.rightShoulderParrot;
    state.nameTag = null;
    state.nameTagAttachment = null;
    state.customName = null;
    state.isBaby = this.isBaby;
    state.appearsGlowing = this.isGlowing;
    state.isSpectator = false;
    state.ageInTicks = 1000;
    state.walkAnimationPos = 0.0F;
    state.walkAnimationSpeed = 0.0F;
    state.pose = this.isCrouching ? Pose.CROUCHING : Pose.STANDING;

    // X and Y rotations are switched for some reason

    this.model.leftArm.xRot = this.leftArmXRot;
    this.model.leftArm.yRot = this.leftArmYRot;
    this.model.leftArm.zRot = this.leftArmZRot;

    this.model.rightArm.xRot = this.rightArmXRot;
    this.model.rightArm.yRot = this.rightArmYRot;
    this.model.rightArm.zRot = this.rightArmZRot;

    this.model.leftLeg.xRot = this.leftLegXRot;
    this.model.leftLeg.yRot = this.leftLegYRot;
    this.model.leftLeg.zRot = this.leftLegZRot;

    this.model.rightLeg.xRot = this.rightLegXRot;
    this.model.rightLeg.yRot = this.rightLegYRot;
    this.model.rightLeg.zRot = this.rightLegZRot;

    this.model.root().xRot = this.bodyXRot;
    this.model.root().yRot = this.bodyYRot;

    this.model.head.xRot = this.headXRot;
    this.model.head.yRot = this.headYRot;
    this.model.head.zRot = this.headZRot;
  }

  @Override
  public void render(@NotNull PlayerRenderState state, @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight) {
    updatePlayerProperties(state);
    super.render(state, poseStack, bufferSource, packedLight);
  }
}
