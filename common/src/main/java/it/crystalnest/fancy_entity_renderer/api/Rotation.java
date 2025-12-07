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
   * Creates a new rotation from the given radians of rotations.
   *
   * @param x {@link #x} in radians.
   * @param y {@link #y} in radians.
   * @param z {@link #z} in radians.
   * @return new rotation.
   */
  public static Rotation fromRad(float x, float y, float z) {
    return new Rotation(x, y, z);
  }

  /**
   * Creates a new rotation from the given degrees of rotations.
   *
   * @param x {@link #x} in degrees.
   * @param y {@link #y} in degrees.
   * @param z {@link #z} in degrees.
   * @return new rotation.
   */
  public static Rotation fromDeg(float x, float y, float z) {
    return new Rotation().setXDeg(x).setYDeg(y).setZDeg(z);
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
   * Sets the value for the {@link #x} component.
   *
   * @param x {@link #x} component.
   * @return this rotation.
   */
  public Rotation setX(double x) {
    this.x = (float) x;
    return this;
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
   * Sets the value for the {@link #x} component in degrees.
   *
   * @param x {@link #x} component in degrees.
   * @return this rotation.
   */
  public Rotation setXDeg(double x) {
    setX(Math.toRadians(x));
    return this;
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
   * Sets the value for the {@link #y} component.
   *
   * @param y {@link #y} component.
   * @return this rotation.
   */
  public Rotation setY(double y) {
    this.y = (float) y;
    return this;
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
   * Sets the value for the {@link #y} component in degrees.
   *
   * @param y {@link #y} component in degrees.
   * @return this rotation.
   */
  public Rotation setYDeg(double y) {
    setY(Math.toRadians(y));
    return this;
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
   * Sets the value for the {@link #z} component.
   *
   * @param z {@link #z} component.
   * @return this rotation.
   */
  public Rotation setZ(double z) {
    this.z = (float) z;
    return this;
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
   * Sets the value for the {@link #z} component in degrees.
   *
   * @param z {@link #z} component in degrees.
   * @return this rotation.
   */
  public Rotation setZDeg(double z) {
    setZ(Math.toRadians(z));
    return this;
  }

  /**
   * Returns a vector describing this rotation as an offset rotation.
   *
   * @return this rotation as a vector.
   */
  public Vector3f getOffset() {
    return new Vector3f(getX(), getY(), getZ());
  }

  /**
   * Returns a vector describing this rotation as an offset rotation in degrees.
   *
   * @return this rotation as a vector.
   */
  public Vector3f getOffsetDeg() {
    return new Vector3f(getXDeg(), getYDeg(), getZDeg());
  }

  /**
   * Updates this rotation to match the given rotation.
   *
   * @param rotation rotation to copy.
   * @return this rotation.
   */
  public Rotation copy(Rotation rotation) {
    return setX(rotation.getX()).setY(rotation.getY()).setZ(rotation.getZ());
  }

  /**
   * Updates this rotation with the provided values.
   *
   * @param x {@link #x} in radians.
   * @param y {@link #y} in radians.
   * @param z {@link #z} in radians.
   * @return this rotation.
   */
  public Rotation set(double x, double y, double z) {
    return setX(x).setY(y).setZ(z);
  }

  /**
   * Updates this rotation with the provided values.
   *
   * @param x {@link #x} in degrees.
   * @param y {@link #y} in degrees.
   * @param z {@link #z} in degrees.
   * @return this rotation.
   */
  public Rotation setDeg(double x, double y, double z) {
    return setXDeg(x).setYDeg(y).setZDeg(z);
  }

  /**
   * Adds the given rotation amounts.
   *
   * @param x {@link #x} in radians.
   * @param y {@link #y} in radians.
   * @param z {@link #z} in radians.
   * @return this rotation.
   */
  public Rotation add(double x, double y, double z) {
    return set(getX() + x, getY() + y, getZ() + z);
  }

  /**
   * Adds the given rotation amounts.
   *
   * @param x {@link #x} in degrees.
   * @param y {@link #y} in degrees.
   * @param z {@link #z} in degrees.
   * @return this rotation.
   */
  public Rotation addDegrees(double x, double y, double z) {
    return set(getX() + x, getY() + y, getZ() + z);
  }

  /**
   * Add the given {@link Rotation}.
   *
   * @param rotation {@link Rotation} to add.
   * @return this rotation.
   */
  public Rotation add(Rotation rotation) {
    return set(getX() + rotation.getX(), getY() + rotation.getY(), getZ() + rotation.getZ());
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
    return "Rotation{xr=" + getX() + ", yr=" + getY() + ", zr=" + getZ() + ", xd=" + getXDeg() + ", yd=" + getYDeg() + ", zd=" + getZDeg() + "}";
  }
}
