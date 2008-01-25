package utils.sound.test;

import java.awt.DisplayMode;
import java.awt.HeadlessException;

import utils.graphics.impl.OrionGraphics;
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

	public int mWidth;

	public int mHeight;

	private int[] mPixel;

	private byte[] mSoundBuffer;

	private OrionGraphics mOrionGraphics;

	/**
	 * @throws HeadlessException
	 */
	public Oscilloscope()
	{
		mOrionGraphics = new OrionGraphics(	"Oscilloscope",
																				OrionGraphics.cLAST_DEVICE);
	}

	/**
	 * @param pWidth
	 * @param pHeight
	 */
	public void start(final int pWidth, final int pHeight)
	{
		mOrionGraphics.addModeInWishList(new DisplayMode(pWidth, pHeight, 32, 0));

		mOrionGraphics.startGraphics();

		mPixel = new int[pWidth * pHeight];
		mWidth = pWidth;
		mHeight = pHeight;

		mSoundBuffer = new byte[2 * pWidth];

		mSoundRecorder = new OrionSoundIn();
		mSoundRecorder.start();

		mSoundPlayer = new OrionSoundOut();
		mSoundPlayer.start();

		while (!mOrionGraphics.mMouseRight)
		{
			final int lBytesRead = mSoundRecorder.record(mSoundBuffer);

			mSoundPlayer.play(mSoundBuffer, lBytesRead);

			// System.out.println(lBytesRead);

			for (int i = 0; i < mWidth * mHeight; i++)
				mPixel[i] = 0;

			int lPreviousSignal = 0;
			int lSignalLock = 0;
			for (int i = 0; i < mWidth; i++)
			{
				final int lSignal = (mSoundBuffer[2 * i] + 256 * mSoundBuffer[2 * i + 1]) / 256;

				if ((lSignal < -64) && (lPreviousSignal > 64))
				{
					lSignalLock = i;
					break;
				}

				lPreviousSignal = lSignal;
			}

			for (int i = 0; i < mWidth; i++)
			{
				int lIndex = 2 * (i + lSignalLock);
				if (lIndex > 2 * mWidth - 2)
					lIndex = 2 * mWidth - 2;
				final int lSignal = mSoundBuffer[lIndex] + 256
														* mSoundBuffer[lIndex + 1];
				int lValue = (mHeight / 2 + lSignal / 128);
				if (lValue < 0)
					lValue = 0;
				else if (lValue >= mHeight)
					lValue = mHeight - 1;
				mPixel[i + mWidth * lValue] = 255 + 256 * 255 + 256 * 256 * 255;
			}

			mOrionGraphics.update(mPixel);
			mOrionGraphics.paintPixels();
			mOrionGraphics.refresh(); /**/
		}

		mSoundRecorder.stop();
		mSoundPlayer.stop();

		System.exit(0);
	}

	public static void main(final String[] args)
	{
		final Oscilloscope lOscilloscope = new Oscilloscope();
		// mOrionGraphics.setDefaultCloseOperation(mOrionGraphics.EXIT_ON_CLOSE);
		lOscilloscope.start(1024, 768);

	}
}