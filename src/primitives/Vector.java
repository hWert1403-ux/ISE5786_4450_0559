package primitives;

/**
 * Class Vector represents a vector in 3D space, defined by its direction and length.
 * The class inherits from Point and represents the vector from the origin (0,0,0) 
 * to the point (x,y,z).
 * * <p>Note: A vector cannot be the zero vector (0,0,0).</p>
 * * @author hadas&shani
 */
public final class Vector extends Point {
	
	/**
     * Constructor to initialize a Vector with three double coordinates.
     * * @param x coordinate on the X axis
     * @param y coordinate on the Y axis
     * @param z coordinate on the Z axis
     * @throws IllegalArgumentException if the resulting vector is the zero vector
     */
	public Vector(double x, double y, double z){
		super(x, y, z);
		if (this._xyz.equals(Double3.ZERO)) {
            throw new IllegalArgumentException("Vector(0,0,0) is not allowed");
        }
	}
	
	/**
     * Constructor to initialize a Vector with a Double3 object.
     * * @param xyz Triplet of coordinate values
     * @throws IllegalArgumentException if the resulting vector is the zero vector
     */
	public Vector(Double3 xyz){
		super(xyz);
		if (this._xyz.equals(Double3.ZERO)) {
            throw new IllegalArgumentException("Vector(0,0,0) is not allowed");
        }
	}

	/**
     * Adds another vector to this vector using vector addition.
     * * @param vector the vector to add
     * @return a new Vector resulting from the addition
     */
    public Vector add(Vector vector) {
        return new Vector(_xyz.add(vector._xyz));
    }

    /**
     * Multiplies the vector by a scalar.
     * * @param scalar- the scalar value
     * @return a new Vector resulting from the scaling
     */
    public Vector scale(double scalar) {
        return new Vector(_xyz.scale(scalar));
    }

    /**
     * Calculates the dot product (scalar product) between this vector and another.
     * * @param other
     * @return the scalar result of the dot product
     */
    public double dotProduct(Vector other) {
        return _xyz._d1() * other._xyz._d1() + 
               _xyz._d2() * other._xyz._d2() + 
               _xyz._d3() * other._xyz._d3();
    }

    /**
     * Calculates the cross product (vector product) between this vector and another.
     * * @param other
     * @return a new Vector orthogonal to both input vectors
     * @throws IllegalArgumentException if the vectors are parallel (cross product is zero)
     */
    public Vector crossProduct(Vector other) {
        double ax = _xyz._d1(), ay = _xyz._d2(), az = _xyz._d3();
        double bx = other._xyz._d1(), by = other._xyz._d2(), bz = other._xyz._d3();

        return new Vector(
            ay * bz - az * by,
            az * bx - ax * bz,
            ax * by - ay * bx
        );
    }

    /**
     * Calculates the squared length of the vector.
     * * @return the squared length
     */
    public double lengthSquared() {
        return _xyz._d1() * _xyz._d1() + _xyz._d2() * _xyz._d2() + _xyz._d3() * _xyz._d3();
    }

    /**
     * Calculates the actual length of the vector.
     * * @return the length of the vector
     */
    public double length() {
        return Math.sqrt(lengthSquared());
    }

    /**
     * Normalizes the vector so its length will be 1.
     * * @return a new normalized Vector in the same direction
     */
    public Vector normalize() {
        double len = length();
        return new Vector(_xyz.scale(1 / len));
    }

    @Override
    public String toString() { return "->" + super.toString(); }
    
    @Override
    public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass())
    	return false;
    return super.equals(obj);
    }
    
    // No need to override hashCode() – it’s inherited from Point
}


