package it.crystalnest.fancy_entity_renderer.mixin;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.authlib.HttpAuthenticationService;
import com.mojang.authlib.exceptions.MinecraftClientException;
import com.mojang.authlib.minecraft.client.MinecraftClient;
import com.mojang.authlib.yggdrasil.ProfileResult;
import com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService;
import com.mojang.authlib.yggdrasil.response.MinecraftProfilePropertiesResponse;
import it.crystalnest.fancy_entity_renderer.api.FancySessionService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.net.URL;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Mixin(YggdrasilMinecraftSessionService.class)
public abstract class YggdrasilMinecraftSessionServiceMixin implements FancySessionService {
  @Shadow
  private static final Logger LOGGER = LoggerFactory.getLogger(YggdrasilMinecraftSessionService.class);

  @Final
  @Shadow
  private String baseUrl;

  @Final
  @Shadow
  private MinecraftClient client;

  @Unique
  private final LoadingCache<String, Optional<UUID>> insecureUUIDs = CacheBuilder
    .newBuilder()
    .expireAfterWrite(6, TimeUnit.HOURS)
    .build(new CacheLoader<>() {
      @NotNull
      @Override
      public Optional<UUID> load(final @NotNull String key) {
        return Optional.ofNullable(fetchProfileUncached(key, false) instanceof ProfileResult result ? result.profile().getId() : null);
      }
    });

  @Final
  @Shadow
  private LoadingCache<UUID, Optional<ProfileResult>> insecureProfiles;

  @Unique
  @Nullable
  @Override
  public ProfileResult fetchProfile(final String profileName, final boolean requireSecure) {
    if (!requireSecure) {
      return insecureUUIDs.getUnchecked(profileName).orElse(null) instanceof UUID uuid ? insecureProfiles.getUnchecked(uuid).orElse(null) : null;
    }
    return fetchProfileUncached(profileName, true);
  }

  @Unique
  @Nullable
  private ProfileResult fetchProfileUncached(final String profileName, final boolean requireSecure) {
    try {
      URL url = HttpAuthenticationService.constantURL("https://api.minecraftservices.com/minecraft/profile/lookup/name/" + profileName);
      url = HttpAuthenticationService.concatenateURL(url, "unsigned=" + !requireSecure);
      final MinecraftProfilePropertiesResponse response = client.get(url, MinecraftProfilePropertiesResponse.class);
      if (response == null) {
        LOGGER.debug("Couldn't fetch profile properties for {} as the profile does not exist", profileName);
        return null;
      }
      return fetchProfile(response.id(), requireSecure);
    } catch (final MinecraftClientException | IllegalArgumentException e) {
      LOGGER.warn("Couldn't look up profile properties for {}", profileName, e);
      return null;
    }
  }
}
