package it.crystalnest.fancy_entity_renderer.api.entity.player.model;

import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class FancyPlayerModel extends PlayerModel {
  private static final LayerDefinition FANCY_PLAYER = LayerDefinition.create(createMesh(CubeDeformation.NONE, false), 64, 64);

  private static final LayerDefinition FANCY_PLAYER_SLIM = LayerDefinition.create(createMesh(CubeDeformation.NONE, true), 64, 64);

  private final Consumer<PlayerRenderState> updater;

  public FancyPlayerModel(Consumer<PlayerRenderState> updater, boolean isSlim, boolean isBaby) {
    super(getModelPart(isSlim, isBaby), isSlim);
    this.updater = updater;
  }

  private static ModelPart getModelPart(boolean isSlim, boolean isBaby) {
    LayerDefinition layerDefinition = isSlim ? FANCY_PLAYER_SLIM : FANCY_PLAYER;
    if (isBaby) {
      layerDefinition = layerDefinition.apply(BABY_TRANSFORMER);
    }
    return layerDefinition.bakeRoot();
  }

  @Override
  public void setupAnim(@NotNull PlayerRenderState state) {
    super.setupAnim(state);
    updater.accept(state);
  }
}
