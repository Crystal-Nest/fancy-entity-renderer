package it.crystalnest.fancy_entity_renderer.api.entity.player;

import com.mojang.blaze3d.vertex.PoseStack;
import it.crystalnest.fancy_entity_renderer.api.Rotation;
import it.crystalnest.fancy_entity_renderer.api.entity.player.model.FancyPlayerModel;
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

  private final Rotation leftArmRot = new Rotation();

  private final Rotation rightArmRot = new Rotation();

  private final Rotation leftLegRot = new Rotation();

  private final Rotation rightLegRot = new Rotation();

  private final Rotation headRot = new Rotation();

  private final Rotation bodyRot = new Rotation();

  public PlayerSkin skin = DefaultPlayerSkin.getDefaultSkin();

  public boolean isCrouching = false;

  public boolean isBaby;

  public boolean isGlowing = false;

  @Nullable
  public Parrot.Variant leftShoulderParrot = null;

  @Nullable
  public Parrot.Variant rightShoulderParrot = null;

  public FancyPlayerRenderer(boolean isSlim, boolean isBaby) {
    super(RENDER_CONTEXT, false);
    model = new FancyPlayerModel(isSlim, isBaby);
    this.isBaby = isBaby;
  }

  public void updatePlayerProperties(@NotNull PlayerRenderState state) {
    state.skin = skin;
    state.isCrouching = isCrouching;
    state.parrotOnLeftShoulder = leftShoulderParrot;
    state.parrotOnRightShoulder = rightShoulderParrot;
    state.nameTag = null;
    state.nameTagAttachment = null;
    state.customName = null;
    state.isBaby = isBaby;
    state.appearsGlowing = isGlowing;
    state.isSpectator = false;
    state.ageInTicks = 1000;
    state.walkAnimationPos = 0.0F;
    state.walkAnimationSpeed = 0.0F;
    state.pose = isCrouching ? Pose.CROUCHING : Pose.STANDING;
    state.isUpsideDown = true;

    // X and Y rotations are switched for some reason

    model.leftArm.xRot = leftArmRot.getX();
    model.leftArm.yRot = leftArmRot.getY();
    model.leftArm.zRot = leftArmRot.getZ();

    model.rightArm.xRot = rightArmRot.getX();
    model.rightArm.yRot = rightArmRot.getY();
    model.rightArm.zRot = rightArmRot.getZ();

    model.leftLeg.xRot = leftLegRot.getX();
    model.leftLeg.yRot = leftLegRot.getY();
    model.leftLeg.zRot = leftLegRot.getZ();

    model.rightLeg.xRot = rightLegRot.getX();
    model.rightLeg.yRot = rightLegRot.getY();
    model.rightLeg.zRot = rightLegRot.getZ();

    model.root().xRot = bodyRot.getX();
    model.root().yRot = bodyRot.getY();
    model.root().zRot = bodyRot.getZ();

    model.head.xRot = headRot.getX();
    model.head.yRot = headRot.getY();
    model.head.zRot = headRot.getZ();
  }

  @Override
  public void render(@NotNull PlayerRenderState state, @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight) {
    updatePlayerProperties(state);
    super.render(state, poseStack, bufferSource, packedLight);
  }
}
