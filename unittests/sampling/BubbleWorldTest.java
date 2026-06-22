package sampling;

import static java.awt.Color.GREEN;
import static java.awt.Color.RED;
import static java.awt.Color.WHITE;
import static java.awt.Color.YELLOW;

import org.junit.jupiter.api.Test;

import geometries.impl.Plane;
import geometries.impl.Sphere;
import geometries.impl.Triangle;
import lighting.DirectionalLight;
import lighting.PointLight;
import lighting.SpotLight;
import primitives.Color;
import primitives.Double3;
import primitives.Material;
import primitives.Point;
import primitives.Vector;
import renderer.Camera;
import renderer.RayTracerType;
import scene.Scene;

public class BubbleWorldTest {

	/**
	 * מתודת עזר סטטית לבניית סצנה כבדה ומורכבת. עומדת בדרישות הפרויקט: מעל 1000
	 * גופים, כל סוגי הגופים, 5 מקורות אור, שקיפות, השתקפות והצללות. הסצנה
	 * דטרמיניסטית (ללא Random) כך שהיא זהה לחלוטין בכל קריאה.
	 */
	public static Scene buildHeavyWingScene() {
		Scene scene = new Scene("Heavy Wing Scene for Performance Measurement");

		// ==========================================
		// 1. הוספת גופי הבסיס (Plane, Triangles)
		// ==========================================

		// הים (Plane) - עם השתקפות (KR) חזקה
		Plane sea = new Plane(new Point(0, -50, 0), new Vector(0, 1, 0));
		sea.setEmission(new Color(0, 20, 80));
		sea.setMaterial(new Material().setKD(new Double3(0.5)).setKS(new Double3(0.5)).setNShininess(60)
				.setKR(new Double3(0.5))); // משתקף כמו מים

		// כנף המטוס (מורכבת מ-2 משולשים ליצירת צורת יהלום/עפיפון)
		Triangle wingRight = new Triangle(new Point(0, 0, -80), new Point(100, 0, -20), new Point(0, 0, 10));
		wingRight.setEmission(new Color(100, 100, 100));
		wingRight.setMaterial(new Material().setKD(new Double3(0.6)).setKS(new Double3(0.4)).setNShininess(30));

		Triangle wingLeft = new Triangle(new Point(0, 0, -80), new Point(-100, 0, -20), new Point(0, 0, 10));
		wingLeft.setEmission(new Color(100, 100, 100));
		wingLeft.setMaterial(new Material().setKD(new Double3(0.6)).setKS(new Double3(0.4)).setNShininess(30));

		// חופת תא הטייס (Sphere) - שקופה (KT) ומשתקפת (KR) כמו זכוכית
		Sphere canopy = new Sphere(new Point(0, 2, -40), 12);
		canopy.setEmission(new Color(10, 10, 10));
		canopy.setMaterial(new Material().setKD(new Double3(0.1)).setKS(new Double3(0.9)).setNShininess(100)
				.setKT(new Double3(0.8)) // שקוף כמעט לגמרי
				.setKR(new Double3(0.2))); // השתקפות קלה של אור

		scene._geometries.add(sea, wingRight, wingLeft, canopy);

		// ==========================================
		// 2. יצירת עומס קיצוני (Stress Test) - מעל 1,200 גופים
		// ==========================================

		// יצירת מטר של "בועות/טיפות" תלת-ממדיות באוויר
		// אנחנו משתמשים בלולאות ופונקציות טריגונומטריות כדי שהמיקומים ייראו אקראיים,
		// אבל למעשה הם דטרמיניסטיים (אותה סצנה תיווצר בדיוק בכל פעם!).
		Material bubbleMat = new Material().setKD(new Double3(0.2)).setKS(new Double3(0.8)).setNShininess(80)
				.setKT(new Double3(0.6)).setKR(new Double3(0.4));

		for (int i = 0; i < 15; i++) {
			for (int j = 0; j < 15; j++) {
				for (int k = 0; k < 6; k++) {
					double x = -150 + (i * 20) + (Math.sin(j * k) * 10);
					double y = 10 + (k * 15) + (Math.cos(i * j) * 5);
					double z = -150 + (j * 20) + (Math.sin(i * k) * 10);

					Sphere bubble = new Sphere(new Point(x, y, z), 2.5);
					bubble.setEmission(new Color(20, 20, 30));
					bubble.setMaterial(bubbleMat);
					scene._geometries.add(bubble);
				}
			}
		}
		// סך הכל ייווצרו בלולאה 15 * 15 * 6 = 1,350 ספרות שקופות! (הופך את הרינדור לכבד
		// מאוד ומעולה למדידת ביצועים).

		// ==========================================
		// 3. הוספת 5 מקורות אור
		// ==========================================
		scene.lights.add(new DirectionalLight(new Color(150, 140, 130), new Vector(1, -1, -1))); // שמש
		scene.lights.add(new PointLight(new Color(RED), new Point(-90, 10, -20)).setKl(0.0001).setKQ(0.000001)); // אור
																													// אזהרה
																													// שמאלי
		scene.lights.add(new PointLight(new Color(GREEN), new Point(90, 10, -20)).setKl(0.0001).setKQ(0.000001)); // אור
																													// אזהרה
																													// ימני
		scene.lights.add(new SpotLight(new Color(WHITE), new Point(0, 15, -10), new Vector(0, -0.5, -1)).setKl(0.0001)
				.setKQ(0.000001)); // זרקור קדמי
		scene.lights.add(new SpotLight(new Color(YELLOW), new Point(0, -5, -40), new Vector(0, -1, 0)).setKl(0.0001)
				.setKQ(0.000001)); // פנס אל הים

		return scene;
	}

