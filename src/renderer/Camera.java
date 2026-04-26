package renderer;

import static primitives.Util.isZero;

import java.util.MissingResourceException;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

/**
 * Camera class represents a physical camera in a 3D scene. It manages the
 * camera's location, orientation, and view plane properties. The class
 * implements Cloneable to support the Builder pattern approach.
 */
public class Camera implements Cloneable {

	/** Camera's location point in the 3D space */
	private Point _p0;

	/** Camera's direction vectors: to (forward), up, and right */
	private Vector _vTo, _vUp, _vRight;

	/** View Plane dimensions: width, height, and the distance from the camera */
	private double _width, _height, _distance;

	/** Resolution of the View Plane: number of pixels in X and Y directions */
	private int _nX = 1, _nY = 1;

	/** Computed center point of the View Plane */
	private Point _vpCenter;

	/** Computed dimensions of a single pixel */
	private double _pixelWidth, _pixelHeight;

	/**
	 * Private default constructor for the Camera class. Used exclusively by the
	 * Builder to create a new Camera instance.
	 */
	private Camera() {
	}

	/**
	 * Static method to get a new instance of the nested Builder class.
	 * 
	 * @return A new Builder object
	 */
	public static Builder getBuilder() {
		return new Builder();
	}

	/**
	 * Constructs a ray passing through the center of a specific pixel (xIndex,
	 * yIndex) on the View Plane based on the formulas from the course slides.
	 * * @param xIndex Column index (j) - horizontal pixel index
	 * 
	 * @param yIndex Row index (i) - vertical pixel index
	 * @return A Ray starting from the camera location and passing through the
	 *         center of the pixel
	 */
	public Ray constructRay(int xIndex, int yIndex) {
		// Pc (View Plane Center) is already calculated in build() as _vpCenter
		Point pIJ = _vpCenter;

		// Calculate horizontal offset (x-axis):
		// (x - #pixelsX / 2) * Rx + Rx / 2
		double deltaX = (xIndex - _nX / 2.0) * _pixelWidth + _pixelWidth / 2.0;

		// Calculate vertical offset (y-axis):
		// -((y - #pixelsY / 2) * Ry + Ry / 2)
		// The minus is because 'y' index grows top-down, opposite to vUp
		double deltaY = -((yIndex - _nY / 2.0) * _pixelHeight + _pixelHeight / 2.0);

		// Add horizontal offset to the center point using vRight
		if (!isZero(deltaX)) {
			pIJ = pIJ.add(_vRight.scale(deltaX));
		}

		// Add vertical offset to the center point using vUp
		if (!isZero(deltaY)) {
			pIJ = pIJ.add(_vUp.scale(deltaY));
		}

		// The ray direction is the vector from the camera (P0) to the calculated point
		// Pij
		Vector direction = pIJ.subtract(_p0);

		// Return the ray (the Ray constructor or its internal logic should ensure
		// normalization)
		return new Ray(_p0, direction);
	}

	/**
	 * Inner static class to implement the Builder design pattern for the Camera
	 * class. This class allows for step-by-step configuration and validation of the
	 * Camera object.
	 */
	public static class Builder {
		/** The camera instance being constructed. */
		private final Camera _camera = new Camera();

		/** General "up" vector for orientation, defaults to Y axis. */
		private Vector vUp = Vector.AXIS_Y;

		/** Temporary storage for the camera's direction vector. */
		private Vector vTo = null;

		/** Temporary storage for the point the camera is looking at. */
		private Point target = null;

		/**
		 * Sets the camera's location point.
		 * 
		 * @param location The position of the camera in 3D space
		 * @return The Builder instance
		 */
		public Builder setLocation(Point location) {
			_camera._p0 = location;
			return this;
		}

		/**
		 * Sets the camera's direction using direction and up vectors.
		 * 
		 * @param to Direction vector
		 * @param up General up vector
		 * @return The Builder instance for method chaining
		 */
		public Builder setDirection(Vector to, Vector up) {
			this.vTo = to;
			this.vUp = up;
			this.target = null; // We have a vector, so clear target
			return this;
		}

