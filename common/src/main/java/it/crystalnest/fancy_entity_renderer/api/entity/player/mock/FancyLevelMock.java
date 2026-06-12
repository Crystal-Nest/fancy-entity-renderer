package it.crystalnest.fancy_entity_renderer.api.entity.player.mock;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
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
   * Depth of mock level construction on the current thread.
   */
  private static final ThreadLocal<Integer> CONSTRUCTION_DEPTH = ThreadLocal.withInitial(() -> 0);

  /**
   * @param gameProfile mock game profile.
   */
  public FancyLevelMock(GameProfile gameProfile) {
    super(
      beginConstruction(new FancyPacketListenerMock(gameProfile)),
      new ClientLevelData(Difficulty.PEACEFUL, false, false),
      Level.OVERWORLD,
      Holder.direct(
        new DimensionType(
          OptionalLong.empty(),
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
          new DimensionType.MonsterSettings(false, false, UniformInt.of(0, 7), 0)
        )
      ),
      0,
      0,
      Minecraft.getInstance()::getProfiler,
      null,
      true,
      0
    );
    endConstruction();
  }

  /**
   * @return whether a mock level is being constructed on the current thread.
   */
  public static boolean isConstructing() {
    return CONSTRUCTION_DEPTH.get() > 0;
  }

  /**
   * Marks the current thread as constructing a mock level before the {@link ClientLevel} constructor runs.
   *
   * @param packetListener packet listener to pass to the super constructor.
   * @return packet listener.
   */
  private static ClientPacketListener beginConstruction(ClientPacketListener packetListener) {
    CONSTRUCTION_DEPTH.set(CONSTRUCTION_DEPTH.get() + 1);
    return packetListener;
  }

  /**
   * Clears mock level construction state for the current thread.
   */
  private static void endConstruction() {
    int depth = CONSTRUCTION_DEPTH.get() - 1;
    if (depth <= 0) {
      CONSTRUCTION_DEPTH.remove();
    } else {
      CONSTRUCTION_DEPTH.set(depth);
    }
  }
}
