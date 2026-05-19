package renderer;

import geometries.api.Intersectable.Intersection;
import lighting.LightSource;
import primitives.Color;
import primitives.Double3;
import primitives.Ray;
import primitives.Vector;
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
		return calcColor(closestIntersection, ray.direction());
	}

	/**
	 * Helper method to calculate the color at a specific point. Incorporates
	 * emission color and ambient light attenuated by the material's kA factor.
	 * * @param intersection The intersection point including geometry and material
	 * data.
	 * 
	 * @param v ray direction
	 * @return The calculated color at the point.
	 */
	private Color calcColor(Intersection intersection, Vector v) {
		return !preprocessIntersection(intersection, v) ? Color.BLACK
				: _scene._ambientLight.getIntensity().scale(intersection.material.kA)
						.add(calcColorLocalEffects(intersection));
	}

	/** sum of local effects from all external light sources in the scene */
	private Color calcColorLocalEffects(Intersection intersecion) {
		Color color = intersecion.geometry.getEmission();
		for (LightSource lightSource : _scene.lights) {
			if (preprocessLightSource(intersecion, lightSource)) {
				color = color.add(lightSource.getIntensity(intersecion.point)
						.scale(calcDiffuse(intersecion).add(calcSpecular(intersecion))));
			}
		}
		return color;
	}

	/**
	 * Calculate the total diffusion(פיזור) coefficient of light reflection from the
	 * current light source * @param intersection The intersection point data
	 * (cached)
	 * 
	 * @return The diffuse reflection component as a Double3
	 */
	private Double3 calcDiffuse(Intersection intersection) {
		// Get the material's diffuse reflection coefficient (kD)
		Double3 kd = intersection.geometry.getMaterial().kD;

		// l is the direction vector from the light source to the point
		Vector l = intersection.l;
		// n is the normal vector at the intersection point
		Vector n = intersection.normal;

		// According to Phong model, diffuse reflection depends on |nl| (cosine of
		// angle)
		// We use absolute value or ensure signs match based on orientation,
		// but standard Phong uses max(0, -l.dotProduct(n)) or absolute depending on
		// implementation.
		// Note: getL returns vector from light to point, so -l points towards the light
		// source.
		double minusL_dot_n = Math.max(0, l.scale(-1).dotProduct(n));

		return kd.scale(minusL_dot_n);
	}

	/**
	 * Calculate the total specular(מראתיות, ברק) coefficient of light reflection
	 * from the current light source * @param intersection The intersection point
	 * data (cached)
	 * 
	 * @return The specular reflection component as a Double3
	 */
	private Double3 calcSpecular(Intersection intersection) {
		// Get the material's specular reflection coefficient (kS) and shininess
		// (nShininess)
		Double3 ks = intersection.geometry.getMaterial().kS;
		int nShininess = intersection.geometry.getMaterial().nShininess;

		// l is the direction vector from the light source to the point
		Vector l = intersection.l;
		// n is the normal vector at the intersection point
		Vector n = intersection.normal;
		// v is the direction vector from the camera (viewer) to the point
		Vector v = intersection.v;

		// Calculate the reflection vector: r = l - 2 * (l . n) * n
		double l_dot_n = l.dotProduct(n);
		Vector r = l.subtract(n.scale(2 * l_dot_n)); // standard reflection formula

		// Specular intensity depends on the cosine of the angle between the reflection
		// vector (r)
		// and the viewing vector (v). Since v points from camera to object, -v points
		// to the viewer.
		double minusV_dot_r = Math.max(0, v.scale(-1).dotProduct(r));

		// Phong component: (max(0, -v . r))^nShininess
		double specularFactor = Math.pow(minusV_dot_r, nShininess);

		return ks.scale(specularFactor);
	}
}
