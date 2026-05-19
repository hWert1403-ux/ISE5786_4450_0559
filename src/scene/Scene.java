package scene;

import java.util.ArrayList;
import java.util.List;

import geometries.impl.Geometries;
import lighting.AmbientLight;
import lighting.LightSource;
import primitives.Color;

/**
 * Class representing a 3D scene containing geometries, background color, and
 * ambient lighting. This class is a PDS (Plain Data Structure) with public
 * fields.
 * 
 * @author shani&hadas
 */
public class Scene {

	/** The name of the scene. */
	public String _name;

	/** The background color of the scene, default is Black. */
	public Color _background = Color.BLACK;

	/** The ambient light of the scene, default is NONE. */
	public AmbientLight _ambientLight = AmbientLight.NONE;

	/** The collection of geometric shapes in the scene. */
	public Geometries _geometries = new Geometries();

	public List<LightSource> lights = new ArrayList<>();

	/**
	 * Constructor for Scene.
	 * 
	 * @param name the name of the scene[cite: 32].
	 */
	public Scene(String name) {
		_name = name;
	}

	/**
	 * Setter for background color using Chaining.
	 * 
	 * @param background the background color to set.
	 * @return the current scene object for chaining.
	 */
	public Scene setBackground(Color background) {
		_background = background;
		return this;
	}

	/**
	 * Setter for ambient light using Chaining.
	 * 
	 * @param ambientLight the ambient light to set.
	 * @return the current scene object for chaining.
	 */
	public Scene setAmbientLight(AmbientLight ambientLight) {
		_ambientLight = ambientLight;
		return this;
	}

	/**
	 * Setter for geometries using Chaining.
	 * 
	 * @param geometries the collection of geometries to set[cite: 33].
	 * @return the current scene object for chaining[cite: 33].
	 */
	public Scene setGeometries(Geometries geometries) {
		_geometries = geometries;
		return this;
	}

}
