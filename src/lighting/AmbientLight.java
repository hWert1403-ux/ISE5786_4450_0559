/**
 * 
 */
package lighting;

import primitives.Color;

/**
 * Class representing ambient light in a 3D scene. This class is immutable.
 */
public class AmbientLight {

	/**
	 * The intensity of the ambient light.
	 */
	private final Color _intensity;

	/**
	 * Static constant representing no ambient light (Black color).
	 */
	public static final AmbientLight NONE = new AmbientLight(Color.BLACK);

	/**
	 * Constructor for AmbientLight.
	 * 
	 * @param intensity the color intensity of the light.
	 */
	public AmbientLight(Color intensity) {
		_intensity = intensity;
	}

	/**
	 * Getter for the light intensity.
	 * 
	 * @return the intensity color.
	 */
	public Color geIntensity() {
		return _intensity;
	}
}
