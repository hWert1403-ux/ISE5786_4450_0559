package geometries.api;

import java.util.List;

import lighting.LightSource;
import primitives.Material;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

/**
 * Abstract class to calculate intersections between rays and objects. Supports
 * both point-only intersections and full intersection data (point and
 * geometry).
 */
public abstract class Intersectable {

	/**
	 * Inner static class to represent an intersection point with its geometry. This
	 * is a PDS (Plain Data Structure) class.
	 */
	public static final class Intersection {
		/** The geometry that was intersected */
		public final Geometry geometry;
		/** The point of intersection */
		public final Point point;
		/** The material with k (Ambient) */
		public Material material;

		/** normal at intersection point */
		public Vector normal;

		/** direction of intersection ray */
		public Vector v;

		/** dot scale between v and normal */
		public double vNormal;

		/** the active light source */
		public LightSource light;

		/** light direction */
		public Vector l;

		/** dot scale between l and normal */
		public double lNormal;

		/**
		 * Constructor for Intersection.
		 * 
		 * @param geometry the geometry that was intersected
		 * @param point    the point of intersection
		 */
		public Intersection(Geometry geometry, Point point) {
			this.geometry = geometry;
			this.point = point;
			// null if there is no geometry
			this.material = (geometry == null) ? new Material() : geometry.getMaterial();
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj instanceof Intersection other)
				return this.geometry == other.geometry && this.point.equals(other.point);
			return false;
		}

		@Override
		public String toString() {
			return "Intersection [geometry=" + geometry + ", point=" + point + "]";
		}
	}

	/**
	 * Calculates all intersection points between a ray and the object. This method
	 * is final and uses the NVI pattern to extract points from intersections.
	 * * @param ray the ray to check for intersections
	 * 
	 * @return list of intersection points, or null if no intersections found
	 */
	public final List<Point> findIntersections(Ray ray) {
		var intersections = calcIntersections(ray);
		return intersections == null ? null : intersections.stream().map(intersection -> intersection.point).toList();
	}

	/**
	 * Calculates all intersection data (geometry and point) between a ray and the
	 * object. This method is final and cannot be overridden.
	 * 
	 * @param ray the ray to check for intersections
	 * @return list of Intersection objects, or null if no intersections found
	 */
	public final List<Intersection> calcIntersections(Ray ray) {
		return calcIntersectionsHelper(ray);
	}

	/**
	 * Helper method to implement the actual intersection logic in subclasses. Part
	 * of the NVI pattern.
	 * 
	 * @param ray the ray to check for intersections
	 * @return list of Intersection objects, or null if no intersections found
	 */
	protected abstract List<Intersection> calcIntersectionsHelper(Ray ray);
}
