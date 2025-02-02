package it.crystalnest.fancy_entity_renderer.api.entity.player;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.math.Axis;
import de.keksuccino.fancymenu.customization.element.elements.playerentity.v2.textures.SkinResourceSupplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.CommonComponents;
import org.jetbrains.annotations.NotNull;

public class FancyPlayerWidget extends AbstractWidget {
  // TODO: By moving back the state into the renderer, now we must find a way to centralize the state despite the fact that a separate state is created each time a renderer is created.
  private final FancyPlayerRenderer renderer;

  private final FancyPlayerRenderer wideRenderer = new FancyPlayerRenderer(false);

  private final FancyPlayerRenderer slimRenderer = new FancyPlayerRenderer(true);

  private PlayerSkin skin = DefaultPlayerSkin.get(Minecraft.getInstance().getGameProfile());

  private final SkinResourceSupplier skinres = new SkinResourceSupplier("brrrlol", true);

  private final PlayerRenderer testRender = new PlayerRenderer(FancyPlayerRenderer.RENDER_CONTEXT, false);

  public FancyPlayerWidget(int x, int y, int width, int height) {
    super(x, y, width, height, CommonComponents.EMPTY);
    renderer = DefaultPlayerSkin.get(Minecraft.getInstance().getGameProfile()).model() == PlayerSkin.Model.SLIM ? slimRenderer : wideRenderer;
    renderer.state.headFollowsMouse = true;
    renderer.state.bodyFollowsMouse = true;
    renderer.state.skin = skin;
//    PlayerSkin ps = Minecraft.getInstance().getSkinManager().lookupInsecure(new GameProfile(UUID.fromString("b6ce7857-012b-41d0-90b3-dff25058e379"), "brrrlol")).get();
    setSkin(new PlayerSkin(skinres.getSkinLocation(), null, null, null, skinres.isSlimPlayerNameSkin() ? PlayerSkin.Model.SLIM : PlayerSkin.Model.WIDE, false));
  }

  public void setSkin(@NotNull PlayerSkin skin) {
    this.skin = skin;
  }

  @Override
  protected void renderWidget(GuiGraphics gfx, int mouseX, int mouseY, float partialTick) {
    renderer.updateRenderState(getX(), getY(), getWidth(), getHeight(), mouseX, mouseY, partialTick);
    gfx.pose().pushPose();
    gfx.pose().translate(getX() + getWidth() / 2F, getY() + getHeight(), 100);
    gfx.flush();
//    gfx.pose().mulPose(Axis.ZP.rotationDegrees(180.0F));
//    gfx.pose().scale(1,1,1); // render flame
    Lighting.setupForEntityInInventory(Axis.XP.rotationDegrees(renderer.state.bodyRot.getX()));
    gfx.drawSpecial(src -> renderer.render(gfx.pose(), src, 15728880));
    gfx.flush();
    Lighting.setupFor3DItems();
//    gfx.drawSpecial(src -> testRender.render(testRender.createRenderState(), gfx.pose(), src, 15728880 ));
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
