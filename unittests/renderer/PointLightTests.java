package renderer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import lighting.PointLight;
import primitives.Color;
import primitives.Point;
import primitives.Vector;

/**
 * Unit tests for the PointLight class.
 */
public class PointLightTests {

	/**
	 * Test method for {@link lighting.PointLight#getIntensity(Point)}. Verifies
	 * that the light intensity is calculated correctly using default attenuation
	 * coefficients (kc=1, kl=0, kq=0).
	 */
	@Test
	public void testGetIntensity() {
		PointLight light = new PointLight(new Color(100, 100, 100), new Point(0, 0, 0));
		Point p = new Point(0, 0, 2); // Distance d = 2

		assertNotNull(light.getIntensity(p), "getIntensity should not return null");
		assertEquals(new Color(100, 100, 100), light.getIntensity(p),
				"Wrong intensity for point light with default attenuation");
	}

	/**
	 * Test method for {@link lighting.PointLight#getL(Point)}. Verifies that the
	 * direction vector points from the light source position to the given point.
	 */
	@Test
	public void testGetL() {
		PointLight light = new PointLight(new Color(100, 100, 100), new Point(0, 0, 0));
		Point p = new Point(0, 0, 5);

		assertNotNull(light.getL(p), "getL should not return null");
		assertEquals(new Vector(0, 0, 1), light.getL(p), "Wrong direction vector from point light");
	}
}
