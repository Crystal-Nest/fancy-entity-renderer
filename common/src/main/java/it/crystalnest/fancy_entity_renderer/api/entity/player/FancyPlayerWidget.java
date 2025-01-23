package it.crystalnest.fancy_entity_renderer.api.entity.player;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.math.Axis;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class FancyPlayerWidget extends AbstractWidget {
  private final PlayerRenderState renderState = new PlayerRenderState();

  private final FancyPlayerRenderer renderer = new FancyPlayerRenderer(true, true);

  private float rotationX = 0F;

  private float rotationY = 0F;

  public FancyPlayerWidget(int x, int y, int width, int height) {
    super(x, y, width, height, CommonComponents.EMPTY);
    renderer.isGlowing = true;
    renderer.isBaby = false;
    renderer.isCrouching = true;
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
    gfx.drawSpecial(src -> renderer.render(renderState, gfx.pose(), src, 15728880));
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
}


