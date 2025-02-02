package it.crystalnest.fancy_entity_renderer.api.entity.player.state;

import it.crystalnest.fancy_entity_renderer.api.Rotation;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;

public class FancyPlayerRenderState extends PlayerRenderState {
  public final Rotation leftArmRot = new Rotation();

  public final Rotation rightArmRot = new Rotation();

  public final Rotation leftLegRot = new Rotation();

  public final Rotation rightLegRot = new Rotation();

  public final Rotation headRot = new Rotation();

  public final Rotation bodyRot = new Rotation();

  public boolean bodyFollowsMouse = false;

  public boolean headFollowsMouse = false;

  public boolean copyLocalPlayer = false;

  public boolean isSlim = false;

  public FancyPlayerRenderState() {}
}
