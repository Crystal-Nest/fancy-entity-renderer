package it.crystalnest.fancy_entity_renderer.api.entity.player.model;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

public class FancyPlayerCapeModel extends HumanoidModel<PlayerRenderState> {
  private final ModelPart cape = this.body.getChild("cape");

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

//    cape.rotateBy(
//      new Quaternionf()
//        .rotateY((float) -Math.PI)
//        .rotateX((6.0F + renderState.capeLean / 2.0F + renderState.capeFlap) * (float) (Math.PI / 180.0))
//        .rotateZ(renderState.capeLean2 / 2.0F * (float) (Math.PI / 180.0))
//        .rotateY((180.0F - renderState.capeLean2 / 2.0F) * (float) (Math.PI / 180.0))
//    );

    cape.rotateBy(
      new Quaternionf()
        .rotateY((float) -Math.PI)
        .rotateX((6.0F + renderState.capeLean / 2.0F + renderState.capeFlap) * (float) (Math.PI / 180.0))
        .rotateZ(renderState.capeLean2 / 2.0F * (float) (Math.PI / 180.0))
        .rotateY((180.0F - renderState.capeLean2 / 2.0F) * (float) (Math.PI / 180.0))
    );
  }
}
