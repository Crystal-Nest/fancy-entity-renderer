package it.crystalnest.fancy_entity_renderer.entity.player;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.math.Axis;
import it.crystalnest.fancy_entity_renderer.entity.player.model.PlayerModel;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class FancyPlayerWidget extends AbstractWidget {
  private final FancyPlayerWidget.Model model;

  private final Supplier<PlayerSkin> skin;

  private float rotationX = 0F;

  private float rotationY = 0F;

  public FancyPlayerWidget(int x, int y, int width, int height, Supplier<PlayerSkin> skin) {
    super(x, y, width, height, CommonComponents.EMPTY);
    this.model = FancyPlayerWidget.Model.bake();
    this.skin = skin;
  }

  @Override
  protected void renderWidget(GuiGraphics gfx, int mouseX, int mouseY, float partialTick) {
    gfx.pose().pushPose();
    gfx.pose().translate(getX() + getWidth() / 2.0F, (float) (getY() + getHeight()), 100.0F);
    float f = getHeight() / 2.125F;
    gfx.pose().scale(f, f, f);
    gfx.pose().translate(0.0F, -0.0625F, 0.0F);
    gfx.pose().rotateAround(Axis.XP.rotationDegrees(this.rotationX), 0.0F, -1.0625F, 0.0F);
    gfx.pose().mulPose(Axis.YP.rotationDegrees(this.rotationY));
    gfx.flush();
    Lighting.setupForEntityInInventory(Axis.XP.rotationDegrees(this.rotationX));
    this.model.render(gfx, this.skin.get());
    gfx.flush();
    Lighting.setupFor3DItems();
    gfx.pose().popPose();
  }

  @Override
  protected void onDrag(double mouseX, double mouseY, double dragX, double dragY) {
    this.rotationX = Mth.clamp(this.rotationX - (float) dragY * 2.5F, -50.0F, 50.0F);
    this.rotationY = Mth.clamp(this.rotationY + (float) dragX * 2.5F, -50.0F, 50.0F);
  }

  @Override
  public void playDownSound(@NotNull SoundManager soundManager) {
    // Disable playing any sound when clicked.
  }

  @Override
  public boolean isActive() {
    return false;
  }

  @Override
  protected void updateWidgetNarration(@NotNull NarrationElementOutput output) {
    // TODO: Maybe add narration for when the player name is visible (what about when the name is visible and the player is crouching?).
  }

  record Model(PlayerModel wideModel, PlayerModel slimModel, PlayerModel babyWideModel, PlayerModel babySlimModel) {
    static LayerDefinition WIDE = LayerDefinition.create(PlayerModel.createMesh(CubeDeformation.NONE, false), 64, 64);
    static LayerDefinition SLIM = LayerDefinition.create(PlayerModel.createMesh(CubeDeformation.NONE, true), 64, 64);

    public static FancyPlayerWidget.Model bake() {
      // Qui al posto di prendere l'EntityModelSet ecc, creo direttamente le cose.
      PlayerModel wideModel = new PlayerModel(WIDE.bakeRoot(), false);
      PlayerModel slimModel = new PlayerModel(SLIM.bakeRoot(), true);
      PlayerModel babyWideModel = new PlayerModel(WIDE.apply(PlayerModel.BABY_TRANSFORMER).bakeRoot(), false);
      PlayerModel babySlimModel = new PlayerModel(SLIM.apply(PlayerModel.BABY_TRANSFORMER).bakeRoot(), true);
      return new FancyPlayerWidget.Model(wideModel, slimModel, babyWideModel, babySlimModel);
    }

    public void render(GuiGraphics gfx, PlayerSkin skin) {
      gfx.pose().pushPose();
      gfx.pose().scale(1.0F, 1.0F, -1.0F);
      gfx.pose().translate(0.0F, -1.501F, 0.0F);
      PlayerModel model = skin.model() == PlayerSkin.Model.SLIM ? this.slimModel : this.wideModel;
      RenderType rendertype = model.renderType(skin.texture());
      gfx.drawSpecial(source -> model.renderToBuffer(gfx.pose(), source.getBuffer(rendertype), 15728880, OverlayTexture.NO_OVERLAY));
      gfx.pose().popPose();
    }
  }
}


