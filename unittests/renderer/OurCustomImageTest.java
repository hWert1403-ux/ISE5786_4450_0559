package renderer;

import org.junit.jupiter.api.Test;

import geometries.impl.Plane;
import geometries.impl.Sphere;
import geometries.impl.Triangle;
import lighting.AmbientLight;
import lighting.PointLight;
import lighting.SpotLight;
import primitives.Color;
import primitives.Material;
import primitives.Point;
import primitives.Vector;
import scene.Scene;

/**
 * Test to generate a custom image combining shadows, reflection, and
 * transparency.
 */
public class OurCustomImageTest {

	@Test
	public void generateStage8CustomImage() {
		// 1. הגדרת הסצנה וצבע רקע
		Scene scene = new Scene("Custom Stage 8 Image").setBackground(new Color(10, 15, 25)) // רקע כחול כהה עמוק
																								// לקונטרסט
				.setAmbientLight(new AmbientLight(new Color(20, 20, 20)));

		/** Camera builder for the tests */
		final Camera.Builder _cameraBuilder = Camera.getBuilder() //
				.setRayTracer(scene, RayTracerType.SIMPLE);

		// 2. הגדרת חומרים (Materials) עבור האפקטים השונים
		// א. חומר מראה - השתקפות גבוהה (kR = 0.9), בלי שקיפות (kT = 0)
		Material mirrorMaterial = new Material().setKD(0.1).setKS(0.4).setNShininess(100).setKR(0.9);

		// ב. חומר זכוכית - שקיפות גבוהה (kT = 0.7), בלי השתקפות (kR = 0) כדי למנוע
		// לכידת קרניים!
		Material glassMaterial = new Material().setKD(0.2).setKS(0.2).setNShininess(50).setKT(0.7);

		// ג. חומר אטום רגיל - עבור הטלת צללים ברורים (kT = 0, kR = 0)
		Material opaqueMaterial = new Material().setKD(0.4).setKS(0.6).setNShininess(80);

		// 3. הוספת גופים לסצנה
		scene._geometries.add(
				// גוף 1: רצפת מראה גדולה (מישור) שמחזירה את כל האלמנטים
				new Plane(new Point(0, -50, 0), new Vector(0, 1, 0)).setEmission(new Color(15, 15, 15))
						.setMaterial(mirrorMaterial),

				// גוף 2: כדור אטום גדול (אדום) שמטיל צל משמעותי על הרצפה
				new Sphere(new Point(-30, 0, -50), 30).setEmission(new Color(150, 0, 0)).setMaterial(opaqueMaterial),

				// גוף 3: כדור שקוף (ירוק זכוכית) שנמצא קצת מקדימה
				new Sphere(new Point(30, -10, -30), 20).setEmission(new Color(0, 100, 50)).setMaterial(glassMaterial),

				// גוף 4: משולש אטום (זהב) עומד ברקע
				new Triangle(new Point(-60, -50, -120), new Point(-20, -50, -120), new Point(-40, 20, -120))
						.setEmission(new Color(160, 130, 30)).setMaterial(opaqueMaterial));

		// 4. הגדרת מקורות אור
		scene.lights.add(new SpotLight(new Color(700, 700, 700), new Point(60, 80, 100), new Vector(-60, -80, -100))
				.setKl(0.0001).setKQ(0.00001)); // שים לב ל-kQ באות גדולה לפי המקובל במחלקות התאורה
		scene.lights.add(new PointLight(new Color(300, 300, 300), new Point(-50, 60, 50)).setKl(0.0005).setKQ(0.0005));

//		// 5. בניית המצלמה והרנדור בהתאמה מדויקת ל-Camera.Builder שלך
//		Camera camera = new Camera.Builder().setLocation(new Point(0, 30, 150))
//				.setDirection(new Vector(0, -0.2, -1), new Vector(0, 1, -0.2)) // הגדרת כיוון ולמעלה יחד כפי שמוגדר אצלך
//				.setVpSize(200, 200).setVpDistance(150).setResolution(600, 600).setRayTracer(new SimpleRayTracer(scene))
//				.build(); // יצירת האובייקט הסופי מהבילדר

		_cameraBuilder.setLocation(new Point(0, 30, 150)) //
				.setDirection(new Vector(0, -0.2, -1), new Vector(0, 1, -0.2)) //
				.setVpDistance(150).setVpSize(200, 200) //
				.setResolution(600, 600) //
				.build() //
				.renderImage() //
				.writeToImage("stage8_custom_image");

//		// 6. הפעלת הרנדור ויצירת התמונה
//		camera.renderImage();
//		camera.writeToImage("stage8_custom_image");
	}
}