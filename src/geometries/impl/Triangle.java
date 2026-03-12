package geometries.impl;

import primitives.Point;

/**
 * Class Triangle represents a triangle in 3D space. It inherits from Polygon
 * and is defined by three vertices.
 * 
 * @author hadas&shani
 */
public class Triangle extends Polygon {

	/**
	 * Constructor to initialize a triangle from three points.
	 * 
	 * @param p1 first vertex
	 * @param p2 second vertex
	 * @param p3 third vertex
	 */

	public Triangle(Point p1, Point p2, Point p3) {
		super(p1, p2, p3);
	}

	@Override
	public String toString() {
		return "Triangle: " + super.toString();
	}
}
