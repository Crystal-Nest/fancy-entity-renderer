package it.crystalnest.fancy_entity_renderer.api.entity.player;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.yggdrasil.ProfileResult;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.server.Services;
import net.minecraft.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.BooleanSupplier;

/**
 * Game profile fetcher.
 */
public class FancyProfileFetcher {
  /**
   * Game profile cache by username.
   */
  @Nullable
  private static LoadingCache<String, CompletableFuture<Optional<GameProfile>>> profileCacheByName;

  /**
   * Game profile cache by UUID.
   */
  @Nullable
  private static LoadingCache<UUID, CompletableFuture<Optional<GameProfile>>> profileCacheById;

  /**
   * Sets up the profile caches and prepares the services for fetching profiles.
   */
  public static void setup() {
    Minecraft minecraft = Minecraft.getInstance();
    Services services = Services.create(minecraft.authenticationService, minecraft.gameDirectory);
    services.profileCache().setExecutor(minecraft);
    profileCacheByName = CacheBuilder.newBuilder().expireAfterAccess(Duration.ofMinutes(10L)).maximumSize(256L).build(new CacheLoader<>() {
      public @NotNull CompletableFuture<Optional<GameProfile>> load(@NotNull String username) {
        return FancyProfileFetcher.fetchProfile(username, services);
      }
    });
    profileCacheById = CacheBuilder.newBuilder().expireAfterAccess(Duration.ofMinutes(10L)).maximumSize(256L).build(new CacheLoader<>() {
      public @NotNull CompletableFuture<Optional<GameProfile>> load(@NotNull UUID id) {
        return FancyProfileFetcher.fetchProfile(id, services, () -> profileCacheById == null);
      }
    });
  }

  /**
   * Fetches a profile by the username.
   *
   * @param name username.
   * @param services remote services.
   * @return game profile.
   */
  static CompletableFuture<Optional<GameProfile>> fetchProfile(String name, Services services) {
    return services.profileCache().getAsync(name).thenCompose(cached -> {
      LoadingCache<UUID, CompletableFuture<Optional<GameProfile>>> loadingcache = profileCacheById;
      return loadingcache != null && cached.isPresent() ? loadingcache.getUnchecked(cached.get().getId()).thenApply(profile -> profile.or(() -> cached)) : CompletableFuture.completedFuture(Optional.empty());
    });
  }

  /**
   * Fetches a profile by the UUID.
   *
   * @param id UUID.
   * @param services remote services.
   * @param cacheUninitialized checker for whether the cache is not initialized.
   * @return game profile.
   */
  static CompletableFuture<Optional<GameProfile>> fetchProfile(UUID id, Services services, BooleanSupplier cacheUninitialized) {
    return CompletableFuture.supplyAsync(() -> cacheUninitialized.getAsBoolean() ? Optional.empty() : Optional.ofNullable(services.sessionService().fetchProfile(id, true)).map(ProfileResult::profile), Util.backgroundExecutor().forName("fetchProfile"));
  }

  /**
   * Fetches a profile by the profile name.
   *
   * @param profileName profile name.
   * @return game profile.
   */
  public static CompletableFuture<Optional<GameProfile>> fetchProfile(String profileName) {
    LoadingCache<String, CompletableFuture<Optional<GameProfile>>> loadingcache = profileCacheByName;
    return loadingcache != null && StringUtil.isValidPlayerName(profileName) ? loadingcache.getUnchecked(profileName) : CompletableFuture.completedFuture(Optional.empty());
  }


  /**
   * Fetches a profile by the profile UUID.
   *
   * @param profileId profile UUID.
   * @return game profile.
   */
  public static CompletableFuture<Optional<GameProfile>> fetchProfile(UUID profileId) {
    LoadingCache<UUID, CompletableFuture<Optional<GameProfile>>> loadingcache = profileCacheById;
    return loadingcache != null ? loadingcache.getUnchecked(profileId) : CompletableFuture.completedFuture(Optional.empty());
  }
}
