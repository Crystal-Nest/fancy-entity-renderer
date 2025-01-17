//package it.crystalnest.fancy_entity_renderer.mixin.client;
//
//import com.google.common.collect.ImmutableMap;
//import it.crystalnest.fancy_entity_renderer.Constants;
//import it.crystalnest.fancy_entity_renderer.entity.player.model.FancyPlayerModel;
//import net.minecraft.client.model.HumanoidModel;
//import net.minecraft.client.model.PlayerModel;
//import net.minecraft.client.model.ZombieModel;
//import net.minecraft.client.model.geom.LayerDefinitions;
//import net.minecraft.client.model.geom.ModelLayerLocation;
//import net.minecraft.client.model.geom.builders.CubeDeformation;
//import net.minecraft.client.model.geom.builders.LayerDefinition;
//import net.minecraft.resources.ResourceLocation;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Redirect;
//
//import static it.crystalnest.fancy_entity_renderer.entity.player.model.FancyPlayerModel.BABY_PLAYER;
//
//@Mixin(LayerDefinitions.class)
//public abstract class LayerDefinitionsMixin {
//
//  protected LayerDefinitionsMixin() {
//    super();
//  }
//
//  @Redirect(method = "createRoots", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableMap$Builder;build()Lcom/google/common/collect/ImmutableMap;"))
//  private static ImmutableMap<ModelLayerLocation, LayerDefinition> onCreateRoots(ImmutableMap.Builder<ModelLayerLocation, LayerDefinition> instance) {
//    LayerDefinition pippo = LayerDefinition.create(PlayerModel.createMesh(CubeDeformation.NONE, false), 64, 64).apply(PlayerModel .BABY_TRANSFORMER);
//    instance.put(BABY_PLAYER, pippo);
//    return instance.build();
//  }
//}
