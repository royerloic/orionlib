/*
 * Created on 22.4.2005 by Dipl.-Inf. MSc. Ing Loic Royer
 */
package utils.graphics;

import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import utils.graphics.impl.FullScreenPixelAndVector;
import utils.graphics.impl.WindowedPixelAndVector;

public class OrionGraphicsFactory
{
	public static final int	PIXEL					= 1;

	public static final int	VECTOR				= 2;

	public static final int	cLAST_DEVICE	= Integer.MAX_VALUE;

	public static final int	cFIRST_DEVICE	= 0;

	private OrionGraphicsFactory()
	{
		super();
	}

	public static IOrionGraphics getOrionGraphics(final int pDevice,
																								final DisplayMode pDisplayMode,
																								final int pType)
	{
		// First we try full screen:
		try
		{
			return getFullScreenOrionGraphics(pDevice, pDisplayMode, pType);
		}
		catch (final Throwable eFailedFullScreen)
		{
			try
			{
				return getWindowedOrionGraphics(pDevice, pDisplayMode, pType);
			}
			catch (final Throwable eFailedWindowed)
			{
				System.err.print("The factory could not return a Full Screen or a Windowed OrionGraphics. ");
				eFailedWindowed.printStackTrace();
			}
		}
		return null;
	}

	public static IOrionGraphics getFullScreenOrionGraphics(final int pDevice,
																													final DisplayMode pDisplayMode,
																													final int pType)
	{
		switch (pType)
		{
			// case VECTOR:
			// return new FullScreenVectorOnly(pDevice, pDisplayMode);

			default:
				return new FullScreenPixelAndVector(pDevice, pDisplayMode);
		}
	}

	public static IOrionGraphics getWindowedOrionGraphics(final int pDevice,
																												final DisplayMode pDisplayMode,
																												final int pType)
	{
		switch (pType)
		{

			default:
				return new WindowedPixelAndVector(pDevice, pDisplayMode);

		}
	}

	public static DisplayMode getCurrentDisplayModeOnDevice(final int pDevice)
	{
		final GraphicsEnvironment lGraphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();

		final GraphicsDevice[] lAllGraphicsDevices = lGraphicsEnvironment.getScreenDevices();

		int lDevice = pDevice;
		if (pDevice == OrionGraphicsFactory.cLAST_DEVICE)
			lDevice = lAllGraphicsDevices.length - 1;

		final GraphicsDevice lGraphicsDevice = lAllGraphicsDevices[lDevice];

		final DisplayMode lDisplayMode = lGraphicsDevice.getDisplayMode();

		return lDisplayMode;
	}
}
