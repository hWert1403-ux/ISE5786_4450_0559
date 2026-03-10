package primitives;

/**
 * Class Point represents a point in a 3D space.
 * * The point is defined by a triplet of double values (d1, d2, d3)
 * which specify its location along the X, Y, and Z axes.
 * * This class is a fundamental building block for geometric operations, 
 * such as calculating distances between points or defining vectors.
 * * @author hadas&shani
 */

public class Point {
	public static final Point ZERO = new Point(Double3.ZERO);
	protected final Double3 _xyz;
	
	/**
     * Constructor to initialize Point with three double values.
     * * @param x coordinate on the X axis
     * @param y coordinate on the Y axis
     * @param z coordinate on the Z axis
     */
	public Point(double x,double y,double z) {
	   _xyz=new Double3(x,y,z);
	}
	
	/**
	 * constructor to initialize Point with Double3 object
	 * @param xyz - Triplet of coordinate values
	 */
	public Point(Double3 xyz) {
		_xyz=xyz; 
	}
	/**
	 * calculate the vector from other point to current point
	 * @param other
	 * @return -return vector
	 */
	Vector substract(Point other) {
		
	}
	/**
	 * calculate new position for the point
	 * @param vector
	 * @return the new point after moving with vector
	 */
	 Point add(Vector vector) {
		 
	 }
	 /**
	  * calculate squared distance between current and other points
	  * @param other
	  * @return- squared distance between current and other points
	  */
	double distanceSquared(Point other) {
		
	}
	/**
	 * calculate distance between current and other points
	 * <p>based on distanceSquared func<p>
	 * @param other
	 * @return - distance between current and other points
	 */
	double distance(Point other) {
		
	}
	@Override
	public String toString() { return "" + _xyz; }
	@Override
	public boolean equals(Object obj) {
	if (this == obj) return true;
	if (obj == null || getClass() != obj.getClass()) return false;
	return _xyz.equals(((Point) obj)._xyz);
	}
	@Override
	public int hashCode() {
	return _xyz.hashCode());
	}

}
