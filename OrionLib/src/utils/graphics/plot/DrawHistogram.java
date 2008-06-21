package utils.graphics.plot;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import utils.math.statistics.Histogram;

public class DrawHistogram
{
	public static void draw(double[] pHistogramArray,
													Graphics2D pGraphics,
													int pX,
													int pY,
													int pW,
													int pH)
	{
		final double[] hist = pHistogramArray;

		double lWidth = 1 / ((double) hist.length);

		pGraphics.setBackground(new Color(1f, 1f, 1f, 0.5f));
		pGraphics.clearRect(pX, pY, pW, pH);

		for (int i = 0; i < hist.length; i++)
		{

			final double topleftX = lWidth * i;
			final double topleftY = hist[i];

			final float intensity = 1 - (float) (0.5 + 0.5 * hist[i]);
			pGraphics.setColor(new Color(intensity, intensity, intensity));

			pGraphics.fillRect(	pX + round(topleftX * pW),
			                   	pY+pH-round(hist[i] * pH),
													round(lWidth * pW),
													round(hist[i] * pH));
		}

	}

	private static int round(double pD)
	{
		return (int)pD;
	}
}
