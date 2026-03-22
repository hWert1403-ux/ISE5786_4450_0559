package geometries;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import geometries.impl.Triangle;
import primitives.Point;
import primitives.Vector;

/**
 * Unit tests for class {@link geometries.impl.Triangle}.
 * The tests verify construction and normal calculation for triangles in 3D space.
 * Tests follow the methodology of Equivalence Partitions (EP) and Boundary Values (BVA).
 * @author hadas&shani
 */
class TriangleTests {

    /** Default constructor to satisfy JavaDoc generator */
    TriangleTests() { /* to satisfy JavaDoc generator */ }

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
}