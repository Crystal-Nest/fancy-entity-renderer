package it.crystalnest.fancy_entity_renderer.api.entity.player.mock;

import com.mojang.authlib.GameProfile;
import com.mojang.serialization.Lifecycle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.ClientRegistryLayer;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.Connection;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.damagesource.DamageEffects;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.damagesource.DeathMessageType;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.OptionalLong;

/**
 * Mock for client packet listener.
 */
public class FancyPacketListenerMock extends ClientPacketListener {
  /**
   * Minimal dynamic registries expected by {@link net.minecraft.world.level.Level}.
   */
  private static final RegistryAccess.Frozen REGISTRY_ACCESS = createRegistryAccess();

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

  @Override
  public @NotNull RegistryAccess registryAccess() {
    return REGISTRY_ACCESS;
  }

  /**
   * @return registered overworld dimension type holder.
   */
  static Holder<DimensionType> overworldDimensionType() {
    return REGISTRY_ACCESS.registryOrThrow(Registries.DIMENSION_TYPE).getHolderOrThrow(BuiltinDimensionTypes.OVERWORLD);
  }

  private static RegistryAccess.Frozen createRegistryAccess() {
    MappedRegistry<DimensionType> dimensionTypes = new MappedRegistry<>(Registries.DIMENSION_TYPE, Lifecycle.stable());
    MappedRegistry<DamageType> damageTypes = new MappedRegistry<>(Registries.DAMAGE_TYPE, Lifecycle.stable());
    MappedRegistry<Biome> biomes = new MappedRegistry<>(Registries.BIOME, Lifecycle.stable());

    registerDimensionTypes(dimensionTypes);
    registerDamageTypes(damageTypes);
    registerBiomes(biomes);

    RegistryAccess.Frozen dynamicRegistries = new RegistryAccess.ImmutableRegistryAccess(List.of(dimensionTypes, damageTypes, biomes)).freeze();
    return ClientRegistryLayer.createRegistryAccess().replaceFrom(ClientRegistryLayer.REMOTE, dynamicRegistries).compositeAccess();
  }

  private static void registerDimensionTypes(Registry<DimensionType> registry) {
    Registry.register(
      registry,
      BuiltinDimensionTypes.OVERWORLD,
      new DimensionType(
        OptionalLong.empty(),
        true,
        false,
        false,
        true,
        1,
        true,
        false,
        -64,
        384,
        384,
        BlockTags.INFINIBURN_OVERWORLD,
        BuiltinDimensionTypes.OVERWORLD_EFFECTS,
        0,
        new DimensionType.MonsterSettings(false, true, UniformInt.of(0, 7), 0)
      )
    );
  }

