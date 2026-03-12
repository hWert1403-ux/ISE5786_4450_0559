
package geometries.api;

import java.util.Vector;

import primitives.Point;

/**
 * Interface Geometry is the common interface for all geometric shapes in the project.
 * All shapes must be able to provide a normal vector at a given point on their surface.
 * * @author hadas&shani
 */
public abstract class Geometry {

	/**
     * Calculates the normal vector to the geometric body at a specific point.
     * The normal is a unit vector (normalized) that is orthogonal 
     * to the surface at the given point.
     * * @param point- The point on the surface to find the normal at.
     * @return The normalized normal vector.
     */
	public abstract Vector getNormal(Point point);

}
