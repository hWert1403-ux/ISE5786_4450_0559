package renderer;

import static primitives.Util.alignZero;

import geometries.api.Intersectable.Intersection;
import lighting.LightSource;
import primitives.Color;
import primitives.Point;
import primitives.Ray;
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

	/**
	 * Constructs a ray from the camera lens to a specific point and fetches its
	 * color.
	 */
	abstract Color traceRayToPoint(Point p, Point origin);

	/** set date related to intersection */
	protected boolean preprocessIntersection(Intersection intersection, Vector v) {
		intersection.v = v;
		intersection.normal = intersection.geometry.getNormal(intersection.point);
		intersection.vNormal = alignZero(intersection.v.dotProduct(intersection.normal));

		return intersection.vNormal != 0;
	}

	/** set date related to light source */
	protected boolean preprocessLightSource(Intersection intersection, LightSource light) {
		intersection.light = light;
		intersection.l = light.getL(intersection.point);
		intersection.lNormal = alignZero(intersection.l.dotProduct(intersection.normal));
		return intersection.lNormal * intersection.vNormal > 0;
	}
}