package renderer;

import static primitives.Util.isZero;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.stream.IntStream;

import primitives.Color;
import primitives.Point;
import primitives.Point2D;
import primitives.Ray;
import primitives.Vector;
import renderer.PixelManager.Pixel;
import sampling.Blackboard;
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

	/*
	 * pixel grid (regular or jittered)
	 */
	private SamplingGrid _samplingGrid;

	private PixelManager _pixelManager; // pixel manager object

	/**
	 * … -2 auto raw threads, -1 parallel stream, 0 no threads, 1+ raw threads count
	 */
	private int _threadsCount = 0;

	private double _printInterval = 0; // printing progress percentage interval (0 – no printing)

	private static final int SPARE_THREADS = 2; // Spare threads if trying to use all the cores

	/** Toggle switch to enable or disable Adaptive Super-Sampling */
	private boolean _useAdaptiveSuperSampling = false;

	/** Maximum recursion depth for adaptive pixel splitting */
	private int _maxAdaptiveDepth = 4;

	/** Color sensitivity threshold for adaptive sampling */
	private double _colorTolerance = 0.01;

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
	 * Constructs a beam of rays through a specific pixel. Decouples spatial mapping
	 * and geometric offset generation from the camera by leveraging the Blackboard
	 * and SamplingGrid infrastructure components. * Why in this class? According to
	 * the RDD principle, the camera is the only component that holds the geometric
	 * information of the view plane. Therefore, it is solely responsible for
	 * translating pixel indices into a physical 3D center position before
	 * delegating sample placement. * @param xIndex Column index (j) - the
	 * horizontal pixel coordinate
	 * 
	 * @param yIndex Row index (i) - the vertical pixel coordinate
	 * @return List of rays forming the sampling beam through the specified pixel
	 */
	public List<Ray> constructRayBeam(int xIndex, int yIndex) {
		List<Ray> rayBeam = new ArrayList<>();

		// 1. Calculate the center point of the target pixel (Camera's geometric
		// responsibility)
		Point pixelCenter = _vpCenter;
		double deltaX = (xIndex - _nX / 2.0) * _pixelWidth + _pixelWidth / 2.0;
		double deltaY = -((yIndex - _nY / 2.0) * _pixelHeight + _pixelHeight / 2.0);

		if (!isZero(deltaX)) {
			pixelCenter = pixelCenter.add(_vRight.scale(deltaX));
		}
		if (!isZero(deltaY)) {
			pixelCenter = pixelCenter.add(_vUp.scale(deltaY));
		}

		// 2. Compute the physical target dimensions scaled by the custom sample area
		// factor
		double targetWidth = _pixelWidth * _sampleAreaScale;
		double targetHeight = _pixelHeight * _sampleAreaScale;

		// 3. Create a local Blackboard context for this specific pixel (Complete
		// separation of concerns)
		Blackboard blackboard = new Blackboard(pixelCenter, _vUp, _vRight, targetWidth, targetHeight);

		// 4. Fetch normalized 2D offsets from the infrastructure grid (Leverages
		// internal performance cache)
		List<Point2D> gridPoints2D = _samplingGrid.generateGridPoints(_superSamplingRows, _superSamplingCols,
				_samplingShape, _samplingPattern);

		// 5. Transform the normalized 2D offsets into physical 3D scene coordinates via
		// the Blackboard
		List<Point> points3D = blackboard.convert2DTo3D(gridPoints2D);

		// 6. Construct sample rays originating from the camera lens center (p0) towards
		// each 3D point
		for (Point samplePoint : points3D) {
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
	public Camera renderImageNoThreads() {
		for (int i = 0; i < _nX; i++)
			for (int j = 0; j < _nY; j++)
				castRay(i, j);
		return this;
	}

//	/**
//	 * Calculates and sets colors to ALL pixels using multi-threading capability.
//	 * Fully compliant with the course's official PixelManager infrastructure.
//	 * * @return the Camera instance
//	 */
//	public Camera renderImageNoThreads() {
//		// 1. קביעת מספר הנימים לעבודה (למשל 4 נימים, או דינמי לפי ליבות המעבד)
//		int threadsCount = Runtime.getRuntime().availableProcessors();
//
//		// 3. יצירת רשימה לניהול הנימים (Threads)
//		List<Thread> threads = new ArrayList<>();
//
//		// 4. בניית והגדרת הנימים
//		for (int t = 0; t < threadsCount; t++) {
//			threads.add(new Thread(() -> {
//				PixelManager.Pixel pixel;
//
//				// כל נים מושך בצורה בטוחה (Thread-Safe) את הפיקסל הבא שפנוי לעבודה
//				while ((pixel = _pixelManager.nextPixel()) != null) {
//					// קריאה למתודת castRay הקיימת שלך (col הוא xIndex, row הוא yIndex)
//					castRay(pixel.col(), pixel.row());
//				}
//			}));
//		}
//
//		// 5. הפעלת כל הנימים במקביל (Concurrent execution)
//		for (Thread thread : threads) {
//			thread.start();
//		}
//
//		// 6. המתנה (Join) - המצלמה מחכה שכל הנימים יסיימו לחלוטין את עבודתם
//		// לפני שהיא מאפשרת להמשיך הלאה (מונע כתיבת קובץ תמונה חלקי או ריק)
//		try {
//			for (Thread thread : threads) {
//				thread.join();
//			}
//		} catch (InterruptedException e) {
//			// שומר על סטטוס ה-interrupt של הנים הנוכחי במידה ונפסק באמצע
//			Thread.currentThread().interrupt();
//		}
//
//		return this;
//	}

	/**
	 * Routing method based on the official course presentation. Calculates and sets
	 * colors to ALL pixels using multi-threading capability. * @return the Camera
	 * instance
	 */
	public Camera renderImage() {
		_pixelManager = new PixelManager(_nY, _nX, _printInterval);
		return switch (_threadsCount) {
		case 0 -> renderImageNoThreads();
		case -1 -> renderImageStream();
		default -> renderImageRawThreads();
		};
	}

	/**
	 * Renders the image by manually creating and managing Java Threads. Each thread
	 * repeatedly pulls the next available pixel from the pixel manager and casts a
	 * ray through it until no pixels are left. * @return this Camera object
	 */
	private Camera renderImageRawThreads() {
		var threads = new LinkedList<Thread>();
		var count = _threadsCount;
		while (count-- > 0)
			threads.add(new Thread(() -> {
				Pixel pixel;
				while ((pixel = _pixelManager.nextPixel()) != null)
					castRay(pixel.col(), pixel.row());
			}));
		for (var thread : threads)
			thread.start();
		try {
			for (var thread : threads)
				thread.join();
		} catch (InterruptedException _) {
		}
		return this;

	}

	/**
	 * Renders the image using Java's parallel IntStream API.
	 * <p>
	 * This method is synchronous and blocking; it splits the row processing (Y
	 * axis) across the default ForkJoinPool and will not return until all pixels
	 * have finished rendering (acting similarly to starting and joining manual
	 * threads).
	 * </p>
	 * Parallelism is applied only to the outer loop to minimize thread-management
	 * overhead. * @return this Camera object
	 */
	public Camera renderImageStream() {
		IntStream.range(0, _nY).parallel()
				.forEach(yIndex -> IntStream.range(0, _nX).parallel().forEach(xIndex -> castRay(xIndex, yIndex)));
		return this;
	}

	/*
	 * /** calculate and set color to specific pixel
	 * 
	 * @param xIndex - column index
	 * 
	 * @param yIndex - row index
	 */
	/*
	 * private void castRay(int xIndex, int yIndex) { Ray r = constructRay(xIndex,
	 * yIndex); Color c = _rayTracer.traceRay(r); _imageWriter.writePixel(xIndex,
	 * yIndex, c); }
	 */

	/**
	 * Calculates and sets the color for a specific pixel. Automatically switches
	 * between adaptive super-sampling, regular super-sampling, and a single ray.
	 * * @param xIndex - column index (horizontal axis)
	 * 
	 * @param yIndex - row index (vertical axis)
	 */
	private void castRay(int xIndex, int yIndex) {
		Color pixelColor;

		// 1. Check if Adaptive Super-Sampling is enabled
		if (_useAdaptiveSuperSampling) {
			// Calculate the center point of the target pixel
			Point pixelCenter = _vpCenter;
			double deltaX = (xIndex - _nX / 2.0) * _pixelWidth + _pixelWidth / 2.0;
			double deltaY = -((yIndex - _nY / 2.0) * _pixelHeight + _pixelHeight / 2.0);

			if (!isZero(deltaX)) {
				pixelCenter = pixelCenter.add(_vRight.scale(deltaX));
			}
			if (!isZero(deltaY)) {
				pixelCenter = pixelCenter.add(_vUp.scale(deltaY));
			}

			// Calculate the 4 extreme corners of the pixel on the View Plane
			Point pNW = pixelCenter.add(_vRight.scale(-_pixelWidth / 2.0)).add(_vUp.scale(_pixelHeight / 2.0));
			Point pNE = pixelCenter.add(_vRight.scale(_pixelWidth / 2.0)).add(_vUp.scale(_pixelHeight / 2.0));
			Point pSW = pixelCenter.add(_vRight.scale(-_pixelWidth / 2.0)).add(_vUp.scale(-_pixelHeight / 2.0));
			Point pSE = pixelCenter.add(_vRight.scale(_pixelWidth / 2.0)).add(_vUp.scale(-_pixelHeight / 2.0));

			// Trace rays to the 4 corners and find their colors
			Color cNW = _rayTracer.traceRayToPoint(pNW, _p0);
			Color cNE = _rayTracer.traceRayToPoint(pNE, _p0);
			Color cSW = _rayTracer.traceRayToPoint(pSW, _p0);
			Color cSE = _rayTracer.traceRayToPoint(pSE, _p0);

			// Execute the recursive adaptive super-sampling (start at depth 1)
			pixelColor = adaptiveSuperSamplingRec(pNW, pNE, pSW, pSE, cNW, cNE, cSW, cSE, 1);
		}
		// 2. Check if the dynamic regular super-sampling feature is enabled
		else if (_useSuperSampling) {
			// Generate the completely dynamic beam of rays for this pixel using xIndex and
			// yIndex
			List<Ray> rayBeam = constructRayBeam(xIndex, yIndex);

			// Accumulate the color returned from tracing each individual ray in the beam
			Color totalColor = Color.BLACK;
			for (Ray ray : rayBeam) {
				totalColor = totalColor.add(_rayTracer.traceRay(ray));
			}

			// Average the color by dividing the accumulated sum by the total number of rays
			pixelColor = totalColor.reduce(rayBeam.size());
		}
		// 3. Fallback: Construct and trace a single central ray if super-sampling is
		// disabled
		else {
			Ray singleRay = constructRay(xIndex, yIndex);
			pixelColor = _rayTracer.traceRay(singleRay);
		}

		// Write the final calculated color to the image writer
		_imageWriter.writePixel(xIndex, yIndex, pixelColor);

		_pixelManager.pixelDone();
	}

	/**
	 * Recursive function for Adaptive Super-Sampling. Splits the pixel into 4
	 * quadrants if the colors at the corners are too different.
	 */
	private Color adaptiveSuperSamplingRec(Point pNW, Point pNE, Point pSW, Point pSE, Color cNW, Color cNE, Color cSW,
			Color cSE, int depth) {
		// Stop condition 1: Reached max depth OR
		// Stop condition 2: Colors are similar enough
		if (depth == _maxAdaptiveDepth || isSimilar(cNW, cNE, cSW, cSE)) {
			return cNW.add(cNE).add(cSW).add(cSE).reduce(4); // Average of 4 corners
		}

		// Calculate geometric midpoints (center and edges)
		Point pTopMid = pNW.getMidPoint(pNE);
		Point pBottomMid = pSW.getMidPoint(pSE);
		Point pLeftMid = pNW.getMidPoint(pSW);
		Point pRightMid = pNE.getMidPoint(pSE);
		Point pCenter = pNW.getMidPoint(pSE);

		// Trace rays to the new 5 points and find their colors
		Color cTopMid = _rayTracer.traceRayToPoint(pTopMid, _p0);
		Color cBottomMid = _rayTracer.traceRayToPoint(pBottomMid, _p0);
		Color cLeftMid = _rayTracer.traceRayToPoint(pLeftMid, _p0);
		Color cRightMid = _rayTracer.traceRayToPoint(pRightMid, _p0);
		Color cCenter = _rayTracer.traceRayToPoint(pCenter, _p0);

		// Recursive calls for each of the 4 sub-quadrants (reuses existing colors to
		// save performance)
		Color cNW_quad = adaptiveSuperSamplingRec(pNW, pTopMid, pLeftMid, pCenter, cNW, cTopMid, cLeftMid, cCenter,
				depth + 1);
		Color cNE_quad = adaptiveSuperSamplingRec(pTopMid, pNE, pCenter, pRightMid, cTopMid, cNE, cCenter, cRightMid,
				depth + 1);
		Color cSW_quad = adaptiveSuperSamplingRec(pLeftMid, pCenter, pSW, pBottomMid, cLeftMid, cCenter, cSW,
				cBottomMid, depth + 1);
		Color cSE_quad = adaptiveSuperSamplingRec(pCenter, pRightMid, pBottomMid, pSE, cCenter, cRightMid, cBottomMid,
				cSE, depth + 1);

		// Merge and return the average of the 4 quadrants
		return cNW_quad.add(cNE_quad).add(cSW_quad).add(cSE_quad).reduce(4);
	}

	/**
	 * compare color similarity c1 to 3 other colors
	 * 
	 * @return true if colors are similar using tolerance variable
	 */
	private boolean isSimilar(Color c1, Color c2, Color c3, Color c4) {
		return c1.isSimilar(c2, c3, c4, _colorTolerance);
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
		public Builder setRayTracer(Scene scene, RayTracerType type) {
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
			this._camera._samplingGrid = new SamplingGrid();
			return this;
		}

		/*
		 * Sets the number of threads for multithreading. * @param threads the number of
		 * threads to use: <ul> <li>-2: automatically calculates threads based on
		 * available CPU cores</li> <li>-1: no multithreading (single thread)</li> <li>0
		 * or higher: uses the exact number of specified threads</li> </ul>
		 * 
		 * @return this Builder object
		 * 
		 * @throws IllegalArgumentException if threads count is less than -2
		 */
		public Builder setMultithreading(int threads) {
			if (threads < -2)
				throw new IllegalArgumentException("Multithreading must be -2 or higher");
			if (threads >= -1)
				_camera._threadsCount = threads;
			else { // == -2
				int cores = Runtime.getRuntime().availableProcessors() - SPARE_THREADS;
				_camera._threadsCount = cores <= 2 ? 1 : cores;
			}
			return this;
		}

		/**
		 * Sets the time interval for printing debug information. * @param interval the
		 * time between debug prints (must be 0 or higher)
		 * 
		 * @return this Builder object
		 * @throws IllegalArgumentException if the interval value is negative
		 */
		public Builder setDebugPrint(double interval) {
			if (interval < 0)
				throw new IllegalArgumentException("Interval value must be non-negative");
			_camera._printInterval = interval;
			return this;
		}

		/**
		 * Configures Adaptive Super-Sampling parameters. * @param enable true to turn
		 * on adaptive sampling
		 * 
		 * @param maxDepth  maximum recursion depth (e.g., 3 or 4)
		 * @param tolerance color sensitivity threshold (e.g., 0.1 or 0.05)
		 * @return the Builder instance
		 */
		public Builder setAdaptiveSuperSampling(boolean enable, int maxDepth, double tolerance) {
			if (maxDepth < 0 || tolerance < 0) {
				throw new IllegalArgumentException("Depth and tolerance must be non-negative values.");
			}
			this._camera._useAdaptiveSuperSampling = enable;
			this._camera._maxAdaptiveDepth = maxDepth;
			this._camera._colorTolerance = tolerance;
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
