package org.royerloic.sound.test;

import java.awt.DisplayMode;
import java.awt.HeadlessException;

import org.royerloic.graphics.impl.OrionGraphics;
import org.royerloic.sound.OrionSoundIn;
import org.royerloic.sound.OrionSoundOut;

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

	OrionSoundIn					mSoundRecorder;

	OrionSoundOut					mSoundPlayer;

	public int						mWidth;

	public int						mHeight;

	private int[]					mPixel;

	private byte[]				mSoundBuffer;

	private OrionGraphics	mOrionGraphics;

	/**
	 * @throws HeadlessException
	 */
	public Oscilloscope()
	{
		this.mOrionGraphics = new OrionGraphics("Oscilloscope", OrionGraphics.cLAST_DEVICE);
	}

	/**
	 * @param pWidth
	 * @param pHeight
	 */
	public void start(final int pWidth, final int pHeight)
	{
		this.mOrionGraphics.addModeInWishList(new DisplayMode(pWidth, pHeight, 32, 0));

		this.mOrionGraphics.startGraphics();

		this.mPixel = new int[pWidth * pHeight];
		this.mWidth = pWidth;
		this.mHeight = pHeight;

		this.mSoundBuffer = new byte[2 * pWidth];

		this.mSoundRecorder = new OrionSoundIn();
		this.mSoundRecorder.start();

		this.mSoundPlayer = new OrionSoundOut();
		this.mSoundPlayer.start();

		while (!this.mOrionGraphics.mMouseRight)
		{
			final int lBytesRead = this.mSoundRecorder.record(this.mSoundBuffer);

			this.mSoundPlayer.play(this.mSoundBuffer, lBytesRead);

			// System.out.println(lBytesRead);

			for (int i = 0; i < this.mWidth * this.mHeight; i++)
				this.mPixel[i] = 0;

			int lPreviousSignal = 0;
			int lSignalLock = 0;
			for (int i = 0; i < this.mWidth; i++)
			{
				final int lSignal = (this.mSoundBuffer[2 * i] + 256 * this.mSoundBuffer[2 * i + 1]) / 256;

				if ((lSignal < -64) && (lPreviousSignal > 64))
				{
					lSignalLock = i;
					break;
				}

				lPreviousSignal = lSignal;
			}

			for (int i = 0; i < this.mWidth; i++)
			{
				int lIndex = 2 * (i + lSignalLock);
				if (lIndex > 2 * this.mWidth - 2)
					lIndex = 2 * this.mWidth - 2;
				final int lSignal = this.mSoundBuffer[lIndex] + 256 * this.mSoundBuffer[lIndex + 1];
				int lValue = (this.mHeight / 2 + lSignal / 128);
				if (lValue < 0)
					lValue = 0;
				else if (lValue >= this.mHeight)
					lValue = this.mHeight - 1;
				this.mPixel[i + this.mWidth * lValue] = 255 + 256 * 255 + 256 * 256 * 255;
			}

			this.mOrionGraphics.update(this.mPixel);
			this.mOrionGraphics.paintPixels();
			this.mOrionGraphics.refresh(); /**/
		}

		this.mSoundRecorder.stop();
		this.mSoundPlayer.stop();

		System.exit(0);
	}

	public static void main(final String[] args)
	{
		final Oscilloscope lOscilloscope = new Oscilloscope();
		// mOrionGraphics.setDefaultCloseOperation(mOrionGraphics.EXIT_ON_CLOSE);
		lOscilloscope.start(1024, 768);

	}
}