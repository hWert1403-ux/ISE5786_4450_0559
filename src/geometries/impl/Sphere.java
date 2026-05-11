package geometries.impl;

import static primitives.Util.alignZero;
import static geometries.api.Intersectable.Intersection;
import java.util.List;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

/**
 * Class Sphere represents a sphere in 3D space. Defined by a center point and a
 * radius.
 * 
 * @author hadas&shani
 */

public class Sphere extends RadialGeometry {

	/**
	 * the center of the sphere
	 */
	public final Point _center;

	/**
	 * Constructor to initialize a sphere with center and radius.
	 * 
	 * @param radius- the radius of the sphere
	 * @param center- the center point
	 */
	public Sphere(Point center, double radius) {
		super(radius);
		this._center = center;

	}

	@Override
	public Vector getNormal(Point point) {
		// Normal to a sphere at point P is (P - Center) normalized
		return point.subtract(_center).normalize();
	}

	@Override
	public String toString() {
		return "Sphere: center=" + _center + ", " + super.toString();
	}

	@Override
	 protected List<Intersection> calcIntersectionsHelper(Ray ray) {
		Point p0 = ray.origin();
		Vector v = ray.direction();
		Vector l;

		try {
			l = _center.subtract(p0);
		} catch (IllegalArgumentException ignore) {
			// p0 is at the center, only one intersection at t = radius
			return List.of(new Intersection(this, ray.getPoint(_radius)));
		}

		double tm = alignZero(v.dotProduct(l));
		double dSquared = alignZero(l.lengthSquared() - tm * tm);
		double rSquared = _radius * _radius;

		// BV: Ray is outside or tangent to the sphere (d^2 >= r^2)
		if (alignZero(dSquared - rSquared) >= 0)
			return null;

		double th = alignZero(Math.sqrt(rSquared - dSquared));
		double t1 = alignZero(tm - th);
		double t2 = alignZero(tm + th);

		// Filter results: only t > 0 and no intersections at the ray head (t=0)
		boolean t1Valid = t1 > 0;
		boolean t2Valid = t2 > 0;

		if (!t1Valid && !t2Valid)
			return null;

		if (t1Valid && t2Valid) {
			// Return two intersections: each one contains 'this' (the sphere) and the point 
	        return t1 < t2 
	            ? List.of(new Intersection(this, ray.getPoint(t1)), new Intersection(this, ray.getPoint(t2))) 
	            : List.of(new Intersection(this, ray.getPoint(t2)), new Intersection(this, ray.getPoint(t1)));
		}

		// Only one intersection point is valid 
	    return t1Valid 
	        ? List.of(new Intersection(this, ray.getPoint(t1))) 
	        : List.of(new Intersection(this, ray.getPoint(t2)));
	}
}
