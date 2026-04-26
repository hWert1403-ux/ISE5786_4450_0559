package renderer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import geometries.api.Intersectable;
import geometries.impl.Plane;
import geometries.impl.Sphere;
import geometries.impl.Triangle;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

/**
 * Integration tests for Camera rays and Geometries intersections
 */
public class CameraIntersectionIntegration {

	/** Camera resolution for all integration tests */
	private final int nX = 3;
	private final int nY = 3;

	// initialize cameras
	private final Camera camera1 = Camera.getBuilder().setLocation(Point.ZERO)
			.setDirection(new Vector(0, 0, -1), new Vector(0, 1, 0)).setVpDistance(1).setVpSize(3, 3)
			.setResolution(nX, nY).build();

	private final Camera camera2 = Camera.getBuilder().setLocation(new Point(0, 0, 0.5))
			.setDirection(new Vector(0, 0, -1), new Vector(0, 1, 0)).setVpDistance(1).setVpSize(3, 3)
			.setResolution(nX, nY).build();

	/**
	 * Helper method to count intersections of all rays from a camera with a
	 * geometry
	 * 
	 * @param camera   The camera to shoot rays from
	 * @param body     The geometry to check intersections with
	 * @param expected The expected number of intersection points
	 * @param testName Name of the test for error reporting
	 */
	private void assertIntersectionsCount(Camera camera, Intersectable body, int expected, String testName) {
		int count = 0;

		// check all pixels (3x3)
		for (int i = 0; i < nY; ++i) {
			for (int j = 0; j < nX; ++j) {
				// create ray through mid pixel
				Ray ray = camera.constructRay(j, i);

				// calculate intersections
				var intersections = body.findIntersections(ray);

				// sum amount of intersections
				if (intersections != null) {
					count += intersections.size();
				}
			}
		}

		// Assert
		assertEquals(expected, count, "Wrong number of intersections for: " + testName);
	}

	/**
	 * test cases for sphere
	 */
	@Test
	public void testCameraRaySphereIntegration() {
		// Case 1: Sphere r=1 (2 intersections)
		assertIntersectionsCount(camera1, new Sphere(new Point(0, 0, -3), 1), 2, "Sphere Case 1");

		// Case 2: Sphere r=2.5 (18 intersections)
		assertIntersectionsCount(camera2, new Sphere(new Point(0, 0, -2.5), 2.5), 18, "Sphere Case 2");

		// Case 3: Sphere r=2 (10 intersections)
		assertIntersectionsCount(camera2, new Sphere(new Point(0, 0, -2), 2), 10, "Sphere Case 3");
	}

	/**
	 * test cases for plane
	 */
	@Test
	public void testCameraRayPlaneIntegration() {
		// Case 1: Plane parallel to VP (9 intersections)
		assertIntersectionsCount(camera1, new Plane(new Point(0, 0, -2), new Vector(0, 0, 1)), 9, "Plane Case 1");

		// Case 2: Tilted plane (9 intersections)
		assertIntersectionsCount(camera1, new Plane(new Point(0, 0, -1.5), new Vector(0, -0.5, 1)), 9, "Plane Case 2");

		// Case 3: Plane tilted away (6 intersections)
		assertIntersectionsCount(camera1, new Plane(new Point(0, 0, -5), new Vector(0, -1, 1)), 6, "Plane Case 3");
	}

	/**
	 * test cases for triangle
	 */
	@Test
	public void testCameraRayTriangleIntegration() {
		// Case 1: Small triangle (1 intersection)
		assertIntersectionsCount(camera1,
				new Triangle(new Point(0, 1, -2), new Point(1, -1, -2), new Point(-1, -1, -2)), 1, "Triangle Case 1");

		// Case 2: Large triangle (2 intersections)
		assertIntersectionsCount(camera1,
				new Triangle(new Point(0, 20, -2), new Point(1, -1, -2), new Point(-1, -1, -2)), 2, "Triangle Case 2");
	}
}