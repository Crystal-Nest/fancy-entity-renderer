package it.crystalnest.fancy_entity_renderer.api.entity.player.state;

import it.crystalnest.fancy_entity_renderer.api.Rotation;
import it.crystalnest.fancy_entity_renderer.api.entity.player.FancyPlayerRenderer;
import it.crystalnest.fancy_entity_renderer.api.entity.player.FancyPlayerWidget;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Extension of {@link PlayerRenderState}.
 */
public class FancyPlayerRenderState extends PlayerRenderState {
  /**
   * Internal {@link FancyPlayerRenderer} reference.
   */
  @ApiStatus.Internal
  public FancyPlayerRenderer renderer = null;

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
   * Whether to copy the appearance a player.
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
   * Whether to show the player's name.
   */
  public boolean showPlayerName;

  /**
   * Whether to pin the player's name at the top of the bounding box.
   */
  public boolean pinName;

  /**
   * Item held in the right hand.<br>
   * {@code null} if none.
   */
  @Nullable
  public ItemStack rightHandHeldItem;

  /**
   * Item held in the left hand.<br>
   * {@code null} if none.
   */
  @Nullable
  public ItemStack leftHandHeldItem;

  /**
   * Player to mimic when mimicking a player.
   */
  @Nullable
  public AbstractClientPlayer mimickedPlayer;

  /**
   * Allowed poses when mimicking a player.
   */
  public List<Pose> allowedPoses = new ArrayList<>();

  /**
   * Updates the scale and bounding box properties from the given height value.
   *
   * @param height height.
   */
  public void updateScale(float height) {
    boundingBoxHeight = height;
    boundingBoxWidth = height / FancyPlayerWidget.PLAYER_SIZE_RATIO;
    scale = height / FancyPlayerWidget.PLAYER_RENDER_HEIGHT;
  }
}
