package it.crystalnest.fancy_entity_renderer.entity.player;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.math.Axis;
import it.crystalnest.fancy_entity_renderer.entity.player.model.PlayerModel;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.model.geom.EntityModelSet;
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

public class FancyPlayerEntityWidget extends AbstractWidget {
  private final FancyPlayerEntityWidget.Model model;
  private final Supplier<PlayerSkin> skin;
  private float rotationX = -5.0F;
  private float rotationY = 30.0F;

  public FancyPlayerEntityWidget(int x, int y, int width, int height, EntityModelSet model, Supplier<PlayerSkin> skin) {
    super(x, y, width, height, CommonComponents.EMPTY);
    this.model = FancyPlayerEntityWidget.Model.bake(model);
    this.skin = skin;
  }

  @Override
  public void playDownSound(@NotNull SoundManager soundManager) {}

  @Override
  public boolean isActive() {
    return false;
  }

  @Override
  protected void renderWidget(GuiGraphics gfx, int mouseX, int mouseY, float partialTick) {
    gfx.pose().pushPose();
    gfx.pose().translate((float)this.getX() + (float)this.getWidth() / 2.0F, (float)(this.getY() + this.getHeight()), 100.0F);
    float f = (float)this.getHeight() / 2.125F;
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
  protected void updateWidgetNarration(NarrationElementOutput _f) {

  }

  @Override
  protected void onDrag(double p_299829_, double p_299876_, double p_300028_, double p_299872_) {
    this.rotationX = Mth.clamp(this.rotationX - (float)p_299872_ * 2.5F, -50.0F, 50.0F);
    this.rotationY += (float)p_300028_ * 2.5F;
  }

  record Model(PlayerModel wideModel, PlayerModel slimModel) {

    public static FancyPlayerEntityWidget.Model bake(EntityModelSet modelSet) {
      // Qui al posto di prendere il modelSEt ecc, creo direttamente le cose.
      PlayerModel wideModel = new PlayerModel(LayerDefinition.create(PlayerModel.createMesh(CubeDeformation.NONE, false), 64, 64)/*.apply(PlayerModel.BABY_TRANSFORMER)*/.bakeRoot(), false);
      PlayerModel slimModel = new PlayerModel(LayerDefinition.create(PlayerModel.createMesh(CubeDeformation.NONE, true), 64, 64)/*.apply(PlayerModel.BABY_TRANSFORMER)*/.bakeRoot(), true);
      return new FancyPlayerEntityWidget.Model(wideModel, slimModel);
    }

    public void render(GuiGraphics gfx, PlayerSkin skin) {
      gfx.pose().pushPose();
      //gfx.pose().scale(1.0F, 1.0F, -1.0F);
      gfx.pose().translate(0.0F, -1.501F, 0.0F);
      PlayerModel model = skin.model() == PlayerSkin.Model.SLIM ? this.slimModel : this.wideModel;

      RenderType rendertype = model.renderType(skin.texture());
      gfx.drawSpecial(source ->
        model.renderToBuffer(gfx.pose(), source.getBuffer(rendertype), 15728880, OverlayTexture.NO_OVERLAY)
      );
      gfx.pose().popPose();
    }
  }
}


