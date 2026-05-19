package primitives;

/**
 * Class Material is a PDS (Passive Data Structure) representing the physical
 * properties of a geometric body's material in the light propagation model. In
 * this stage, it manages the ambient light attenuation coefficient (kA).
 * * @author [Your Name]
 */
public class Material {

	/**
	 * Ambient light attenuation coefficient (kA). Represented as a Double3 to allow
	 * different attenuation for Red, Green, and Blue. Initialized to Double3.ONE
	 * (1,1,1) by default.
	 */
	public Double3 kA = Double3.ONE, kD = Double3.ZERO, kS = Double3.ZERO;

	public int nShininess = 0;

	/**
	 * Default constructor for Material.
	 */
	public Material() {
	}

	/**
	 * Sets the ambient attenuation coefficient (kA) using a Double3 value. * @param
	 * k The attenuation value as a Double3 object.
	 * 
	 * @return The Material object itself (for builder pattern chaining).
	 */
	public Material setKA(Double3 k) {
		this.kA = k;
		return this;
	}

	/**
	 * Sets the ambient attenuation coefficient (kA) using a single double value.
	 * Creates a Double3 object where all three components (RGB) are the same.
	 * * @param k The attenuation value as a double.
	 * 
	 * @return The Material object itself (for builder pattern chaining).
	 */
	public Material setKA(double k) {
		this.kA = new Double3(k);
		return this;
	}

	public Material setKD(double k) {
		this.kD = new Double3(k);
		return this;
	}

	public Material setKD(Double3 k) {
		this.kD = k;
		return this;
	}

	public Material setKS(double k) {
		this.kS = new Double3(k);
		return this;
	}

	public Material setKS(Double3 k) {
		this.kS = k;
		return this;
	}

	public Material setNShininess(int n) {
		this.nShininess = n;
		return this;
	}
}