package it.crystalnest.fancy_entity_renderer.mixin.client;

import it.crystalnest.fancy_entity_renderer.client.screen.FerDevTestScreen;
import it.crystalnest.fancy_entity_renderer.platform.Services;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Adds a dev-only entry point for FER's manual test screen.
 */
@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {
  protected TitleScreenMixin(Component title) {
    super(title);
  }

  @Inject(method = "init", at = @At("TAIL"))
  private void fer$addDevTestButton(CallbackInfo ci) {
    if (!Services.PLATFORM.isDevEnv() || minecraft == null) {
      return;
    }

    addRenderableWidget(
      Button.builder(Component.literal("FER Test"), button -> minecraft.setScreen(new FerDevTestScreen((TitleScreen) (Object) this)))
        .bounds(20, 20, 88, 20)
        .build()
    );
  }
}
