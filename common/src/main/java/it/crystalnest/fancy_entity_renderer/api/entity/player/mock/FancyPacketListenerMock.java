package it.crystalnest.fancy_entity_renderer.api.entity.player.mock;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.Connection;

/**
 * Mock for client packet listener.
 */
@SuppressWarnings("DataFlowIssue")
public class FancyPacketListenerMock extends ClientPacketListener {
  /**
   * @param gameProfile mock game profile.
   */
  public FancyPacketListenerMock(GameProfile gameProfile) {
    super(
      Minecraft.getInstance(),
      null,
      new Connection(null),
      null,
      gameProfile,
      Minecraft.getInstance().getTelemetryManager().createWorldSessionManager(false, null, null)
    );
  }
}
