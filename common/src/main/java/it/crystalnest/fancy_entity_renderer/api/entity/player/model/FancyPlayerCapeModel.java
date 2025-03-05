package it.crystalnest.fancy_entity_renderer.api.entity.player.model;

import it.crystalnest.fancy_entity_renderer.api.entity.player.state.FancyPlayerRenderState;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

public class FancyPlayerCapeModel extends HumanoidModel<PlayerRenderState> {
  private final ModelPart cape = body.getChild("cape");

  public FancyPlayerCapeModel(EntityModelSet modelSet, boolean isBaby) {
    super(getModelPart(modelSet, isBaby));
  }

  private static ModelPart getModelPart(EntityModelSet modelSet, boolean isBaby) {
    LayerDefinition layerDefinition = modelSet.roots.get(ModelLayers.PLAYER_CAPE);
    if (isBaby) {
      layerDefinition = layerDefinition.apply(BABY_TRANSFORMER);
    }
    return layerDefinition.bakeRoot();
  }

  public void setupAnim(@NotNull PlayerRenderState renderState) {
    super.setupAnim(renderState);
    cape.rotateBy(new Quaternionf().rotateY((float) -Math.PI).rotateX((float) (renderState.capeLean / 2 + Math.PI / 12)).rotateY((float) Math.PI));
    FancyPlayerRenderState state = (FancyPlayerRenderState) renderState;
    root().xRot += state.bodyRot.getX();
    root().yRot += state.bodyRot.getY();
    root().zRot += state.bodyRot.getZ();
  }
}
