package geometries.impl;

import static primitives.Util.alignZero;
import static primitives.Util.isZero;

import java.util.List;

import geometries.api.Geometry;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

/**
 * Represents a convex polygon in a 3D Cartesian coordinate system.
 * <p>
 * The polygon is defined by an ordered sequence of vertices. All vertices must
 * lie in the same plane and be arranged along the polygon edge path.
 * </p>
 * <p>
 * The polygon must be convex.
 * </p>
 * 
 * @author Dan Zilberstein
 */
public class Polygon extends Geometry {
	/** Ordered list of polygon vertices */
	protected final List<Point> _vertices;
	/** Plane containing the polygon */
	protected final Plane _plane;
	/** Number of vertices */
	private final int _size;

	/**
	 * Constructs a convex polygon from ordered vertices.
	 * <p>
	 * The vertices must:
	 * </p>
	 * <ul>
	 * <li>Contain at least three points</li>
	 * <li>Be ordered along the polygon edge path</li>
	 * <li>Lie in the same plane</li>
	 * <li>Form a convex polygon</li>
	 * </ul>
	 * 
	 * @param vertices polygon vertices in edge order
	 * @throws IllegalArgumentException if the vertices do not form a valid convex
	 *                                  polygon
	 */
	public Polygon(Point... vertices) {
		if (vertices.length < 3)
			throw new IllegalArgumentException("A polygon can't have less than 3 vertices");
		_vertices = List.of(vertices);
		_size = vertices.length;

		// Create the supporting plane using the first three vertices.
		// The plane stores the constant normal of the polygon.
		_plane = new Plane(vertices[0], vertices[1], vertices[2]);
		if (_size == 3)
			return; // no need for more tests for a Triangle

		Vector n = _plane.getNormal(vertices[0]);
		// Subtracting identical vertices would create a zero vector (illegal)
		Vector edge1 = vertices[_size - 1].subtract(vertices[_size - 2]);
		Vector edge2 = vertices[0].subtract(vertices[_size - 1]);

		// Cross product of consecutive edges determines orientation.
		// All edge pairs must produce the same sign relative to the normal,
		// otherwise the polygon is concave or vertices are unordered.
		boolean positive = edge1.crossProduct(edge2).dotProduct(n) > 0;
		for (var i = 1; i < _size; ++i) {
			// Test that the point is in the same plane as calculated originally
			if (!isZero(vertices[i].subtract(vertices[0]).dotProduct(n)))
				throw new IllegalArgumentException("All vertices of a polygon must lay in the same plane");
			// Test the consequent edges have
			edge1 = edge2;
			edge2 = vertices[i].subtract(vertices[i - 1]);
			if (positive != (edge1.crossProduct(edge2).dotProduct(n) > 0))
				throw new IllegalArgumentException("All vertices must be ordered and the polygon must be convex");
		}
	}

	@Override
	public Vector getNormal(Point point) {
		return _plane.getNormal(point);
	}

	@Override
	public String toString() {
		return "Polygon: vertices=" + _vertices;
	}

	@Override
	protected List<Intersection> calcIntersectionsHelper(Ray ray) {
		// Step 1: Find intersection with the plane containing the polygon
		var planeIntersections = _plane.findIntersections(ray);
		if (planeIntersections == null)
			return null;

		// Step 2: Check if the intersection point is inside the polygon boundaries
		Point p0 = ray.origin();
		Vector v = ray.direction();
		int size = _vertices.size();

		// Calculate the first side's sign
		// Vectors from the ray head to the triangle vertices
		Vector v1 = _vertices.get(size - 1).subtract(p0);
		Vector v2 = _vertices.get(0).subtract(p0);

		// Normal to the plane formed by the ray and the first edge
		double sign = alignZero(v.dotProduct(v1.crossProduct(v2)));
		if (sign == 0)
			return null; // Point is on an edge/vertex

		boolean positive = sign > 0;

		// Iterate through the rest of the edges
		for (int i = 0; i < size - 1; ++i) {
			v1 = v2;
			v2 = _vertices.get(i + 1).subtract(p0);
			sign = alignZero(v.dotProduct(v1.crossProduct(v2)));

			// If sign is 0 or different from the first sign, point is outside
			if (sign == 0 || (sign > 0) != positive)
				return null;
		}

		// Step 3: All signs are the same - point is inside!
		// We return a new Intersection where the geometry is 'this' (the polygon)
		// and we take the point from the plane's calculation
		return List.of(new Intersection(this, planeIntersections.get(0)));

	}
}
