package geometries.impl;

import java.util.ArrayList;
import java.util.List;

import geometries.api.Intersectable;
import primitives.Ray;

/**
 * Composite class for all geometries in the scene. Inherits from Intersectable
 * abstract class. * @author hadas&shani
 */
public class Geometries extends Intersectable {
	/** List of intersectable objects */
	private final List<Intersectable> _geometries = new ArrayList<>();

	/**
	 * Default empty constructor
	 */
	public Geometries() {
	}

	/**
	 * Constructor with initial geometries
	 * 
	 * @param geometries variable amount of geometries to add
	 */
	public Geometries(Intersectable... geometries) {
		add(geometries);
	}

	/**
	 * Adds geometries to the collection. Uses Java's built-in collections for
	 * efficiency.
	 * 
	 * @param geometries variable amount of geometries to add
	 */
	public void add(Intersectable... geometries) {
		if (geometries != null && geometries.length > 0) {
			_geometries.addAll(List.of(geometries));
		}
	}

	/**
	 * Helper method to calculate intersections for all shapes in the collection. It
	 * follows the NVI pattern and returns a list of Intersection objects. * @param
	 * ray The ray that hits the geometries
	 * 
	 * @return List of intersections, or null if no intersections are found
	 */
	@Override
	protected List<Intersection> calcIntersectionsHelper(Ray ray) {
		List<Intersection> result = null;

		for (Intersectable geo : _geometries) {
			// IMPORTANT: We must call the public calcIntersections of each geometry
			var geoIntersections = geo.calcIntersections(ray);

			if (geoIntersections != null) {
				// Initialize the list only when the first intersection is found
				if (result == null) {
					result = new ArrayList<>();
				}
				// Add all found intersections from the specific geometry
				result.addAll(geoIntersections);
			}
		}
		return result;
	}
}