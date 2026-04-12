package geometries.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

/**
 * Unit tests for class {@link geometries.impl.Plane}. The tests verify
 * construction and normal calculation for planes in 3D space. Tests follow the
 * methodology of Equivalence Partitions (EP) and Boundary Values (BVA).
 * * @author hadas&shani
 */
class PlaneTests {

	/** Default constructor to satisfy JavaDoc generator */
	PlaneTests() {
		/* to satisfy JavaDoc generator */ }

	/** Delta value for accuracy when comparing double values */
	private static final double DELTA = 1e-6;

	/** Point (0, 0, 1) used for various tests */
	private static final Point P1 = new Point(0, 0, 1);
	/** Point (1, 0, 0) used for various tests */
	private static final Point P2 = new Point(1, 0, 0);
	/** Point (0, 1, 0) used for various tests */
	private static final Point P3 = new Point(0, 1, 0);

	/** verification Point (2, 0, -1) used for various tests */
	private static final Point P4 = new Point(2, 0, -1);
	/** verification Point (0.5, 0.5, 0) used for various tests */
	private static final Point P5 = new Point(0.5, 0.5, 0);

	/** non-unit vector (0, 0, 5) used for various tests */
	private static final Vector V1 = new Vector(0, 0, 5);

	// ============ Error Messages Constants ============
	/** Error message for wrong normal calculation */
	private static final String ERR_NORMAL = "ERROR: Plane getNormal() wrong result";
	/** Error message for wrong construction */
	private static final String ERR_CONSTRUCTOR = "ERROR: Plane constructor fails";
	/** Error message when an exception should have been thrown but wasn't */
	private static final String ERR_EXPECTED_EXCEPTION = "ERROR: Expected exception was not thrown";

	/**
	 * Test method for
	 * {@link geometries.impl.Plane#Plane(primitives.Point, primitives.Point, primitives.Point)}.
	 */
	@Test
	void testConstructorThreePoints() {
		// ============ Equivalence Partitions Tests ==============

		// TC01: Correct plane construction with 3 non-collinear points
		assertDoesNotThrow(() -> new Plane(P1, P2, P3), ERR_CONSTRUCTOR);

		// =============== Boundary Values Tests ==================

		// TC11: Two points are identical (co-located) - should throw exception
		assertThrows(IllegalArgumentException.class, () -> new Plane(P1, P1, P2), ERR_EXPECTED_EXCEPTION);

		// TC12: Three points are identical (1/3)
		assertThrows(IllegalArgumentException.class, () -> new Plane(P1, P1, P1), ERR_EXPECTED_EXCEPTION);

		// TC13: Three points are on the same line (collinear) - should throw exception
		// Point P4(2, 0, -1) is on the line passing through P1(0,0,1) and P2(1,0,0)
		assertThrows(IllegalArgumentException.class, () -> new Plane(P1, P2, P4), ERR_EXPECTED_EXCEPTION);
	}

	/**
	 * Test method for
	 * {@link geometries.impl.Plane#Plane(primitives.Point, primitives.Vector)}.
	 */
	@Test
	void testConstructorPointVector() {
		// ============ Equivalence Partitions Tests ==============

		// TC01: Test that the constructor normalizes the direction vector
		// Using a non-unit vector V1 (0, 0, 5)
		Plane plane = new Plane(P1, V1);
		Vector normal = plane.getNormal(P1);

		// Assert: check if the stored normal is a unit vector (length 1)
		assertEquals(1d, normal.length(), DELTA, ERR_CONSTRUCTOR);
	}

	/**
	 * Test method for {@link geometries.impl.Plane#getNormal(primitives.Point)}.
	 */
	@Test
	void testGetNormal() {
		Plane plane = new Plane(P1, P2, P3);

		// ============ Equivalence Partitions Tests ==============

		// TC01: Simple test for getNormal with a point on the plane (not the reference
		// point)
		// Point P5 (0.5, 0.5, 0) lies on the plane x+y+z=1
		Point pOnPlane = P5;
		Vector normal = plane.getNormal(pOnPlane);

		// Assert: check if the normal is a unit vector
		assertEquals(1d, normal.length(), DELTA, ERR_NORMAL);

		// Assert: check if the normal is orthogonal to a vector on the plane (P2-P1)
		assertEquals(0d, normal.dotProduct(P2.subtract(P1)), DELTA, ERR_NORMAL);

		// =============== Boundary Values Tests ==================

		// TC11: Test getNormal with the reference point itself (the one used during
		// construction)
		// This is a boundary case to ensure no zero-vector is created internally
		assertDoesNotThrow(() -> plane.getNormal(P1), ERR_NORMAL);
	}

