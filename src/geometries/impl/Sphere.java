package geometries.impl;

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
	public List<Point> findIntersections(Ray ray) {
		// TODO Auto-generated method stub
		return null;
	}
}
