
package geometries.impl;

import static primitives.Util.alignZero;
import static primitives.Util.isZero;

import java.util.List;

import geometries.api.Geometry;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

/**
 * Class Plane represents a plane in 3D space. A plane is defined by a point and
 * a normal vector orthogonal to the plane. * @author hadas&shani
 */
public class Plane extends Geometry {
	/** A point on the plane */
	private final Point _point;
	/** The normal vector u to the plane */
	private final Vector _normal;

	/**
	 * Constructor to initialize a plane from three points. The points are expected
	 * to be ordered counter-clockwise. At this stage, only the first point is
	 * stored. * @param p1 first point
	 * 
	 * @param p2 second point
	 * @param p3 third point
	 */
	// public Plane(Point p1, Point p2, Point p3) {
	// _point = p1;
	// _normal = null; // Placeholder as per instructions for this stage
	// }
	public Plane(Point p1, Point p2, Point p3) {
		_point = p1;

		// 1. Calculating two vectors on the plane that originate from the same point
		Vector v1 = p2.subtract(p1);
		Vector v2 = p3.subtract(p1);

		// 2. Performing vector multiplication to obtain a vector perpendicular to the
		// plane
		Vector n = v1.crossProduct(v2);

		// 3. Normalize the vector so that its length is
		_normal = n.normalize();
	}

	/**
	 * Constructor to initialize a plane from a point and a normal vector. The
	 * normal vector is normalized to unit length. * @param point - a point on the
	 * plane
	 * 
	 * @param normal - the normal vector to the plane
	 */
	public Plane(Point point, Vector normal) {
		_point = point;
		_normal = normal.normalize();
	}

	@Override
	public Vector getNormal(Point point) {
		return _normal;
	}

	@Override
	public String toString() {
		return "Plane: point=" + _point + ", normal=" + _normal;
	}

	@Override
	public List<Point> findIntersections(Ray ray) {
		Point p0 = ray.origin();
		Vector v = ray.direction();
		Vector n = _normal;

		// n * v
		double nv = n.dotProduct(v);

		// If the ray is parallel to the plane (nv == 0), there are no intersections.
		// This also covers the case where the ray is included in the plane.
		if (isZero(nv))
			return null;

		// n * (_point - P0)
		// If P0 == _point, the vector _point-P0 will throw an exception (Zero Vector),
		// but this case means the ray starts on the plane, which should return null.
		Point Q0 = _point;
		try {
			Vector p0Q0 = Q0.subtract(p0);
			double nQ0MinusP0 = n.dotProduct(p0Q0);
			double t = alignZero(nQ0MinusP0 / nv);

			// Return intersection point only if t > 0 (strictly positive)
			return t <= 0 ? null : List.of(ray.getPoint(t));

		} catch (IllegalArgumentException e) {
			// This happens when p0 == q0 (ray starts at the plane's reference point)
			return null;
		}
	}

}
