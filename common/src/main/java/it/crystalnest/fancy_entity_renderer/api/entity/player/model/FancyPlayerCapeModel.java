package it.crystalnest.fancy_entity_renderer.api.entity.player.model;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.LayerDefinitions;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;

/**
 * Custom player cape model.
 */
public class FancyPlayerCapeModel extends HumanoidModel<AvatarRenderState> {
  /**
   * @param modelSet entity model set.
   * @param isBaby whether the player is baby.
   */
  public FancyPlayerCapeModel(EntityModelSet modelSet, boolean isBaby) {
    super(getModelPart(modelSet, isBaby));
  }

  /**
   * Returns the correct {@link ModelPart} depending on whether the player is baby.
   *
   * @param modelSet entity model set.
   * @param isBaby whether the player is baby
   * @return correct model part.
   */
  private static ModelPart getModelPart(EntityModelSet modelSet, boolean isBaby) {
    LayerDefinition layerDefinition = LayerDefinitions.createRoots().get(ModelLayers.PLAYER_CAPE);
    if (isBaby) {
      layerDefinition = layerDefinition.apply(BABY_TRANSFORMER);
    }
    return layerDefinition.bakeRoot();
  }
}
