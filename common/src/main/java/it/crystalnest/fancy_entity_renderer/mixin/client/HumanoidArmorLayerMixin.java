package it.crystalnest.fancy_entity_renderer.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.blaze3d.vertex.PoseStack;
import it.crystalnest.fancy_entity_renderer.api.entity.player.state.FancyPlayerRenderState;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 *
 */
@Mixin(HumanoidArmorLayer.class)
public abstract class HumanoidArmorLayerMixin {
  /**
   *
   *
   * @param original
   * @param poseStack
   * @param submitNodeCollector
   * @param itemStack
   * @param slot
   * @param lightCoords
   * @param state
   * @return
   */
  // org.objectweb.asm.Opcodes.GETFIELD = 180
  @ModifyExpressionValue(method = "renderArmorPiece", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/entity/state/HumanoidRenderState;isBaby:Z", opcode = 180))
  private boolean modifyBabyCheck(boolean original, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, ItemStack itemStack, EquipmentSlot slot, int lightCoords, HumanoidRenderState state) {
    return original && !(state instanceof FancyPlayerRenderState);
  }
}
