
package geometries.api;

import primitives.Color;
import primitives.Material;
import primitives.Point;
import primitives.Vector;

/**
 * Interface Geometry is the common interface for all geometric shapes in the
 * project. All shapes must be able to provide a normal vector at a given point
 * on their surface. * @author hadas&shani
 */
public abstract class Geometry extends Intersectable {

	private Color _emission = Color.BLACK;
	private Material _material = new Material();

	/**
	 * Calculates the normal vector to the geometric body at a specific point. The
	 * normal is a unit vector (normalized) that is orthogonal to the surface at the
	 * given point. * @param point- The point on the surface to find the normal at.
	 * 
	 * @return The normalized normal vector.
	 */
	public abstract Vector getNormal(Point point);

	/**
	 * Getter for the emission color. * @return The emission color.
	 */
	public Color getEmission() {
		return _emission;
	}

	/**
	 * Setter for the emission color (Builder pattern). * @param emission The new
	 * emission color.
	 * 
	 * @return The Geometry object itself for chaining.
	 */
	public Geometry setEmission(Color emission) {
		this._emission = emission;
		return this;
	}

	/**
	 * Getter for the Ambient color.
	 */
	public Material getMaterial() {
		return _material;
	}

	/**
	 * Setter for the Ambient color (Builder pattern).
	 * 
	 * @return The Geometry object itself for chaining.
	 */
	public Geometry setMaterial(Material material) {
		_material = material;
		return this;
	}

}
