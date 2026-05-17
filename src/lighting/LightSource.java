/**
 * 
 */
package lighting;

import primitives.Color;
import primitives.Point;
import primitives.Vector;

/**
 * 
 */
public interface LightSource {

	/**
	 * calculate light intensity that comes from specific point
	 * 
	 * @param p
	 * @return light intensity
	 */
	public Vector getL(Point p);

	/**
	 * Calculates the normalized light-direction-vector from the light source to the
	 * point
	 * 
	 * @param p
	 * @return normalized light-direction-vector
	 */
	public Color getIntensity(Point p);

}
