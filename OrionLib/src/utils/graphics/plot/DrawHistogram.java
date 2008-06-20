package utils.graphics.plot;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import utils.math.statistics.Histogram;

public class DrawHistogram 
{
	public static void draw(	double[] pHistogramArray,
										Graphics2D pGraphics,
										int pWidth,
										int pHeight)
	{
		final double[] hist = pHistogramArray;

		double lWidth = 1 / ((double) hist.length);

		pGraphics.setBackground(new Color(1f,1f,1f,0.5f));
		pGraphics.clearRect(0, 0, pWidth, pHeight);
		
		for (int i = 0; i < hist.length; i++)
		{

			final double topleftX = lWidth * i;
			final double topleftY = hist[i];

			final float intensity = 1-(float) (0.5 + 0.5 * hist[i]);
			pGraphics.setColor(new Color(intensity, intensity, intensity));

			pGraphics.fillRect(	round(topleftX * pWidth),
			                   	pHeight-round(topleftY * pHeight),
													round(lWidth * pWidth),
													pHeight-round(hist[i] * pHeight));
		}

	}

	private static int round(double pD)
	{
		return (int) Math.round(pD);
	}
}
