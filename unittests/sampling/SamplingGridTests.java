package sampling;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import primitives.Point2D;

/**
 * Unit tests for SamplingGrid class to ensure correct 2D offset generation.
 */
public class SamplingGridTests {

	/**
	 * Test method for {@link SamplingGrid#generateGridPoints(int, int)}.
	 */
	@Test
	public void testGenerateGridPoints2x2() {
		SamplingGrid grid = new SamplingGrid();

		// Act: generate a 2x2 grid (total 4 samples)
		List<Point2D> points = grid.generateGridPoints(2, 2);

		// Assert 1: Check if the total number of points generated is exactly 4
		assertEquals(4, points.size(), "A 2x2 grid should generate exactly 4 points");

		// Assert 2: Verify the coordinates of the sub-pixel centers
		// Expected coordinates for 2x2 grid normalized between -0.5 and 0.5 are:
		// (-0.25, -0.25), (0.25, -0.25), (-0.25, 0.25), (0.25, 0.25)
		for (Point2D p : points) {
			assertTrue(Math.abs(p.x) == 0.25, "X coordinate should be either -0.25 or 0.25");
			assertTrue(Math.abs(p.y) == 0.25, "Y coordinate should be either -0.25 or 0.25");
		}
	}

}
