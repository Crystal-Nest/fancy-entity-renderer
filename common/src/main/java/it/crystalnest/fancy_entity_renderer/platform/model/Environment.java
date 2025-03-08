package it.crystalnest.fancy_entity_renderer.platform.model;

/**
 * Environment.
 */
public enum Environment {
  /**
   * Development environment identifier.
   */
  DEVELOPMENT,
  /**
   * Production environment identifier.
   */
  PRODUCTION;

  @Override
  public String toString() {
    return name().toLowerCase();
  }
}
