package sampling;

import static java.awt.Color.YELLOW;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import geometries.impl.Sphere;
import geometries.impl.Triangle;
import lighting.AmbientLight;
import lighting.DirectionalLight;
import lighting.PointLight;
import primitives.Color;
import primitives.Material;
import primitives.Point;
import primitives.Vector;
import renderer.Camera;
import renderer.RayTracerType;
import scene.Scene;

/**
 * מבט מחלון מטוס בשעת שקיעה.
 * <p>
 * מיפוי מסך: שמאל=+X, ימין=-X, למעלה=+Y, למטה=-Y. Preview = אותה סצנה, בלי ASS.
 */
class AirplaneWindowView1Test {

	private static final Color HORIZON = new Color(255, 162, 98);

	/*
	 * שמיים ב-Z גבוה (רחוק) כדי שלא יחסמו את הים: קרן למטה פוגעת בים ב-z~400 לפני
	 * שמיים ב-z=1150. קיר שמיים ב-z=300 יצר את הפס הוורוד-כתום באמצע.
	 */
	private static final Point CAMERA = new Point(0, 20, -1000);
	private static final Point LOOK_AT = new Point(128, -4, 58);
	private static final int RESOLUTION = 1000;

	private static final double SEA_Y = -8;
	private static final double SKY_Z = 1150;

	AirplaneWindowView1Test() {
		/* to satisfy JavaDoc generator */
	}

	@Test
	void testAirplaneWindowViewPreview() {
		System.out.println("RUNNING: Airplane Window View - PREVIEW (no super sampling)");
		run(false, false, -2, "airplane_window_view_preview", false);
	}

	@Test
	@Disabled
	void testAirplaneWindowView() {
		System.out.println("RUNNING: Airplane Window View - FULL (ASS)");
		run(false, true, -2, "airplane_window_view", true);
	}

	Camera.Builder prepareScene() {
		Scene scene = new Scene("Airplane Window View").setBackground(HORIZON)
				.setAmbientLight(new AmbientLight(new Color(42, 38, 36)));

		Material wingMat = new Material().setKD(0.42).setKS(0.88).setNShininess(120).setKR(0.12);
		Material seaMat = new Material().setKD(0.18).setKS(0.38).setNShininess(42).setKR(0.12);
		Material landMat = new Material().setKD(0.42).setKS(0.14).setNShininess(18);
		Material fairingMat = new Material().setKD(0.3).setKS(0.68).setNShininess(85).setKR(0.08);
		Material skyMat = new Material().setKA(1.0).setKD(0).setKS(0);

		buildScene(scene, wingMat, seaMat, landMat, fairingMat, skyMat);

		scene.lights.add(new DirectionalLight(new Color(1050, 820, 480), new Vector(-1, -0.15, 0.05)));
		scene.lights.add(new DirectionalLight(new Color(110, 150, 220), new Vector(0.08, -1, 0.03)));
		scene.lights.add(new PointLight(new Color(180, 115, 70), new Point(90, 35, -200)).setKQ(0.00004));

		return Camera.getBuilder().setResolution(RESOLUTION, RESOLUTION).setRayTracer(scene, RayTracerType.SIMPLE)
				.setLocation(CAMERA).setDirection(LOOK_AT, new Vector(0, 1, 0)).setVpDistance(1000).setVpSize(200, 200);
	}

	private void buildScene(Scene scene, Material wingMat, Material seaMat, Material landMat, Material fairingMat,
			Material skyMat) {

		addSea(scene, seaMat);
		addSky(scene, skyMat);
		addIslands(scene, landMat);
		addWing(scene, wingMat, fairingMat);
	}

	private void addSea(Scene scene, Material seaMat) {
		Color sea = new Color(5, 14, 38);
		final double x0 = -500;
		final double z0 = -1200;
		final double dx = 52;
		final double dz = 34;
		final int nx = 20;
		final int nz = 72;

		for (int i = 0; i < nx; i++) {
			for (int j = 0; j < nz; j++) {
				double x1 = x0 + i * dx;
				double x2 = x0 + (i + 1) * dx;
				double z1 = z0 + j * dz;
				double z2 = z0 + (j + 1) * dz;
				double w = 0.45 * Math.sin(i * 0.25 + j * 0.18);

				Point bl = new Point(x1, SEA_Y + w, z1);
				Point br = new Point(x2, SEA_Y + w, z1);
				Point tl = new Point(x1, SEA_Y + w, z2);
				Point tr = new Point(x2, SEA_Y + w, z2);

				scene._geometries.add(new Triangle(bl, br, tr).setEmission(sea).setMaterial(seaMat),
						new Triangle(bl, tr, tl).setEmission(sea).setMaterial(seaMat));
			}
		}
	}

