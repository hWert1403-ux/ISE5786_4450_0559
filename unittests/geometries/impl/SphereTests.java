package geometries.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;

import org.junit.jupiter.api.Test;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

/**
 * Unit tests for class {@link geometries.impl.Sphere}. The tests verify
 * construction and normal calculation for spheres in 3D space. Tests follow the
 * methodology of Equivalence Partitions (EP) and Boundary Values (BVA).
 * 
 * @author hadas&shani
 */
class SphereTests {

	/** Default constructor to satisfy JavaDoc generator */
	SphereTests() {
		/* to satisfy JavaDoc generator */ }

	/** Delta value for accuracy when comparing double values */
	private static final double DELTA = 1e-6;

	// ============ normal tests ============

	/** Center point for testing (0,0,0) */
	private static final Point CENTER = new Point(0, 0, 0);
	/** Point on the sphere's surface (0, 1, 0) */
	private static final Point P_SURFACE = new Point(0, 1, 0);
	/** Expected normal vector for point (0,1,0) when center is (0,0,0) */
	private static final Vector EXPECTED_NORMAL = new Vector(0, 1, 0);
	/** Radius for testing */
	private static final double RADIUS = 1d;

	// ============ Error Messages Constants ============
	/** Error message for wrong normal calculation */
	private static final String ERR_NORMAL = "ERROR: Sphere getNormal() wrong result";
	/** Error message for non-unit normal vector */
	private static final String ERR_NORMAL_LENGTH = "ERROR: Sphere normal is not a unit vector";

	/**
	 * Test method for {@link geometries.impl.Sphere#getNormal(primitives.Point)}.
	 */
	@Test
	void testGetNormal() {
		// ============ Equivalence Partitions Tests ==============
		// TC01: Simple test for getNormal on a sphere surface

		// Create a sphere with center (0,0,0) and radius 1
		Sphere sphere = new Sphere(CENTER, RADIUS);

		// Get the normal at point (0,1,0)
		Vector normal = sphere.getNormal(P_SURFACE);

		// 1. Check if the normal is a unit vector (length 1)
		assertEquals(1d, normal.length(), DELTA, ERR_NORMAL_LENGTH);

		// 2. Check if the normal is correct ( (P-O)/r )
		// In this case, for P(0,1,0) and O(0,0,0), the normal should be (0,1,0)
		assertEquals(EXPECTED_NORMAL, normal, ERR_NORMAL);
	}

	// ============ intersections tests ============
	/** Point (1,0,0) used in some tests */
	private static final Point P100 = new Point(1, 0, 0);
	/** Point (-1,0,0) used in some tests */
	private static final Point P01 = new Point(-1, 0, 0);
	/** Vector (1,1,0) used in some tests */
	private static final Vector V110 = new Vector(1, 1, 0);
	/** Vector (3,1,0) used in some tests */
	private static final Vector V310 = new Vector(3, 1, 0);

	/** Point used as intersection in some tests */
	private static final Point INTERSECTION1 = new Point(0.0651530771650466, 0.355051025721682, 0);
	/** Point used as intersection in some tests */
	private static final Point INTERSECTION2 = new Point(1.53484692283495, 0.844948974278318, 0);
	/** Expected list of intersections in some tests */
	private static final List<Point> EXPECTED1 = List.of(INTERSECTION1, INTERSECTION2);

	/** Sphere used in some tests (Center at P100, Radius 1) */
	private static final Sphere SPHERE = new Sphere(P100, 1d);

	// ============ Error Messages Constants ============
	private static final String ERR_INTERSECTIONS_NUM = "ERROR: Wrong number of intersections";
	private static final String ERR_INTERSECTIONS_PTS = "ERROR: Wrong intersection points";
	private static final String ERR_SHOULD_BE_NULL = "ERROR: Ray outside/after sphere should return null";
	private static final String ERR_SHOULD_NOT_BE_NULL = "ERROR: Ray crossing sphere should not return null";

