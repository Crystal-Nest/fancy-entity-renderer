package it.crystalnest.fancy_entity_renderer.api.entity.player;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.math.Axis;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.CommonComponents;
import org.jetbrains.annotations.NotNull;

public class FancyPlayerWidget extends AbstractWidget {
  private final FancyPlayerRenderer renderer = new FancyPlayerRenderer();

  public FancyPlayerWidget(int x, int y, int width, int height) {
    super(x, y, width, height, CommonComponents.EMPTY);
  }

  @Override
  protected void renderWidget(GuiGraphics gfx, int mouseX, int mouseY, float partialTick) {
//    if (this.bodyFollowsMouse) {
    float c = (float) (getX() + (getX() + getWidth())) / 2.0F;
    float g = (float) (getY() + (getY() + getHeight())) / 2.0F;
    float h = (float) Math.atan(((c - mouseX) / 40.0F));
    float i = (float) Math.atan(((g - mouseY) / 40.0F));
    // Must rotate around Y axis when mouse moves along X axis and vice versa.
    renderer.bodyRot.setX((float) Math.toRadians(-i * 20.0F));
    renderer.bodyRot.setY((float) -Math.toRadians(h * 20.0F));
//    } else {
//      renderer.bodyRot.setX((float) Math.toRadians(this.stringToFloat(this.bodyXRot)));
//      renderer.bodyRot.setY((float) Math.toRadians(this.stringToFloat(this.bodyYRot)));
//    }

    gfx.pose().pushPose();
    gfx.pose().translate(getX() + getWidth() / 2F, getY() + getHeight(), 100);
    float f = getHeight() / 2.125F;
    gfx.pose().scale(f, f, f);
    gfx.pose().translate(0, -0.0625F, 0);
    gfx.pose().rotateAround(Axis.XP.rotationDegrees(renderer.bodyRot.getX()), 0, -1.0625F, 0);
    gfx.pose().mulPose(Axis.YP.rotationDegrees(renderer.bodyRot.getY()));
    gfx.flush();
    Lighting.setupForEntityInInventory(Axis.XP.rotationDegrees(renderer.bodyRot.getX()));
    gfx.drawSpecial(src -> renderer.render(gfx.pose(), src, 15728880));
    Lighting.setupFor3DItems();
    gfx.pose().popPose();
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


