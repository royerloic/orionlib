package utils.graphics.plot.test;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

import utils.graphics.plot.DrawHistogram;
import utils.graphics.util.ViewImage;
import utils.math.statistics.Histogram;

public class DrawHistogramTest
{

	// @Test
	public void HistogramTest() throws IOException, InterruptedException
	{
		Histogram lHistogram = new Histogram(20);

		Random rnd = new Random();

		for (int i = 0; i < 100000; i++)
		{
			lHistogram.enter(rnd.nextGaussian());
		}

		double[] lHistogramArray = lHistogram.getStatistic();

		final int width = 400;
		final int height = 400;

		BufferedImage lBufferedImage = new BufferedImage(	width,
																											height,
																											BufferedImage.TYPE_INT_RGB);

		DrawHistogram.draw(	lHistogramArray,
												(Graphics2D) lBufferedImage.getGraphics(),
												100,
												100,
												100,
												100);

		ViewImage.view(lBufferedImage);

		Thread.sleep(10000);

	}
}
