package utils.sound.test;

import java.awt.DisplayMode;
import java.awt.HeadlessException;

import utils.graphics.IOrionGraphics;
import utils.graphics.OrionGraphicsFactory;
import utils.sound.OrionSoundIn;
import utils.sound.OrionSoundOut;

/**
 * Oscilloscope Project
 *
 *
 * Created on 6 mars 2004
 *
 * MSc. Ing. Loic Royer
 */

/**
 * Oscilloscope
 * 
 * @author MSc. Ing. Loic Royer
 */
public class Oscilloscope
{

	OrionSoundIn mSoundRecorder;

	OrionSoundOut mSoundPlayer;

	private byte[] mSoundBuffer;

	private final IOrionGraphics mOrionGraphics;

	/**
	 * @throws HeadlessException
	 */
	public Oscilloscope()
	{

		final int lDevice = OrionGraphicsFactory.cLAST_DEVICE;

		final DisplayMode lDisplayMode = OrionGraphicsFactory.getCurrentDisplayModeOnDevice(lDevice);

		// lDisplayMode = new
		// DisplayMode(800,600,32,DisplayMode.REFRESH_RATE_UNKNOWN);

		mOrionGraphics = OrionGraphicsFactory.getWindowedOrionGraphics(	lDevice,
																																		lDisplayMode,
																																		OrionGraphicsFactory.PIXEL + OrionGraphicsFactory.VECTOR);

		mOrionGraphics.setNumberOfBuffers(2);

	}

	/**
	 * @param pWidth
	 * @param pHeight
	 */
	public void start()
	{
		// mOrionGraphics.addModeInWishList(new DisplayMode(pWidth, pHeight, 32,
		// 0));

		mOrionGraphics.startGraphics();
		final int lHeight = mOrionGraphics.getHeight();
		final int lWidth = 16; // mOrionGraphics.getWidth();

		mSoundBuffer = new byte[2 * lWidth];

		mSoundRecorder = new OrionSoundIn();
		mSoundRecorder.start();

		mSoundPlayer = new OrionSoundOut();
		mSoundPlayer.start();

		while (!mOrionGraphics.getMouseInfo().getMouseRight())
		{
			final int lBytesRead = mSoundRecorder.record(mSoundBuffer);

			mSoundPlayer.play(mSoundBuffer, lBytesRead);

			/*************************************************************************
			 * int lPreviousSignal = 0; int lSignalLock = 0; for (int i = 0; i <
			 * lWidth; i++) { final int lSignal = (mSoundBuffer[2 * i] + 256 *
			 * mSoundBuffer[2 * i + 1]) / 256;
			 * 
			 * if ((lSignal < -64) && (lPreviousSignal > 64)) { lSignalLock = i;
			 * break; }
			 * 
			 * lPreviousSignal = lSignal; }
			 * 
			 * 
			 * final Graphics2D lGraphics = mOrionGraphics.getDrawGraphics();
			 * 
			 * lGraphics.clearRect(0, 0, lWidth, lHeight); for (int i = 1; i < lWidth;
			 * i++) {
			 * 
			 * int lIndex = 2 * (i + lSignalLock); if (lIndex > 2 * lWidth - 2) lIndex =
			 * 2 * lWidth - 2; final int lSignal = mSoundBuffer[lIndex] + 256
			 * mSoundBuffer[lIndex + 1]; int lValue = (lHeight / 2 + lSignal / 128);
			 * if (lValue < 0) lValue = 0; else if (lValue >= lHeight) lValue =
			 * lHeight - 1;
			 * 
			 * lGraphics.drawLine(i, lHeight / 2, i, lValue); //mPixel[i + lWidth *
			 * lValue] = 255 + 256 * 255 + 256 * 256 * 255; }
			 * 
			 * 
			 * 
			 * mOrionGraphics.showGraphics();/
			 ************************************************************************/
		}

		mSoundRecorder.stop();
		mSoundPlayer.stop();

		System.exit(0);
	}

	public static void main(final String[] args)
	{
		final Oscilloscope lOscilloscope = new Oscilloscope();
		// mOrionGraphics.setDefaultCloseOperation(mOrionGraphics.EXIT_ON_CLOSE);
		lOscilloscope.start();

	}
}