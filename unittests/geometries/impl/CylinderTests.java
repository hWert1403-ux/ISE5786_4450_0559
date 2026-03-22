package geometries.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

/**
 * Unit tests for class {@link geometries.impl.Cylinder}. Tests follow the
 * methodology of Equivalence Partitions (EP) and Boundary Values (BVA).
 * 
 * @author hadas&shani
 */
class CylinderTests {

	/** Default constructor to satisfy JavaDoc generator */
	CylinderTests() {
		/* to satisfy JavaDoc generator */ }

	/** Delta value for accuracy when comparing double values */
	private static final double DELTA = 1e-6;

	// ============ Static Test Data ============
	/** Axis ray: from (0,0,0) towards (0,0,1) */
	private static final Ray AXIS_RAY = new Ray(new Point(0, 0, 0), new Vector(0, 0, 1));
	/** Cylinder height and radius */
	private static final double HEIGHT = 2d;
	private static final double RADIUS = 1d;

	// ============ Error Messages Constants ============
	/** Error message for wrong normal calculation */
	private static final String ERR_NORMAL = "ERROR: Cylinder getNormal() wrong result";
	/** Error message for non-unit normal vector */
	private static final String ERR_NORMAL_LENGTH = "ERROR: Cylinder normal is not a unit vector";

	/**
	 * Test method for {@link geometries.impl.Cylinder#getNormal(primitives.Point)}.
	 */
	@Test
	void testGetNormal() {
		// Arrange
		Cylinder cylinder = new Cylinder(RADIUS, AXIS_RAY, HEIGHT);

		// ============ Equivalence Partitions Tests ==============

		// TC01: Point on the side shell (like Tube)
		Point pSide = new Point(1, 0, 1);
		Vector nSide = cylinder.getNormal(pSide);
		assertEquals(1d, nSide.length(), DELTA, ERR_NORMAL_LENGTH);
		assertEquals(new Vector(1, 0, 0), nSide, ERR_NORMAL);

		// TC02: Point on the bottom base (z=0)
		Point pBottom = new Point(0.5, 0, 0);
		Vector nBottom = cylinder.getNormal(pBottom);
		assertEquals(1d, nBottom.length(), DELTA, ERR_NORMAL_LENGTH);
		assertEquals(new Vector(0, 0, -1), nBottom, ERR_NORMAL);

		// TC03: Point on the top base (z=2)
		Point pTop = new Point(0.5, 0, 2);
		Vector nTop = cylinder.getNormal(pTop);
		assertEquals(1d, nTop.length(), DELTA, ERR_NORMAL_LENGTH);
		assertEquals(new Vector(0, 0, 1), nTop, ERR_NORMAL);

		// =============== Boundary Values Tests ==================

		// TC11: Point at the center of the bottom base (Ray head)
		Point pCenterBottom = new Point(0, 0, 0);
		Vector nCenterBottom = cylinder.getNormal(pCenterBottom);
		assertEquals(new Vector(0, 0, -1), nCenterBottom, ERR_NORMAL);

		// TC12: Point at the center of the top base
		Point pCenterTop = new Point(0, 0, 2);
		Vector nCenterTop = cylinder.getNormal(pCenterTop);
		assertEquals(new Vector(0, 0, 1), nCenterTop, ERR_NORMAL);

		// TC13: Point on the edge of the bottom base (connection between shell and
		// base)
		// According to the logic, it can be either shell normal or base normal.
		// Usually, we return the base normal.
		Point pEdgeBottom = new Point(1, 0, 0);
		Vector nEdgeBottom = cylinder.getNormal(pEdgeBottom);
		assertEquals(new Vector(0, 0, -1), nEdgeBottom, "TC13: " + ERR_NORMAL);

		// TC14: Point on the edge of the top base
		Point pEdgeTop = new Point(1, 0, 2);
		Vector nEdgeTop = cylinder.getNormal(pEdgeTop);
		assertEquals(new Vector(0, 0, 1), nEdgeTop, "TC14: " + ERR_NORMAL);
	}
}