package it.crystalnest.fancy_entity_renderer.mixin.client.accessor;

import com.mojang.blaze3d.textures.GpuTexture;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PictureInPictureRenderer.class)
public interface PictureInPictureRendererAccessor {
  @Accessor("bufferSource")
  MultiBufferSource.BufferSource fer$getBufferSource();

  @Accessor("texture")
  @Nullable
  GpuTexture fer$getTexture();
}
