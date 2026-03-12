
package geometries.impl;

import geometries.api.Geometry;
import primitives.Point;
import primitives.Vector;

/**
 * Class Plane represents a plane in 3D space.
 * A plane is defined by a point and a normal vector orthogonal to the plane.
 * * @author hadas&shani
 */
public class Plane extends Geometry {
	/** A point on the plane */
	private final Point _point;
	/** The normal vector u to the plane */
	private final Vector _normal;
	
	
	/**
     * Constructor to initialize a plane from three points.
     * The points are expected to be ordered counter-clockwise.
     * At this stage, only the first point is stored.
     * * @param p1 first point
     * @param p2 second point
     * @param p3 third point
     */
	public Plane(Point p1, Point p2, Point p3) {
		_point=p1;
		_normal=null; // Placeholder as per instructions for this stage
	}
	
	/**
     * Constructor to initialize a plane from a point and a normal vector.
     * The normal vector is normalized to unit length.
     * * @param point - a point on the plane
     * @param normal - the normal vector to the plane
     */
	public Plane(Point point, Vector normal) {
		_point=point;
		_normal= normal.normalize();
	}
	
	@Override
    public Vector getNormal(Point point) {
        return _normal;
    }

	
}
