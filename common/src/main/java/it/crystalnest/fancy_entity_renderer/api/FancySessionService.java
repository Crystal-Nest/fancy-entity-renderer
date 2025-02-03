package it.crystalnest.fancy_entity_renderer.api;

import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.ProfileResult;
import org.jetbrains.annotations.Nullable;

public interface FancySessionService extends MinecraftSessionService {
  @Nullable
  ProfileResult fetchProfile(final String profileName, final boolean requireSecure);
}