	// ============ Intersection tests ============

	// ============ Static Test Data ============
	/** A plane on the XY axis (Z=0) with normal (0,0,1) */
	private static final Point P_ON_PLANE = new Point(0, 0, 0);
	private static final Vector V_NORMAL = new Vector(0, 0, 1);
	private static final Plane PLANE = new Plane(P_ON_PLANE, V_NORMAL);

	// ============ Error Messages Constants ============
	private static final String ERR_INTERSECTIONS_NUM = "ERROR: Wrong number of intersections";
	private static final String ERR_SHOULD_BE_NULL = "ERROR: Ray should not intersect the plane";
	private static final String ERR_SHOULD_NOT_BE_NULL = "ERROR: Ray should intersect the plane";

	/**
	 * Test method for
	 * {@link geometries.impl.Plane#findIntersections(primitives.Ray)}.
	 */
	@Test
	void testFindIntersections() {
		// ============ Equivalence Partitions Tests ==============

		// EP01: Ray intersects the plane (1 point)
		// Ray starts at (0,0,1) and goes towards the plane
		var resultEP01 = PLANE.findIntersections(new Ray(new Point(0, 0, 1), new Vector(1, 1, -1)));
		assertNotNull(resultEP01, ERR_SHOULD_NOT_BE_NULL);
		assertEquals(1, resultEP01.size(), ERR_INTERSECTIONS_NUM);

		// EP02: Ray does not intersect the plane (0 points)
		// Ray starts at (0,0,1) and goes away from the plane
		assertNull(PLANE.findIntersections(new Ray(new Point(0, 0, 1), new Vector(1, 1, 1))), ERR_SHOULD_BE_NULL);

		// =============== Boundary Values Tests ==================

		// **** Group 1: Ray is parallel to the plane
		// BV11: Ray included in the plane (0 points)
		assertNull(PLANE.findIntersections(new Ray(new Point(1, 1, 0), new Vector(1, 0, 0))),
				"BV11: Ray included in plane should return null");

		// BV12: Ray parallel to the plane, not included in it (0 points)
		assertNull(PLANE.findIntersections(new Ray(new Point(1, 1, 1), new Vector(1, 0, 0))),
				"BV12: Ray parallel to plane (above it) should return null");

		// **** Group 2: Ray is orthogonal to the plane
		// BV21: Ray starts before the plane (1 point)
		var resultBV21 = PLANE.findIntersections(new Ray(new Point(0, 0, 1), new Vector(0, 0, -1)));
		assertNotNull(resultBV21, ERR_SHOULD_NOT_BE_NULL);
		assertEquals(1, resultBV21.size(), ERR_INTERSECTIONS_NUM);

		// BV22: Ray starts at the plane (0 points)
		assertNull(PLANE.findIntersections(new Ray(new Point(1, 1, 0), new Vector(0, 0, -1))),
				"BV22: Orthogonal ray starting at plane should return null");

		// BV23: Ray starts after the plane (0 points)
		assertNull(PLANE.findIntersections(new Ray(new Point(0, 0, -1), new Vector(0, 0, -1))),
				"BV23: Orthogonal ray starting after plane should return null");

		// **** Group 3: Ray is neither orthogonal nor parallel
		// BV31: Ray starts at the plane (0 points)
		assertNull(PLANE.findIntersections(new Ray(new Point(1, 1, 0), new Vector(1, 1, 1))),
				"BV31: Ray starting at plane (diagonal) should return null");

		// BV32: Ray starts at the reference point of the plane (0 points)
		// במקום getPoint() השתמשתי בנקודה ששימשה לבניית המישור
		assertNull(PLANE.findIntersections(new Ray(P_ON_PLANE, new Vector(1, 1, 1))),
				"BV32: Ray starting at plane's reference point should return null");
	}
}
