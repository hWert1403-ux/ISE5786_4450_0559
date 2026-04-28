package renderer;

import primitives.Color;
import primitives.Ray;
import scene.Scene;

/**
 * Base class for ray tracing engines.
 * This class is abstract and defines the basic structure for all ray tracers. 
 * * @author hadas&shani
 */
abstract class RayTracerBase {

	/**
	 * The scene to be rendered. 
	 */
	protected Scene _scene;

	/**
	 * Constructor for RayTracerBase. 
	 * * @param scene The scene to be rendered.
	 */
	public RayTracerBase(Scene scene) {
		_scene = scene;
	}

	/**
	 * Traces a ray and calculates the color of the point it hits. 
	 * * @param ray The ray to trace.
	 * @return The color at the intersection point.
	 */
	abstract Color traceRay(Ray ray);
}