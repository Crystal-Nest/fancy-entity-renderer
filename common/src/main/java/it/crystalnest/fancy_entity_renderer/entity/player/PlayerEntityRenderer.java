//package it.crystalnest.fancy_entity_renderer.entity.player;
//
//import com.mojang.blaze3d.vertex.PoseStack;
//import it.crystalnest.fancy_entity_renderer.entity.player.model.FancyPlayerModel;
//import it.crystalnest.fancy_entity_renderer.entity.player.state.FancyPlayerRenderState;
//import net.minecraft.client.model.geom.ModelLayers;
//import net.minecraft.client.renderer.MultiBufferSource;
//import net.minecraft.client.renderer.entity.EntityRendererProvider;
//import net.minecraft.client.renderer.entity.LivingEntityRenderer;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.world.entity.player.Player;
//import org.jetbrains.annotations.NotNull;
//
//public class PlayerEntityRenderer extends LivingEntityRenderer<Player, FancyPlayerRenderState, FancyPlayerModel> {
//  public void PlayerEntityRender(EntityRendererProvider.Context ctx) {
//    super(ctx,
//      new FancyPlayerModel(ctx.bakeLayer(ModelLayers.PLAYER), false),
//      new FancyPlayerModel(ctx.bakeLayer(ModelLayers.PLAYER_SLIM), true),
//      0.7f // Shadow radius?
//    );
//  }
//
//  private final M adultModel;
//  private final M babyModel;
//
//  public AgeableMobRenderer(EntityRendererProvider.Context context, M adultModel, M babyModel, float scale) {
//    super(context, adultModel, scale);
//    this.adultModel = adultModel;
//    this.babyModel = babyModel;
//  }
//
//  @Override
//  public void render(S p_362004_, PoseStack p_361118_, MultiBufferSource p_363184_, int p_361276_) {
//    this.model = p_362004_.isBaby ? this.babyModel : this.adultModel;
//    super.render(p_362004_, p_361118_, p_363184_, p_361276_);
//  }
//
//  public PlayerEntityRenderer(EntityRendererProvider.Context context, FancyPlayerModel model, float shadowRadius) {
//    super(context, model, shadowRadius);
//  }
//
//  @Override
//  public @NotNull ResourceLocation getTextureLocation(FancyPlayerRenderState renderState) {
//    return renderState.skin.texture();
//  }
//
//  @Override
//  public @NotNull FancyPlayerRenderState createRenderState() {
//    return new FancyPlayerRenderState();
//  }
//}
