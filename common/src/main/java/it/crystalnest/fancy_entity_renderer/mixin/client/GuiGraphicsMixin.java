package it.crystalnest.fancy_entity_renderer.mixin.client;

import it.crystalnest.fancy_entity_renderer.imixin.SpecialDrawer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.function.Consumer;

@Mixin(GuiGraphics.class)
public abstract class GuiGraphicsMixin implements SpecialDrawer {
  @Final
  @Shadow
  private MultiBufferSource.BufferSource bufferSource;

  private GuiGraphicsMixin() {}

  @Override
  public void drawSpecial(Consumer<MultiBufferSource> drawer) {
    drawer.accept(bufferSource);
    bufferSource.endBatch();
  }
}
