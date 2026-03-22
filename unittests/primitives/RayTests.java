package primitives;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}