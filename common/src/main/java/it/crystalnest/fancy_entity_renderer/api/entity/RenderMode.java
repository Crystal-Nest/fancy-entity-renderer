package it.crystalnest.fancy_entity_renderer.api.entity;

/**
 * Entity render mode.
 */
public enum RenderMode {
  /**
   * Entity rendered normally.
   */
  NORMAL,
  /**
   * Entity not rendered.
   */
  INVISIBLE,
  /**
   * Only the entity's head is rendered and is translucent.
   */
  SPECTATOR,
  /**
   * The whole entity is rendered but is translucent.
   */
  GHOST
}
