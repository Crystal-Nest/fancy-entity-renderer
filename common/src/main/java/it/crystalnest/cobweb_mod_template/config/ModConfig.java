package it.crystalnest.cobweb_mod_template.config;

import it.crystalnest.cobweb.api.config.CommonConfig;
import it.crystalnest.cobweb_mod_template.Constants;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.ApiStatus;

/**
 * Mod common configuration.
 */
@ApiStatus.Internal
public final class ModConfig extends CommonConfig {
  /**
   * Mod common configuration.
   */
  public static final ModConfig CONFIG = register(Constants.MOD_ID, ModConfig::new);

  /**
   * Config example value.
   */
  private ModConfigSpec.BooleanValue example;

  /**
   * @param builder configuration builder.
   */
  private ModConfig(ModConfigSpec.Builder builder) {
    super(builder);
  }

  /**
   * Returns the value of {@link #example} as read from the configuration file.
   *
   * @return the value of {@link #example} as read from the configuration file.
   */
  public static Boolean getExample() {
    return CONFIG.example.get();
  }

  @Override
  protected void define(ModConfigSpec.Builder builder) {
    example = builder.comment(" Config example value").define("example", true);
  }
}
