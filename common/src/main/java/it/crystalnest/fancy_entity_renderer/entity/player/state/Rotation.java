package it.crystalnest.fancy_entity_renderer.entity.player.state;

import java.util.Objects;

public class Rotation {
  private float x;
  private float y;
  private float z;

  public Rotation(float x, float y, float z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public Rotation() {
    this(0, 0, 0);
  }

  public float getX() {
    return x;
  }

  public void setX(float x) {
    this.x = x;
  }

  public float getY() {
    return y;
  }

  public void setY(float y) {
    this.y = y;
  }

  public float getZ() {
    return z;
  }

  public void setZ(float z) {
    this.z = z;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Rotation rotation)) {
      return false;
    }
    return Float.compare(x, rotation.x) == 0 && Float.compare(y, rotation.y) == 0 && Float.compare(z, rotation.z) == 0;
  }

  @Override
  public int hashCode() {
    return Objects.hash(x, y, z);
  }

  @Override
  public String toString() {
    return "Rotation{x=" + x + ", y=" + y + ", z=" + z + "}";
  }
}
