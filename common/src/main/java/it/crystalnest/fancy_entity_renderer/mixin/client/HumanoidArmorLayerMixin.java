package it.crystalnest.fancy_entity_renderer.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.blaze3d.vertex.PoseStack;
import it.crystalnest.fancy_entity_renderer.api.entity.player.state.FancyPlayerRenderState;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Injects into {@link HumanoidArmorLayer} to handle baby player models.
 */
@Mixin(HumanoidArmorLayer.class)
public abstract class HumanoidArmorLayerMixin {
  /**
   * Modifies the expression value of getting the {@link LivingEntityRenderState#isBaby} field.<br>
   * Returns the appropriate result considering fancy player renders.
   *
   * @param original original check value.
   * @param poseStack pose stack.
   * @param submitNodeCollector node collector.
   * @param itemStack item stack.
   * @param slot equipment slot.
   * @param lightCoords light coordinates.
   * @param state render state.
   * @return whether to consider valid the original baby check value.
   */
  @ModifyExpressionValue(method = "renderArmorPiece", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/entity/state/HumanoidRenderState;isBaby:Z", opcode = Opcodes.GETFIELD))
  private boolean modifyBabyCheck(boolean original, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, ItemStack itemStack, EquipmentSlot slot, int lightCoords, HumanoidRenderState state) {
    return original && !(state instanceof FancyPlayerRenderState);
  }
}
