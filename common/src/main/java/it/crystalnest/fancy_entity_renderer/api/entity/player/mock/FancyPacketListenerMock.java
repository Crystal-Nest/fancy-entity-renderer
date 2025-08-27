package it.crystalnest.fancy_entity_renderer.api.entity.player.mock;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.CommonListenerCookie;

public class FancyPacketListenerMock extends ClientPacketListener {
  public FancyPacketListenerMock() {
    super(
      Minecraft.getInstance(),
      null,
      new CommonListenerCookie(
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        false,
        null,
        null
      )
    );
  }
}
