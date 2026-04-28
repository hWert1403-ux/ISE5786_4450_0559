package primitives;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for class {@link primitives.Ray}. The tests verify that the ray is
 * constructed correctly, especially the normalization of the direction vector.
 * Tests follow the methodology of Equivalence Partitions (EP) and Boundary
 * Values (BVA).
 * 
 * @author hadas&shani
 */
class RayTests {

	/** Default constructor to satisfy JavaDoc generator */
	RayTests() {
		/* to satisfy JavaDoc generator */ }

	/** Delta value for accuracy when comparing double values */
	private static final double DELTA = 1e-6;

	/** Static point for ray origin */
	private static final Point P1 = new Point(1, 2, 3);
	/** Static vector for ray direction (not normalized) */
	private static final Vector V1 = new Vector(2, 0, 0);

	// ============ Error Messages Constants ============
	/** Error message for wrong ray construction */
	private static final String ERR_CONSTRUCTOR = "ERROR: Ray constructor fails";
	/** Error message for direction vector not being normalized */
	private static final String ERR_NOT_NORMALIZED = "ERROR: Ray direction vector is not normalized";

	/**
	 * Test method for
	 * {@link primitives.Ray#Ray(primitives.Point, primitives.Vector)}.
	 */
	@Test
	void testConstructor() {
		// ============ Equivalence Partitions Tests ==============

		// TC01: Test that the constructor creates a valid ray and normalizes the
		// direction
		Ray ray = new Ray(P1, V1);

		// Arrange & Act: get the direction vector
		Vector direction = ray.direction();

		// Assert: check if direction is normalized (length should be 1)
		assertEquals(1d, direction.length(), DELTA, ERR_NOT_NORMALIZED);

		// Assert: check if the direction is in the same direction as the original
		// vector
		// (Cross product of parallel vectors should throw an exception or be zero)
		Vector normalizedV1 = V1.normalize();
		assertEquals(normalizedV1, direction, ERR_CONSTRUCTOR);
	}

	// ============ Refactoring: getPoint ============

	/** Error message for wrong point calculation */
	private static final String ERR_WRONG_POINT = "ERROR: getPoint() calculated wrong point";

	/**
	 * Test method for {@link primitives.Ray#getPoint(double)}.
	 */
	@Test
	void testGetPoint() {
		Ray ray = new Ray(new Point(1, 1, 1), new Vector(1, 0, 0));

		// ============ Equivalence Partitions Tests ==============

		// EP01: t is positive (t > 0)
		assertEquals(new Point(2, 1, 1), ray.getPoint(1), ERR_WRONG_POINT);

		// EP02: t is negative (t < 0)
		assertEquals(new Point(0, 1, 1), ray.getPoint(-1), ERR_WRONG_POINT);

		// =============== Boundary Values Tests ==================

		// BV01: t is zero (t = 0)
		assertEquals(new Point(1, 1, 1), ray.getPoint(0), ERR_WRONG_POINT);
	}

	/**
	 * Test method for {@link primitives.Ray#findClosestPoint(java.util.List)}.
	 */
	@Test
	void testFindClosestPoint() {
		Ray ray = new Ray(new Point(0, 0, 1), new Vector(0, 0, 1));
		Point p1 = new Point(0, 0, 2);
		Point p2 = new Point(0, 0, 3);
		Point p3 = new Point(0, 0, 4);

		// ============ Equivalence Partitions Tests =============

		// EP: The closest point is in the middle of the list
		List<Point> pointsMid = List.of(p2, p1, p3);
		assertEquals(p1, ray.findClosestPoint(pointsMid), "The point in the middle should be the closest");

		// =============== Boundary Values Tests ==================

		// BVA: The list is null
		assertNull(ray.findClosestPoint(null), "The method should return null for a null list");

		// BVA: The first point is the closest
		List<Point> pointsFirst = List.of(p1, p2, p3);
		assertEquals(p1, ray.findClosestPoint(pointsFirst), "The first point should be the closest");

		// BVA: The last point is the closest [cite: 47]
		List<Point> pointsLast = List.of(p2, p3, p1);
		assertEquals(p1, ray.findClosestPoint(pointsLast), "The last point should be the closest");
	}
}
