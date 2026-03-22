package geometries;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import geometries.impl.Tube;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

/**
 * Unit tests for class {@link geometries.impl.Tube}.
 * The tests verify normal calculation for tubes in 3D space.
 * Tests follow the methodology of Equivalence Partitions (EP) and Boundary Values (BVA).
 * @author hadas&shani
 */
class TubeTests {

    /** Default constructor to satisfy JavaDoc generator */
    TubeTests() { /* to satisfy JavaDoc generator */ }

    /** Delta value for accuracy when comparing double values */
    private static final double DELTA = 1e-6;

    // ============ Static Test Data ============
    /** Axis ray for the tube (origin at 0,0,0, direction along Z axis) */
    private static final Ray AXIS_RAY = new Ray(new Point(0, 0, 0), new Vector(0, 0, 1));
    /** Radius for testing */
    private static final double RADIUS = 1d;
    
    /** vector (1, 0, 0) used for various tests */
	private static final Vector V1 = new Vector(1, 0, 0);

    // ============ Error Messages Constants ============
    /** Error message for wrong normal calculation */
    private static final String ERR_NORMAL = "ERROR: Tube getNormal() wrong result";
    /** Error message for non-unit normal vector */
    private static final String ERR_NORMAL_LENGTH = "ERROR: Tube normal is not a unit vector";

    /**
     * Test method for {@link geometries.impl.Tube#getNormal(primitives.Point)}.
     */
    @Test
    void testGetNormal() {
        // Arrange
        Tube tube = new Tube(AXIS_RAY, RADIUS);

        // ============ Equivalence Partitions Tests ==============

        // TC01: Point on the surface "in front of" the ray head (positive projection on axis)
        Point p1 = new Point(1, 0, 2); 
        Vector n1 = tube.getNormal(p1);
        
        assertEquals(1d, n1.length(), DELTA, ERR_NORMAL_LENGTH);
        assertEquals(V1, n1, ERR_NORMAL);

        // TC02: Point on the surface "behind" the ray head (negative projection on axis)
        Point p2 = new Point(1, 0, -2);
        Vector n2 = tube.getNormal(p2);

        assertEquals(1d, n2.length(), DELTA, ERR_NORMAL_LENGTH);
        assertEquals(V1, n2, ERR_NORMAL);

        // =============== Boundary Values Tests ==================

        // TC11: Point on the surface directly across from the ray head (projection is zero)
        Point pHead = V1;
        Vector nHead = tube.getNormal(pHead);

        assertEquals(1d, nHead.length(), DELTA, ERR_NORMAL_LENGTH);
        assertEquals(V1, nHead, ERR_NORMAL);
    }
}