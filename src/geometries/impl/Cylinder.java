package geometries.impl;

import static primitives.Util.isZero;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

/**
 * Class Cylinder represents a finite cylinder in 3D space. It inherits from
 * Tube and adds a height.
 * 
 * @author hadas&shani
 */
public class Cylinder extends Tube {

	/**
	 * The height of the finite cylinder
	 */
	private final double _height;

	/**
	 * Constructor to initialize a cylinder.
	 * 
	 * @param radius- the radius of the cylinder
	 * @param axis    - the central axis ray
	 * @param height  -the height (length) of the cylinder
	 */
	public Cylinder(double radius, Ray axis, double height) {
		super(radius, axis);
		this._height = height;
	}

	@Override
	public Vector getNormal(Point point) {
		Point p0 = _axis.origin();
		Vector v = _axis.direction();

		// t = v * (point - p0)
		double t = v.dotProduct(point.subtract(p0));

		// Check if the point is on the bottom base (t is effectively 0)
		if (isZero(t)) {
			return v.scale(-1);
		}

		// Check if the point is on the top base (t is effectively the height)
		if (isZero(t - _height)) {
			return v;
		}

		// Otherwise, it's on the side shell (same as Tube)
		Point o = p0.add(v.scale(t));
		return point.subtract(o).normalize();
	}

	@Override
	public String toString() {
		return "Cylinder: height=" + _height + ", " + super.toString();
	}

}
