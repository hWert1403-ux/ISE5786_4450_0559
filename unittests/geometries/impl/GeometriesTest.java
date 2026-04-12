package geometries.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

/**
 * Unit tests for geometries.impl.Geometries class.
 * 
 * @author hadas&shani
 */
class GeometriesTests {

	// ============ Static Test Data ============
	private static final Sphere SPHERE = new Sphere(new Point(1, 0, 0), 1d);
	private static final Plane PLANE = new Plane(new Point(0, 0, -1), new Vector(0, 0, 1));
	private static final Triangle TRIANGLE = new Triangle(new Point(1, 1, 0.5), new Point(2, 1, 0.5),
			new Point(1, 2, 0.5));

	// ============ Error Messages Constants ============
	private static final String ERR_INTERSECTIONS_NUM = "ERROR: Wrong number of intersections";
	private static final String ERR_SHOULD_BE_NULL = "ERROR: Ray should not intersect any geometry";
	private static final String ERR_SHOULD_NOT_BE_NULL = "ERROR: Ray should intersect geometries";

	/**
	 * Test method for
	 * {@link geometries.impl.Geometries#findIntersections(primitives.Ray)}.
	 */
	@Test
	void testFindIntersections() {
		Geometries geometries = new Geometries(SPHERE, PLANE, TRIANGLE);

		// ============ Equivalence Partitions Tests ==============

		// EP01: Some geometries intersect (but not all)
		// Ray intersects the Plane and the Sphere, but not the Triangle
		var resultEP01 = geometries.findIntersections(new Ray(new Point(0.5, 0, -2), new Vector(0, 0, 1)));
		assertNotNull(resultEP01, ERR_SHOULD_NOT_BE_NULL);
		assertEquals(3, resultEP01.size(), ERR_INTERSECTIONS_NUM); // 2 (sphere) + 1 (plane)

		// =============== Boundary Values Tests ==================

		// BVA01: Empty collection of geometries
		assertNull(new Geometries().findIntersections(new Ray(new Point(1, 1, 1), new Vector(0, 0, 1))),
				ERR_SHOULD_BE_NULL);

		// BVA02: No geometry is intersected
		assertNull(geometries.findIntersections(new Ray(new Point(10, 10, 10), new Vector(1, 1, 1))),
				ERR_SHOULD_BE_NULL);

		// BVA03: Only one geometry is intersected
		// Ray intersects only the Plane
		var resultBVA03 = geometries.findIntersections(new Ray(new Point(-1, -1, -2), new Vector(0, 0, 1)));
		assertNotNull(resultBVA03, ERR_SHOULD_NOT_BE_NULL);
		assertEquals(1, resultBVA03.size(), ERR_INTERSECTIONS_NUM);

		// BVA04: All geometries are intersected
		// Ray intersects Sphere (2), Triangle (1), and Plane (1)
		var resultBVA04 = geometries.findIntersections(new Ray(new Point(1.2, 1.2, -2), new Vector(0, 0, 1)));
		assertNotNull(resultBVA04, ERR_SHOULD_NOT_BE_NULL);
		assertEquals(4, resultBVA04.size(), "BVA04: " + ERR_INTERSECTIONS_NUM);
	}
}