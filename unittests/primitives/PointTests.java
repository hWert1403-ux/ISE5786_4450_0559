package primitives;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for class {@link Point}. The tests verify the basic functionality
 * of point operations like addition, subtraction and distance calculations. *
 * Tests follow the methodology of Equivalence Partitions (EP) and Boundary
 * Values (BVA). * @author hadas&shani
 */
class PointTests {

	/** Default constructor to satisfy JavaDoc generator */
	PointTests() {
		/* to satisfy JavaDoc generator */ }

	/** Delta value for accuracy when comparing double values. */
	private static final double DELTA = 1e-6;

	/** Point (1, 2, 3) used for various tests */
	private static final Point P1 = new Point(1, 2, 3);
	/** Point (2, 4, 6) used for various tests */
	private static final Point P2 = new Point(2, 4, 6);
	/** Point (0, 0, 0) used for various tests */
	private static final Point Pzero = new Point(0, 0, 0);
	/** Vector (1, 2, 3) used for various tests */
	private static final Vector V1 = new Vector(1, 2, 3);
	/** vector (-1, -2, -3) used for various tests */
	private static final Vector V1opposite = new Vector(-1, -2, -3);

	// ============ Error Messages Constants ============
	/** Error message for wrong point addition result */
	private static final String ERR_ADD = "ERROR: Point add() wrong result";
	/** Error message for wrong point subtraction result */
	private static final String ERR_SUBTRACT = "ERROR: Point subtract() wrong result";
	/** Error message for distance calculation failure */
	private static final String ERR_DISTANCE = "ERROR: Point distance() wrong result";
	/** Error message when an exception should have been thrown but wasn't */
	private static final String ERR_EXPECTED_EXCEPTION = "ERROR: Expected exception was not thrown";

	/**
	 * Test method for {@link primitives.Point#subtract(primitives.Point)}.
	 */
	@Test
	void testSubtract() {
		// ============ Equivalence Partitions Tests ==============
		// TC01: Simple subtract
		assertEquals(V1, P2.subtract(P1), ERR_SUBTRACT);

		// =============== Boundary Values Tests ==================
		// TC11: subtract point from itself (should throw exception for zero vector)
		assertThrows(IllegalArgumentException.class, () -> P1.subtract(P1), ERR_SUBTRACT);
	}

	/**
	 * Test method for {@link primitives.Point#add(primitives.Vector)}.
	 */
	@Test
	void testAdd() {
		// ============ Equivalence Partitions Tests ==============
		// TC01: Simple add
		assertEquals(P2, P1.add(V1), ERR_ADD);

		// =============== Boundary Values Tests ==================
		// TC11: add opposite vector (result is center of coordinates)
		assertEquals(Pzero, P1.add(V1opposite), ERR_ADD);
	}

	/**
	 * Test method for {@link primitives.Point#distanceSquared(primitives.Point)}.
	 */
	@Test
	void testDistanceSquared() {
		// ============ Equivalence Partitions Tests ==============
		// TC01: Simple distance squared
		assertEquals(14d, P1.distanceSquared(Pzero), DELTA, ERR_DISTANCE);

		// =============== Boundary Values Tests ==================
		// TC11: distance squared to itself
		assertEquals(0d, P1.distanceSquared(P1), DELTA, ERR_DISTANCE);
	}

	/**
	 * Test method for {@link primitives.Point#distance(primitives.Point)}.
	 */
	@Test
	void testDistance() {
		// ============ Equivalence Partitions Tests ==============
		// TC01: Simple distance
		assertEquals(Math.sqrt(14), P1.distance(Pzero), DELTA, ERR_DISTANCE);

		// =============== Boundary Values Tests ==================
		// TC11: distance to itself
		assertEquals(0d, P1.distance(P1), DELTA, ERR_DISTANCE);
	}
}