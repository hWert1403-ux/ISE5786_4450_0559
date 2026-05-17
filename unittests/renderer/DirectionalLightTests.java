package renderer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import lighting.DirectionalLight;
import primitives.Color;
import primitives.Point;
import primitives.Vector;

/**
 * Unit tests for the DirectionalLight class.
 */
public class DirectionalLightTests {

	/**
	 * Test method for {@link lighting.DirectionalLight#getIntensity(Point)}.
	 * Verifies that the light intensity is constant and does not change with
	 * distance.
	 */
	@Test
	public void testGetIntensity() {
		DirectionalLight light = new DirectionalLight(new Color(255, 255, 255), new Vector(0, 0, -1));
		Point p = new Point(1, 2, 3);

		assertNotNull(light.getIntensity(p), "getIntensity should not return null");
		assertEquals(new Color(255, 255, 255), light.getIntensity(p), "Wrong intensity for directional light");
	}

	/**
	 * Test method for {@link lighting.DirectionalLight#getL(Point)}. Verifies that
	 * the direction vector from the light to any point is always the light's
	 * direction.
	 */
	@Test
	public void testGetL() {
		DirectionalLight light = new DirectionalLight(new Color(255, 255, 255), new Vector(0, 0, -1));
		Point p = new Point(1, 2, 3);

		assertNotNull(light.getL(p), "getL should not return null");
		assertEquals(new Vector(0, 0, -1).normalize(), light.getL(p), "Wrong direction vector (getL)");
	}
}