package it.crystalnest.fancy_entity_renderer.api.entity.player;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import it.crystalnest.fancy_entity_renderer.api.entity.player.model.FancyPlayerModel;
import it.crystalnest.fancy_entity_renderer.api.entity.player.state.FancyPlayerRenderState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.model.HumanoidArmorModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.EquipmentAssetManager;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

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

  private void renderFlame(PoseStack poseStack, MultiBufferSource bufferSource, EntityRenderState renderState, Quaternionf quaternion) {
    TextureAtlasSprite textureatlassprite = ModelBakery.FIRE_0.sprite();
    TextureAtlasSprite textureatlassprite1 = ModelBakery.FIRE_1.sprite();
    poseStack.pushPose();
    float f = renderState.boundingBoxWidth * 1.4F;
    poseStack.scale(f, f, f);
    float f1 = 0.5F;
    float f2 = 0.0F;
    float f3 = renderState.boundingBoxHeight / f;
    float f4 = 0.0F;
    poseStack.mulPose(quaternion);
    poseStack.translate(0.0F, 0.0F, 0.3F - (float)((int)f3) * 0.02F);
    float f5 = 0.0F;
    int i = 0;
    VertexConsumer vertexconsumer = bufferSource.getBuffer(Sheets.cutoutBlockSheet());

    for (PoseStack.Pose posestack$pose = poseStack.last(); f3 > 0.0F; i++) {
      TextureAtlasSprite textureatlassprite2 = i % 2 == 0 ? textureatlassprite : textureatlassprite1;
      float f6 = textureatlassprite2.getU0();
      float f7 = textureatlassprite2.getV0();
      float f8 = textureatlassprite2.getU1();
      float f9 = textureatlassprite2.getV1();
      if (i / 2 % 2 == 0) {
        float f10 = f8;
        f8 = f6;
        f6 = f10;
      }

      fireVertex(posestack$pose, vertexconsumer, -f1 - 0.0F, 0.0F - f4, f5, f8, f9);
      fireVertex(posestack$pose, vertexconsumer, f1 - 0.0F, 0.0F - f4, f5, f6, f9);
      fireVertex(posestack$pose, vertexconsumer, f1 - 0.0F, 1.4F - f4, f5, f6, f7);
      fireVertex(posestack$pose, vertexconsumer, -f1 - 0.0F, 1.4F - f4, f5, f8, f7);
      f3 -= 0.45F;
      f4 -= 0.45F;
      f1 *= 0.9F;
      f5 -= 0.03F;
    }

    poseStack.popPose();
  }

  private static void fireVertex(
    PoseStack.Pose matrixEntry, VertexConsumer buffer, float x, float y, float z, float texU, float texV
  ) {
    buffer.addVertex(matrixEntry, x, y, z)
      .setColor(-1)
      .setUv(texU, texV)
      .setUv1(0, 10)
      .setLight(240)
      .setNormal(matrixEntry, 0.0F, 1.0F, 0.0F);
  }

  private final FancyPlayerModel adultModel;

  private final FancyPlayerModel babyModel;

  public FancyPlayerRenderState state;

  @Override
  protected int getBlockLightLevel(AbstractClientPlayer entity, BlockPos pos) {
    return 15;
  }

  public float height = 0;

  public FancyPlayerRenderer(boolean slim) {
    super(RENDER_CONTEXT, slim);
    layers.set(0, new HumanoidArmorLayer<>(
      this,
      new HumanoidArmorModel(FancyPlayerModel.Definitions.FANCY_PLAYER_INNER_ARMOR),
      new HumanoidArmorModel(FancyPlayerModel.Definitions.FANCY_PLAYER_OUTER_ARMOR),
      new HumanoidArmorModel(FancyPlayerModel.Definitions.FANCY_PLAYER_BABY_INNER_ARMOR),
      new HumanoidArmorModel(FancyPlayerModel.Definitions.FANCY_PLAYER_BABY_OUTER_ARMOR),
      RENDER_CONTEXT.getEquipmentRenderer()
    ));

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
    renderFlame(poseStack, bufferSource, state, Axis.YP.rotationDegrees(90));
  }

  public static void renderEntityInInventory(
    GuiGraphics guiGraphics,
    float x,
    float y,
    float scale,
    Vector3f translate,
    Quaternionf pose,
    @javax.annotation.Nullable Quaternionf cameraOrientation,
    LivingEntity entity
  ) {
    guiGraphics.pose().pushPose();
    guiGraphics.pose().translate(x, y, 50.0);
    guiGraphics.pose().scale(scale, scale, -scale);
    guiGraphics.pose().translate(translate.x, translate.y, translate.z);
    guiGraphics.pose().mulPose(pose);
    guiGraphics.flush();
    Lighting.setupForEntityInInventory();
    EntityRenderDispatcher entityrenderdispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
    if (cameraOrientation != null) {
      entityrenderdispatcher.overrideCameraOrientation(cameraOrientation.conjugate(new Quaternionf()).rotateY((float) Math.PI));
    }

    entityrenderdispatcher.setRenderShadow(false);
    guiGraphics.drawSpecial(p_370280_ -> entityrenderdispatcher.render(entity, 0.0, 0.0, 0.0, 1.0F, guiGraphics.pose(), p_370280_, 15728880));
    guiGraphics.flush();
    entityrenderdispatcher.setRenderShadow(true);
    guiGraphics.pose().popPose();
    Lighting.setupFor3DItems();
  }

  public void renderEntityInInventoryFollowsMouse(
    GuiGraphics guiGraphics,
    int x,
    int y,
    int width,
    int height,
    int scale,
    float yOffset,
    float mouseX,
    float mouseY
  ) {
    float f = (float)(x + width) / 2.0F;
    float f1 = (float)(y + height) / 2.0F;
    guiGraphics.enableScissor(x, y, width, height);
    float f2 = (float)Math.atan((f - mouseX) / 40.0F);
    float f3 = (float)Math.atan((f1 - mouseY) / 40.0F);
    Quaternionf quaternionf = new Quaternionf().rotateZ((float) Math.PI);
    Quaternionf quaternionf1 = new Quaternionf().rotateX(f3 * 20.0F * (float) (Math.PI / 180.0));
    quaternionf.mul(quaternionf1);
    float f4 = state.bodyRot.getY();
    float f5 = state.yRot;
    float f6 = state.xRot;
    float f7 = 0; // Rotatio offset
    float f8 = state.headRot.getY();
    state.bodyRot.setY(180.0F + f2 * 20.0F);
    state.yRot = 180.0F + f2 * 40.0F;
    state.xRot = -f3 * 20.0F;
    state.headRot.setY(state.yRot);
    //entity.yHeadRotO = entity.getYRot();
    float f9 = state.scale;
    Vector3f vector3f = new Vector3f(0.0F, type.getDimensions().height() / 2.0F + yOffset * f9, 0.0F);
    float f10 = (float)scale / f9;
    renderEntityInInventory(guiGraphics, f, f1, f10, vector3f, quaternionf, quaternionf1, entity);
    entity.yBodyRot = f4;
    entity.setYRot(f5);
    entity.setXRot(f6);
    entity.yHeadRotO = f7;
    entity.yHeadRot = f8;
    guiGraphics.disableScissor();
  }

  public void render(@NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight) {
    //entityRenderDispatcher.render(null, 0, 0, 0, 0, poseStack, bufferSource, packedLight, this);
    render(state, poseStack, bufferSource, packedLight);
  }
  private final EntityType type = EntityType.PLAYER;

  public void updateRenderState(int x, int y, int width, int height, int mouseX, int mouseY, float partialTick) {
    state.boundingBoxWidth = width;
//    state.boundingBoxHeight = height;
    state.isBaby = false;
    state.ageInTicks = 3000;
    state.walkAnimationPos = 0;
    state.walkAnimationSpeed = 0;
    state.isCrouching = false;
    state.isDiscrete = false;
    state.pose = Pose.STANDING;
    state.appearsGlowing = true;
    this.height = height;
    // TODO: The divisors below are probably due to the entity proportions, might be better to derive them from something rather than using magic numbers.
    state.scale = Math.min(width / 0.875F, height / 1.875F);
    if (state.bodyFollowsMouse || state.headFollowsMouse) {
      // Must rotate around Y axis when mouse moves along X axis and vice versa.
      double xRot = -Math.atan(mouseX - ((x + width) / 2F) / 40) * 20;
      double yRot = -Math.atan(mouseY - ((y + height) / 2F) / 40) * 20;
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
    state.headEquipment = Items.NETHERITE_HELMET.getDefaultInstance();
    state.chestEquipment = Items.NETHERITE_CHESTPLATE.getDefaultInstance();
    // TODO: Render elytra (if cape has a texture, elytra should be renderer with that texture too).
    // TODO: Only makes the body disappear, but maybe it should also make the head transparent. It might be nice to have a flag to choose between "no body, solid head" and "no body, transparent head".
//    state.isSpectator = true;
    // TODO: Flame is not rendered.
    state.displayFireAnimation = true;
  }
}
