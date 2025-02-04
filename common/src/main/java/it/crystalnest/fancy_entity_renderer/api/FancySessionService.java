package it.crystalnest.fancy_entity_renderer.api;

import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.ProfileResult;
import org.jetbrains.annotations.Nullable;

/**
 * Extension of {@link MinecraftSessionService} to fetch profiles by name other than UUID.
 */
public interface FancySessionService extends MinecraftSessionService {
  /**
   * Fetches a profile from its name.
   *
   * @param profileName user name.
   * @param requireSecure whether to force fetch rather than trying to use the cache.
   * @return profile associated with the given name.
   */
  @Nullable
  ProfileResult fetchProfile(final String profileName, final boolean requireSecure);
}
