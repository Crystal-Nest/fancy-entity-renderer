package it.crystalnest.fancy_entity_renderer.mixin.client;

import it.crystalnest.fancy_entity_renderer.api.entity.player.FancyPlayerWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
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
    addRenderableWidget(new FancyPlayerWidget(80, 200, 85, 120));
//    addRenderableWidget(new PlayerSkinWidget(80, 200, Minecraft.getInstance().getEntityModels(), DefaultPlayerSkin::getDefaultSkin));
  }
}
