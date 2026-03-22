package primitives;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for class {@link primitives.Vector}. The tests verify vector
 * operations such as addition, subtraction, dot product, cross product, and
 * normalization. Tests follow the methodology of Equivalence Partitions (EP)
 * and Boundary Values (BVA).
 * 
 * @author hadas&shani
 */
class VectorTests {

	/** Default constructor to satisfy JavaDoc generator */
	VectorTests() {
		/* to satisfy JavaDoc generator */ }

	/** Delta value for accuracy when comparing double values */
	private static final double DELTA = 1e-6;

	/** vector (1, 2, 3) used for various tests */
	private static final Vector V1 = new Vector(1, 2, 3);
	/** vector (-1, -2, -3) used for various tests */
	private static final Vector V1opposite = new Vector(-1, -2, -3);
	/** vector (0, 3, -2) used for various tests */
	private static final Vector V2 = new Vector(0, 3, -2);
	/** vector (-2, -4, -6) used for various tests */
	private static final Vector V3 = new Vector(-2, -4, -6);
	/** result vector (1, 5, 1) used for various tests */
	private static final Vector V4 = new Vector(1, 5, 1);
	/** result vector (1, -1, 5) used for various tests */
	private static final Vector V5 = new Vector(1, -1, 5);

	// ============ Error Messages Constants ============
	/** Error message for wrong vector addition result */
	private static final String ERR_ADD = "ERROR: Vector add() wrong result";
	/** Error message for wrong vector subtraction result */
	private static final String ERR_SUBTRACT = "ERROR: Vector subtract() wrong result";
	/** Error message for wrong dot product result */
	private static final String ERR_DOT_PRODUCT = "ERROR: Vector dotProduct() wrong result";
	/** Error message for wrong cross product result */
	private static final String ERR_CROSS_PRODUCT = "ERROR: Vector crossProduct() wrong result";
	/** Error message for wrong vector length calculation */
	private static final String ERR_LENGTH = "ERROR: Vector length() wrong result";
	/** Error message for normalization failure */
	private static final String ERR_NORMALIZE = "ERROR: Vector normalize() wrong result";
	/** Error message when an exception should have been thrown but wasn't */
	private static final String ERR_EXPECTED_EXCEPTION = "ERROR: Expected exception was not thrown";

	/**
	 * Test method for {@link primitives.Vector#add(primitives.Vector)}.
	 */
	@Test
	void testAdd() {
		// ============ Equivalence Partitions Tests ==============
		// TC01: Simple vector addition
		assertEquals(V4, V1.add(V2), ERR_ADD);

		// =============== Boundary Values Tests ==================
		// TC11: Addition resulting in zero vector (should throw exception)
		assertThrows(IllegalArgumentException.class, () -> V1.add(V1opposite), ERR_EXPECTED_EXCEPTION);
	}

	/**
	 * Test method for {@link primitives.Vector#subtract(primitives.Point)}.
	 */
	@Test
	void testSubtract() {
		// ============ Equivalence Partitions Tests ==============
		// TC01: Simple vector subtraction
		assertEquals(V5, V1.subtract(V2), ERR_SUBTRACT);

		// =============== Boundary Values Tests ==================
		// TC11: Subtraction resulting in zero vector (should throw exception)
		assertThrows(IllegalArgumentException.class, () -> V1.subtract(V1), ERR_EXPECTED_EXCEPTION);
	}

	/**
	 * Test method for {@link primitives.Vector#dotProduct(primitives.Vector)}.
	 */
	@Test
	void testDotProduct() {
		// ============ Equivalence Partitions Tests ==============
		// TC01: Simple dot product
		assertEquals(0d, V1.dotProduct(V2), DELTA, ERR_DOT_PRODUCT);

		// =============== Boundary Values Tests ==================
		// TC11: Dot product with itself (squared length)
		assertEquals(14d, V1.dotProduct(V1), DELTA, ERR_DOT_PRODUCT);
	}

	/**
	 * Test method for {@link primitives.Vector#crossProduct(primitives.Vector)}.
	 */
	@Test
	void testCrossProduct() {
		// ============ Equivalence Partitions Tests ==============
		Vector vr = V1.crossProduct(V2);
		// TC01: Check length of cross product
		assertEquals(V1.length() * V2.length(), vr.length(), DELTA, ERR_CROSS_PRODUCT);
		// TC02: Check orthogonality to operands
		assertEquals(0d, vr.dotProduct(V1), DELTA, ERR_CROSS_PRODUCT);
		assertEquals(0d, vr.dotProduct(V2), DELTA, ERR_CROSS_PRODUCT);

		// =============== Boundary Values Tests ==================
		// TC11: Cross product of parallel vectors
		assertThrows(IllegalArgumentException.class, () -> V1.crossProduct(V3), ERR_EXPECTED_EXCEPTION);
	}

	/**
	 * Test method for {@link primitives.Vector#length()}.
	 */
	@Test
	void testLength() {
		// ============ Equivalence Partitions Tests ==============
		// TC01: Simple length calculation
		assertEquals(Math.sqrt(14), V1.length(), DELTA, ERR_LENGTH);
	}

	/**
	 * Test method for {@link primitives.Vector#normalize()}.
	 */
	@Test
	void testNormalize() {
		Vector v = new Vector(0, 3, 4);
		Vector u = v.normalize();
		// ============ Equivalence Partitions Tests ==============
		// TC01: Normalized vector is a unit vector
		assertEquals(1d, u.length(), DELTA, ERR_NORMALIZE);
		// TC02: Normalized vector is parallel to the original
		assertThrows(IllegalArgumentException.class, () -> v.crossProduct(u), ERR_NORMALIZE);
		// TC03: Normalized vector is in the same direction
		assertEquals(5d, v.dotProduct(u), DELTA, ERR_NORMALIZE);

	}
}