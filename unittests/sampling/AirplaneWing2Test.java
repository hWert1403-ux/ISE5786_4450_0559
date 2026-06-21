package sampling;

import static java.awt.Color.YELLOW;

import org.junit.jupiter.api.Test;

import geometries.impl.Sphere;
import geometries.impl.Triangle;
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
 * טסט מבט מחלון המטוס על הכנף והים - מכויל במדויק לפי קואורדינטות ה-Teapot
 */
class AirplaneWing2Test {

	/** Default constructor to satisfy JavaDoc generator */
	AirplaneWing2Test() {
		/* to satisfy JavaDoc generator */
	}

	/**
	 * טסט המפעיל את שיטת ההאצה המתקדמת ביותר: Adaptive Super Sampling בשילוב
	 * Threads
	 */
	@Test
	void testAirplaneWingAdaptiveSS() {
		System.out.println("RUNNING: Airplane Wing View - Adaptive Super Sampling & Threads");
		// כיול זהה ל-teapot3: מינוס 2 לתהליכונים, ASS פעיל
		runAndMeasureAirplaneWing(false, true, -2, "airplane_wing_view_adaptive_ss");
	}

	/**
	 * הכנת נתוני הסצנה - המצלמה ב-Z=-1000 והאובייקטים ב-Z חיובי (בדיוק כמו בקומקום)
	 */
	Camera.Builder prepareAirplaneWing() {
		Scene scene = new Scene("Airplane Wing View Scene");

		// --- הגדרת חומרים וצבעים ---
		Material wingMaterial = new Material().setKD(0.2).setKS(0.8).setNShininess(100).setKR(0.15);
		Color wingColor = new Color(240, 240, 245); // לבן-אפור מתכתי בוהק של מטוס

		Material seaMaterial = new Material().setKD(0.4).setKS(0.6).setNShininess(35).setKR(0.2);
		Color seaColor = new Color(10, 75, 140); // כחול ים עמוק

		Material engineMaterial = new Material().setKD(0.3).setKS(0.7).setNShininess(80).setKR(0.1);
		Color engineColor = new Color(160, 165, 170); // מנוע מתכתי כהה

		// ==========================================================================
		// 1. בניית מאות אובייקטים בטווח הראייה (Z חיובי)
		// ==========================================================================

		// א. בניית רשת משולשים צפופה ומעוקלת עבור כנף המטוס המשתרעת באלכסון
		int wingSegmentsX = 5;
		for (int i = 0; i < wingSegmentsX; i++) {
			double t1 = (double) i / wingSegmentsX;
			double t2 = (double) (i + 1) / wingSegmentsX;

			// אינטרפולציה ליניארית ממתאר בסיס הכנף לקצה הכנף
			double x1 = -80 + t1 * (150 - (-80));
			double x2 = -80 + t2 * (150 - (-80));

			double y1 = 20 + t1 * (80 - 20);
			double y2 = 20 + t2 * (80 - 20);

			// מיקומי העומק (Z חיובי לחלוטין, מול משטח הראייה!)
			double zStart1 = 50 + t1 * (300 - 50);
			double zEnd1 = 150 + t1 * (700 - 150);

			double zStart2 = 50 + t2 * (300 - 50);
			double zEnd2 = 150 + t2 * (700 - 150);

			Point pTopLeft = new Point(x1, y1, zStart1);
			Point pTopRight = new Point(x2, y2, zStart2);
			Point pBottomLeft = new Point(x1, y1 - 10, zEnd1);
			Point pBottomRight = new Point(x2, y2 - 10, zEnd2);

			scene._geometries.add(
					new Triangle(pTopLeft, pTopRight, pBottomLeft).setEmission(wingColor).setMaterial(wingMaterial),
					new Triangle(pTopRight, pBottomRight, pBottomLeft).setEmission(wingColor)
							.setMaterial(wingMaterial));
		}

		// ב. בניית רשת גלי ים תלת-ממדית (רשת ענקית בתחתית של מאות משולשים)
		int seaGridX = 10;
		int seaGridZ = 10;
		for (int i = 0; i < seaGridX; i++) {
			for (int j = 0; j < seaGridZ; j++) {
				double x1 = -800 + i * 40.0;
				double x2 = -800 + (i + 1) * 40.0;
				double z1 = 0 + j * 40.0;
				double z2 = 0 + (j + 1) * 40.0;

				// גובה פני המים (Y=-200 קבוע עם שינויי סינוס קלים ליצירת גלים)
				double y11 = -200 + 4.0 * Math.sin(i * 0.5) * Math.cos(j * 0.5);
				double y21 = -200 + 4.0 * Math.sin((i + 1) * 0.5) * Math.cos(j * 0.5);
				double y12 = -200 + 4.0 * Math.sin(i * 0.5) * Math.cos((j + 1) * 0.5);
				double y22 = -200 + 4.0 * Math.sin((i + 1) * 0.5) * Math.cos((j + 1) * 0.5);

				Point p11 = new Point(x1, y11, z1);
				Point p21 = new Point(x2, y21, z1);
				Point p12 = new Point(x1, y12, z2);
				Point p22 = new Point(x2, y22, z2);

				scene._geometries.add(new Triangle(p11, p21, p12).setEmission(seaColor).setMaterial(seaMaterial),
						new Triangle(p21, p22, p12).setEmission(seaColor).setMaterial(seaMaterial));
			}
		}

		// ג. הוספת בית מנוע גלילי מתחת לכנף (Spheres משולבות)
		scene._geometries.add(
				new Sphere(new Point(30, 0, 250), 25.0).setEmission(engineColor).setMaterial(engineMaterial),
				new Sphere(new Point(30, 2, 290), 22.0).setEmission(engineColor).setMaterial(engineMaterial));

		// ==========================================================================
		// 2. הגדרת 5 מקורות אור מותאמים למיקומים החדשים
		// ==========================================================================

		// 1. DirectionalLight - אור שמש ישיר השוטף את הסצנה מלמעלה
		scene.lights.add(new DirectionalLight(new Color(800, 700, 600), new Vector(0.3, -1, 0.5)));

		// 2. PointLight - פנס ניווט אדום בוהק הממוקם בדיוק בקצה המרוחק של הכנף
		scene.lights.add(new PointLight(new Color(1000, 200, 200), new Point(150, 85, 705)).setKQ(0.00001));

		// 3. SpotLight - פנס נחיתה חזק מגוף המטוס המאיר באלומה ממוקדת לעבר מרכז הכנף
		// והמנוע
		scene.lights.add(new SpotLight(new Color(800, 800, 1000), new Point(-75, 120, 40), new Vector(1, -0.1, 0.8))
				.setKQ(0.00002));

		// 4. PointLight - תאורת אווירה חמה המדמה את השתקפות אורות הקבינה הפנימיים על
		// החלון
		scene.lights.add(new PointLight(new Color(300, 200, 100), new Point(-95, 110, -10)).setKQ(0.0005));

		// 5. DirectionalLight - אור שמיים משני בגוון כחלחל רך למילוי הצללים (Sky fill
		// light)
		scene.lights.add(new DirectionalLight(new Color(100, 150, 250), new Vector(-0.2, -1, -0.2)));

		// ==========================================================================
		// 3. הגדרת כיול המצלמה (זהה לחלוטין לכיול של ה-Teapot המקורי)
		// ==========================================================================
		return Camera.getBuilder().setResolution(1000, 1000).setRayTracer(scene, RayTracerType.SIMPLE)
				.setLocation(new Point(-100, 100, -1000)) // המצלמה משמאל, בגובה 100, ב-Z=-1000
				.setDirection(new Vector(0, 0, 1), new Vector(0, 1, 0)) // מסתכלת ישר קדימה לכיוון ה-Z החיובי
				.setVpDistance(1000) // משטח הראייה מתמקם ב-Z=0
				.setVpSize(200, 200);
	}

