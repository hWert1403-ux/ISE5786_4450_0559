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
	public void generateStage8CustomImage1() {
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

	@Test
	public void generateStage8CustomImage2() {
		// 1. הגדרת הסצנה וצבע רקע
		Scene scene = new Scene("Soap Bubbles Custom Image").setBackground(new Color(10, 12, 25)) // רקע כחול כהה עמוק
																									// מאוד שיבליט את
																									// צבעי הבועות
				.setAmbientLight(new AmbientLight(new Color(15, 15, 15)));

		/** Camera builder for the tests */
		final Camera.Builder _cameraBuilder = Camera.getBuilder() //
				.setRayTracer(scene, RayTracerType.SIMPLE);

		// 2. הגדרת חומרים (Materials) עבור האפקטים השונים
		// א. חומר מראה עבור הרצפה - השתקפות גבוהה (kR = 0.8)
		Material mirrorFloorMaterial = new Material().setKD(0.1).setKS(0.4).setNShininess(100).setKR(0.8);

		// ב. חומר בועת סבון חיצונית - שקיפות גבוהה מאוד (kT = 0.85), מעט השתקפות (kR =
		// 0.25) וברק גבוה
		Material outerBubbleMaterial = new Material().setKD(0.05).setKS(0.8).setNShininess(120).setKT(0.85).setKR(0.25);

		// ג. חומר בועות פנימיות - שקיפות קצת פחות גבוהה (kT = 0.65) כדי שיראו אותן
		// בבירור בפנים
		Material innerBubbleMaterial = new Material().setKD(0.1).setKS(0.7).setNShininess(90).setKT(0.65).setKR(0.15);

		// ד. חומר אטום/מט עבור המשולש שברקע (kT = 0, kR = 0)
		Material opaqueMaterial = new Material().setKD(0.5).setKS(0.4).setNShininess(50);

		// 3. הוספת גופים לסצנה
		scene._geometries.add(
				// גוף 1: רצפת מראה כהה (מישור) שמחזירה ומשקפת את כל מחזה הבועות מלמטה
				new Plane(new Point(0, -65, 0), new Vector(0, 1, 0)).setEmission(new Color(15, 15, 20))
						.setMaterial(mirrorFloorMaterial),

				// גוף 2: הבועה הגדולה החיצונית במרכז - גוון ירקרק-תכלכל עדין האופייני לסבון
				new Sphere(new Point(0, 0, -50), 50).setEmission(new Color(10, 35, 30))
						.setMaterial(outerBubbleMaterial),

				// גוף 3: בועה פנימית 1 (בתוך הבועה הגדולה) - גוון ורדרד/מגנטה
				new Sphere(new Point(-15, -10, -40), 15).setEmission(new Color(55, 15, 35))
						.setMaterial(innerBubbleMaterial),

				// גוף 4: בועה פנימית 2 (בתוך הבועה הגדולה) - גוון צהבהב/זהוב
				new Sphere(new Point(15, 15, -45), 12).setEmission(new Color(45, 40, 10))
						.setMaterial(innerBubbleMaterial),

				// גוף 5: בועה פנימית 3 (בתוך הבועה הגדולה) - גוון תכלת עמוק
				new Sphere(new Point(0, -20, -55), 10).setEmission(new Color(10, 30, 50))
						.setMaterial(innerBubbleMaterial),

				// גוף 6: משולש אטום גדול (בגוון נחושת/זהב עמוק) עומד ברקע ישירות מאחורי הבועות
				// מיקומו מאחורי הבועה מדגיש בצורה יפה את השקיפות כשהצופה רואה אותו דרך הבועות
				new Triangle(new Point(-75, -60, -100), new Point(75, -60, -100), new Point(0, 65, -100))
						.setEmission(new Color(50, 35, 15)).setMaterial(opaqueMaterial));

		// 4. הגדרת מקורות אור ליצירת ניצוצות (Specular Highlights) על הבועות
		// אור ספוט חזק מקדימה ומלמעלה
		scene.lights.add(new SpotLight(new Color(750, 750, 750), new Point(60, 80, 100), new Vector(-60, -80, -100))
				.setKl(0.0001).setKQ(0.00001));

		// אור נקודתי מהצד השני לאיזון הצללים והחזרי האור
		scene.lights.add(new PointLight(new Color(400, 400, 400), new Point(-70, 60, 50)).setKl(0.0005).setKQ(0.0005));

		// אור נקודתי אחורי וחלש יותר כדי להעיר את המשולש ולתת נפח לבועות מאחור
		scene.lights.add(new PointLight(new Color(250, 250, 300), new Point(0, -30, -30)).setKl(0.001).setKQ(0.001));

		// 5. בניית המצלמה והרנדור בהתאמה מדויקת ל-Builder
		_cameraBuilder.setLocation(new Point(0, 20, 140)) // מיקום מעט מורם מעל קו האמצע למבט דינמי
				.setDirection(new Vector(0, -0.14, -1), new Vector(0, 1, -0.14)) // כיוון זוויתי קל מטה
				.setVpDistance(140).setVpSize(200, 200) //
				.setResolution(600, 600) //
				.build() //
				.renderImage() //
				.writeToImage("soap_bubbles_custom_image");
	}

	@Test
	public void generateStage8CustomImage3() {
		// 1. הגדרת הסצנה וצבע רקע
		Scene scene = new Scene("Professional Custom Image").setBackground(new Color(10, 15, 25)) // רקע כחול כהה עמוק
																									// לקונטרסט
				.setAmbientLight(new AmbientLight(new Color(20, 20, 20)));

		/** Camera builder for the tests */
		final Camera.Builder _cameraBuilder = Camera.getBuilder() //
				.setRayTracer(scene, RayTracerType.SIMPLE);

		// 2. הגדרת חומרים (Materials) עבור האפקטים השונים
		// א. חומר מראה עבור הרצפה - השתקפות גבוהה (kR = 0.8), ברק בינוני
		Material mirrorFloorMaterial = new Material().setKD(0.1).setKS(0.4).setNShininess(100).setKR(0.8);

		// ב. חומר בועת סבון חיצונית - שקיפות גבוהה (kT = 0.8), מעט השתקפות (kR = 0.15)
		// וברק גבוה
		Material bubbleMaterial = new Material().setKD(0.1).setKS(0.7).setNShininess(100).setKT(0.8).setKR(0.15);

		// ג. חומר אטום מתכתי לקיר המשולשים - ברק גבוה (KS), צבע מט דומיננטי (KD)
		Material triangleWallMaterial = new Material().setKD(0.5).setKS(0.5).setNShininess(50);

		// 3. הוספת גופים לסצנה
		scene._geometries.add(
				// גוף 1: רצפת מראה (מישור) שמחזירה ומשקפת את כל האלמנטים מלמטה
				new Plane(new Point(0, -50, 0), new Vector(0, 1, 0)).setEmission(new Color(15, 15, 20))
						.setMaterial(mirrorFloorMaterial));

		// יצירת קיר המשולשים המורכב (Triangle Wall) בגב הסצנה
		// אנו יוצרים רשת של נקודות בגב הסצנה ומחברים אותן ליצירת משולשים
		double wallZ = -120; // מיקום הקיר בציר Z
		double startX = -80, endX = 80;
		double startY = -60, endY = 80;
		int rows = 3, cols = 4; // מספר השורות והעמודות של הריבועים
		double dx = (endX - startX) * cols;
		double dy = (endY - startY) * rows;

		for (int i = 0; i < cols; i++) {
			for (int j = 0; j < rows; j++) {
				// הגדרת ארבע הנקודות של הריבוע (quad)
				Point p0 = new Point(startX + i * dx, startY + j * dy, wallZ);
				Point p1 = new Point(startX + (i + 1) * dx, startY + j * dy, wallZ);
				Point p2 = new Point(startX + i * dx, startY + (j + 1) * dy, wallZ);
				Point p3 = new Point(startX + (i + 1) * dx, startY + (j + 1) * dy, wallZ);

				// יצירת שני משולשים מתוך הריבוע
				scene._geometries.add(
						new Triangle(p0, p1, p2).setEmission(new Color(60, 30, 10)).setMaterial(triangleWallMaterial),
						new Triangle(p1, p3, p2).setEmission(new Color(60, 30, 10)).setMaterial(triangleWallMaterial));
			}
		}

		// 4. הוספת בועות סבון (Spheres) בגדלים ומיקומים שונים
		// בועה גדולה מרכזית - גוון ירקרק-תכלכל עדין
		scene._geometries.add(
				new Sphere(new Point(0, 0, -50), 50).setEmission(new Color(10, 35, 30)).setMaterial(bubbleMaterial));

		// בועות פנימיות (בתוך הבועה הגדולה) - גוונים ריאליים
		scene._geometries.add(
				// בועה פנימית 1 - גוון ורדרד עמוק
				new Sphere(new Point(-15, -10, -40), 15).setEmission(new Color(55, 15, 35)).setMaterial(bubbleMaterial),
				// בועה פנימית 2 - גוון צהוב זהוב
				new Sphere(new Point(15, 15, -45), 12).setEmission(new Color(45, 40, 10)).setMaterial(bubbleMaterial),
				// בועה פנימית 3 - גוון תכלת-כחול
				new Sphere(new Point(0, -20, -55), 10).setEmission(new Color(10, 30, 50)).setMaterial(bubbleMaterial));

		// בועות חיצוניות (מחוץ לבועה הגדולה) - גוונים ריאליים
		scene._geometries.add(
				// בועה חיצונית 1 - גוון ורדרד עדין
				new Sphere(new Point(-30, 30, -30), 18).setEmission(new Color(45, 20, 30)).setMaterial(bubbleMaterial),
				// בועה חיצונית 2 - גוון צהבהב
				new Sphere(new Point(30, -30, -30), 16).setEmission(new Color(35, 30, 10)).setMaterial(bubbleMaterial),
				// בועה חיצונית 3 - גוון תכלת-כחול
				new Sphere(new Point(60, 50, -70), 14).setEmission(new Color(10, 30, 45)).setMaterial(bubbleMaterial));

		// 5. הגדרת מקורות אור
		// אור ספוט חזק וממוקד כדי ליצור ניצוצות (specular highlights)
		scene.lights.add(new SpotLight(new Color(750, 750, 750), new Point(60, 80, 100), new Vector(-60, -80, -100))
				.setKl(0.0001).setKQ(0.00001));

		// אור נקודתי נוסף מהצד השני לאיזון הצללים והחזרי האור
		scene.lights.add(new PointLight(new Color(400, 400, 400), new Point(-70, 60, 50)).setKl(0.0005).setKQ(0.0005));

		// 6. בניית המצלמה והרנדור בהתאמה מדויקת ל-Camera.Builder שלך
		_cameraBuilder.setLocation(new Point(0, 30, 150)) // מיקום מעט מורם מעל קו האמצע למבט דינמי
				.setDirection(new Vector(0, -0.2, -1), new Vector(0, 1, -0.2)) // כיוון זוויתי קל מטה
				.setVpDistance(150).setVpSize(200, 200) //
				.setResolution(800, 800) // רזולוציה גבוהה לנראות מקצועית
				.build() // יצירת האובייקט הסופי מהבילדר
				.renderImage() // הפעלת הרנדור
				.writeToImage("professional_bubbles_image"); // שמירת התמונה
	}

	@Test
	public void generateStage8CustomImage4() {
		// 1. הגדרת הסצנה וצבע רקע כהה מאוד עם שמץ כחול עמוק
		Scene scene = new Scene("Replicated Soap Bubble Scene").setBackground(new Color(4, 6, 14))
				.setAmbientLight(new AmbientLight(new Color(8, 8, 12)));

		/** Camera builder for the tests */
		final Camera.Builder _cameraBuilder = Camera.getBuilder() //
				.setRayTracer(scene, RayTracerType.SIMPLE);

		// 2. הגדרת חומרים מתאימים לאפקט ריאליסטי
		// א. רצפת מראה כהה ומבריקה מאוד
		Material floorMaterial = new Material().setKD(0.05).setKS(0.7).setNShininess(120).setKR(0.7);

		// ב. קיר משולשים מוזהב/מתכתי - דומיננטיות של ברק (Specular) ליצירת גווני אור
		// וצל
		Material goldWallMaterial = new Material().setKD(0.4).setKS(0.7).setNShininess(40);

		// ג. חומר בועת סבון - שקיפות גבוהה מאוד (kT) עם החזר אור קל (kR) וברק קיצוני
		// לניצוצות
		Material bubbleMaterial = new Material().setKD(0.02).setKS(0.9).setNShininess(150).setKT(0.85).setKR(0.2);

		// 3. הוספת רצפת המראה
		scene._geometries.add(new Plane(new Point(0, -45, 0), new Vector(0, 1, 0)).setEmission(new Color(10, 12, 18))
				.setMaterial(floorMaterial));

		// 4. בניית קיר משולשים תלת-ממדי (Faceted Pyramid Wall) בצד ימין מאחור
		// כדי לקבל את המראה שבתמונה, כל תא ברשת מורכב מ-4 משולשים שנפגשים בנקודה מרכזית
		// בולטת קדימה
		double wallZ = -70;
		double startX = 15, endX = 95;
		double startY = -45, endY = 45;
		int rows = 3, cols = 3;
		double dx = (endX - startX) / cols;
		double dy = (endY - startY) / rows;

		for (int i = 0; i < cols; i++) {
			for (int j = 0; j < rows; j++) {
				// 4 פינות של התא הריבועי ברקע הקיר
				Point topLeft = new Point(startX + i * dx, startY + (j + 1) * dy, wallZ);
				Point topRight = new Point(startX + (i + 1) * dx, startY + (j + 1) * dy, wallZ);
				Point bottomLeft = new Point(startX + i * dx, startY + j * dy, wallZ);
				Point bottomRight = new Point(startX + (i + 1) * dx, startY + j * dy, wallZ);

				// נקודת האמצע נדחפת קדימה בציר Z כדי ליצור פירמידה תלת-ממדית שתתפוס את האור
				Point centerPoint = new Point(startX + i * dx + dx / 2, startY + j * dy + dy / 2, wallZ + 12);

				Color goldColor = new Color(140, 95, 35); // גוון ברונזה/זהב עמוק

				// הוספת 4 המשולשים המרכיבים את הפירמידה לקבלת הטקסטורה הגיאומטרית
				scene._geometries.add(
						new Triangle(bottomLeft, bottomRight, centerPoint).setEmission(goldColor)
								.setMaterial(goldWallMaterial),
						new Triangle(bottomRight, topRight, centerPoint).setEmission(goldColor)
								.setMaterial(goldWallMaterial),
						new Triangle(topRight, topLeft, centerPoint).setEmission(goldColor)
								.setMaterial(goldWallMaterial),
						new Triangle(topLeft, bottomLeft, centerPoint).setEmission(goldColor)
								.setMaterial(goldWallMaterial));
			}
		}

		// 5. בועת הסבון הגדולה והראשית (במרכז-שמאל)
		scene._geometries.add(new Sphere(new Point(-10, 5, -20), 42).setEmission(new Color(15, 30, 25)) // גוון
																										// ירקרק-תכלכל
																										// עדין בבסיס
				.setMaterial(bubbleMaterial));

		// 6. בועות פנימיות (בתוך הבועה הגדולה) בצבעים ומיקומים תואמים בדיוק לתמונה
		scene._geometries.add(
				// בועה פנימית ורודה (למטה משמאל)
				new Sphere(new Point(-23, -12, -15), 13).setEmission(new Color(80, 25, 45)).setMaterial(bubbleMaterial),

				// בועה פנימית תכולה (במרכז)
				new Sphere(new Point(-3, 2, -18), 10).setEmission(new Color(20, 55, 75)).setMaterial(bubbleMaterial),

				// בועה פנימית צהובה/זהובה (למעלה מימין)
				new Sphere(new Point(10, 16, -15), 12).setEmission(new Color(75, 55, 15)).setMaterial(bubbleMaterial));

		// 7. מערך מקורות אור ליצירת ניצוצות חזקים והרמוניה של צבעים
		// אור ספוט ראשי מלמעלה-קדימה ליצירת הניצוץ הלבן הבוהק בראשי הבועות
		scene.lights.add(new SpotLight(new Color(850, 850, 850), new Point(-20, 100, 60), new Vector(20, -100, -60))
				.setKl(0.0001).setKQ(0.00001));

		// אור נקודתי שני מימין כדי להאיר בצורה דרמטית את קיר הפירמידות המוזהב
		scene.lights.add(new PointLight(new Color(600, 500, 350), new Point(70, 40, 20)).setKl(0.0003).setKQ(0.0002));

		// אור מילוי רך משמאל למניעת אזורים חשוכים לחלוטין בבועות
		scene.lights.add(new PointLight(new Color(200, 250, 300), new Point(-80, 20, 30)).setKl(0.0005).setKQ(0.0005));

		// 8. הגדרת המצלמה והרנדור
		_cameraBuilder.setLocation(new Point(0, 10, 130)) // מבט פרונטלי בגובה העיניים עם הטיה קלה
				.setDirection(new Vector(0, -0.07, -1), new Vector(0, 1, -0.07)).setVpDistance(130).setVpSize(200, 200)
				.setResolution(1000, 1000) // רזולוציה גבוהה במיוחד לחדות מקסימלית של המשולשים וההשתקפויות
				.build().renderImage().writeToImage("replicated_soap_bubbles_scene");
	}

}