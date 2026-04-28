package renderer;

import primitives.Color;
import primitives.Point;
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
		var intersections = _scene._geometries.findIntersections(ray);

		// If no intersections, return the scene's background color
		if (intersections == null) {
			return _scene._background;
		}

		// Find the closest point to the ray's head
		Point closestPoint = ray.findClosestPoint(intersections);

		// Calculate the color at that point
		return calcColor(closestPoint);
	}

	/**
	 * Helper method to calculate the color at a specific point. Currently returns
	 * the combined color of ambient light and background.
	 * 
	 * @param intersection The point of intersection.
	 * @return The color at the point.
	 */
	private Color calcColor(Point intersection) {
		// Return the intensity of ambient light
		return _scene._ambientLight.getIntensity();
	}
}
