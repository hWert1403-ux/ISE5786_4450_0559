package geometries.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

/**
 * Unit tests for class {@link geometries.impl.Triangle}. The tests verify
 * construction and normal calculation for triangles in 3D space. Tests follow
 * the methodology of Equivalence Partitions (EP) and Boundary Values (BVA).
 * 
 * @author hadas&shani
 */
class TriangleTests {

	/** Default constructor to satisfy JavaDoc generator */
	TriangleTests() {
		/* to satisfy JavaDoc generator */ }

	/** Delta value for accuracy when comparing double values */
	private static final double DELTA = 1e-6;

	/** Point P1 (0, 0, 1) for triangle vertices */
	private static final Point P1 = new Point(0, 0, 1);
	/** Point P2 (1, 0, 0) for triangle vertices */
	private static final Point P2 = new Point(1, 0, 0);
	/** Point P3 (0, 1, 0) for triangle vertices */
	private static final Point P3 = new Point(0, 1, 0);

	// ============ Error Messages Constants ============
	/** Error message for wrong normal calculation */
	private static final String ERR_NORMAL = "ERROR: Triangle getNormal() wrong result";
	/** Error message for non-unit normal vector */
	private static final String ERR_NORMAL_LENGTH = "ERROR: Triangle normal is not a unit vector";

	/**
	 * Test method for {@link geometries.impl.Triangle#getNormal(primitives.Point)}.
	 */
	@Test
	void testGetNormal() {
		// ============ Equivalence Partitions Tests ==============
		// TC01: Simple test for getNormal with a point inside the triangle

		Triangle triangle = new Triangle(P1, P2, P3);

		Vector normal = triangle.getNormal(P1);

		// 1. Check if the normal is a unit vector (length 1)
		assertEquals(1d, normal.length(), DELTA, ERR_NORMAL_LENGTH);

		// 2. Check if the normal is orthogonal to the edges
		Vector v12 = P2.subtract(P1);
		Vector v13 = P3.subtract(P1);
		assertEquals(0d, normal.dotProduct(v12), DELTA, ERR_NORMAL);
		assertEquals(0d, normal.dotProduct(v13), DELTA, ERR_NORMAL);
	}

	// ============ intersection tests ============
	/** A triangle for the tests */
	private final Triangle tr = new Triangle(new Point(1, 0, 0), new Point(0, 1, 0), new Point(0, 0, 1));

	// ============ Error Messages Constants ============
	private static final String ERR_INTERSECTIONS_NUM = "ERROR: Wrong number of intersections";
	private static final String ERR_SHOULD_BE_NULL = "ERROR: Ray should not intersect the triangle";
	private static final String ERR_SHOULD_NOT_BE_NULL = "ERROR: Ray should intersect the triangle";

	/**
	 * Test method for
	 * {@link geometries.Triangle#findIntersections(primitives.Ray)}.
	 */
	@Test
	void testFindIntersections() {
		Point p1 = new Point(1, 0, 0);
		Point p2 = new Point(0, 1, 0);
		Point p3 = new Point(0, 0, 1);
		Triangle tr = new Triangle(p1, p2, p3);

		// ============ Equivalence Partitions Tests ==============

		// EP01: Ray intersects the triangle (1 point)
		// (Based on Plane EP01: Ray starts before and crosses the plane)
		Ray rayEP01 = new Ray(new Point(0.5, 0.5, 0.5), new Vector(-1, -1, -1));
		var resultEP01 = tr.findIntersections(rayEP01);
		assertNotNull(resultEP01, ERR_SHOULD_NOT_BE_NULL);
		assertEquals(1, resultEP01.size(), ERR_INTERSECTIONS_NUM);

		// EP02: Ray outside against edge (0 points)
		// (Unique to Triangle - intersection point is outside the triangle)
		Ray rayEP02 = new Ray(new Point(1, 1, 1), new Vector(1, 1, 1));
		assertNull(tr.findIntersections(rayEP02), ERR_SHOULD_BE_NULL);

		// EP03: Ray outside against vertex (0 points)
		// (Unique to Triangle - intersection point is outside the triangle)
		Ray rayEP03 = new Ray(new Point(0, 0, 2), new Vector(-1, -1, 0));
		assertNull(tr.findIntersections(rayEP03), ERR_SHOULD_BE_NULL);

//		 EP04: Ray starts before and goes away from plane (0 points)
//		 (Based on Plane EP02: No intersection with the plane at all)
//		Ray rayEP04 = new Ray(new Point(2, 2, 2), new Vector(1, 1, 1));
//		assertNull(tr.findIntersections(rayEP04), ERR_SHOULD_BE_NULL);

		// =============== Boundary Values Tests ==================

		// ---- Group 1: Based on Plane BVA (Parallel/Orthogonal/Starts on plane) ----

		// BV11: Ray parallel to triangle's plane (0 points)
		// (Based on Plane BV11/BV12)
		Ray rayBV11 = new Ray(new Point(1, 1, 1), new Vector(1, -1, 0));
		assertNull(tr.findIntersections(rayBV11), ERR_SHOULD_BE_NULL);

		// BV12: Ray starts at the plane (0 points)
		// (Based on Plane BV31)
		Ray rayBV12 = new Ray(new Point(0.5, 0.2, 0.3), new Vector(1, 0, 0));
		assertNull(tr.findIntersections(rayBV12), ERR_SHOULD_BE_NULL);

		// ---- Group 2: Unique to Triangle BVA (Intersection on edges/vertices) ----

		// BV21: Intersection on edge (0 points)
		Ray rayBV21 = new Ray(new Point(0.5, 0.5, -1), new Vector(0, 0, 1));
		assertNull(tr.findIntersections(rayBV21), ERR_SHOULD_BE_NULL);

		// BV22: Intersection on vertex (0 points)
		Ray rayBV22 = new Ray(new Point(1, 0, -1), new Vector(0, 0, 1));
		assertNull(tr.findIntersections(rayBV22), ERR_SHOULD_BE_NULL);

		// BV23: Intersection on edge's continuation (0 points)
		Ray rayBV23 = new Ray(new Point(2, -1, -1), new Vector(0, 0, 1));
		assertNull(tr.findIntersections(rayBV23), ERR_SHOULD_BE_NULL);
	}
}