	// ==========================================
	// סדרת מדידות (Test Suite)
	// ==========================================

	@Test
	void measurePerformance_1_Base() {
		System.out.println("RUNNING: Base Measurement (No Optimizations)");
		Scene scene = buildHeavyWingScene();

		Camera.getBuilder().setRayTracer(scene, RayTracerType.SIMPLE).setLocation(new Point(0, 40, 150))
				.setDirection(new Vector(0, -0.2, -1), new Vector(0, 1, -0.2)).setVpDistance(100).setVpSize(200, 200)
				.setResolution(1000, 1000).setMultithreading(0) // ללא שיפור חוטים
				.setAdaptiveSuperSampling(false, 1, 0) // ללא שיפור החלקה
				.setDebugPrint(10) // ירוץ לאט מאוד בגלל ה-1350 בועות!
				.build().renderImage().writeToImage("Measure_1_Base");
	}

	@Test
	void measurePerformance_2_ThreadsOnly() {
		System.out.println("RUNNING: Measurement with Multithreading (-2)");
		Scene scene = buildHeavyWingScene();

		Camera.getBuilder().setRayTracer(scene, RayTracerType.SIMPLE).setLocation(new Point(0, 40, 150))
				.setDirection(new Vector(0, -0.2, -1), new Vector(0, 1, -0.2)).setVpDistance(100).setVpSize(200, 200)
				.setResolution(1000, 1000).setMultithreading(-2) // הפעלת Threads
				.setAdaptiveSuperSampling(false, 1, 0).setDebugPrint(2).build().renderImage()
				.writeToImage("Measure_2_Threads");
	}

	@Test
	void measurePerformance_3_FullOptimizations() {
		System.out.println("RUNNING: Measurement with Multithreading & Adaptive Super Sampling");
		Scene scene = buildHeavyWingScene();

		Camera.getBuilder().setRayTracer(scene, RayTracerType.SIMPLE).setLocation(new Point(0, 40, 150))
				.setDirection(new Vector(0, -0.2, -1), new Vector(0, 1, -0.2)).setVpDistance(100).setVpSize(200, 200)
				.setResolution(1000, 1000).setMultithreading(-2) // הפעלת Threads
				.setAdaptiveSuperSampling(true, 5, 0.03) // הפעלת ASS
				.setDebugPrint(2).build().renderImage().writeToImage("Measure_3_FullOpt");
	}
}