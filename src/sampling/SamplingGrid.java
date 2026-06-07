package sampling;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import primitives.Point2D;

/**
 * Infrastructure (Tashtit) class responsible for generating 2D sample points.
 * Implements the DRY principle so all features can reuse this grid.
 */
/**
 * Why this class? According to RDD principle, the camera/light source has no
 * reason to know how to generate a grid of points or how randomness works.
 * SamplingGrid will centralize this responsibility and return an abstract list
 * of 2D points.
 */
public class SamplingGrid {

	/** area shapes options inside pixel super sampling */
	public enum AreaShape {
		SQUARE, CIRCLE
	}

	/** pattern options inside pixel super sampling */
	public enum SamplingPattern {
		REGULAR, JITTERED
	}

	private final Random random = new Random();

	/**
	 * Overloaded method for backward compatibility with existing tests. Generates a
	 * regular square grid of points by default. * @param nRows Number of rows
	 * 
	 * @param nCols Number of columns
	 * @return List of normalized 2D coordinates
	 */
	public List<Point2D> generateGridPoints(int nRows, int nCols) {
		return generateGridPoints(nRows, nCols, AreaShape.SQUARE, SamplingPattern.REGULAR);
	}

	/**
	 * Generates normalized 2D points inside a target area based on shape and
	 * pattern.
	 * 
	 * @param nRows   Number of rows (sub-divisions)
	 * @param nCols   Number of columns (sub-divisions)
	 * @param shape   The geometric boundary (SQUARE or CIRCLE)
	 * @param pattern The distribution strategy (REGULAR grid centers or JITTERED
	 *                random offsets)
	 * @return List of normalized 2D coordinates
	 */
	public List<Point2D> generateGridPoints(int nRows, int nCols, AreaShape shape, SamplingPattern pattern) {
		List<Point2D> points = new ArrayList<>();

		double stepX = 1.0 / nCols;
		double stepY = 1.0 / nRows;

		for (int i = 0; i < nRows; i++) {
			for (int j = 0; j < nCols; j++) {
				double x, y;

				if (pattern == SamplingPattern.JITTERED) {
					// Jittered: Random point inside the sub-pixel box boundaries
					x = (j + random.nextDouble()) * stepX - 0.5;
					y = (i + random.nextDouble()) * stepY - 0.5;
				} else {
					// Regular: Exactly at the center of the sub-pixel box
					x = (j + 0.5) * stepX - 0.5;
					y = (i + 0.5) * stepY - 0.5;
				}

				// Shape constraint filtering
				if (shape == AreaShape.CIRCLE) {
					// Inside a circle of radius 0.5 centered at (0,0)
					if (x * x + y * y <= 0.25) {
						points.add(new Point2D(x, y));
					}
				} else {
					// Square: All calculated points are valid within the [-0.5, 0.5] range
					points.add(new Point2D(x, y));
				}
			}
		}

		// Safety check: ensure at least one fallback point exists if circle filtering
		// dropped everything
		if (points.isEmpty()) {
			points.add(new Point2D(0, 0));
		}

		return points;
	}
}