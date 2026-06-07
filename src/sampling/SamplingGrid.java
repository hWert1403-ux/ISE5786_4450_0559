package sampling;

import java.util.ArrayList;
import java.util.List;

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

	/**
	 * Generates a regular Grid of 2D points normalized between -0.5 and 0.5.
	 * * @param nRows Number of rows (samples along the Y axis)
	 * 
	 * @param nCols Number of columns (samples along the X axis)
	 * @return List of 2D coordinates for the samples
	 */
	public List<Point2D> generateGridPoints(int nRows, int nCols) {
		List<Point2D> points = new ArrayList<>();

		// Step size for each sub-pixel section
		double stepX = 1.0 / nCols;
		double stepY = 1.0 / nRows;

		// Traverse through each sub-pixel section to find its center
		for (int i = 0; i < nRows; i++) {
			for (int j = 0; j < nCols; j++) {
				// Calculate center offset of the sub-pixel relative to the target area center
				double x = (j + 0.5) * stepX - 0.5;
				double y = (i + 0.5) * stepY - 0.5;

				points.add(new Point2D(x, y));
			}
		}
		return points;
	}
}