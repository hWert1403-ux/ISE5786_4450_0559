package geometries.impl;

import primitives.Ray;

/**
 * Class Cylinder represents a finite cylinder in 3D space.
 * It inherits from Tube and adds a height.
 * @author hadas&shani
 */
public class Cylinder extends Tube {
	
	/**
	 * The height of the finite cylinder
	 */
	private final double _height;
	
	/**
     * Constructor to initialize a cylinder.
     * @param radius- the radius of the cylinder
     * @param axis - the central axis ray
     * @param height -the height (length) of the cylinder
     */
	public Cylinder(double radius, Ray axis,double height) {
		super(radius, axis);
		this._height=height;
	}

	

}