	/**
	 * מתודת עזר הרצה, מודדת ומפעילה את ה-Adaptive Super Sampling
	 */
	private void runAndMeasureAirplaneWing(boolean useSS, boolean useASS, int threadsCount, String imageName) {
		Camera.Builder builder = prepareAirplaneWing().setMultithreading(threadsCount).setDebugPrint(2);

		if (useSS) {
			builder.setSuperSampling(true, 9, 9, 1.0, SamplingGrid.AreaShape.SQUARE,
					SamplingGrid.SamplingPattern.REGULAR);
		} else {
			builder.setSuperSampling(false, 1, 1, 1.0, SamplingGrid.AreaShape.SQUARE,
					SamplingGrid.SamplingPattern.REGULAR);
		}

		if (useASS) {
			builder.setSuperSampling(false, 9, 9, 1.0, SamplingGrid.AreaShape.SQUARE,
					SamplingGrid.SamplingPattern.REGULAR);
			builder.setAdaptiveSuperSampling(true, 5, 0.03); // רמת עומק מקסימלית של אדפטיביות
		} else {
			builder.setAdaptiveSuperSampling(false, 1, 0.0);
		}

		long startTime = System.currentTimeMillis();

		builder.build().renderImage().printGrid(50, new Color(YELLOW)).writeToImage(imageName);

		long endTime = System.currentTimeMillis();
		System.out.println(">> Test [" + imageName + "] completed in: " + (endTime - startTime) + " ms");
		System.out.println("========================================================");
	}
}