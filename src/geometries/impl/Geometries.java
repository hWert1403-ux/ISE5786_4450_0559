package geometries.impl;

import java.util.ArrayList;
import java.util.List;

import geometries.api.Intersectable;
import primitives.Point;
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

	@Override
	public List<Point> findIntersections(Ray ray) {
		List<Point> result = null;

		// Iterate through all geometries using foreach loop
		for (Intersectable geo : _geometries) {
			var geoPoints = geo.findIntersections(ray);

			if (geoPoints != null) {
				// Initialize the list only when the first intersection is found
				if (result == null) {
					result = new ArrayList<>();
				}
				// Collect all intersection points (Delegation)
				result.addAll(geoPoints);
			}
		}
		return result;
	}
}