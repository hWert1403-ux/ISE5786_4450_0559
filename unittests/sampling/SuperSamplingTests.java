package sampling;

import org.junit.jupiter.api.Test;

import geometries.impl.Sphere;
import geometries.impl.Triangle;
import lighting.SpotLight;
import primitives.Color;
import primitives.Material;
import primitives.Point;
import primitives.Vector;
import renderer.Camera;
import renderer.RayTracerType;
import scene.Scene;

/**
 * Visual integration tests for the Anti-Aliasing Super-Sampling extension.
 * Compares a standard single-ray render against a multi-ray beam render using
 * the project's exact style.
 */
class SuperSamplingTests {
	/** Default constructor to satisfy JavaDoc generator */
	SuperSamplingTests() {
	}

	/** Scene for the tests */
	private final Scene _scene = new Scene("Test scene");
	/** Camera builder for the tests using the configured ray tracer type */
	private final Camera.Builder _cameraBuilder = Camera.getBuilder().setRayTracer(_scene, RayTracerType.SIMPLE);

	/**
	 * Produce a picture of a sphere and sharp triangle with Super-Sampling DISABLED
	 * to capture aliasing artifacts
	 */
	@Test
	@SuppressWarnings("java:S109")
	void testSuperSamplingDisabled() {
		_scene._geometries.add(new Sphere(new Point(0, 0, -50), 50D).setEmission(new Color(0, 0, 200)) // Blue
																										// sphere
				.setMaterial(new Material().setKD(0.4).setKS(0.3).setNShininess(100)),
				new Triangle(new Point(-60, -60, -80), new Point(60, -60, -80), new Point(0, 60, -80)) // Sharp
																										// diagonal
																										// triangle
						.setEmission(new Color(200, 0, 0)) // Red triangle
						.setMaterial(new Material().setKD(0.2).setKS(0.2).setNShininess(10)));
		_scene.lights.add(new SpotLight(new Color(1000, 600, 0), new Point(-100, -100, 500), new Vector(-1, -1, -2)) //
				.setKl(0.0004).setKQ(0.0000006));

		_cameraBuilder.setLocation(new Point(0, 0, 1000)) //
				.setDirection(Point.ZERO, Vector.AXIS_Y) //
				.setVpDistance(1000).setVpSize(150, 150) //
				.setResolution(500, 500) //
				.setSuperSampling(false, 1, 1, 1.0, SamplingGrid.AreaShape.CIRCLE, SamplingGrid.SamplingPattern.REGULAR) // Disabled
				.build() //
				.renderImage() //
				.writeToImage("superSamplingDisabled");
	}

	/**
	 * Produce a picture of the exact same scene with Super-Sampling ENABLED to
	 * demonstrate smooth edges
	 */
	@Test
	@SuppressWarnings("java:S109")
	void testSuperSamplingEnabled() {
		_scene._geometries.add( //
				new Sphere(new Point(0, 0, -50), 50D).setEmission(new Color(0, 0, 200)) // Blue sphere
						.setMaterial(new Material().setKD(0.4).setKS(0.3).setNShininess(100)), //
				new Triangle(new Point(-60, -60, -80), new Point(60, -60, -80), new Point(0, 60, -80)) // Sharp
																										// diagonal
																										// triangle
						.setEmission(new Color(200, 0, 0)) // Red triangle
						.setMaterial(new Material().setKD(0.2).setKS(0.2).setNShininess(10))); //
		_scene.lights.add(new SpotLight(new Color(1000, 600, 0), new Point(-100, -100, 500), new Vector(-1, -1, -2)) //
				.setKl(0.0004).setKQ(0.0000006));

		_cameraBuilder.setLocation(new Point(0, 0, 1000)) //
				.setDirection(Point.ZERO, Vector.AXIS_Y) //
				.setVpDistance(1000).setVpSize(150, 150) //
				.setResolution(500, 500) //
				.setSuperSampling(true, 9, 9, 1.0, SamplingGrid.AreaShape.CIRCLE, SamplingGrid.SamplingPattern.JITTERED) // Enabled
																															// with
																															// 9x9
																															// //
																															// grid
				.build() //
				.renderImage() //
				.writeToImage("superSamplingEnabled");
	}

}
