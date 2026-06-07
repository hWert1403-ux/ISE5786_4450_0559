package renderer;

import static primitives.Util.isZero;

import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;

import primitives.Color;
import primitives.Point;
import primitives.Point2D;
import primitives.Ray;
import primitives.Vector;
import sampling.SamplingGrid;
import scene.Scene;

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

	/** to write picture file */
	ImageWriter _imageWriter;

	/** to calculate color */
	RayTracerBase _rayTracer;

	/**
	 * Toggle switch to enable or disable the Anti-Aliasing Super-Sampling feature.
	 */
	private boolean _useSuperSampling = false;

	/**
	 * The number of horizontal sub-divisions (rows) inside a single pixel's
	 * sampling grid.
	 */
	private int _superSamplingRows = 1;

	/**
	 * The number of vertical sub-divisions (columns) inside a single pixel's
	 * sampling grid.
	 */
	private int _superSamplingCols = 1;

	/**
	 * Scale factor determining the size of the target sampling area relative to the
	 * pixel's boundaries.
	 * 
	 * A value of 1.0 covers 100% of the pixel. Adjusting this scale allows
	 * narrowing or widening the ray beam, providing flexibility for other
	 * super-sampling extensions (e.g., depth of field or soft shadows).
	 */
	private double _sampleAreaScale = 1.0;

	/**
	 * Defines the geometric boundary shape of the sampling zone (SQUARE or CIRCLE).
	 */
	private SamplingGrid.AreaShape _samplingShape = SamplingGrid.AreaShape.SQUARE;

	/**
	 * Specifies the spatial distribution strategy of the sample points (REGULAR
	 * grid vs JITTERED random). REGULAR aligns points perfectly at sub-pixel
	 * centers, while JITTERED introduces stochastic random.
	 */
	private SamplingGrid.SamplingPattern _samplingPattern = SamplingGrid.SamplingPattern.REGULAR;

	/** Shared instance to reuse the grid and avoid stack-overflow */
	private final SamplingGrid _samplingGrid = new SamplingGrid();

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
	 * Why in this class: According to RDD principle, the camera is the only
	 * component in the system that holds the geometric information of the image
	 * plane. Therefore, it is the one that must translate the theoretical 2D
	 * displacements into real positions in 3D space.
	 */
	/**
	 * Constructs a beam of rays through a specific pixel based on fully dynamic
	 * configuration. * @param xIndex Column index (j) - horizontal pixel index
	 * 
	 * @param yIndex Row index (i) - vertical pixel index
	 * @return List of rays forming the sampling beam through the pixel
	 */
	public List<Ray> constructRayBeam(int xIndex, int yIndex) {
		List<Ray> rayBeam = new ArrayList<>();

		// 1. Find the center point of the current pixel (Reusing your exact logic)
		Point pixelCenter = _vpCenter;
		double deltaX = (xIndex - _nX / 2.0) * _pixelWidth + _pixelWidth / 2.0;
		double deltaY = -((yIndex - _nY / 2.0) * _pixelHeight + _pixelHeight / 2.0);

		if (!isZero(deltaX)) {
			pixelCenter = pixelCenter.add(_vRight.scale(deltaX));
		}
		if (!isZero(deltaY)) {
			pixelCenter = pixelCenter.add(_vUp.scale(deltaY));
		}

		// 2. Fetch the 2D configuration-driven points from our reusable grid instance
		List<Point2D> gridPoints = _samplingGrid.generateGridPoints(_superSamplingRows, _superSamplingCols,
				_samplingShape, _samplingPattern);

		// 3. Transform each 2D offset into a 3D ray scaled by the configured size
		// factor
		for (primitives.Point2D wp : gridPoints) {
			Point samplePoint = pixelCenter;

			// Scale the pixel dimensions by the custom sampleAreaScale setting
			double currentWidth = _pixelWidth * _sampleAreaScale;
			double currentHeight = _pixelHeight * _sampleAreaScale;

			// Move along _vRight vector
			if (!isZero(wp.x)) {
				samplePoint = samplePoint.add(_vRight.scale(wp.x * currentWidth));
			}

			// Move along _vUp vector
			if (!isZero(wp.y)) {
				samplePoint = samplePoint.add(_vUp.scale(wp.y * currentHeight));
			}

			// Create the final sample ray
			Vector direction = samplePoint.subtract(_p0);
			rayBeam.add(new Ray(_p0, direction));
		}

		return rayBeam;
	}

	/**
	 * Checks whether super-sampling is enabled for this camera. * @return true if
	 * enabled, false otherwise
	 */
	public boolean isSuperSamplingEnabled() {
		return _useSuperSampling;
	}

	/**
	 * calculate and set colors to ALL pixel
	 * 
	 * @return the Camera instance
	 */
	public Camera renderImage() {
		for (int i = 0; i < _nX; i++)
			for (int j = 0; j < _nY; j++)
				castRay(i, j);
		return this;
	}

	/**
	 * calculate and set color to specific pixel
	 * 
	 * @param xIndex - column index
	 * @param yIndex - row index
	 */
	private void castRay(int xIndex, int yIndex) {
		Ray r = constructRay(xIndex, yIndex);
		Color c = _rayTracer.traceRay(r);
		_imageWriter.writePixel(xIndex, yIndex, c);
	}

	/**
	 * Prints a grid on top of the image without creating new rays
	 * 
	 * @param interval - the size of each square in the grid
	 * @param color    - the color of the grid lines
	 * @return the Camera instance
	 */
	public Camera printGrid(int interval, Color color) {
		for (int i = 0; i < _nX; i++)
			for (int j = 0; j < _nY; j++)
				if (i % interval == 0 || j % interval == 0)
					_imageWriter.writePixel(i, j, color);
		return this;
	}

	public Camera writeToImage(String fileName) {
		_imageWriter.writeToImage(fileName);
		return this;
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
		 * choose ray-tracer type
		 * 
		 * @param scene
		 * @param type
		 * @return
		 */
		Builder setRayTracer(Scene scene, RayTracerType type) {
			if (type == RayTracerType.SIMPLE)
				this._camera._rayTracer = new SimpleRayTracer(scene);
			else
				throw new IllegalArgumentException("Unsupported Ray Tracer Type: " + type);
			return this;
		}

		/**
		 * Configures comprehensive super-sampling parameters for the camera. * @param
		 * enable true to turn on anti-aliasing
		 * 
		 * @param rows    number of sampling rows
		 * @param cols    number of sampling columns
		 * @param scale   scale factor of the area size (typically 1.0)
		 * @param shape   SQUARE or CIRCLE geometry filter
		 * @param pattern REGULAR grid alignment or JITTERED random scattering
		 * @return the Builder instance
		 */
		public Builder setSuperSampling(boolean enable, int rows, int cols, double scale, SamplingGrid.AreaShape shape,
				SamplingGrid.SamplingPattern pattern) {
			if (rows <= 0 || cols <= 0 || scale <= 0) {
				throw new IllegalArgumentException("Rows, columns and scale factor must be positive values.");
			}
			this._camera._useSuperSampling = enable;
			this._camera._superSamplingRows = rows;
			this._camera._superSamplingCols = cols;
			this._camera._sampleAreaScale = scale;
			this._camera._samplingShape = shape;
			this._camera._samplingPattern = pattern;
			return this;
		}

		/**
		 * Validates the configuration and constructs the final Camera object. * This
		 * method ensures all necessary components (Resolution, Location, Direction,
		 * View Plane) are properly initialized. It also ensures that a RayTracer is
		 * initialized, providing a default one if none was set.
		 * 
		 * @return A cloned instance of the constructed Camera
		 * @throws IllegalArgumentException if resolution or view plane data is invalid
		 * @throws MissingResourceException if location or direction data is missing
		 */
		public Camera build() {
			checkResolution();
			checkLocationAndDirection();
			checkViewPlane();

			if (_camera._rayTracer == null) {
				setRayTracer(new Scene("test"), RayTracerType.SIMPLE);
			}

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
				// _camera._vUp = _camera._vRight.crossProduct(_camera._vTo).normalize();
			} catch (IllegalArgumentException e) {
				throw new IllegalArgumentException("vTo and vUp cannot be parallel");
			}
		}

		/**
		 * Validates that the resolution values (nX, nY) are positive. create
		 * _imageWriter param with resolution
		 */
		private void checkResolution() {
			if (_camera._nX <= 0 || _camera._nY <= 0) {
				throw new IllegalArgumentException("Resolution (nX, nY) must be greater than 0");
			}
			_camera._imageWriter = new ImageWriter(_camera._nX, _camera._nY);
		}

		// ++++++++++++++++BONUS ROTATE++++++++++++++
		/**
		 * Rotates the camera around its view direction (vTo). * @param angle Rotation
		 * angle in degrees, clockwise.
		 * 
		 * @return The Builder instance
		 */
		public Builder rotate(double angle) {
			// 1. Ensure the direction vector exists
			if (vTo == null) {
				if (target == null)
					throw new MissingResourceException("Missing direction", "Camera", "vTo");
				vTo = target.subtract(_camera._p0);
			}

			// 2. Normalize and fix vectors to prevent accumulation of errors
			Vector to = vTo.normalize();
			Vector up = vUp.normalize();
			Vector right;
			try {
				right = to.crossProduct(up).normalize();
				up = right.crossProduct(to).normalize(); // Ensure vUp is 100% orthogonal to vTo
			} catch (IllegalArgumentException e) {
				throw new IllegalArgumentException("Cannot rotate: vTo and vUp are parallel");
			}

			// 3. Convert to radians (using positive angle to match test expectations)
			double radians = Math.toRadians(angle);
			double cosTheta = Math.cos(radians);
			double sinTheta = Math.sin(radians);

			// 4. "Clean" values close to zero for precise results at right angles (90, 180,
			// etc.)
			if (isZero(cosTheta))
				cosTheta = 0;
			if (isZero(sinTheta))
				sinTheta = 0;

			// 5. Calculate the new vector using Rodrigues' rotation formula:
			// vUp_new = vUp * cos(theta) + (vTo x vUp) * sin(theta)
			Vector term1 = (cosTheta == 0) ? null : up.scale(cosTheta);
			Vector term2 = (sinTheta == 0) ? null : right.scale(sinTheta);

			if (term1 == null && term2 == null)
				return this;
			if (term1 == null)
				this.vUp = term2.normalize();
			else if (term2 == null)
				this.vUp = term1.normalize();
			else
				this.vUp = term1.add(term2).normalize();

			return this;
		}
	}
}