		/**
		 * Sets the camera's direction towards a target point with a specific up vector.
		 * 
		 * @param target Point to look at
		 * @param up     General up vector
		 * @return The Builder instance for method chaining
		 */
		public Builder setDirection(Point target, Vector up) {
			this.target = target;
			this.vUp = up;
			this.vTo = null; // Clear vTo, will be calculated from target in build()
			return this;
		}

		/**
		 * Sets the camera's direction towards a target point using the current up
		 * vector.
		 * 
		 * @param target Point to look at
		 * @return The Builder instance for method chaining
		 */
		public Builder setDirection(Point target) {
			this.target = target;
			this.vTo = null;
			return this;
		}

		/**
		 * Sets the View Plane dimensions.
		 * 
		 * @param width  Horizontal size of the View Plane
		 * @param height Vertical size of the View Plane
		 * @return The Builder instance for method chaining
		 */
		public Builder setVpSize(double width, double height) {
			_camera._width = width;
			_camera._height = height;
			return this;
		}

		/**
		 * Sets the distance between the camera and the View Plane.
		 * 
		 * @param distance Distance value
		 * @return The Builder instance for method chaining
		 */
		public Builder setVpDistance(double distance) {
			_camera._distance = distance;
			return this;
		}

		/**
		 * Sets the View Plane resolution.
		 * 
		 * @param nX Number of pixels horizontally
		 * @param nY Number of pixels vertically
		 * @return The Builder instance for method chaining
		 */
		public Builder setResolution(int nX, int nY) {
			_camera._nX = nX;
			_camera._nY = nY;
			return this;
		}

		/**
		 * Validates the configuration and constructs the final Camera object.
		 * 
		 * @return A cloned instance of the constructed Camera
		 * @throws IllegalArgumentException if resolution or view plane data is invalid
		 * @throws MissingResourceException if location or direction data is missing
		 */
		public Camera build() {
			checkResolution();
			checkLocationAndDirection();
			checkViewPlane();
			try {
				return (Camera) _camera.clone();
			} catch (CloneNotSupportedException _) {
				return null;
			}
		}

		/**
		 * Validates and calculates View Plane center and pixel size. Ensures dimensions
		 * are positive.
		 */
		private void checkViewPlane() {
			// Validation
			if (_camera._distance <= 0 || isZero(_camera._distance))
				throw new IllegalArgumentException("Distance cannot be zero");
			if (_camera._width <= 0 || _camera._height <= 0)
				throw new IllegalArgumentException("View plane dimensions must be positive");

			// Calculate View Plane Center point: Pc = P0 + d * vTo
			_camera._vpCenter = _camera._p0.add(_camera._vTo.scale(_camera._distance));

			// Calculate pixel dimensions: Ratio of total size to resolution
			_camera._pixelWidth = _camera._width / _camera._nX;
			_camera._pixelHeight = _camera._height / _camera._nY;
		}

		/**
		 * Validates and calculates camera orientation vectors (vTo, vUp, vRight).
		 * Normalizes vectors and ensures they are orthogonal.
		 */
		private void checkLocationAndDirection() {
			// Check location
			if (_camera._p0 == null)
				throw new MissingResourceException("Camera location (p0) is missing", "Camera", "p0");

			// Calculate vTo from target if needed
			if (vTo == null) {
				if (target == null)
					throw new MissingResourceException("Camera direction (vTo or target) is missing", "Camera", "vTo");
				vTo = target.subtract(_camera._p0);
			}

			// Normalize vTo and vUp
			_camera._vTo = vTo.normalize();
			_camera._vUp = vUp.normalize();

			// Check if vectors are parallel (cross product would be zero vector)
			try {
				_camera._vRight = _camera._vTo.crossProduct(_camera._vUp).normalize();
				// Recalculate vUp to ensure it's exactly 90 degrees to vTo and vRight
				_camera._vUp = _camera._vRight.crossProduct(_camera._vTo).normalize();
			} catch (IllegalArgumentException e) {
				throw new IllegalArgumentException("vTo and vUp cannot be parallel");
			}
		}

		/**
		 * Validates that the resolution values (nX, nY) are positive.
		 */
		private void checkResolution() {
			if (_camera._nX <= 0 || _camera._nY <= 0) {
				throw new IllegalArgumentException("Resolution (nX, nY) must be greater than 0");
			}
		}
	}
}
