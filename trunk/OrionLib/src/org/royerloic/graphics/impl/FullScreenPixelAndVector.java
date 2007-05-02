/*
 * Created on 21.4.2005 by Dipl.-Inf. MSc. Ing Loic Royer
 */
package org.royerloic.graphics.impl;

import java.awt.BufferCapabilities;
import java.awt.Color;
import java.awt.DisplayMode;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.ImageCapabilities;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DirectColorModel;
import java.awt.image.MemoryImageSource;
import java.io.File;
import java.net.URL;

import javax.swing.SwingUtilities;

import org.royerloic.graphics.IMouseInfo;
import org.royerloic.graphics.IOrionGraphics;
import org.royerloic.graphics.IOrionGraphicsMouseListener;
import org.royerloic.graphics.OrionGraphicsFactory;

public class FullScreenPixelAndVector implements IOrionGraphics
{

	private static GraphicsEnvironment	mGraphicsEnvironment;

	private static GraphicsDevice				mGraphicsDevice;

	private static Frame								mFrame;

	private DisplayMode									mDisplayMode;

	private MemoryImageSource						mMemoryImageSource;

	private BufferStrategy							mStrategy;

	private Image												mPixelOffscreen;

	private int													mWidth;

	private int													mHeight;

	private boolean											mTimerStarted;

	private long												mInitialTime;

	private int													mFrameCounter;

	private boolean											mDisplayFramerate;

	private int													mNumberOfBuffers;

	private double											mFrameRate;

	private int													mMaxFramesForFrameRate;

	private IOrionGraphicsMouseListener	mOrionGraphicsMouseListener;

	private MouseAdapter								mMouseListenerAdapter;

	private MouseMotionAdapter					mMouseMotionListenerAdapter;

	private MouseWheelListener					mMouseWheelListenerAdapter;

	public class MyMouseInfo implements IMouseInfo
	{
		protected boolean	mMouseLeft;

		protected boolean	mMouseMiddle;

		protected boolean	mMouseRight;

		protected int			mMouseX;

		protected int			mMouseY;

		protected int			mMouseDeltaZ;

		protected boolean	mShift;

		protected boolean	mCtrl;

		protected boolean	mAlt;

		public int getMouseX()
		{
			return this.mMouseX;
		}

		public int getMouseY()
		{
			return this.mMouseY;
		}

		public int getMouseDeltaZ()
		{
			return this.mMouseDeltaZ;
		}

		public boolean getMouseLeft()
		{
			return this.mMouseLeft;
		}

		public boolean getMouseMiddle()
		{
			return this.mMouseMiddle;
		}

		public boolean getMouseRight()
		{
			return this.mMouseRight;
		}

		public boolean getShift()
		{
			return this.mShift;
		}

		public boolean getCtrl()
		{
			return this.mCtrl;
		}

		public boolean getAlt()
		{
			return this.mAlt;
		}

	};

	private MyMouseInfo	mMouseInfo;

	private String			mIconFileName;

	private KeyListener	mKeyListenerAdapter;

	public FullScreenPixelAndVector(final int pDevice, final DisplayMode pDisplayMode)
	{
		super();
		this.mIconFileName = null;
		this.mDisplayFramerate = false;
		this.mMaxFramesForFrameRate = 512;
		this.mNumberOfBuffers = 3;
		System.setProperty("sun.java2d.translaccel", "true");
		// System.setProperty("sun.java2d.accthreshold", "0");
		System.setProperty("sun.java2d.ddscale", "true");
		// System.setProperty("sun.java2d.ddforcevram", "true");
		// System.setProperty("sun.java2d.opengl", "True");

		mGraphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();

		final GraphicsDevice[] lAllGraphicsDevices = mGraphicsEnvironment.getScreenDevices();

		int lDevice = pDevice;
		if (pDevice == OrionGraphicsFactory.cLAST_DEVICE)
			lDevice = lAllGraphicsDevices.length - 1;

		mGraphicsDevice = lAllGraphicsDevices[lDevice];

		this.mDisplayMode = pDisplayMode;

		this.mWidth = this.mDisplayMode.getWidth();
		this.mHeight = this.mDisplayMode.getHeight();

	}

