package it.crystalnest.fancy_entity_renderer.entity.player.model;

import it.crystalnest.fancy_entity_renderer.Constants;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.resources.ResourceLocation;

public class PlayerModel extends net.minecraft.client.model.PlayerModel {
  public static ModelLayerLocation BABY_PLAYER = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "baby_player"), "main");


  public PlayerModel(ModelPart root, boolean isSlim) {
    super(root, isSlim);
  }


}
