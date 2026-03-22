package geometries.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import primitives.Point;
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
}