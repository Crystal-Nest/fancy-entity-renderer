package it.crystalnest.fancy_entity_renderer.api.entity.player.mock;

import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Lifecycle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.CommonListenerCookie;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.Connection;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.flag.FeatureFlagSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

public class FancyPacketListenerMock extends ClientPacketListener {
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
//            if (registryKey.equals(Registries.DAMAGE_TYPE)) {
              //noinspection unchecked
              return (Registry<E>) new Registry<DamageType>() {
                @Override
                public Holder.@NotNull Reference<DamageType> getHolderOrThrow(@NotNull ResourceKey<DamageType> key) {
                  return Holder.Reference.createStandAlone(null, null);
                }

                @NotNull
                @Override
                public Iterator<DamageType> iterator() {
                  return null;
                }

                @Override
                public @NotNull ResourceKey<? extends Registry<DamageType>> key() {
                  return Registries.DAMAGE_TYPE;
                }

                @Nullable
                @Override
                public ResourceLocation getKey(@NotNull DamageType damageType) {
                  return null;
                }

                @Override
                public @NotNull Optional<ResourceKey<DamageType>> getResourceKey(@NotNull DamageType damageType) {
                  return Optional.empty();
                }

                @Override
                public int getId(@Nullable DamageType damageType) {
                  return 0;
                }

                @Nullable
                @Override
                public DamageType byId(int i) {
                  return null;
                }

                @Override
                public int size() {
                  return 0;
                }

                @Nullable
                @Override
                public DamageType get(@Nullable ResourceKey<DamageType> resourceKey) {
                  return null;
                }

                @Nullable
                @Override
                public DamageType get(@Nullable ResourceLocation resourceLocation) {
                  return null;
                }

                @Override
                public @NotNull Optional<RegistrationInfo> registrationInfo(@NotNull ResourceKey<DamageType> resourceKey) {
                  return Optional.empty();
                }

                @Override
                public @NotNull Lifecycle registryLifecycle() {
                  return null;
                }

                @Override
                public @NotNull Optional<Holder.Reference<DamageType>> getAny() {
                  return Optional.empty();
                }

                @Override
                public @NotNull Set<ResourceLocation> keySet() {
                  return Set.of();
                }

                @Override
                public @NotNull Set<Map.Entry<ResourceKey<DamageType>, DamageType>> entrySet() {
                  return Set.of();
                }

                @Override
                public @NotNull Set<ResourceKey<DamageType>> registryKeySet() {
                  return Set.of();
                }

                @Override
                public @NotNull Optional<Holder.Reference<DamageType>> getRandom(@NotNull RandomSource randomSource) {
                  return Optional.empty();
                }

                @Override
                public boolean containsKey(@NotNull ResourceLocation resourceLocation) {
                  return false;
                }

                @Override
                public boolean containsKey(@NotNull ResourceKey<DamageType> resourceKey) {
                  return false;
                }

                @Override
                public @NotNull Registry<DamageType> freeze() {
                  return this;
                }

                @Override
                public Holder.@NotNull Reference<DamageType> createIntrusiveHolder(@NotNull DamageType damageType) {
                  return null;
                }

                @Override
                public @NotNull Optional<Holder.Reference<DamageType>> getHolder(int i) {
                  return Optional.empty();
                }

                @Override
                public @NotNull Optional<Holder.Reference<DamageType>> getHolder(@NotNull ResourceLocation resourceLocation) {
                  return Optional.empty();
                }

                @Override
                public @NotNull Optional<Holder.Reference<DamageType>> getHolder(@NotNull ResourceKey<DamageType> resourceKey) {
                  return Optional.empty();
                }

                @Override
                public @NotNull Holder<DamageType> wrapAsHolder(@NotNull DamageType damageType) {
                  return null;
                }

                @Override
                public @NotNull Stream<Holder.Reference<DamageType>> holders() {
                  return Stream.empty();
                }

                @Override
                public @NotNull Optional<HolderSet.Named<DamageType>> getTag(@NotNull TagKey<DamageType> tagKey) {
                  return Optional.empty();
                }

                @Override
                public HolderSet.@NotNull Named<DamageType> getOrCreateTag(@NotNull TagKey<DamageType> tagKey) {
                  return null;
                }

                @Override
                public @NotNull Stream<Pair<TagKey<DamageType>, HolderSet.Named<DamageType>>> getTags() {
                  return Stream.empty();
                }

                @Override
                public @NotNull Stream<TagKey<DamageType>> getTagNames() {
                  return Stream.empty();
                }

                @Override
                public void resetTags() {

                }

                @Override
                public void bindTags(@NotNull Map<TagKey<DamageType>, List<Holder<DamageType>>> map) {

                }

                @Override
                public @NotNull HolderOwner<DamageType> holderOwner() {
                  return null;
                }

                @Override
                public HolderLookup.@NotNull RegistryLookup<DamageType> asLookup() {
                  return null;
                }
              };
//            }
//            return Frozen.super.registryOrThrow(registryKey);
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
