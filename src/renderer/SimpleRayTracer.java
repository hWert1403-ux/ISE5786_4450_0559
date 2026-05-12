package renderer;

import geometries.api.Intersectable.Intersection;
import primitives.Color;
import primitives.Double3;
import primitives.Ray;
import scene.Scene;

/**
 * Basic implementation of a ray tracer.
 */
class SimpleRayTracer extends RayTracerBase {

	/**
	 * Constructor receiving the scene to render
	 * 
	 * @param scene The scene for the ray tracer.
	 */
	public SimpleRayTracer(Scene scene) {
		super(scene);
	}

	/**
	 * Traces a ray and returns the color of the closest intersection point.
	 * 
	 * @param ray The ray to trace.
	 * @return The color at the intersection point or the background color.
	 */
	@Override
	public Color traceRay(Ray ray) {
		// Find all intersections with geometries in the scene
		var intersections = _scene._geometries.calcIntersections(ray);

		// If no intersections, return the scene's background color
		if (intersections == null) {
			return _scene._background;
		}

		// Find the closest point to the ray's head
		Intersection closestIntersection = ray.findClosestIntersection(intersections);

		// Calculate the color at that point
		return calcColor(closestIntersection);
	}

	/**
	 * Helper method to calculate the color at a specific point. Incorporates
	 * emission color and ambient light attenuated by the material's kA factor.
	 * * @param intersection The intersection point including geometry and material
	 * data.
	 * 
	 * @return The calculated color at the point.
	 */
	private Color calcColor(Intersection intersection) {
		// 1. Get the ambient light intensity from the scene
		Color ambientLight = _scene._ambientLight.getIntensity();

		// 2. Get the kA factor from the material of the specific geometry
		Double3 kA = intersection.material.kA;

		// 3. Formula: Ip = Ie + kA * Ia
		// Scale the ambient light by kA and add the emission color
		return intersection.geometry.getEmission().add(ambientLight.scale(kA));
	}
}
