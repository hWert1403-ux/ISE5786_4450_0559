package renderer;

import geometries.api.Intersectable.Intersection;
import lighting.LightSource;
import primitives.Color;
import primitives.Ray;
import primitives.Util;
import primitives.Vector;
import scene.Scene;

/**
 * Base class for ray tracing engines. This class is abstract and defines the
 * basic structure for all ray tracers. * @author hadas&shani
 */
abstract class RayTracerBase {

	/**
	 * The scene to be rendered.
	 */
	protected Scene _scene;

	/**
	 * Constructor for RayTracerBase. * @param scene The scene to be rendered.
	 */
	public RayTracerBase(Scene scene) {
		_scene = scene;
	}

	/**
	 * Traces a ray and calculates the color of the point it hits. * @param ray The
	 * ray to trace.
	 * 
	 * @return The color at the intersection point.
	 */
	abstract Color traceRay(Ray ray);

	/** set date related to intersection */
	protected boolean preprocessIntersection(Intersection intersection, Vector v) {
		intersection.v = v;
		intersection.normal = intersection.geometry.getNormal(intersection.point);
		intersection.vNormal = Util.alignZero(intersection.v.dotProduct(intersection.normal)); // TODO why have
																								// util.zero and not
																								// zero alone
		return intersection.vNormal != 0;
	}

	/** set date related to light source */
	protected boolean preprocessLightSource(Intersection intersection, LightSource light) {
		intersection.light = light;
		intersection.l = light.getL(intersection.point);
		intersection.lNormal = Util.alignZero(intersection.l.dotProduct(intersection.normal));
		return intersection.lNormal * intersection.vNormal > 0;
	}
}