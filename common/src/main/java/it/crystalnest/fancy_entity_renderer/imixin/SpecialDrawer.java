package it.crystalnest.fancy_entity_renderer.imixin;

import net.minecraft.client.renderer.MultiBufferSource;

import java.util.function.Consumer;

public interface SpecialDrawer {
  void drawSpecial(Consumer<MultiBufferSource> drawer);
}
