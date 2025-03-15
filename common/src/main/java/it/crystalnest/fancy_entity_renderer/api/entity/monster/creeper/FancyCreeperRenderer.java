package it.crystalnest.fancy_entity_renderer.api.entity.monster.creeper;

import com.mojang.blaze3d.vertex.PoseStack;
import it.crystalnest.fancy_entity_renderer.api.RenderConstants;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.CreeperRenderer;
import net.minecraft.client.renderer.entity.state.CreeperRenderState;
import net.minecraft.world.entity.monster.Creeper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

public class FancyCreeperRenderer extends CreeperRenderer {
  public FancyCreeperRenderer(CreeperRenderState state) {
    super(RenderConstants.RENDER_CONTEXT);
    entityRenderDispatcher.overrideCameraOrientation(new Quaternionf());
    entityRenderDispatcher.setRenderShadow(false);
    entityRenderDispatcher.setRenderHitBoxes(false);
    reusedState = state;
  }

  /**
   * Renders the player model.<br>
   * Called after {@link #render(PoseStack, MultiBufferSource, int)}.
   *
   * @param state render state.
   * @param poseStack pose stack.
   * @param bufferSource buffer source.
   * @param packedLight packed light.
   */
  @Override
  public void render(@NotNull CreeperRenderState state, @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight) {
    super.render(state, poseStack, bufferSource, packedLight);
  }

  /**
   * Renders the player model.
   *
   * @param poseStack pose stack.
   * @param bufferSource buffer source.
   * @param packedLight packed light.
   */
  public void render(@NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight) {
//    poseStack.rotateAround(new Quaternionf().rotateX(reusedState.bodyRot.getX()).rotateY(-reusedState.bodyRot.getY()).rotateZ(reusedState.bodyRot.getZ()), 0, 0, 0);
    // Entity is null, but it won't get used anyway because extractRenderState was overridden.
    // noinspection DataFlowIssue
    entityRenderDispatcher.render(null, 0, 0, 0, 0, poseStack, bufferSource, packedLight, this);
  }

  /**
   * Updates the given render state with data from the given player.<br>
   * Since there is no player entity for this renderer, the render state is updated from the global render state passed in the constructor and retrieved with {@link #reusedState}.
   *
   * @param player player entity (always {@code null}).
   * @param renderState render state to update.
   * @param partialTick partial tick.
   */
  @Override
  public void extractRenderState(@Nullable Creeper player, @NotNull CreeperRenderState renderState, float partialTick) {

  }
}
