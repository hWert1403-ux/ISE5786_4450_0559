
package geometries.impl;

import geometries.api.Geometry;

/**
 * Abstract class RadialGeometry is the base class for all geometric shapes that
 * have a radius (e.g., Sphere, Cylinder, Tube). It inherits from the
 * {@link Geometry} class. * @author hadas&shani
 */
public abstract class RadialGeometry extends Geometry {
	/** The radius of the radial geometry */
	protected final double _radius;
	/** The squared radius, pre-calculated for performance optimization */
	protected final double _radiusSquared;

	/**
	 * Constructor to initialize a radial geometry with a given radius. It also
	 * calculates and stores the squared radius. * @param radius- the radius of the
	 * shape
	 */
	public RadialGeometry(double radius) {
		this._radius = radius;
		this._radiusSquared = radius * radius;
	}

	@Override
	public String toString() {
		return "radius=" + _radius;
	}

}
