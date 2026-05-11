package primitives;

import static primitives.Util.isZero;

import java.util.List;
import java.util.Objects;

import geometries.api.Intersectable.Intersection;

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
	 * Finds the closest intersection point to the ray origin from a list of
	 * intersections. This method is the new standard for finding intersections
	 * including geometry data. * @param intersections List of intersections
	 * (geometry and point)
	 * 
	 * @return The closest intersection, or null if the list is empty or null
	 */
	public Intersection findClosestIntersection(List<Intersection> intersections) {
		if (intersections == null || intersections.isEmpty())
			return null;

		Intersection closest = null;
		double minDistance = Double.POSITIVE_INFINITY;

		for (var intersection : intersections) {
			double distance = _origin.distance(intersection.point);
			if (distance < minDistance) {
				minDistance = distance;
				closest = intersection;
			}
		}
		return closest;
	}

	/**
	 * Finds the closest point to the ray origin from a list of points. This is a
	 * wrapper method for backward compatibility with previous stages. * @param
	 * points List of intersection points
	 * 
	 * @return The closest point, or null if the list is empty or null
	 */
	public Point findClosestPoint(List<Point> points) {
		return points == null ? null
				: findClosestIntersection(points.stream().map(point -> new Intersection(null, point)).toList()).point;
	}

}