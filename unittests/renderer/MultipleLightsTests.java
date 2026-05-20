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
 * Visual integration tests combining multiple light sources (Directional,
 * Point, Spot) in a single scene according to Phase 7 instructions.
 */
public class MultipleLightsTests {

	/** Constant for tests resolution */
	private static final int RESOLUTION = 500;

	/** First scene for sphere test */
	private final Scene _scene1 = new Scene("Test scene Sphere");

	/** Second scene for triangles test */
	private final Scene _scene2 = new Scene("Test scene Triangles")
			.setAmbientLight(new AmbientLight(new Color(38, 38, 38)));

	/** First camera builder for the sphere test */
	private final Camera.Builder _camera1 = Camera.getBuilder() //
			.setRayTracer(_scene1, RayTracerType.SIMPLE) //
			.setLocation(new Point(0, 0, 1000)) //
			.setDirection(Point.ZERO, Vector.AXIS_Y) //
			.setVpSize(150, 150).setVpDistance(1000);

	/** Second camera builder for the triangles test */
	private final Camera.Builder _camera2 = Camera.getBuilder() //
			.setRayTracer(_scene2, RayTracerType.SIMPLE) //
			.setLocation(new Point(0, 0, 1000)) //
			.setDirection(Point.ZERO, Vector.AXIS_Y) //
			.setVpSize(200, 200).setVpDistance(1000);

	// --- העתקה מדויקת של נתוני הבסיס מתוך LightsTests (סעיף ג') ---
	private static final int SHININESS = 301;
	private static final double KD = 0.5;
	private static final double KS = 0.5;

	private static final Double3 KD3 = new Double3(0.2, 0.6, 0.4);
	private static final Double3 KS3 = new Double3(0.2, 0.4, 0.3);

	private static final Point SPHERE_CENTER = new Point(0, 0, -50);
	private static final double SPHERE_RADIUS = 50D;

	private static final Point[] VERTICES = { new Point(-110, -110, -150), // left-bottom shared
			new Point(95, 100, -150), // right-top shared
			new Point(110, -110, -150), // right-bottom
			new Point(-75, 78, 100) // left-top
	};

	/**
	 * Default constructor made public for JUnit 5 runner execution.
	 */
	public MultipleLightsTests() {
		/* to satisfy JavaDoc generator */ }

	/**
	 * א. מתודת טסט עבור סצנה עם הכדור המשלבת ריבוי מקורות אור חיצוניים.
	 */
	@Test
	public void testSphereMultipleLights() {
		// יצירת מופע מקומי של הגוף למניעת שיתוף סטטי שינוי חומרים (סעיף י')
		Geometry sphere = new Sphere(SPHERE_CENTER, SPHERE_RADIUS).setEmission(new Color(BLUE).reduce(2))
				.setMaterial(new Material().setKD(KD).setKS(KS).setNShininess(SHININESS));

		_scene1._geometries.add(sphere);

		// 1. אור כיווני: צבע לבן חזק, מגיע מזווית עליונה-ימנית אחורית
		_scene1.lights.add(new DirectionalLight(new Color(300, 300, 300), new Vector(1, -1, -0.8)));

		// 2. אור נקודתי: צבע אדום בוהק ומנוגד, ממוקם משמאל למטה וקרוב מאוד לחזית
		_scene1.lights.add(new PointLight(new Color(700, 0, 0), new Point(-55, -55, 45)).setKl(0.003).setKQ(0.0005));

		// 3. אור ספוט: צבע ירוק זרחני ממוקד, ממוקם מימין למעלה ומכוון למרכז
		_scene1.lights.add(new SpotLight(new Color(0, 800, 0), new Point(40, 40, 60), new Vector(-1, -1, -2))
				.setKl(0.001).setKQ(0.0003));

		_camera1.setResolution(RESOLUTION, RESOLUTION).build().renderImage().writeToImage("sphereMultipleLights");
	}

	/**
	 * ב. מתודת טסט עבור סצנה עם המשולשים המשלבת ריבוי מקורות אור חיצוניים.
	 */
	@Test
	public void testTrianglesMultipleLights() {
		// יצירת המשולשים המקוריים במדויק מנתוני ה-LightsTests (סעיפים ג' ו-י')
		Material material = new Material().setKD(KD3).setKS(KS3).setNShininess(SHININESS);

		Geometry triangle1 = new Triangle(VERTICES[0], VERTICES[1], VERTICES[2]).setMaterial(material);
		Geometry triangle2 = new Triangle(VERTICES[0], VERTICES[1], VERTICES[3]).setMaterial(material);

		_scene2._geometries.add(triangle1, triangle2);

		// 1. אור כיווני: צבע כחול חלש ומעומעם מאוד, מגיע מלמעלה ימין
		// הוא משמש רק רקע קל כדי שהאזורים החשוכים לא יהיו שחורים לחלוטין
		_scene2.lights.add(new DirectionalLight(new Color(0, 0, 150), new Vector(1, -1, -1)));

		// 2. אור נקודתי: צבע אדום חזק, ממוקם רחוק בצד ימין למטה (Z = -130)
		// הוא שוטף את החלק הימני התחתון של המשטח באור אדום דיפוזי רחב
		_scene2.lights.add(new PointLight(new Color(400, 0, 0), new Point(60, -50, -130)).setKl(0.002).setKQ(0.0004));

		// 3. ה-SPOTLIGHT המרכזי: צבע ירוק-זרחני עוצמתי (מנוגד לחלוטין לאדום ולכחול)
		// מיקמנו אותו בנקודה מוגבהת וקרובה מאוד (X=-30, Y=50, Z=-50)
		// וכיוונו את האלומה שלו באלכסון חד ישירות אל מרכז המשולש השמאלי (Vector(1, -1,
		// -1)).
		// המרחק הקרוב והזווית החדה יגרמו לאור הירוק להופיע כ"כתם חרוט" (Cone) מוגדר,
		// שדועך באופן מעגלי מובהק ויוצר הפרדה מוחלטת של ספוט!
		_scene2.lights.add(new SpotLight(new Color(0, 800, 0), new Point(-30, 50, -50), new Vector(1, -1, -1))
				.setKl(0.003).setKQ(0.0005));

		_camera2.setResolution(RESOLUTION, RESOLUTION).build().renderImage().writeToImage("trianglesMultipleLights");
	}
}