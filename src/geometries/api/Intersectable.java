package geometries.api;

import java.util.List;

import primitives.Point;
import primitives.Ray;

/**
 * Abstract class to calculate intersections between rays and objects
 */
public abstract class Intersectable {
	/**
	 * Abstract function to calculate intersections between rays and objects
	 * 
	 * 
	 * @param ray- the ray from the camera
	 * @return the list of intersection points of the object by the ray. if there
	 *         are no intersections-return null (not an empty list)
	 */
	public abstract List<Point> findIntersections(Ray ray);
}
