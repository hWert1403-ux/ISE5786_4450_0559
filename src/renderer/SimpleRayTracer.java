package renderer;

import geometries.api.Intersectable.Intersection;
import lighting.LightSource;
import primitives.Color;
import primitives.Double3;
import primitives.Ray;
import primitives.Util;
import primitives.Vector;
import scene.Scene;

/**
 * A basic implementation of a ray tracer that computes the color of
 * intersection points in a scene using ambient lighting.
 */
class SimpleRayTracer extends RayTracerBase {

//	private static final double DELTA = 0.1;
	private static final int MAX_CALC_COLOR_LEVEL = 10;
	private static final double MIN_CALC_COLOR_K = 0.001;
	private static final Double3 INITIAL_K = Double3.ONE;

	/**
	 * Constructs a simple ray tracer for the given scene.
	 *
	 * @param scene the scene to render
	 */
	public SimpleRayTracer(Scene scene) {
		super(scene);
	}

	@Override
	Color traceRay(Ray ray) {
		Intersection intersection = findClosestIntersection(ray);
		return intersection == null ? _scene._background : calcColor(intersection, ray.direction());
	}

	/**
	 * Calculates the color at a specific intersection point. At this stage, it only
	 * returns the ambient light's intensity.
	 *
	 * @param intersection the intersection point on a geometry
	 * @return the calculated color (ambient light)
	 */
	private Color calcColor(Intersection intersection, Vector v) {
		return preprocessIntersection(intersection, v)
				? _scene._ambientLight.getIntensity().scale(intersection.geometry.getMaterial().kA).add(
						calcColor(intersection, MAX_CALC_COLOR_LEVEL, INITIAL_K))
				: Color.BLACK;
	}

	/**
	 * Recursive color calculation (without Ambient Light)
	 * 
	 * @param intersection
	 * @param level        of recursion
	 * @param k
	 * @return additional color
	 */
	private Color calcColor(Intersection intersection, int level, Double3 k) {
		Color color = calcColorLocalEffects(intersection, k);
		return 1 == level ? color : color.add(calcGlobalEffects(intersection, level, k));
	}

	public Color calcColorLocalEffects(Intersection intersection, Double3 k) {
		Color color = intersection.geometry.getEmission();
		for (LightSource lightSource : _scene.lights) {
			if (preprocessLightSource(intersection, lightSource) && unshaded(intersection)) {
				color = color.add(lightSource.getIntensity(intersection.point)
						.scale(calcDiffuse(intersection).add(calcSpecular(intersection))));
				color = color.add(color.scale(k));
			}
		}
		return color;
	}

	Double3 calcDiffuse(Intersection intersection) {
		return intersection.material.kD.scale(intersection.lNormal > 0 ? intersection.lNormal : -intersection.lNormal);
	}

	Double3 calcSpecular(Intersection intersection) {
		Vector r = intersection.normal.scale(2 * intersection.lNormal).subtract(intersection.l);
		return intersection.material.kS
				.scale(Math.pow(Math.max(0.0, (intersection.v.dotProduct(r))), intersection.material.nShininess));
	}

	/**
	 * Avoid self-shadowing by slightly shifting the shadow ray head along the
	 * normal toward the light source
	 * 
	 * @param intersection
	 * @return true if the point is unshaded (no opaque geometries block the light),
	 *         false otherwise
	 */
	private boolean unshaded(Intersection intersection) {
		Vector pointToLight = intersection.l.scale(-1);

		Ray shadowRay = new Ray(intersection.point, pointToLight, intersection.normal);

		var shadowIntersections = _scene._geometries.calcIntersections(shadowRay);
		if (shadowIntersections == null)
			return true;

		double lightDistance = intersection.light.getDistance(intersection.point);
		for (var s : shadowIntersections)// s is point
		{
			double distanceToObstacle = intersection.point.distance(s.point);
			if (distanceToObstacle < lightDistance) {
				// get kt of blocking geometry
				Double3 kT = s.geometry.getMaterial().kT;

				if (!kT.isGreaterThan(MIN_CALC_COLOR_K))
					return false; // the geometry is completely on-transpatent
			}
		}
		return true;
	}

	/**
	 * Constructs a transparency ray (refraction ray) continuing in the same
	 * direction. * @param gp The geometry-point intersection
	 * 
	 * @return A new Ray for the transparency calculation
	 */
	private Ray constructTransparencyRay(Intersection gp) {
		Vector n = gp.geometry.getNormal(gp.point);
		return new Ray(gp.point, gp.v, n);
	}

	/**
	 * Constructs a reflection ray based on the incoming ray direction and the
	 * surface normal. * @param gp The geometry-point intersection
	 * 
	 * @return A new Ray for the reflection calculation
	 */
	private Ray constructReflectionRay(Intersection gp) {
		Vector n = gp.geometry.getNormal(gp.point);

		// r = v - 2 * (v . n) * n
		double vn = gp.v.dotProduct(n);

		// If vector is upright (vn == 0) there is no significant reflection
		if (Util.isZero(vn)) {
			return new Ray(gp.point, gp.v, n);
		}

		Vector r = gp.v.subtract(n.scale(2 * vn));
		return new Ray(gp.point, r, n);
	}

	/**
	 * calculate global effects(transpires and reflection) by calling recursive
	 * function calcGlobalEffects
	 * 
	 * @param intersection
	 * @param level
	 * @param k
	 * @return new color by original and global effects(Transparency and reflection)
	 */
	private Color calcGlobalEffects(Intersection intersection, int level, Double3 k) {
		return calcGlobalEffect(constructTransparencyRay(intersection), level, k, intersection.material.kT)
				.add(calcGlobalEffect(constructReflectionRay(intersection), level, k, intersection.material.kR));
	}

	/**
	 * recursive calcGlobalEffects
	 * 
	 * @param ray
	 * @param level
	 * @param k
	 * @param kx
	 * @return color by global effects(Transparency and Reflection)
	 */
	private Color calcGlobalEffect(Ray ray, int level, Double3 k, Double3 kx) {
		Double3 kkx = k.product(kx);
		if (kkx.isLowerThan(MIN_CALC_COLOR_K))
			return Color.BLACK;
		Intersection intersection = findClosestIntersection(ray);
		if (intersection == null)
			return _scene._background.scale(kx);
		return preprocessIntersection(intersection, ray.direction()) ? calcColor(intersection, level - 1, kkx).scale(kx)
				: Color.BLACK;
	}

	/**
	 * Find the closest intersection point of a ray with the scene's geometries.
	 * 
	 * @param ray The ray to trace
	 * @return The closest Intersection point, or null if there are no
	 *         intersections.
	 */
	private Intersection findClosestIntersection(Ray ray) {
		var intersections = _scene._geometries.calcIntersections(ray);
		return intersections == null ? null : ray.findClosestIntersection(intersections);
	}
}