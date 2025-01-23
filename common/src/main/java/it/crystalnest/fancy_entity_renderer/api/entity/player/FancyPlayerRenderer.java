package it.crystalnest.fancy_entity_renderer.api.entity.player;

import com.mojang.blaze3d.vertex.PoseStack;
import it.crystalnest.fancy_entity_renderer.api.Rotation;
import it.crystalnest.fancy_entity_renderer.api.entity.player.model.FancyPlayerModel;
import it.crystalnest.fancy_entity_renderer.api.entity.player.state.FancyPlayerRenderState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.client.resources.model.EquipmentAssetManager;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
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

  public final Rotation leftArmRot = new Rotation();

  public final Rotation rightArmRot = new Rotation();

  public final Rotation leftLegRot = new Rotation();

  public final Rotation rightLegRot = new Rotation();

  public final Rotation headRot = new Rotation();

  public final Rotation bodyRot = new Rotation();

  public final PlayerRenderState state = createRenderState();

  public PlayerSkin skin = DefaultPlayerSkin.get(Minecraft.getInstance().getGameProfile());

  public boolean isCrouching = false;

  public boolean isBaby = false;

  public boolean isSlim = false;

  public boolean isGlowing = false;

  public Pose pose = Pose.STANDING;

  @Nullable
  public Parrot.Variant leftShoulderParrot = null;

  @Nullable
  public Parrot.Variant rightShoulderParrot = null;

  public FancyPlayerRenderer() {
    super(RENDER_CONTEXT, false);
    model = new FancyPlayerModel(this::updateModel, isSlim, isBaby);
    entityRenderDispatcher.overrideCameraOrientation(new Quaternionf());
  }

  @NotNull
  @Override
  public PlayerRenderState createRenderState() {
    return new FancyPlayerRenderState();
  }

  public void updateRenderState(@NotNull PlayerRenderState state) {
    // TODO: Implement copying the local player (name, texture, showCape/showHat/show..., cape texture)
    // TODO: Implement choosing local texture files (both skin and cape), as well as choosing the texture from a player's name/uuid.
    state.skin = skin;
    state.isCrouching = isCrouching;
    state.parrotOnLeftShoulder = leftShoulderParrot;
    state.parrotOnRightShoulder = rightShoulderParrot;
//    reusedState.nameTag = this.getNameTag(p_entity);
//    reusedState.nameTagAttachment = new Player().getAttachments().getNullable(EntityAttachment.NAME_TAG, 0, p_entity.getYRot(partialTick));
    /**
     * Values below copied from {@link EntityType.Builder#nameTagOffset(float)}
     */
    state.nameTag = Component.literal("TEST");
    state.nameTagAttachment = new Vec3(0.0F, 2.05F, 0.0F);
    // TODO: Fix name tag. It doesn't render currently.
    state.customName = null;
    state.isBaby = isBaby;
    // TODO: Glowing effect doesn't work. The entity renders the same regardless. Could ignore this, since it was not in the original FancyManu, but it would be nice to have (not even sure this is the right property).
    state.appearsGlowing = isGlowing;
    state.isSpectator = false;
    state.pose = isCrouching ? Pose.CROUCHING : pose;
    state.isDiscrete = isCrouching;
    // TODO: Works almost fine, but the item model is kind of transparent to itself.
//    Minecraft.getInstance().getItemModelResolver().updateForTopItem(state.rightHandItem, Items.NETHERITE_SWORD.getDefaultInstance(), ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, false, null, null, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND.ordinal());
    // TODO: Why is armor not rendered?
//    state.headEquipment = Items.NETHERITE_HELMET.getDefaultInstance();
//    state.chestEquipment = Items.NETHERITE_CHESTPLATE.getDefaultInstance();
    // TODO: The reason why the flame doesn't render might be the same reason why the name tag doesn't render.
//    state.displayFireAnimation = true;
    // TODO: Render elytra (if cape has a texture, elytra should be renderer with that texture too).
  }

  public void updateModel(@NotNull PlayerRenderState state) {
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
  @ApiStatus.Internal
  public void render(@NotNull PlayerRenderState state, @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight) {
    updateRenderState(state);
    super.render(state, poseStack, bufferSource, packedLight);
  }

  public void render(@NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight) {
    render(state, poseStack, bufferSource, packedLight);
  }
}
