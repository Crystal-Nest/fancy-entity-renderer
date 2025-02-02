package it.crystalnest.fancy_entity_renderer.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import it.crystalnest.fancy_entity_renderer.api.entity.player.FancyPlayerRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin {
  @Final
  @Shadow
  private EntityRenderState reusedState;

  @WrapOperation(method = "renderNameTag", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;scale(FFF)V"))
  private void renderNameTag(PoseStack instance, float x, float y, float z, Operation<Void> original) {
    if ((((EntityRenderer<?, ?>) (Object) this) instanceof FancyPlayerRenderer)) {
      // TODO: Scale value should scale depending on the height value. 1 is good for default size.
      //       Name tag is now rendered upsideDown...
      float scale = reusedState.boundingBoxHeight / 120;
      instance.scale(scale, scale, scale);
    } else {
      original.call(instance, x, y, z);
    }
  }
}
