package renderer;

import geometries.api.Intersectable.Intersection;
import lighting.LightSource;
import primitives.Color;
import primitives.Double3;
import primitives.Ray;
import primitives.Vector;
import scene.Scene;

/**
 * A basic implementation of a ray tracer that computes the color of
 * intersection points in a scene using ambient lighting.
 */
class SimpleRayTracer extends RayTracerBase {

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
		// Find all intersection points between the ray and the scene's geometries
		var intersections = _scene._geometries.calcIntersections(ray);

		// If there are no intersections, return the background color
		if (intersections == null) {
			return _scene._background;
		}

		// Find the closest intersection point to the camera
		Intersection closestIntersection = ray.findClosestIntersection(intersections);

		// Calculate and return the color of the closest point
		return calcColor(closestIntersection, ray.direction());
	}

	/**
	 * Calculates the color at a specific intersection point. At this stage, it only
	 * returns the ambient light's intensity.
	 *
	 * @param intersection the intersection point on a geometry
	 * @return the calculated color (ambient light)
	 */
	private Color calcColor(Intersection intersection, Vector v) {
		return !preprocessIntersection(intersection, v) ? Color.BLACK
				: _scene._ambientLight.getIntensity().scale(intersection.material.kA)
						.add(calcColorLocalEffects(intersection));
	}

	public Color calcColorLocalEffects(Intersection intersection) {
		Color color = intersection.geometry.getEmission();
		for (LightSource lightSource : _scene.lights) {
			if (preprocessLightSource(intersection, lightSource)) {
				color = color.add(lightSource.getIntensity(intersection.point)
						.scale(calcDiffuse(intersection).add(calcSpecular(intersection))));
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
}