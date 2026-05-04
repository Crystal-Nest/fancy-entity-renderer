package it.crystalnest.fancy_entity_renderer.platform.model;

/**
 * Platform.
 */
public enum Platform {
  /**
   * Fabric loader identifier.
   */
  FABRIC,
  /**
   * Forge loader identifier.
   */
  FORGE;

  @Override
  public String toString() {
    return name().toLowerCase();
  }
}
