package it.crystalnest.fancy_entity_renderer.api.entity.player.mock;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.CommonListenerCookie;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.Connection;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.flag.FeatureFlagSet;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.stream.Stream;

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
        new RegistryAccess.Frozen() {
          @Override
          public <E> @NotNull Optional<Registry<E>> registry(@NotNull ResourceKey<? extends Registry<? extends E>> resourceKey) {
            return Optional.empty();
          }

          @Override
          public <E> @NotNull Registry<E> registryOrThrow(@NotNull ResourceKey<? extends Registry<? extends E>> registryKey) {
              return new FancyRegistryMock<>(registryKey);
          }

          @Override
          public @NotNull Stream<RegistryEntry<?>> registries() {
            return Stream.empty();
          }
        },
        FeatureFlagSet.of(),
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
