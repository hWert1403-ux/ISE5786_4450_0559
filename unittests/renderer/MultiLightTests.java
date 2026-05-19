package renderer;

import static java.awt.Color.BLUE;

import org.junit.jupiter.api.Test;

import geometries.api.Geometry;
import geometries.impl.Sphere;
import geometries.impl.Triangle;
import lighting.AmbientLight;
import lighting.DirectionalLight;
import lighting.PointLight;
import lighting.SpotLight;
import primitives.Color;
import primitives.Double3;
import primitives.Material;
import primitives.Point;
import primitives.Vector;
import scene.Scene;

/**
 * Test rendering images with multiple light sources combined in a single scene.
 */
class MultipleLightsTests {

	/** First scene for the sphere test */
	private final Scene _scene1 = new Scene("Sphere Multiple Lights Test");

	/** Second scene for the triangles test */
	private final Scene _scene2 = new Scene("Triangles Multiple Lights Test")
			.setAmbientLight(new AmbientLight(new Color(java.awt.Color.WHITE).reduce(6)));

	/** First camera builder for the sphere test */
	private final Camera.Builder _camera1 = Camera.getBuilder().setRayTracer(_scene1, RayTracerType.SIMPLE)
			.setLocation(new Point(0, 0, 1000)).setDirection(Point.ZERO, Vector.AXIS_Y).setVpSize(150, 150)
			.setVpDistance(1000);

	/** Second camera builder for the triangles test */
	private final Camera.Builder _camera2 = Camera.getBuilder().setRayTracer(_scene2, RayTracerType.SIMPLE)
			.setLocation(new Point(0, 0, 1000)).setDirection(Point.ZERO, Vector.AXIS_Y).setVpSize(200, 200)
			.setVpDistance(1000);

// --- Geometries and Materials (Copied exactly from LightsTests.java) ---
	private final Geometry SPHERE = new Sphere(new Point(0, 0, -50), 50).setEmission(new Color(BLUE).reduce(2))
			.setMaterial(new Material().setKD(0.5).setKS(0.5).setNShininess(301));

	private final Material TRIANGLE_MATERIAL = new Material().setKD(new Double3(0.2, 0.6, 0.4))
			.setKS(new Double3(0.2, 0.4, 0.3)).setNShininess(301);

	private final Geometry TRIANGLE1 = new Triangle(new Point(-110, -110, -150), new Point(95, 100, -150),
			new Point(110, -110, -150)).setMaterial(TRIANGLE_MATERIAL);

	private final Geometry TRIANGLE2 = new Triangle(new Point(-110, -110, -150), new Point(95, 100, -150),
			new Point(-75, 78, 100)).setMaterial(TRIANGLE_MATERIAL);

	/**
	 * Produce a picture of a sphere lighted by multiple light sources (Directional,
	 * Point, and Spot) working together.
	 */
	@Test
	void testSphereMultipleLights() {
		_scene1._geometries.add(SPHERE);

// Adding three different types of light sources using List.of and addAll
		_scene1.lights.addAll(java.util.List.of(
// 1. Directional Light (Yellowish) from top-right
				new DirectionalLight(new Color(400, 300, 0), new Vector(1, -1, -0.5)),

// 2. Point Light (Red) on the left side
				new PointLight(new Color(500, 0, 0), new Point(-60, 50, 40)).setKl(0.001).setKQ(0.0002),

// 3. Spot Light (Cyan/Blue-Green) focusing from the center-front
				new SpotLight(new Color(0, 400, 400), new Point(30, 30, 60), new Vector(-1, -1, -2)).setKl(0.001)
						.setKQ(0.0001)));
		_camera1.setResolution(500, 500).build().renderImage().writeToImage("lightSphereMultipleLights");
	}

	/**
	 * Produce a picture of two triangles lighted by multiple light sources
	 * (Directional, Point, and Spot) working together.
	 */
	@Test
	void testTrianglesMultipleLights() {
		_scene2._geometries.add(TRIANGLE1, TRIANGLE2);

// Adding three different types of light sources using List.of and addAll
		_scene2.lights.addAll(java.util.List.of(
// 1. Directional Light (Green) from top-left
				new DirectionalLight(new Color(0, 300, 0), new Vector(1, -1, -1)),

// 2. Point Light (Magenta/Purple) positioned near the right bottom
				new PointLight(new Color(400, 0, 400), new Point(50, -50, -100)).setKl(0.0008).setKQ(0.0004),

// 3. Spot Light (Orange/Yellow) aiming right at the center
				new SpotLight(new Color(500, 350, 150), new Point(30, 10, -80), new Vector(-2, -2, -2)).setKl(0.001)
						.setKQ(0.0001)));

// --- THE MISSING RENDER AND WRITE ACTIONS ---
		_camera2.setResolution(500, 500).build().renderImage().writeToImage("lightTrianglesMultipleLights");
	}
}