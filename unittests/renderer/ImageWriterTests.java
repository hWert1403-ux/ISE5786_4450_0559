package renderer;

import org.junit.jupiter.api.Test;

import primitives.Color;

/**
 * Testing ImageWriter class
 */
public class ImageWriterTests {

	/**
	 * Test method for creating a simple grid image.
	 */
	@Test
	public void testImageWriter() {
		// Define constants for the image
		int nX = 800; // width pixels
		int nY = 500; // height pixels
		int step = 50; // grid square size

		Color backgroundColor = new Color(0, 0, 255); // Blue
		Color gridColor = new Color(255, 255, 0); // Yellow

		// Create the ImageWriter object
		ImageWriter imageWriter = new ImageWriter(nX, nY);

		// Loop through all pixels
		for (int i = 0; i < nX; i++) {
			for (int j = 0; j < nY; j++) {
				// Check if the pixel is on the grid line
				if (i % step == 0 || j % step == 0) {
					imageWriter.writePixel(i, j, gridColor);
				} else {
					imageWriter.writePixel(i, j, backgroundColor);
				}
			}
		}

		// Save the image file
		imageWriter.writeToImage("testYellowGrid");
	}
}