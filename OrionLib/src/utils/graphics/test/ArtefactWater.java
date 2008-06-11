package utils.graphics.test;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.DisplayMode;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.HeadlessException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;

import utils.graphics.impl.OrionGraphics;

public class ArtefactWater
{

	private final OrionGraphics mOrionGraphics;

	/**
	 * @throws HeadlessException
	 */
	public ArtefactWater() throws HeadlessException
	{
		mOrionGraphics = new OrionGraphics(	"ArtefactWater",
																				OrionGraphics.cFIRST_DEVICE);
		// TODO Auto-generated constructor stub
	}

	public Graphics getMyGraphics()
	{
		final Graphics lGraphics = mOrionGraphics.getDrawGraphics();
		final Color lColor = new Color(1.0f, 0.0f, 0.0f);
		lGraphics.setColor(lColor);
		final Font lFontTitle = new Font(null, Font.ITALIC, 90);
		final Font lFontSubTitle = new Font(null, Font.ITALIC, 30);
		return lGraphics;
	}

	public int[] getWaterMap(final int pWidth, final int pHeight)
	{
		Robot lRobot = null;
		final int[] lMoonMapBuffer = new int[pWidth * pHeight];

		try
		{
			lRobot = new Robot();
			final BufferedImage lBufferedimage = lRobot.createScreenCapture(new Rectangle(pWidth,
																																										pHeight));

			final PixelGrabber lPixelGrabber = new PixelGrabber(lBufferedimage,
																													0,
																													0,
																													pWidth,
																													pHeight,
																													lMoonMapBuffer,
																													0,
																													pWidth);
			try
			{
				lPixelGrabber.grabPixels();
			}
			catch (final InterruptedException e)
			{
				System.err.println("interrupted waiting for pixels!");
				return null;
			}
		}
		catch (final AWTException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return lMoonMapBuffer;

	}

	public void main(final int pWidth, final int pHeight)
	{
		mOrionGraphics.addModeInWishList(new DisplayMode(pWidth, pHeight, 32, 0));
		final int[] MoonMap = getWaterMap(pWidth, pHeight);

		final int lSize = pWidth * pHeight;
		final int lPixel[] = new int[lSize];

		int lHeightMatrix1[] = new int[lSize];
		int lHeightMatrix2[] = new int[lSize];
		int lHeightMatrixTemp[];

		for (int index = 0; index < lSize; index++)
		{
			lHeightMatrix1[index] = 0;
			lHeightMatrix2[index] = 0;
		}

		final Color lTitleColor = new Color(1.0f, 0.0f, 0.0f);
		final Font lFontTitle = new Font(null, Font.ITALIC, 90);
		final Font lFontSubTitle = new Font(null, Font.ITALIC, 30);

		mOrionGraphics.mMouseX = pWidth / 2;
		mOrionGraphics.mMouseY = pHeight / 2;

		mOrionGraphics.startGraphics();

		while (!mOrionGraphics.mMouseRight)
		{
			final Graphics lGraphics = mOrionGraphics.getDrawGraphics();

			for (int ly = -10; ly < 10; ly++)
			{
				for (int lx = -10; lx < 10; lx++)
				{
					int lIndex = mOrionGraphics.mMouseX + lx
												+ pWidth
												* (mOrionGraphics.mMouseY + ly);
					if (lIndex < 0)
					{
						lIndex = 0;
					}
					else if (lIndex >= lSize)
					{
						lIndex = lSize - 1;
					}

					lHeightMatrix1[lIndex] = 100;
					lHeightMatrix2[lIndex] = 100;
				}
			}

			for (int index = 3 * pWidth; index < lSize - 3 * pWidth; index++)
			{
				lHeightMatrix2[index] = (lHeightMatrix1[index + 3] + lHeightMatrix1[index - 3]
																	+ lHeightMatrix1[index - 3 * pWidth]
																	+ lHeightMatrix1[index + 3 * pWidth] + 1

				)												/ 2
																- lHeightMatrix2[index];

				final int gX = lHeightMatrix1[index + 1] - lHeightMatrix1[index - 1];
				final int gY = lHeightMatrix1[index + pWidth] - lHeightMatrix1[index - pWidth];
				final int lReflectionIndex = index + 2 * gX + pWidth * 2 * gY;
				if (lReflectionIndex < 0)
				{
					lPixel[index] = 0;
				}
				else if (lReflectionIndex >= lSize)
				{
					lPixel[index] = 0;
				}
				else
				{
					lPixel[index] = MoonMap[lReflectionIndex];
				}

			}

			for (int index = 3 * pWidth; index < lSize - 3 * pWidth; index++)
			{
				lHeightMatrix2[index] = (+lHeightMatrix2[index - 1] + lHeightMatrix2[index + 1]
																	+ lHeightMatrix2[index - 1 * pWidth]
																	+ lHeightMatrix2[index + 1 * pWidth]
				/***********************************************************************
				 * + lHeightMatrix2[index - 2] + lHeightMatrix2[index + 2] +
				 * lHeightMatrix2[index - 2*pWidth] + lHeightMatrix2[index + 2*pWidth] /
				 **********************************************************************/
				+ 1) / 4;
			}

			lHeightMatrixTemp = lHeightMatrix1;
			lHeightMatrix1 = lHeightMatrix2;
			lHeightMatrix2 = lHeightMatrixTemp; /**/

			mOrionGraphics.update(lPixel);
			mOrionGraphics.paintPixels();

			//

			/*
			 * lGraphics.setColor(lTitleColor); lGraphics.setFont(lFontTitle);
			 * lGraphics.drawString("Artefact", 160, 150);
			 * lGraphics.setFont(lFontSubTitle); lGraphics.drawString("Pure Java",
			 * 250, 200);
			 */

			mOrionGraphics.refresh();
		}
		mOrionGraphics.stopGraphics();
		mOrionGraphics.dispose();
	}

	public static void main(final String[] pArguments)
	{
		final ArtefactWater lTestFullScreen1 = new ArtefactWater();
		lTestFullScreen1.main(640, 400);
	}

}