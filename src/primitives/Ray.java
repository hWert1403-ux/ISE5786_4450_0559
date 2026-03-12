package primitives;

import java.util.Objects;

/**
 * Class Ray represents a semi-straight line in 3D space. The ray is defined by
 * an origin point and a normalized direction vector.
 * 
 * @author hadas&shani
 */
public final class Ray {
	/** The origin point of the ray */
	private final Point _origin;
	/** The normalized direction vector of the ray */
	private final Vector _direction;

	/**
	 * Constructor to initialize a Ray with an origin point and a direction vector.
	 * The direction vector is automatically normalized * @param origin - the
	 * starting point of the ray
	 * 
	 * @param direction - the direction vector (will be normalized)
	 */
	public Ray(Point origin, Vector direction) {
		_origin = origin;
		_direction = direction.normalize();
	}
	
	/**
	 * Getter for the direction vector of the ray.
	 * This matches the requirement of the sanity test.
	 * * @return the normalized direction vector
	 */
	public Vector direction() {
		return _direction;
	}

	@Override
	public String toString() {
	    return "Ray: origin=" + _origin + ", direction=" + _direction;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		Ray other = (Ray) obj;
		return this._origin.equals(other._origin) && this._direction.equals(other._direction);
	}

	@Override
	public int hashCode() {
		return Objects.hash(_origin, _direction);
	}
}