  private static void registerDamageTypes(Registry<DamageType> registry) {
    register(registry, DamageTypes.IN_FIRE, new DamageType("inFire", 0.1F, DamageEffects.BURNING));
    register(registry, DamageTypes.LIGHTNING_BOLT, new DamageType("lightningBolt", 0.1F));
    register(registry, DamageTypes.ON_FIRE, new DamageType("onFire", 0.0F, DamageEffects.BURNING));
    register(registry, DamageTypes.LAVA, new DamageType("lava", 0.1F, DamageEffects.BURNING));
    register(registry, DamageTypes.HOT_FLOOR, new DamageType("hotFloor", 0.1F, DamageEffects.BURNING));
    register(registry, DamageTypes.IN_WALL, new DamageType("inWall", 0.0F));
    register(registry, DamageTypes.CRAMMING, new DamageType("cramming", 0.0F));
    register(registry, DamageTypes.DROWN, new DamageType("drown", 0.0F, DamageEffects.DROWNING));
    register(registry, DamageTypes.STARVE, new DamageType("starve", 0.0F));
    register(registry, DamageTypes.CACTUS, new DamageType("cactus", 0.1F));
    register(registry, DamageTypes.FALL, new DamageType("fall", DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.0F, DamageEffects.HURT, DeathMessageType.FALL_VARIANTS));
    register(registry, DamageTypes.FLY_INTO_WALL, new DamageType("flyIntoWall", 0.0F));
    register(registry, DamageTypes.FELL_OUT_OF_WORLD, new DamageType("outOfWorld", 0.0F));
    register(registry, DamageTypes.GENERIC, new DamageType("generic", 0.0F));
    register(registry, DamageTypes.MAGIC, new DamageType("magic", 0.0F));
    register(registry, DamageTypes.WITHER, new DamageType("wither", 0.0F));
    register(registry, DamageTypes.DRAGON_BREATH, new DamageType("dragonBreath", 0.0F));
    register(registry, DamageTypes.DRY_OUT, new DamageType("dryout", 0.1F));
    register(registry, DamageTypes.SWEET_BERRY_BUSH, new DamageType("sweetBerryBush", 0.1F, DamageEffects.POKING));
    register(registry, DamageTypes.FREEZE, new DamageType("freeze", 0.0F, DamageEffects.FREEZING));
    register(registry, DamageTypes.STALAGMITE, new DamageType("stalagmite", 0.0F));
    register(registry, DamageTypes.FALLING_BLOCK, new DamageType("fallingBlock", 0.1F));
    register(registry, DamageTypes.FALLING_ANVIL, new DamageType("anvil", 0.1F));
    register(registry, DamageTypes.FALLING_STALACTITE, new DamageType("fallingStalactite", 0.1F));
    register(registry, DamageTypes.STING, new DamageType("sting", 0.1F));
    register(registry, DamageTypes.MOB_ATTACK, new DamageType("mob", 0.1F));
    register(registry, DamageTypes.MOB_ATTACK_NO_AGGRO, new DamageType("mob", 0.1F));
    register(registry, DamageTypes.PLAYER_ATTACK, new DamageType("player", 0.1F));
    register(registry, DamageTypes.ARROW, new DamageType("arrow", 0.1F));
    register(registry, DamageTypes.TRIDENT, new DamageType("trident", 0.1F));
    register(registry, DamageTypes.MOB_PROJECTILE, new DamageType("mob", 0.1F));
    register(registry, DamageTypes.FIREWORKS, new DamageType("fireworks", 0.1F));
    register(registry, DamageTypes.UNATTRIBUTED_FIREBALL, new DamageType("onFire", 0.1F, DamageEffects.BURNING));
    register(registry, DamageTypes.FIREBALL, new DamageType("fireball", 0.1F, DamageEffects.BURNING));
    register(registry, DamageTypes.WITHER_SKULL, new DamageType("witherSkull", 0.1F));
    register(registry, DamageTypes.THROWN, new DamageType("thrown", 0.1F));
    register(registry, DamageTypes.INDIRECT_MAGIC, new DamageType("indirectMagic", 0.0F));
    register(registry, DamageTypes.THORNS, new DamageType("thorns", 0.1F, DamageEffects.THORNS));
    register(registry, DamageTypes.EXPLOSION, new DamageType("explosion", DamageScaling.ALWAYS, 0.1F));
    register(registry, DamageTypes.PLAYER_EXPLOSION, new DamageType("explosion.player", DamageScaling.ALWAYS, 0.1F));
    register(registry, DamageTypes.SONIC_BOOM, new DamageType("sonic_boom", DamageScaling.ALWAYS, 0.0F));
    register(registry, DamageTypes.BAD_RESPAWN_POINT, new DamageType("badRespawnPoint", DamageScaling.ALWAYS, 0.1F, DamageEffects.HURT, DeathMessageType.INTENTIONAL_GAME_DESIGN));
    register(registry, DamageTypes.OUTSIDE_BORDER, new DamageType("outsideBorder", 0.0F));
    register(registry, DamageTypes.GENERIC_KILL, new DamageType("genericKill", 0.0F));
  }

  private static void registerBiomes(Registry<Biome> registry) {
    Registry.register(
      registry,
      Biomes.PLAINS,
      new Biome.BiomeBuilder()
        .hasPrecipitation(true)
        .temperature(0.8F)
        .downfall(0.4F)
        .specialEffects(
          new BiomeSpecialEffects.Builder()
            .waterColor(0x3F76E4)
            .waterFogColor(0x050533)
            .fogColor(0xC0D8FF)
            .skyColor(0x78A7FF)
            .build()
        )
        .mobSpawnSettings(MobSpawnSettings.EMPTY)
        .generationSettings(BiomeGenerationSettings.EMPTY)
        .build()
    );
  }

  private static void register(Registry<DamageType> registry, ResourceKey<DamageType> key, DamageType damageType) {
    Registry.register(registry, key, damageType);
  }
}