	/**
	 * Test method for
	 * {@link geometries.impl.Sphere#findIntersections(primitives.Ray)}.
	 */
	@Test
	void testFindIntersections() {
		// ============ Equivalence Partitions Tests ==============

		// EP01: Ray's line is outside the sphere (0 points)
		assertNull(SPHERE.findIntersections(new Ray(P01, V110)), ERR_SHOULD_BE_NULL);

		// EP02: Ray starts before and crosses the sphere (2 points)
		final var resultEP02 = SPHERE.findIntersections(new Ray(P01, V310));
		assertNotNull(resultEP02, ERR_SHOULD_NOT_BE_NULL);
		assertEquals(2, resultEP02.size(), ERR_INTERSECTIONS_NUM);
		assertEquals(EXPECTED1, resultEP02, ERR_INTERSECTIONS_PTS);

		// EP03: Ray starts inside the sphere (1 point)
		final var resultEP03 = SPHERE.findIntersections(new Ray(new Point(0.5, 0, 0), new Vector(1, 0, 0)));
		assertEquals(1, resultEP03.size(), ERR_INTERSECTIONS_NUM);

		// EP04: Ray starts after the sphere (0 points)
		assertNull(SPHERE.findIntersections(new Ray(new Point(3, 0, 0), new Vector(1, 0, 0))), ERR_SHOULD_BE_NULL);

		// =============== Boundary Values Tests ==================

		// **** Group 1: Ray's line crosses the sphere (but not the center)
		// BV11: Ray starts at sphere and goes inside (1 points)
		assertEquals(1, SPHERE.findIntersections(new Ray(new Point(0, 0, 0), new Vector(1, 1, 0))).size(),
				ERR_INTERSECTIONS_NUM);

		// BV12: Ray starts at sphere and goes outside (0 points)
		assertNull(SPHERE.findIntersections(new Ray(new Point(0, 0, 0), new Vector(-1, -1, 0))), ERR_SHOULD_BE_NULL);

		// **** Group 2: Ray's line goes through the center
		// BV21: Ray starts before the sphere (2 points)
		final var resultBV21 = SPHERE.findIntersections(new Ray(new Point(-1, 0, 0), new Vector(1, 0, 0)));
		assertEquals(2, resultBV21.size(), ERR_INTERSECTIONS_NUM);

		// BV22: Ray starts at sphere and goes inside (1 points)
		assertEquals(1, SPHERE.findIntersections(new Ray(new Point(0, 0, 0), new Vector(1, 0, 0))).size(),
				ERR_INTERSECTIONS_NUM);

		// BV23: Ray starts inside (1 points)
		assertEquals(1, SPHERE.findIntersections(new Ray(new Point(0.5, 0, 0), new Vector(1, 0, 0))).size(),
				ERR_INTERSECTIONS_NUM);

		// BV24: Ray starts at the center (1 points)
		assertEquals(1, SPHERE.findIntersections(new Ray(P100, new Vector(0, 1, 0))).size(), ERR_INTERSECTIONS_NUM);

		// BV25: Ray starts at sphere and goes outside (0 points)
		assertNull(SPHERE.findIntersections(new Ray(new Point(0, 0, 0), new Vector(-1, 0, 0))), ERR_SHOULD_BE_NULL);

		// BV26: Ray starts after sphere (0 points)
		assertNull(SPHERE.findIntersections(new Ray(new Point(3, 0, 0), new Vector(1, 0, 0))), ERR_SHOULD_BE_NULL);

		// **** Group 3: Ray's line is tangent to the sphere (all tests 0 points)
		// BV31: Ray starts before the tangent point
		assertNull(SPHERE.findIntersections(new Ray(new Point(0, 1, 0), new Vector(1, 0, 0))), ERR_SHOULD_BE_NULL);
		// BV32: Ray starts at the tangent point
		assertNull(SPHERE.findIntersections(new Ray(new Point(1, 1, 0), new Vector(1, 0, 0))), ERR_SHOULD_BE_NULL);
		// BV33: Ray starts after the tangent point
		assertNull(SPHERE.findIntersections(new Ray(new Point(2, 1, 0), new Vector(1, 0, 0))), ERR_SHOULD_BE_NULL);

		// **** Group 4: Special cases
		// BV41: Ray's line is outside sphere, ray is orthogonal to ray start to
		// sphere's center line
		assertNull(SPHERE.findIntersections(new Ray(new Point(-1, 0, 0), new Vector(0, 1, 0))), ERR_SHOULD_BE_NULL);

		// BV42: Ray starts inside, ray is orthogonal to ray start to sphere's center
		// line
		final var resultBV42 = SPHERE.findIntersections(new Ray(new Point(0.5, 0, 0), new Vector(0, 1, 0)));
		assertNotNull(resultBV42, ERR_SHOULD_NOT_BE_NULL);
		assertEquals(1, resultBV42.size(), ERR_INTERSECTIONS_NUM);
	}

}