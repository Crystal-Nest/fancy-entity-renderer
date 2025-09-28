package it.crystalnest.fancy_entity_renderer.api.entity.player.mock;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Holder;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;

import java.util.OptionalLong;

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
      Holder.direct(
        new DimensionType(OptionalLong.empty(),
        true,
        false,
        false,
        true,
        1,
        true,
        false,
        0,
        16,
        16,
        BlockTags.INFINIBURN_OVERWORLD,
        BuiltinDimensionTypes.OVERWORLD_EFFECTS,
        0,
        new DimensionType.MonsterSettings(false, false, UniformInt.of(0, 7), 0))
      ),
      0,
      0,
      Minecraft.getInstance()::getProfiler,
      null,
      true,
      0
    );
  }
}
