package it.crystalnest.fancy_entity_renderer.api.entity.player.mock;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.CommonListenerCookie;
import net.minecraft.network.Connection;
import net.minecraft.world.flag.FeatureFlagSet;

import java.util.Map;

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
      new Connection(null),
      new CommonListenerCookie(
        gameProfile,
        null,
        new FancyFrozenRegistryAccessMock(),
        FeatureFlagSet.of(),
        null,
        null,
        null,
        Map.of(),
        null,
        false,
        Map.of(),
        null
      )
    );
  }
}
