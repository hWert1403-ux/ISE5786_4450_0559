package sampling;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import primitives.Point2D;

/**
 * Infrastructure class responsible for generating 2D sample points. Implements
 * the DRY principle so all features can reuse this grid. * Why this class?
 * According to the RDD principle, the camera/light source has no reason to know
 * how to generate a grid of points or how randomness works. SamplingGrid
 * centralizes this responsibility and returns a list of 2D points.
 */
public class SamplingGrid {

	/** Area shape options inside pixel super-sampling */
	public enum AreaShape {
		SQUARE, CIRCLE
	}

	/** Pattern options inside pixel super-sampling */
	public enum SamplingPattern {
		REGULAR, JITTERED
	}

	private final Random random = new Random();

	// Cache mechanism to prevent redundant object creation in REGULAR pattern mode
	private List<Point2D> cachedRegularPoints = null;
	private int cachedRows = 0;
	private int cachedCols = 0;
	private AreaShape cachedShape = null;

	/**
	 * Generates a regular square grid of points by default. * @param nRows Number
	 * of rows
	 * 
	 * @param nCols Number of columns
	 * @return List of normalized 2D coordinates
	 */
	public List<Point2D> generateGridPoints(int nRows, int nCols) {
		return generateGridPoints(nRows, nCols, AreaShape.SQUARE, SamplingPattern.REGULAR);
	}

	/**
	 * Generates normalized 2D points inside a target area based on shape and
	 * pattern. * @param nRows Number of rows (sub-divisions)
	 * 
	 * @param nCols   Number of columns (sub-divisions)
	 * @param shape   The geometric boundary (SQUARE or CIRCLE)
	 * @param pattern The distribution strategy (REGULAR grid centers or JITTERED
	 *                random offsets)
	 * @return List of normalized 2D coordinates
	 */
	public List<Point2D> generateGridPoints(int nRows, int nCols, AreaShape shape, SamplingPattern pattern) {
		// 1. Cache Check: If the pattern is REGULAR and already calculated for these
		// parameters, return the cached list immediately.
		if (pattern == SamplingPattern.REGULAR && cachedRegularPoints != null && cachedRows == nRows
				&& cachedCols == nCols && cachedShape == shape) {
			return cachedRegularPoints;
		}

		// 2. Adjust rows and columns for CIRCLE according to presentation guidelines
		// (1.273 area factor)
		int workingRows = nRows;
		int workingCols = nCols;

		if (shape == AreaShape.CIRCLE) {
			// Increase the overall grid density to compensate for corner points that will
			// be rejected.
			// Use sqrt of the factor to distribute the padding equally between rows and
			// columns.
			double scaleFactor = Math.sqrt(1.273);
			workingRows = (int) Math.ceil(nRows * scaleFactor);
			workingCols = (int) Math.ceil(nCols * scaleFactor);
		}

		List<Point2D> points = new ArrayList<>(workingRows * workingCols);

		double stepX = 1.0 / workingCols;
		double stepY = 1.0 / workingRows;

		for (int i = 0; i < workingRows; i++) {
			for (int j = 0; j < workingCols; j++) {
				double x, y;

				if (pattern == SamplingPattern.JITTERED) {
					// Jittered: Random point inside the current sub-pixel boundary of the expanded
					// grid
					x = (j + random.nextDouble()) * stepX - 0.5;
					y = (i + random.nextDouble()) * stepY - 0.5;
				} else {
					// Regular: Exactly at the center of the sub-pixel box of the expanded grid
					x = (j + 0.5) * stepX - 0.5;
					y = (i + 0.5) * stepY - 0.5;
				}

				// 3. Apply Rejection Sampling logic from the course presentation:
				// For a circle of radius 0.5, verify if the squared distance is <= 0.25
				if (shape == AreaShape.CIRCLE) {
					if (x * x + y * y <= 0.25) {
						points.add(new Point2D(x, y));
					}
				} else {
					// SQUARE: All calculated points are valid within the [-0.5, 0.5] range
					points.add(new Point2D(x, y));
				}
			}
		}

		// Safety fallback: ensure at least one center point exists if a tiny grid
		// rejected all points
		if (points.isEmpty()) {
			points.add(new Point2D(0, 0));
		}

		// 4. Store in cache if the pattern is fixed (REGULAR)
		if (pattern == SamplingPattern.REGULAR) {
			cachedRegularPoints = points;
			cachedRows = nRows;
			cachedCols = nCols;
			cachedShape = shape;
		}

		return points;
	}

	/**
	 * Invalidates the cached points. Essential if test cases or external
	 * configurations change parameters dynamically during runtime.
	 */
	public void invalidateCache() {
		this.cachedRegularPoints = null;
	}
}