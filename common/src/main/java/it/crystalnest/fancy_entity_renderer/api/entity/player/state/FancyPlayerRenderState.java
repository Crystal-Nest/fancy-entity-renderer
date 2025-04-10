package it.crystalnest.fancy_entity_renderer.api.entity.player.state;

import it.crystalnest.fancy_entity_renderer.api.Rotation;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

/**
 * Extension of {@link PlayerRenderState}.
 */
public class FancyPlayerRenderState extends PlayerRenderState {
  /**
   * Left arm rotation.
   */
  public final Rotation leftArmRot = new Rotation();

  /**
   * Right arm rotation.
   */
  public final Rotation rightArmRot = new Rotation();

  /**
   * Left leg rotation.
   */
  public final Rotation leftLegRot = new Rotation();

  /**
   * Right leg rotation.
   */
  public final Rotation rightLegRot = new Rotation();

  /**
   * Head rotation.
   */
  public final Rotation headRot = new Rotation();

  /**
   * Whole model rotation.
   */
  public final Rotation bodyRot = new Rotation();

  /**
   * Whether the whole model should rotate to follow the mouse.
   */
  public boolean bodyFollowsMouse;

  /**
   * Whether the head should rotate to follow the mouse.
   */
  public boolean headFollowsMouse;

  /**
   * Whether to mimic a player.
   */
  public boolean copyingPlayer;

  /**
   * Whether the model is slim or wide.
   */
  public boolean isSlim = true;

  /**
   * Whether the player should move.
   */
  public boolean isMoving;

  /**
   * Item held in the right hand.<br>
   * {@code null} if none.
   */
  @Nullable
  public Item rightHandHeldItem;

  /**
   * Item held in the left hand.<br>
   * {@code null} if none.
   */
  @Nullable
  public Item leftHandHeldItem;

  /**
   * Whether to show the player's name.
   */
  public boolean showPlayerName;
}