	public boolean startGraphics()
	{

		try
		{
			mFrame = new Frame("OrionGraphics");

			doSetIconImage();

			System.out.println("Available video memory before starting: "
					+ mGraphicsDevice.getAvailableAcceleratedMemory() / (1024 * 1024) + " MB");
			System.out.println("isfullscreen supported:" + mGraphicsDevice.isFullScreenSupported());

			//
			mFrame.setResizable(false);
			mFrame.setIgnoreRepaint(true);
			mFrame.setUndecorated(true);

			mGraphicsDevice.setFullScreenWindow(mFrame);
			mFrame.setVisible(true);
			try
			{
				mGraphicsDevice.setDisplayMode(this.mDisplayMode);
			}
			catch (final RuntimeException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			this.mWidth = mGraphicsDevice.getDisplayMode().getWidth();
			this.mHeight = mGraphicsDevice.getDisplayMode().getHeight();

			// mFrame.setBounds(0, 0, mWidth, mHeight);

			try
			{
				Thread.sleep(0);
			}
			catch (final InterruptedException e)
			{
			}

			mFrame.createBufferStrategy(this.mNumberOfBuffers);
			// mFrame.validate();

			this.mStrategy = mFrame.getBufferStrategy();

			final BufferCapabilities lBufferCapablities = this.mStrategy.getCapabilities();
			System.out.println("isFullScreenRequired: " + lBufferCapablities.isFullScreenRequired());
			System.out.println("isMultiBufferAvailable: " + lBufferCapablities.isMultiBufferAvailable());
			System.out.println("isPageFlipping: " + lBufferCapablities.isPageFlipping());

			System.out.println("Available video memory after starting: "
					+ mGraphicsDevice.getAvailableAcceleratedMemory() / (1024 * 1024) + " MB");

		}
		catch (final RuntimeException e)
		{
			e.printStackTrace();
			return false;
		}

		startMouseEventListeners();

		this.mMouseInfo.mMouseX = mFrame.getWidth() / 2;
		this.mMouseInfo.mMouseY = mFrame.getHeight() / 2;

		return true;
	}

	public void stopGraphics()
	{
		stopMouseEventListeners();
		mGraphicsDevice.setFullScreenWindow(null);
		mFrame.dispose();
	}

	public void setPixelArray(final int[] pPixelArray, final int pOffset, final int pScan)
	{
		final int lScreenWidth = mFrame.getWidth();
		final int lScreenHeight = mFrame.getHeight();

		if (lScreenWidth * lScreenHeight != pPixelArray.length)
			throw new IllegalArgumentException("Wrong dimension for the Pixel Array.");
		final DirectColorModel lColorModel = new DirectColorModel(32, 0x00FF0000, 0x0000FF00, 0x000000FF, 0);
		this.mMemoryImageSource = new MemoryImageSource(lScreenWidth, lScreenHeight, lColorModel, pPixelArray,
				pOffset, pScan);

		this.mMemoryImageSource.setAnimated(true);

		this.mPixelOffscreen = Toolkit.getDefaultToolkit().createImage(this.mMemoryImageSource);
	}

	public void updateAllPixels()
	{
		this.mMemoryImageSource.newPixels();
	}

	public void updatePixelArea(final int pX, final int pY, final int pW, final int pH)
	{
		this.mMemoryImageSource.newPixels(pX, pY, pW, pH);
	}

	public void paintPixels()
	{
		getDrawGraphics().drawImage(this.mPixelOffscreen, 0, 0, null);
	}

	public Graphics2D getDrawGraphics()
	{
		return (Graphics2D) this.mStrategy.getDrawGraphics();
	}

	public void show()
	{
		if (this.mDisplayFramerate)
			displayFrameRate();
		this.mStrategy.show();
	}

	private void displayFrameRate()
	{
		if ((!this.mTimerStarted) || (this.mFrameCounter > this.mMaxFramesForFrameRate))
		{
			this.mTimerStarted = true;
			this.mInitialTime = System.currentTimeMillis();
			this.mFrameCounter = 1;
		}
		else
		{
			this.mFrameCounter++;
			final long lCurrentTime = System.currentTimeMillis();
			final long lDifference = lCurrentTime - this.mInitialTime;
			final long lDeltaInSeconds = lDifference / 1000;
			if (lDeltaInSeconds != 0)
			{
				this.mFrameRate = (this.mFrameCounter) / (lDeltaInSeconds);

				if (this.mDisplayFramerate)
				{
					final Graphics2D lGraphics = getDrawGraphics();
					lGraphics.setColor(Color.BLACK);
					lGraphics.fillRect(0, 0, 150, 15);
					lGraphics.setColor(Color.WHITE);
					lGraphics.drawString("Images per second: " + this.mFrameRate, 10, 10);
					lGraphics.dispose();
				}
			}
		}
	};

	public double getFrameRate()
	{
		return this.mFrameRate;
	}

	public double getInterFrameTime()
	{
		double lInterFrameTime = -1;
		if (this.mFrameRate != 0)
			lInterFrameTime = 1 / this.mFrameRate;
		return lInterFrameTime;
	}

	public boolean isDisplayFramerate()
	{
		return this.mDisplayFramerate;
	}

	public void setDisplayFramerate(final boolean pDisplayFramerate)
	{
		this.mDisplayFramerate = pDisplayFramerate;
	}

	public void startMouseEventListeners()
	{
		this.mMouseInfo = new MyMouseInfo();

		this.mMouseListenerAdapter = new MouseAdapter()
		{

			@Override
			public void mousePressed(final MouseEvent pMouseEvent)
			{
				FullScreenPixelAndVector.this.mMouseInfo.mCtrl = pMouseEvent.isControlDown();
				FullScreenPixelAndVector.this.mMouseInfo.mAlt = pMouseEvent.isAltDown();
				FullScreenPixelAndVector.this.mMouseInfo.mShift = pMouseEvent.isShiftDown();

				FullScreenPixelAndVector.this.mMouseInfo.mMouseLeft = SwingUtilities.isLeftMouseButton(pMouseEvent);
				FullScreenPixelAndVector.this.mMouseInfo.mMouseMiddle = SwingUtilities.isMiddleMouseButton(pMouseEvent);
				FullScreenPixelAndVector.this.mMouseInfo.mMouseRight = SwingUtilities.isRightMouseButton(pMouseEvent);
				if (FullScreenPixelAndVector.this.mOrionGraphicsMouseListener != null)
					if (FullScreenPixelAndVector.this.mMouseInfo.mMouseLeft)
						FullScreenPixelAndVector.this.mOrionGraphicsMouseListener.onLeftPress(FullScreenPixelAndVector.this.mMouseInfo);
					else if (FullScreenPixelAndVector.this.mMouseInfo.mMouseRight)
						FullScreenPixelAndVector.this.mOrionGraphicsMouseListener.onRightPress(FullScreenPixelAndVector.this.mMouseInfo);
					else if (FullScreenPixelAndVector.this.mMouseInfo.mMouseMiddle)
						FullScreenPixelAndVector.this.mOrionGraphicsMouseListener.onMiddlePress(FullScreenPixelAndVector.this.mMouseInfo);

			}

			@Override
			public void mouseReleased(final MouseEvent pMouseEvent)
			{
				FullScreenPixelAndVector.this.mMouseInfo.mCtrl = pMouseEvent.isControlDown();
				FullScreenPixelAndVector.this.mMouseInfo.mAlt = pMouseEvent.isAltDown();
				FullScreenPixelAndVector.this.mMouseInfo.mShift = pMouseEvent.isShiftDown();

				if (SwingUtilities.isLeftMouseButton(pMouseEvent))
					FullScreenPixelAndVector.this.mMouseInfo.mMouseLeft = false;
				if (SwingUtilities.isMiddleMouseButton(pMouseEvent))
					FullScreenPixelAndVector.this.mMouseInfo.mMouseMiddle = false;
				if (SwingUtilities.isRightMouseButton(pMouseEvent))
					FullScreenPixelAndVector.this.mMouseInfo.mMouseRight = false;
			}

			@Override
			public void mouseClicked(final MouseEvent pMouseEvent)
			{
				if (FullScreenPixelAndVector.this.mOrionGraphicsMouseListener != null)
				{
					FullScreenPixelAndVector.this.mMouseInfo.mCtrl = pMouseEvent.isControlDown();
					FullScreenPixelAndVector.this.mMouseInfo.mAlt = pMouseEvent.isAltDown();
					FullScreenPixelAndVector.this.mMouseInfo.mShift = pMouseEvent.isShiftDown();

					FullScreenPixelAndVector.this.mMouseInfo.mMouseLeft = SwingUtilities.isLeftMouseButton(pMouseEvent);
					FullScreenPixelAndVector.this.mMouseInfo.mMouseMiddle = SwingUtilities.isMiddleMouseButton(pMouseEvent);
					FullScreenPixelAndVector.this.mMouseInfo.mMouseRight = SwingUtilities.isRightMouseButton(pMouseEvent);/**/
					if (FullScreenPixelAndVector.this.mMouseInfo.mMouseLeft)
						FullScreenPixelAndVector.this.mOrionGraphicsMouseListener.onLeftClick(FullScreenPixelAndVector.this.mMouseInfo);
					else if (FullScreenPixelAndVector.this.mMouseInfo.mMouseRight)
						FullScreenPixelAndVector.this.mOrionGraphicsMouseListener.onRightClick(FullScreenPixelAndVector.this.mMouseInfo);
					else if (FullScreenPixelAndVector.this.mMouseInfo.mMouseMiddle)
						FullScreenPixelAndVector.this.mOrionGraphicsMouseListener.onMiddleClick(FullScreenPixelAndVector.this.mMouseInfo);
				}
			}

		};
		mFrame.addMouseListener(this.mMouseListenerAdapter);

		this.mMouseMotionListenerAdapter = new MouseMotionAdapter()
		{

			@Override
			public void mouseDragged(final MouseEvent pMouseEvent)
			{
				FullScreenPixelAndVector.this.mMouseInfo.mCtrl = pMouseEvent.isControlDown();
				FullScreenPixelAndVector.this.mMouseInfo.mAlt = pMouseEvent.isAltDown();
				FullScreenPixelAndVector.this.mMouseInfo.mShift = pMouseEvent.isShiftDown();

				FullScreenPixelAndVector.this.mMouseInfo.mMouseX = pMouseEvent.getX();
				FullScreenPixelAndVector.this.mMouseInfo.mMouseY = pMouseEvent.getY();
				if (FullScreenPixelAndVector.this.mOrionGraphicsMouseListener != null)
					FullScreenPixelAndVector.this.mOrionGraphicsMouseListener.onMove(FullScreenPixelAndVector.this.mMouseInfo);
			}

			@Override
			public void mouseMoved(final MouseEvent pMouseEvent)
			{
				FullScreenPixelAndVector.this.mMouseInfo.mCtrl = pMouseEvent.isControlDown();
				FullScreenPixelAndVector.this.mMouseInfo.mAlt = pMouseEvent.isAltDown();
				FullScreenPixelAndVector.this.mMouseInfo.mShift = pMouseEvent.isShiftDown();

				FullScreenPixelAndVector.this.mMouseInfo.mMouseX = pMouseEvent.getX();
				FullScreenPixelAndVector.this.mMouseInfo.mMouseY = pMouseEvent.getY();
				if (FullScreenPixelAndVector.this.mOrionGraphicsMouseListener != null)
					FullScreenPixelAndVector.this.mOrionGraphicsMouseListener.onMove(FullScreenPixelAndVector.this.mMouseInfo);
			}
		};
		mFrame.addMouseMotionListener(this.mMouseMotionListenerAdapter);

		this.mMouseWheelListenerAdapter = new MouseWheelListener()
		{
			public void mouseWheelMoved(final MouseWheelEvent pMouseWheelEvent)
			{
				FullScreenPixelAndVector.this.mMouseInfo.mCtrl = pMouseWheelEvent.isControlDown();
				FullScreenPixelAndVector.this.mMouseInfo.mAlt = pMouseWheelEvent.isAltDown();
				FullScreenPixelAndVector.this.mMouseInfo.mShift = pMouseWheelEvent.isShiftDown();

				FullScreenPixelAndVector.this.mMouseInfo.mMouseDeltaZ = pMouseWheelEvent.getWheelRotation();
				if (FullScreenPixelAndVector.this.mOrionGraphicsMouseListener != null)
					FullScreenPixelAndVector.this.mOrionGraphicsMouseListener.onWheel(FullScreenPixelAndVector.this.mMouseInfo);
			}
		};

		mFrame.addMouseWheelListener(this.mMouseWheelListenerAdapter);

		this.mKeyListenerAdapter = new KeyAdapter()
		{
			@Override
			public void keyTyped(final KeyEvent pE)
			{
				super.keyTyped(pE);
				if (FullScreenPixelAndVector.this.mOrionGraphicsMouseListener != null)
					FullScreenPixelAndVector.this.mOrionGraphicsMouseListener.onKeyTyped(pE);
			}

			@Override
			public void keyPressed(final KeyEvent pE)
			{
				super.keyPressed(pE);
				if (FullScreenPixelAndVector.this.mOrionGraphicsMouseListener != null)
					FullScreenPixelAndVector.this.mOrionGraphicsMouseListener.onKeyPressed(pE);
			}
		};
		mFrame.addKeyListener(this.mKeyListenerAdapter);
	}

	public void stopMouseEventListeners()
	{
		mFrame.removeMouseListener(this.mMouseListenerAdapter);
		mFrame.removeMouseMotionListener(this.mMouseMotionListenerAdapter);
		mFrame.removeMouseWheelListener(this.mMouseWheelListenerAdapter);
		mFrame.removeKeyListener(this.mKeyListenerAdapter);
	}

	public void setMouseListener(final IOrionGraphicsMouseListener pOrionGraphicsMouseListener)
	{
		this.mOrionGraphicsMouseListener = pOrionGraphicsMouseListener;

	}

	public IMouseInfo getMouseInfo()
	{
		return this.mMouseInfo;
	}

	public int getNumberOfBuffers()
	{
		return this.mNumberOfBuffers;
	}

	public void setNumberOfBuffers(final int pNumberOfBuffers)
	{
		this.mNumberOfBuffers = pNumberOfBuffers;
	}

	public int getHeight()
	{
		return this.mHeight;
	}

	public int getWidth()
	{
		return this.mWidth;
	}

	public int getMaxFramesForFrameRate()
	{
		return this.mMaxFramesForFrameRate;
	}

	public void setMaxFramesForFrameRate(final int pMaxFramesForFrameRate)
	{
		this.mMaxFramesForFrameRate = pMaxFramesForFrameRate;
	}

	public void minimize()
	{
		mFrame.toBack();
		mFrame.setExtendedState(Frame.ICONIFIED);
	}

	public void maximize()
	{
		mFrame.toFront();
		mFrame.setExtendedState(Frame.MAXIMIZED_BOTH);
	}

	public void setIconImage(final String pIconFileName)
	{
		this.mIconFileName = pIconFileName;
	}

	public void doSetIconImage()
	{
		if (this.mIconFileName != null)
		{
			final Toolkit lToolkit = Toolkit.getDefaultToolkit();
			final URL lURL = this.getClass().getClassLoader().getResource(this.mIconFileName);
			if (lURL == null)
				throw new NullPointerException("lURL == null");
			new File(lURL.toString());
			mFrame.setIconImage(lToolkit.getImage(lURL));
		}
	}

	public void setFrameName(final String pName)
	{
		mFrame.setTitle(pName);
	}

	public Frame getFrame()
	{
		return mFrame;
	}

	public boolean isImageAccelerated(final BufferedImage pImage)
	{
		final ImageCapabilities lImageCapabilities = pImage.getCapabilities(mGraphicsDevice.getDefaultConfiguration());
		return lImageCapabilities.isAccelerated();
	}

	public void showGraphics()
	{
		show();
	}

	/*****************************************************************************
	 * public static void main(String[] argv) {
	 * 
	 * mFrame = new Frame(mGraphicsDevice.getDefaultConfiguration()); mWindow =
	 * new Window(mFrame); mGraphicsDevice.setFullScreenWindow(mWindow);
	 * mWindow.setIgnoreRepaint(true); mWindow.requestFocus(); try {
	 * mWindow.setAlwaysOnTop(true); } catch (Throwable eProblemWithAllwaysOnTop) {
	 * eProblemWithAllwaysOnTop.printStackTrace(); }
	 * 
	 * try {
	 * 
	 * DisplayMode[] affichages = mGraphicsDevice.getDisplayModes(); int i; for (i =
	 * 0; i < affichages.length; i++) { if ((affichages[i].getWidth() == 800) &&
	 * (affichages[i].getHeight() == 600) && (affichages[i].getBitDepth() == 32)) {
	 * mGraphicsDevice.setDisplayMode(affichages[i]); } }
	 * 
	 * int screenWidth = mWindow.getWidth(); int screenHeight =
	 * mWindow.getHeight();
	 * 
	 * Graphics graphicwindow = mWindow.getGraphics();
	 * graphicwindow.setColor(Color.black); graphicwindow.fillRect(0, 0,
	 * screenWidth, screenHeight);
	 * 
	 * int size = screenWidth * screenHeight; int[] pixels = new int[size];
	 * 
	 * MemoryImageSource source = new MemoryImageSource(screenWidth, screenHeight,
	 * pixels, 0, screenWidth); source.setAnimated(true);
	 * source.setFullBufferUpdates(true);
	 * 
	 * Image offscreen = Toolkit.getDefaultToolkit().createImage(source); int
	 * frames = 0;
	 * 
	 * while (!casse_toi) {
	 * 
	 * frames++; for (i = 0; i < size; i++) { pixels[i] = 0x00000000; }
	 * 
	 * for (i = 0; i < size; i++) { pixels[i] = 0xffff0000; } source.newPixels();
	 * String s = "frame" + frames; graphicwindow.drawImage(offscreen, 0, 0,
	 * null); graphicwindow.setColor(Color.black); graphicwindow.drawString(s, 20,
	 * 20); } } catch (Throwable e) { } }/
	 ****************************************************************************/

}