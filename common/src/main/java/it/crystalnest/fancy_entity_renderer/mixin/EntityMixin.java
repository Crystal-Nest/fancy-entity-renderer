package it.crystalnest.fancy_entity_renderer.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import it.crystalnest.fancy_entity_renderer.api.entity.player.mock.FancyPlayerMock;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Entity.class)
public class EntityMixin {
  @ModifyReturnValue(method = "getBbWidth", at = @At(value = "RETURN"))
  private float modifyGetBbWidth(float original) {
    return ((Object) this) instanceof FancyPlayerMock playerMock ? playerMock.boundingBoxWidth : original;
  }

  @ModifyReturnValue(method = "getBbHeight", at = @At(value = "RETURN"))
  private float modifyGetBbHeight(float original) {
    return ((Object) this) instanceof FancyPlayerMock playerMock ? playerMock.boundingBoxHeight : original;
  }
}
