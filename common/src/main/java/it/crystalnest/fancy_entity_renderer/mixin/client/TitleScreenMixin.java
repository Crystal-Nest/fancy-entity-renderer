package it.crystalnest.fancy_entity_renderer.mixin.client;

import it.crystalnest.fancy_entity_renderer.entity.player.FancyPlayerWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {
  protected TitleScreenMixin(Component title) {
    super(title);
  }

  @Inject(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/TitleScreen;addRenderableWidget(Lnet/minecraft/client/gui/components/events/GuiEventListener;)Lnet/minecraft/client/gui/components/events/GuiEventListener;", ordinal = 3, shift = At.Shift.AFTER))
  private void onInit(CallbackInfo ci) {
    //this.addRenderableWidget(new PlayerSkinWidget(85, 120, this.minecraft.getEntityModels(), DefaultPlayerSkin::getDefaultSkin));
   this.addRenderableWidget(new FancyPlayerWidget(0,0,85, 120, DefaultPlayerSkin::getDefaultSkin));
  }
}
