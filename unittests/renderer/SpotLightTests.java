package renderer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import lighting.SpotLight;
import primitives.Color;
import primitives.Point;
import primitives.Vector;

/**
 * Unit tests for the SpotLight class.
 */
public class SpotLightTests {

	/**
	 * Test method for {@link lighting.SpotLight#getIntensity(Point)}. Verifies that
	 * the light intensity is calculated correctly for a point directly in front of
	 * the spot light.
	 */
	@Test
	public void testGetIntensity() {
		SpotLight light = new SpotLight(new Color(100, 100, 100), new Point(0, 0, 0), new Vector(0, 0, 1));
		Point p = new Point(0, 0, 2); // Point is directly on the light axis, distance d = 2

		assertNotNull(light.getIntensity(p), "getIntensity should not return null");
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

		assertNotNull(light.getL(p), "getL should not return null");
		assertEquals(new Vector(0, 0, 1), light.getL(p), "Wrong direction vector from spot light");
	}
}
