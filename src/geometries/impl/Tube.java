package geometries.impl;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;
/**
 * Class Tube represents a semi-infinite tube in 3D space.
 * The tube is defined by a central axis (Ray) and a radius.
 * * @author Your Name
 */
public class Tube extends RadialGeometry {

	/** The axis ray of the tube */
	protected final Ray _axis;
	
	/**
     * Constructor for Tube
     * * @param radius the radius of the tube
     * @param axis   the central axis ray of the tube
     */
	public Tube(double radius,  Ray axis) {
		super(radius);
		this._axis=axis;
	}

	
	@Override
	public Vector getNormal(Point point) {
		return null; // Implementation will be added late
	}

}
