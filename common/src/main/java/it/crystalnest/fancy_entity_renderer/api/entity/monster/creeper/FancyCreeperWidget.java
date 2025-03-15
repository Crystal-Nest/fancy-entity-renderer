package it.crystalnest.fancy_entity_renderer.api.entity.monster.creeper;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.math.Axis;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.entity.state.CreeperRenderState;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.CommonComponents;
import org.jetbrains.annotations.NotNull;

public class FancyCreeperWidget extends AbstractWidget {
  private final CreeperRenderState renderState = new CreeperRenderState();

  private final FancyCreeperRenderer renderer = new FancyCreeperRenderer(renderState);

  public FancyCreeperWidget(int x, int y, int width, int height) {
    super(x, y, width, height, CommonComponents.EMPTY);
  }

  @Override
  protected void renderWidget(GuiGraphics gfx, int mouseX, int mouseY, float partialTick) {
//    updateRenderState(getX(), getY(), getWidth(), getHeight(), mouseX, mouseY, partialTick);
    renderState.scale = height / 1.7F;
    gfx.pose().pushPose();
    gfx.pose().translate(getX() + getWidth() / 2F, getY() + getHeight(), 100);
    gfx.flush();
    gfx.pose().scale(1, -1, 1);
//    Lighting.setupForEntityInInventory(Axis.XP.rotationDegrees(renderState.bodyRot.getX()));
    Lighting.setupForEntityInInventory(Axis.XP.rotationDegrees(0));
    gfx.drawSpecial(bufferSource -> renderer.render(gfx.pose(), bufferSource, LightTexture.FULL_BRIGHT));
    gfx.flush();
    gfx.pose().popPose();
  }

  /**
   * Plays a sound when the widget is pressed.<br>
   * Here no sound is played.
   *
   * @param soundManager sound manager.
   */
  @Override
  public void playDownSound(@NotNull SoundManager soundManager) {
    // Disable playing any sound when clicked.
  }

  /**
   * Updates the narrator narration for this widget.
   *
   * @param output narration element output.
   */
  @Override
  protected void updateWidgetNarration(@NotNull NarrationElementOutput output) {
    // Disable narrator narration.
  }
}
