package lighting;

import primitives.Color;

/**
 * Abstract base class representing a generic light source in the rendering
 * system. * @author shani&hadas
 */
abstract class Light {

	/**
	 * The intensity of the ambient light.
	 */
	protected final Color _intensity;

	/**
	 * Constructor for Light.
	 * 
	 * @param intensity the color intensity of the light.
	 */
	public Light(Color intensity) {
		_intensity = intensity;
	}

	/**
	 * Getter for the light intensity.
	 * 
	 * @return the intensity color.
	 */
	public Color getIntensity() {
		return _intensity;
	}

}
