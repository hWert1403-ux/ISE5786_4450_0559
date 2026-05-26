//package renderer;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//
//import org.junit.jupiter.api.Test;
//
//import lighting.SpotLight;
//import primitives.Color;
//import primitives.Point;
//import primitives.Vector;
//
///**
// * Unit tests for the SpotLight class.
// */
//public class SpotLightTests {
//
//	/**
//	 * Test method for {@link lighting.SpotLight#getIntensity(Point)}. Verifies that
//	 * the light intensity is calculated correctly for a point directly in front of
//	 * the spot light.
//	 */
//	@Test
//	public void testGetIntensity() {
//		SpotLight light = new SpotLight(new Color(100, 100, 100), new Point(0, 0, 0), new Vector(0, 0, 1));
//		Point p = new Point(0, 0, 2); // Point is directly on the light axis, distance d = 2
//
//		assertNotNull(light.getIntensity(p), "getIntensity should not return null");
//		assertEquals(new Color(100, 100, 100), light.getIntensity(p), "Wrong intensity for spot light on axis");
//	}
//
//	/**
//	 * Test method for {@link lighting.SpotLight#getL(Point)}. Verifies that the
//	 * direction vector points from the spot light source position to the given
//	 * point.
//	 */
//	@Test
//	public void testGetL() {
//		SpotLight light = new SpotLight(new Color(100, 100, 100), new Point(0, 0, 0), new Vector(0, 0, 1));
//		Point p = new Point(0, 0, 3);
//
//		assertNotNull(light.getL(p), "getL should not return null");
//		assertEquals(new Vector(0, 0, 1), light.getL(p), "Wrong direction vector from spot light");
//	}
//}
package renderer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import lighting.SpotLight;
import primitives.Color;
import primitives.Point;
import primitives.Vector;

/**
 * Unit tests for the SpotLight class. * Tests follow the methodology of
 * Equivalence Partitions (EP) and Boundary Values (BVA).
 */
public class SpotLightTests {

	/** Default constructor to satisfy JavaDoc generator */
	public SpotLightTests() {
		/* to satisfy JavaDoc generator */ }

	// ============ Error Messages Constants ============
	/** Error message for wrong spotlight intensity result */
	private static final String ERR_INTENSITY = "ERROR: SpotLight getIntensity() wrong result";
	/** Error message for wrong direction vector result */
	private static final String ERR_L_VECTOR = "ERROR: SpotLight getL() wrong result";
	/** Error message when a return value is unexpectedly null */
	private static final String ERR_NULL_RETURN = "ERROR: Method returned null";

	/**
	 * Test method for {@link lighting.SpotLight#getIntensity(Point)}. Verifies that
	 * the light intensity is calculated correctly for a point directly in front of
	 * the spot light.
	 */
	@Test
	public void testGetIntensity() {
		SpotLight light = new SpotLight(new Color(100, 100, 100), new Point(0, 0, 0), new Vector(0, 0, 1));
		Point p = new Point(0, 0, 2); // Point is directly on the light axis, distance d = 2

		// ============ Equivalence Partitions Tests ==============
		// TC01: Point lies within the valid front hemisphere of the spotlight beam
		assertNotNull(light.getIntensity(p), "getIntensity should not return null");

		// =============== Boundary Values Tests ==================
		// TC11: Point is directly on the spotlight's main axis vector (angle is exactly
		// 0 degrees, cos(θ) = 1)
		assertEquals(new Color(100, 100, 100), light.getIntensity(p), "Wrong intensity for spot light on axis");
	}

	/**
	 * Test method for {@link lighting.SpotLight#getL(Point)}. Verifies that the
	 * direction vector points from the spot light source position to the given
	 * point.
	 */
	@Test
	public void testGetL() {
		SpotLight light = new SpotLight(new Color(100, 100, 100), new Point(0, 0, 0), new Vector(0, 0, 1));
		Point p = new Point(0, 0, 3);

		// ============ Equivalence Partitions Tests ==============
		// TC01: Valid point in space, ensuring a correct direction vector from light
		// source to target
		assertNotNull(light.getL(p), "getL should not return null");

		// =============== Boundary Values Tests ==================
		// TC11: Point is aligned precisely with the spotlight's primary direction
		// vector
		assertEquals(new Vector(0, 0, 1), light.getL(p), "Wrong direction vector from spot light");
	}
}