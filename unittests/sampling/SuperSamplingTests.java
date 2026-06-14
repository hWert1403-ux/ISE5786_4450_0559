package sampling;

import org.junit.jupiter.api.Test;

import geometries.impl.Sphere;
import geometries.impl.Triangle;
import lighting.AmbientLight;
import lighting.DirectionalLight;
import lighting.PointLight;
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
 * Compares standard single-ray render against multi-ray beam renders, while
 * measuring performance and ensuring code design requirements.
 */
class SuperSamplingTests {

	SuperSamplingTests() {
	}

	/**
	 * מתודה חלופית לבניית סצנה עשירה (חובה: לפחות 10 גופים ו-3 מקורות אור) בצורה זו
	 * אנו מונעים כפל קוד בבניית הסצנה בין הטסטים השונים.
	 */
	private Scene createRichScene() {
		Scene scene = new Scene("Rich Test Scene");
		scene.setAmbientLight(new AmbientLight(new Color(20, 20, 20)));

		// 1-2. הגופים המרכזיים המקוריים (ספירה ומשולש גדול)
		scene._geometries.add(
				new Sphere(new Point(0, 0, -50), 50D).setEmission(new Color(0, 0, 200))
						.setMaterial(new Material().setKD(0.4).setKS(0.3).setNShininess(100)),
				new Triangle(new Point(-60, -60, -80), new Point(60, -60, -80), new Point(0, 60, -80))
						.setEmission(new Color(200, 0, 0))
						.setMaterial(new Material().setKD(0.2).setKS(0.2).setNShininess(10)));

		// 3-10. הוספת עוד 8 ספירות קטנות ברקע כדי להגיע ל-10 גופים (דרישת סעיף 5)
		for (int i = 0; i < 8; i++) {
			double x = -70 + (i * 20);
			double y = 70;
			double z = -90;
			scene._geometries.add(new Sphere(new Point(x, y, z), 8D).setEmission(new Color(0, 150, 0)) // ספירות ירוקות
																										// קטנות לבדיקת
																										// עקומות
					.setMaterial(new Material().setKD(0.5).setKS(0.5).setNShininess(50)));
		}

		// הוספת 3 מקורות אור שונים במיקומים שונים (דרישת סעיף 5)
		// מקור אור 1: SpotLight
		scene.lights.add(new SpotLight(new Color(600, 400, 0), new Point(-100, -100, 500), new Vector(-1, -1, -2))
				.setKl(0.0004).setKQ(0.0000006));

		// מקור אור 2: PointLight
		scene.lights
				.add(new PointLight(new Color(400, 0, 400), new Point(100, 100, 300)).setKl(0.0005).setKQ(0.000005));

		// מקור אור 3: DirectionalLight
		scene.lights.add(new DirectionalLight(new Color(200, 200, 200), new Vector(1, -1, -1)));

		return scene;
	}

	private Camera.Builder createBaseCameraBuilder(Scene scene) {
		return Camera.getBuilder().setRayTracer(scene, RayTracerType.SIMPLE).setLocation(new Point(0, 0, 1000))
				.setDirection(Point.ZERO, Vector.AXIS_Y).setVpDistance(1000).setVpSize(150, 150)
				.setResolution(500, 600);
	}

	/**
	 * Test 1: Super-Sampling DISABLED (Single Ray Fallback)
	 */
	@Test
	void testSuperSamplingDisabled() {
		Scene scene = createRichScene();
		Camera.Builder builder = createBaseCameraBuilder(scene).setSuperSampling(false, 1, 1, 1.0,
				SamplingGrid.AreaShape.CIRCLE, SamplingGrid.SamplingPattern.REGULAR);

		long startTime = System.currentTimeMillis();

		builder.build().renderImage().writeToImage("superSamplingDisabled-circle,regular");

		long endTime = System.currentTimeMillis();
		System.out.println(">> Render Time (Anti-Aliasing DISABLED): " + (endTime - startTime) + " ms");
	}

	/**
	 * Test 2: Super-Sampling ENABLED with REGULAR grid (Testing Blackboard & Cache
	 * performance)
	 */
	@Test
	void testSuperSamplingRegularEnabled() {
		Scene scene = createRichScene();
		Camera.Builder builder = createBaseCameraBuilder(scene).setSuperSampling(true, 9, 9, 1.0,
				SamplingGrid.AreaShape.CIRCLE, SamplingGrid.SamplingPattern.REGULAR);

		long startTime = System.currentTimeMillis();

		builder.build().renderImage().writeToImage("superSamplingRegularEnabled-circle,regular");

		long endTime = System.currentTimeMillis();
		System.out.println(">> Render Time (Anti-Aliasing REGULAR 9x9 - Cached): " + (endTime - startTime) + " ms");
	}

	/**
	 * Test 3: Super-Sampling ENABLED with JITTERED grid (Testing Stochastic
	 * Rejection & Smooth edges)
	 */
	@Test
	void testSuperSamplingJitteredEnabled() {
		Scene scene = createRichScene();
		Camera.Builder builder = createBaseCameraBuilder(scene).setSuperSampling(true, 9, 9, 1.0,
				SamplingGrid.AreaShape.CIRCLE, SamplingGrid.SamplingPattern.JITTERED);

		long startTime = System.currentTimeMillis();

		builder.build().renderImage().writeToImage("superSamplingJitteredEnabled- circle,jittered");

		long endTime = System.currentTimeMillis();
		System.out.println(">> Render Time (Anti-Aliasing JITTERED 9x9): " + (endTime - startTime) + " ms");
	}
}