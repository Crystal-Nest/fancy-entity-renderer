package it.crystalnest.fancy_entity_renderer.api;

import org.joml.Vector3f;

import java.util.Objects;

/**
 * Rotation vector.
 */
public class Rotation {
  /**
   * Radians of rotation around the X axis.
   */
  private float x;

  /**
   * Radians of rotation around the Y axis.
   */
  private float y;

  /**
   * Radians of rotation around the Z axis.
   */
  private float z;

  /**
   * @param x {@link #x}.
   * @param y {@link #y}.
   * @param z {@link #z}.
   */
  public Rotation(float x, float y, float z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public Rotation() {
    this(0, 0, 0);
  }

  /**
   * Returns the {@link #x} component.
   *
   * @return the {@link #x} component.
   */
  public float getX() {
    return x;
  }

  /**
   * Returns the {@link #x} component in degrees.
   *
   * @return the {@link #x} component in degrees.
   */
  public float getXDeg() {
    return (float) Math.toDegrees(x);
  }

  /**
   * Sets the value for the {@link #x} component.
   *
   * @param x {@link #x} component.
   */
  public void setX(double x) {
    this.x = (float) x;
  }

  /**
   * Sets the value for the {@link #x} component in degrees.
   *
   * @param x {@link #x} component in degrees.
   */
  public void setXDeg(double x) {
    setX(Math.toRadians(x));
  }

  /**
   * Returns the {@link #y} component.
   *
   * @return the {@link #y} component.
   */
  public float getY() {
    return y;
  }

  /**
   * Returns the {@link #y} component in degrees.
   *
   * @return the {@link #y} component in degrees.
   */
  public float getYDeg() {
    return (float) Math.toDegrees(y);
  }

  /**
   * Sets the value for the {@link #y} component.
   *
   * @param y {@link #y} component.
   */
  public void setY(double y) {
    this.y = (float) y;
  }

  /**
   * Sets the value for the {@link #y} component in degrees.
   *
   * @param y {@link #y} component in degrees.
   */
  public void setYDeg(double y) {
    setY(Math.toRadians(y));
  }

  /**
   * Returns the {@link #z} component.
   *
   * @return the {@link #z} component.
   */
  public float getZ() {
    return z;
  }

  /**
   * Returns the {@link #z} component in degrees.
   *
   * @return the {@link #z} component in degrees.
   */
  public float getZDeg() {
    return (float) Math.toDegrees(z);
  }

  /**
   * Sets the value for the {@link #z} component.
   *
   * @param z {@link #z} component.
   */
  public void setZ(double z) {
    this.z = (float) z;
  }

  /**
   * Sets the value for the {@link #z} component in degrees.
   *
   * @param z {@link #z} component in degrees.
   */
  public void setZDeg(double z) {
    setZ(Math.toRadians(z));
  }

  public Vector3f getOffset() {
    return new Vector3f(getX(), getY(), getZ());
  }

  public Vector3f getOffsetDeg() {
    return new Vector3f(getXDeg(), getYDeg(), getZDeg());
  }

  @Override
  public int hashCode() {
    return Objects.hash(x, y, z);
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
  public String toString() {
    return "Rotation{x=" + x + ", y=" + y + ", z=" + z + "}";
  }
}
