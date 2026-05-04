package it.crystalnest.fancy_entity_renderer.api.entity.player.mock;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.Level;

/**
 * Mock for client level.
 */
@SuppressWarnings("DataFlowIssue")
public class FancyLevelMock extends ClientLevel {
  /**
   * @param gameProfile mock game profile.
   */
  public FancyLevelMock(GameProfile gameProfile) {
    super(
      new FancyPacketListenerMock(gameProfile),
      new ClientLevelData(Difficulty.PEACEFUL, false, false),
      Level.OVERWORLD,
      FancyPacketListenerMock.overworldDimensionType(),
      0,
      0,
      Minecraft.getInstance()::getProfiler,
      null,
      true,
      0
    );
  }
}
