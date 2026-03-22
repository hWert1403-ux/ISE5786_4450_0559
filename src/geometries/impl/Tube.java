package geometries.impl;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

/**
 * Class Tube represents a semi-infinite tube in 3D space. The tube is defined
 * by a central axis (Ray) and a radius. * @author Your Name
 */
public class Tube extends RadialGeometry {

	/** The axis ray of the tube */
	protected final Ray _axis;

	/**
	 * Constructor for Tube * @param radius the radius of the tube
	 * 
	 * @param axis the central axis ray of the tube
	 */
	public Tube(double radius, Ray axis) {
		super(radius);
		this._axis = axis;
	}

	@Override
	public Vector getNormal(Point point) {
		// 1. Find the projection of (P - P0) onto the axis ray direction v
		// t = v * (P - P0)
		Vector v = _axis.direction();
		Point p0 = _axis.origin();

		double t = v.dotProduct(point.subtract(p0));

		// 2. The projection point on the axis is O' = P0 + t*v
		Point oDoublePrime = p0.add(v.scale(t));

		// 3. The normal is (P - O')
		return point.subtract(oDoublePrime).normalize();
	}

	@Override
	public String toString() {
		return "Tube: axis=" + _axis + ", " + super.toString();
	}
}