	/** שמיים רחוקים – רק קרניים כלפי מעלה מגיעים לכאן */
	private void addSky(Scene scene, Material skyMat) {
		final double xMin = -520;
		final double xMax = 520;
		final double yMin = 22;
		final double yMax = 275;
		final int bands = 18;
		final double bandH = (yMax - yMin) / bands;

		Color[] palette = { new Color(118, 142, 212), new Color(128, 152, 218), new Color(140, 162, 222),
				new Color(155, 172, 225), new Color(170, 182, 225), new Color(188, 188, 218), new Color(205, 188, 205),
				new Color(220, 182, 178), new Color(235, 176, 155), new Color(248, 170, 132), new Color(255, 165, 118),
				new Color(255, 160, 108), new Color(255, 158, 102), new Color(255, 162, 98), new Color(255, 162, 98),
				new Color(255, 160, 96), new Color(252, 156, 92), new Color(248, 152, 88) };

		for (int i = 0; i < bands; i++) {
			double y1 = yMin + i * bandH;
			double y2 = yMin + (i + 1) * bandH;

			Point tl = new Point(xMin, y2, SKY_Z);
			Point tr = new Point(xMax, y2, SKY_Z);
			Point bl = new Point(xMin, y1, SKY_Z);
			Point br = new Point(xMax, y1, SKY_Z);

			scene._geometries.add(new Triangle(tl, tr, br).setEmission(palette[i]).setMaterial(skyMat),
					new Triangle(tl, br, bl).setEmission(palette[i]).setMaterial(skyMat));
		}
	}

	private void addIslands(Scene scene, Material mat) {
		addPeak(scene, mat, new Point(-48, SEA_Y, 268), new Point(-118, SEA_Y, 278), new Point(-82, 26, 272),
				new Color(62, 48, 58));
		addPeak(scene, mat, new Point(-128, SEA_Y, 282), new Point(-205, SEA_Y, 292), new Point(-168, 30, 286),
				new Color(72, 56, 66));
		addPeak(scene, mat, new Point(-12, SEA_Y, 292), new Point(-78, SEA_Y, 302), new Point(-42, 18, 296),
				new Color(55, 44, 54));
	}

	private void addPeak(Scene scene, Material mat, Point a, Point b, Point c, Color color) {
		scene._geometries.add(new Triangle(a, b, c).setEmission(color).setMaterial(mat));
	}

	/**
	 * כנף קרובה למצלמה, שורש ב-+X גבוה (שמאל במסך), קצה לכיוון מרכז.
	 */
	private void addWing(Scene scene, Material wingMat, Material fairingMat) {
		final int n = 22;
		Color top = new Color(242, 244, 252);

		Point[] le = new Point[n + 1];
		Point[] te = new Point[n + 1];

		for (int i = 0; i <= n; i++) {
			double t = i / (double) n;
			double z = 22 + t * 78;
			le[i] = new Point(162 - t * 108, -16 + t * 34, z);
			te[i] = new Point(148 - t * 98, -20 + t * 5, z + 10 + t * 5);
		}

		for (int i = 0; i < n; i++) {
			scene._geometries.add(new Triangle(le[i], te[i], le[i + 1]).setEmission(top).setMaterial(wingMat),
					new Triangle(te[i], te[i + 1], le[i + 1]).setEmission(top).setMaterial(wingMat));
		}

		int[] fi = { 3, 8, 13, 18 };
		double[] fr = { 5.5, 5.0, 4.5, 4.0 };
		for (int k = 0; k < fi.length; k++) {
			double t = fi[k] / (double) n;
			double x = 148 - t * 98 - 1.5;
			double y = -20 + t * 5;
			double z = 22 + t * 78 + 10 + t * 5;
			double r = fr[k];
			scene._geometries.add(new Sphere(new Point(x, y - r + 0.4, z), r).setEmission(new Color(188, 192, 202))
					.setMaterial(fairingMat));
		}

		// winglet כחול נמוך בקצה
		double tipX = 162 - 108;
		double tipY = -16 + 34;
		double tipZ = 22 + 78;
		Color blue = new Color(12, 22, 82);
		Material wingletMat = new Material().setKD(0.38).setKS(0.48).setNShininess(65);
		Point base = new Point(tipX, tipY, tipZ);
		Point top1 = new Point(tipX - 2, tipY + 9, tipZ + 3);
		Point topOut = new Point(tipX - 7, tipY + 9, tipZ + 8);
		Point baseOut = new Point(tipX - 7, tipY, tipZ + 8);
		scene._geometries.add(new Triangle(base, top1, topOut).setEmission(blue).setMaterial(wingletMat),
				new Triangle(base, topOut, baseOut).setEmission(blue).setMaterial(wingletMat));
	}

	private void run(boolean useSS, boolean useASS, int threadsCount, String imageName, boolean printGrid) {
		Camera.Builder builder = prepareScene().setMultithreading(threadsCount).setDebugPrint(2);

		builder.setSuperSampling(false, 1, 1, 1.0, SamplingGrid.AreaShape.SQUARE, SamplingGrid.SamplingPattern.REGULAR);

		if (useASS) {
			builder.setSuperSampling(false, 9, 9, 1.0, SamplingGrid.AreaShape.SQUARE,
					SamplingGrid.SamplingPattern.REGULAR);
			builder.setAdaptiveSuperSampling(true, 5, 0.03);
		} else {
			builder.setAdaptiveSuperSampling(false, 1, 0.0);
		}

		if (useSS) {
			builder.setSuperSampling(true, 9, 9, 1.0, SamplingGrid.AreaShape.SQUARE,
					SamplingGrid.SamplingPattern.REGULAR);
		}

		long start = System.currentTimeMillis();
		var camera = builder.build().renderImage();
		if (printGrid) {
			camera.printGrid(50, new Color(YELLOW));
		}
		camera.writeToImage(imageName);
		System.out.println(">> Test [" + imageName + "] completed in: " + (System.currentTimeMillis() - start) + " ms");
	}
}
