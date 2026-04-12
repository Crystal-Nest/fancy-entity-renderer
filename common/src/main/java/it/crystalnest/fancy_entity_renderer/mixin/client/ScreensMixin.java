package it.crystalnest.fancy_entity_renderer.mixin.client;

import it.crystalnest.fancy_entity_renderer.client.screen.DevTestScreen;
import it.crystalnest.fancy_entity_renderer.platform.Services;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Injects into {@link TitleScreen} to add a dev testing.
 */
@Mixin({TitleScreen.class, PauseScreen.class})
public abstract class ScreensMixin extends Screen {
  protected ScreensMixin(Component title) {
    super(title);
  }

  /**
   * Injects at the end of the {@link TitleScreen} constructor.<br>
   * Add a button that sends to the dev testing screen.
   *
   * @param ci {@link CallbackInfo}.
   */
  @Inject(method = "init", at = @At(value = "TAIL"))
  private void onInit(CallbackInfo ci) {
    if (Services.PLATFORM.isDevEnv()) {
      addRenderableWidget(
        Button.builder(Component.literal("FER Test"), button -> minecraft.setScreenAndShow(new DevTestScreen(this, minecraft.options)))
          .bounds(width - 108, 20, 88, 20)
          .build()
      );
    }
  }
}
