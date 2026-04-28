package primitives;

import static primitives.Util.isZero;

import java.util.List;
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
	 * Getter for the direction vector of the ray. This matches the requirement of
	 * the sanity test. * @return the normalized direction vector
	 */
	public Vector direction() {
		return _direction;
	}

	/**
	 * Getter for the origin vector of the ray. This matches the requirement of the
	 * sanity test. * @return the origin point
	 */
	public Point origin() {
		return _origin;
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

	/**
	 * Calculates a point on the line of the ray at distance t from the origin.
	 * 
	 * @param t distance from the ray origin (can be positive, negative, or zero)
	 * @return the calculated point
	 */
	public Point getPoint(double t) {
		// Using try-catch to handle the zero vector safely
		if (isZero(t)) {
			return _origin;
		}
		try {
			return _origin.add(_direction.scale(t));
		} catch (IllegalArgumentException ignore) {
			// Safety measure: if scaling results in a zero vector due to precision
			return _origin;
		}
	}
	
	/**
	 * find the colosest point of the intersection points list
	 * @param points - intersection points list
	 * @return closest point to origin, or null if the list is null
	 */
	public Point findClosestPoint(List<Point> points) {
		if(points == null)
			return null;
		
		Point closest = null;
	    double minDistance = Double.POSITIVE_INFINITY;

	    for (Point p : points) {
	        double distance = _origin.distanceSquared(p); // Use squared distance for efficiency 
	        if (distance < minDistance) {
	            minDistance = distance;
	            closest = p;
	        }
	    }
	    return closest;
	}
}