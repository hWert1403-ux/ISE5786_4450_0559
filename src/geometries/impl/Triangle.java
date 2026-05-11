package geometries.impl;

import static primitives.Util.alignZero;

import java.util.List;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

/**
 * Class Triangle represents a triangle in 3D space. It inherits from Polygon
 * and is defined by three vertices.
 * 
 * @author hadas&shani
 */
public class Triangle extends Polygon {

	/**
	 * Constructor to initialize a triangle from three points.
	 * 
	 * @param p1 first vertex
	 * @param p2 second vertex
	 * @param p3 third vertex
	 */

	public Triangle(Point p1, Point p2, Point p3) {
		super(p1, p2, p3);
	}

	@Override
	public String toString() {
		return "Triangle: " + super.toString();
	}

	@Override
	protected List<Intersection> calcIntersectionsHelper(Ray ray) {
		// Step 1: Find intersections with the plane containing the triangle
		// The triangle is a part of a plane, so if there's no intersection with the
		// plane,
		// there can't be one with the triangle.
		var planeIntersections = _plane.findIntersections(ray);
		if (planeIntersections == null)
			return null;

		// Step 2: Check if the intersection point is inside the triangle boundaries
		Point p0 = ray.origin();
		Vector v = ray.direction();

		// Vectors from the ray head to the triangle vertices
		Vector v1 = _vertices.get(0).subtract(p0);
		Vector v2 = _vertices.get(1).subtract(p0);
		Vector v3 = _vertices.get(2).subtract(p0);

		// Normal vectors to the planes created by the ray and the triangle edges
		// These define the boundaries of the pyramid formed by the ray head and the
		// triangle
		Vector n1 = v1.crossProduct(v2).normalize();
		Vector n2 = v2.crossProduct(v3).normalize();
		Vector n3 = v3.crossProduct(v1).normalize();

		// Dot product of the ray direction and each edge-plane normal
		double s1 = alignZero(v.dotProduct(n1));
		double s2 = alignZero(v.dotProduct(n2));
		double s3 = alignZero(v.dotProduct(n3));

		// If all dot products have the same sign, the point is inside.
		// We return a new Intersection with 'this' (the Triangle)
		if ((s1 > 0 && s2 > 0 && s3 > 0) || (s1 < 0 && s2 < 0 && s3 < 0)) {
			return List.of(new Intersection(this, planeIntersections.get(0)));
		}

		return null;
	}
}
