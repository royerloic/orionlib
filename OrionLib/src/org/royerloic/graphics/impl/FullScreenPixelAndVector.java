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

	private Image												mOffscreenImage;

	private BufferStrategy							mStrategy;

	private Image												mPixelOffscreen;

	private int													mWidth;

	private int													mHeight;

	private boolean											mTimerStarted;

	private long												mInitialTime;

	private int													mFrameCounter;

	private boolean											mDisplayFramerate;

	private int													mNumberOfBuffers;

	private double											mTotalElapsedTime;

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
			return mMouseX;
		}

		public int getMouseY()
		{
			return mMouseY;
		}

		public int getMouseDeltaZ()
		{
			return mMouseDeltaZ;
		}

		public boolean getMouseLeft()
		{
			return mMouseLeft;
		}

		public boolean getMouseMiddle()
		{
			return mMouseMiddle;
		}

		public boolean getMouseRight()
		{
			return mMouseRight;
		}

		public boolean getShift()
		{
			return mShift;
		}

		public boolean getCtrl()
		{
			return mCtrl;
		}

		public boolean getAlt()
		{
			return mAlt;
		}

	};

	private MyMouseInfo	mMouseInfo;

	private String			mIconFileName;

	private KeyListener	mKeyListenerAdapter;

	public FullScreenPixelAndVector(final int pDevice, final DisplayMode pDisplayMode)
	{
		super();
		mIconFileName = null;
		mDisplayFramerate = false;
		mMaxFramesForFrameRate = 512;
		mNumberOfBuffers = 3;
		System.setProperty("sun.java2d.translaccel", "true");
		// System.setProperty("sun.java2d.accthreshold", "0");
		System.setProperty("sun.java2d.ddscale", "true");
		// System.setProperty("sun.java2d.ddforcevram", "true");
		// System.setProperty("sun.java2d.opengl", "True");

		mGraphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();

		GraphicsDevice[] lAllGraphicsDevices = mGraphicsEnvironment.getScreenDevices();

		int lDevice = pDevice;
		if (pDevice == OrionGraphicsFactory.cLAST_DEVICE)
		{
			lDevice = lAllGraphicsDevices.length - 1;
		}

		mGraphicsDevice = lAllGraphicsDevices[lDevice];

		mDisplayMode = pDisplayMode;

		mWidth = mDisplayMode.getWidth();
		mHeight = mDisplayMode.getHeight();

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
				mGraphicsDevice.setDisplayMode(mDisplayMode);
			}
			catch (RuntimeException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			mWidth = mGraphicsDevice.getDisplayMode().getWidth();
			mHeight = mGraphicsDevice.getDisplayMode().getHeight();

			// mFrame.setBounds(0, 0, mWidth, mHeight);

			try
			{
				Thread.sleep(0);
			}
			catch (InterruptedException e)
			{
			}

			mFrame.createBufferStrategy(mNumberOfBuffers);
			// mFrame.validate();

			mStrategy = mFrame.getBufferStrategy();

			BufferCapabilities lBufferCapablities = mStrategy.getCapabilities();
			System.out.println("isFullScreenRequired: " + lBufferCapablities.isFullScreenRequired());
			System.out.println("isMultiBufferAvailable: " + lBufferCapablities.isMultiBufferAvailable());
			System.out.println("isPageFlipping: " + lBufferCapablities.isPageFlipping());

			System.out.println("Available video memory after starting: "
					+ mGraphicsDevice.getAvailableAcceleratedMemory() / (1024 * 1024) + " MB");

		}
		catch (RuntimeException e)
		{
			e.printStackTrace();
			return false;
		}

		startMouseEventListeners();

		mMouseInfo.mMouseX = mFrame.getWidth() / 2;
		mMouseInfo.mMouseY = mFrame.getHeight() / 2;

		return true;
	}

	public void stopGraphics()
	{
		stopMouseEventListeners();
		mGraphicsDevice.setFullScreenWindow(null);
		mFrame.dispose();
	}

	public void setPixelArray(int[] pPixelArray, final int pOffset, final int pScan)
	{
		int lScreenWidth = mFrame.getWidth();
		int lScreenHeight = mFrame.getHeight();

		if (lScreenWidth * lScreenHeight != pPixelArray.length)
		{
			throw new IllegalArgumentException("Wrong dimension for the Pixel Array.");
		}
		DirectColorModel lColorModel = new DirectColorModel(32, 0x00FF0000, 0x0000FF00, 0x000000FF, 0);
		mMemoryImageSource = new MemoryImageSource(lScreenWidth, lScreenHeight, lColorModel, pPixelArray,
				pOffset, pScan);

		mMemoryImageSource.setAnimated(true);

		mPixelOffscreen = Toolkit.getDefaultToolkit().createImage(mMemoryImageSource);
	}

	public void updateAllPixels()
	{
		mMemoryImageSource.newPixels();
	}

	public void updatePixelArea(int pX, int pY, int pW, int pH)
	{
		mMemoryImageSource.newPixels(pX, pY, pW, pH);
	}

	public void paintPixels()
	{
		getDrawGraphics().drawImage(mPixelOffscreen, 0, 0, null);
	}

	public Graphics2D getDrawGraphics()
	{
		return (Graphics2D) mStrategy.getDrawGraphics();
	}

	public void show()
	{
		if (mDisplayFramerate)
		{
			displayFrameRate();
		}
		mStrategy.show();
	}

	private void displayFrameRate()
	{
		if ((!mTimerStarted) || (mFrameCounter > mMaxFramesForFrameRate))
		{
			mTimerStarted = true;
			mInitialTime = System.currentTimeMillis();
			mFrameCounter = 1;
		}
		else
		{
			mFrameCounter++;
			long lCurrentTime = System.currentTimeMillis();
			long lDifference = lCurrentTime - mInitialTime;
			long lDeltaInSeconds = lDifference / 1000;
			if (lDeltaInSeconds != 0)
			{
				mTotalElapsedTime = lDeltaInSeconds;
				mFrameRate = (mFrameCounter) / (lDeltaInSeconds);

				if (mDisplayFramerate)
				{
					Graphics2D lGraphics = getDrawGraphics();
					lGraphics.setColor(Color.BLACK);
					lGraphics.fillRect(0, 0, 150, 15);
					lGraphics.setColor(Color.WHITE);
					lGraphics.drawString("Images per second: " + mFrameRate, 10, 10);
					lGraphics.dispose();
				}
			}
		}
	};

	public double getFrameRate()
	{
		return mFrameRate;
	}

	public double getInterFrameTime()
	{
		double lInterFrameTime = -1;
		if (mFrameRate != 0)
		{
			lInterFrameTime = 1 / mFrameRate;
		}
		return lInterFrameTime;
	}

	public boolean isDisplayFramerate()
	{
		return mDisplayFramerate;
	}

	public void setDisplayFramerate(boolean pDisplayFramerate)
	{
		mDisplayFramerate = pDisplayFramerate;
	}

	public void startMouseEventListeners()
	{
		mMouseInfo = new MyMouseInfo();

		mMouseListenerAdapter = new MouseAdapter()
		{

			public void mousePressed(MouseEvent pMouseEvent)
			{
				mMouseInfo.mCtrl = pMouseEvent.isControlDown();
				mMouseInfo.mAlt = pMouseEvent.isAltDown();
				mMouseInfo.mShift = pMouseEvent.isShiftDown();

				mMouseInfo.mMouseLeft = SwingUtilities.isLeftMouseButton(pMouseEvent);
				mMouseInfo.mMouseMiddle = SwingUtilities.isMiddleMouseButton(pMouseEvent);
				mMouseInfo.mMouseRight = SwingUtilities.isRightMouseButton(pMouseEvent);
				if (mOrionGraphicsMouseListener != null)
					if (mMouseInfo.mMouseLeft)
					{
						mOrionGraphicsMouseListener.onLeftPress(mMouseInfo);
					}
					else if (mMouseInfo.mMouseRight)
					{
						mOrionGraphicsMouseListener.onRightPress(mMouseInfo);
					}
					else if (mMouseInfo.mMouseMiddle)
					{
						mOrionGraphicsMouseListener.onMiddlePress(mMouseInfo);
					}

			}

			public void mouseReleased(MouseEvent pMouseEvent)
			{
				mMouseInfo.mCtrl = pMouseEvent.isControlDown();
				mMouseInfo.mAlt = pMouseEvent.isAltDown();
				mMouseInfo.mShift = pMouseEvent.isShiftDown();

				if (SwingUtilities.isLeftMouseButton(pMouseEvent))
					mMouseInfo.mMouseLeft = false;
				if (SwingUtilities.isMiddleMouseButton(pMouseEvent))
					mMouseInfo.mMouseMiddle = false;
				if (SwingUtilities.isRightMouseButton(pMouseEvent))
					mMouseInfo.mMouseRight = false;
			}

			public void mouseClicked(MouseEvent pMouseEvent)
			{
				if (mOrionGraphicsMouseListener != null)
				{
					mMouseInfo.mCtrl = pMouseEvent.isControlDown();
					mMouseInfo.mAlt = pMouseEvent.isAltDown();
					mMouseInfo.mShift = pMouseEvent.isShiftDown();

					mMouseInfo.mMouseLeft = SwingUtilities.isLeftMouseButton(pMouseEvent);
					mMouseInfo.mMouseMiddle = SwingUtilities.isMiddleMouseButton(pMouseEvent);
					mMouseInfo.mMouseRight = SwingUtilities.isRightMouseButton(pMouseEvent);/**/
					if (mMouseInfo.mMouseLeft)
					{
						mOrionGraphicsMouseListener.onLeftClick(mMouseInfo);
					}
					else if (mMouseInfo.mMouseRight)
					{
						mOrionGraphicsMouseListener.onRightClick(mMouseInfo);
					}
					else if (mMouseInfo.mMouseMiddle)
					{
						mOrionGraphicsMouseListener.onMiddleClick(mMouseInfo);
					}
				}
			}

		};
		mFrame.addMouseListener(mMouseListenerAdapter);

		mMouseMotionListenerAdapter = new MouseMotionAdapter()
		{

			public void mouseDragged(MouseEvent pMouseEvent)
			{
				mMouseInfo.mCtrl = pMouseEvent.isControlDown();
				mMouseInfo.mAlt = pMouseEvent.isAltDown();
				mMouseInfo.mShift = pMouseEvent.isShiftDown();

				mMouseInfo.mMouseX = pMouseEvent.getX();
				mMouseInfo.mMouseY = pMouseEvent.getY();
				if (mOrionGraphicsMouseListener != null)
					mOrionGraphicsMouseListener.onMove(mMouseInfo);
			}

			public void mouseMoved(MouseEvent pMouseEvent)
			{
				mMouseInfo.mCtrl = pMouseEvent.isControlDown();
				mMouseInfo.mAlt = pMouseEvent.isAltDown();
				mMouseInfo.mShift = pMouseEvent.isShiftDown();

				mMouseInfo.mMouseX = pMouseEvent.getX();
				mMouseInfo.mMouseY = pMouseEvent.getY();
				if (mOrionGraphicsMouseListener != null)
					mOrionGraphicsMouseListener.onMove(mMouseInfo);
			}
		};
		mFrame.addMouseMotionListener(mMouseMotionListenerAdapter);

		mMouseWheelListenerAdapter = new MouseWheelListener()
		{
			public void mouseWheelMoved(MouseWheelEvent pMouseWheelEvent)
			{
				mMouseInfo.mCtrl = pMouseWheelEvent.isControlDown();
				mMouseInfo.mAlt = pMouseWheelEvent.isAltDown();
				mMouseInfo.mShift = pMouseWheelEvent.isShiftDown();

				mMouseInfo.mMouseDeltaZ = pMouseWheelEvent.getWheelRotation();
				if (mOrionGraphicsMouseListener != null)
					mOrionGraphicsMouseListener.onWheel(mMouseInfo);
			}
		};

		mFrame.addMouseWheelListener(mMouseWheelListenerAdapter);

		mKeyListenerAdapter = new KeyAdapter()
		{
			public void keyTyped(KeyEvent pE)
			{
				super.keyTyped(pE);
				if (mOrionGraphicsMouseListener != null)
				{
					mOrionGraphicsMouseListener.onKeyTyped(pE);
				}
			}

			public void keyPressed(KeyEvent pE)
			{
				super.keyPressed(pE);
				if (mOrionGraphicsMouseListener != null)
				{
					mOrionGraphicsMouseListener.onKeyPressed(pE);
				}
			}
		};
		mFrame.addKeyListener(mKeyListenerAdapter);
	}

	public void stopMouseEventListeners()
	{
		mFrame.removeMouseListener(mMouseListenerAdapter);
		mFrame.removeMouseMotionListener(mMouseMotionListenerAdapter);
		mFrame.removeMouseWheelListener(mMouseWheelListenerAdapter);
		mFrame.removeKeyListener(mKeyListenerAdapter);
	}

	public void setMouseListener(IOrionGraphicsMouseListener pOrionGraphicsMouseListener)
	{
		mOrionGraphicsMouseListener = pOrionGraphicsMouseListener;

	}

	public IMouseInfo getMouseInfo()
	{
		return mMouseInfo;
	}

	public int getNumberOfBuffers()
	{
		return mNumberOfBuffers;
	}

	public void setNumberOfBuffers(int pNumberOfBuffers)
	{
		mNumberOfBuffers = pNumberOfBuffers;
	}

	public int getHeight()
	{
		return mHeight;
	}

	public int getWidth()
	{
		return mWidth;
	}

	public int getMaxFramesForFrameRate()
	{
		return mMaxFramesForFrameRate;
	}

	public void setMaxFramesForFrameRate(int pMaxFramesForFrameRate)
	{
		mMaxFramesForFrameRate = pMaxFramesForFrameRate;
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

	public void setIconImage(String pIconFileName)
	{
		mIconFileName = pIconFileName;
	}

	public void doSetIconImage()
	{
		if (mIconFileName != null)
		{
			Toolkit lToolkit = Toolkit.getDefaultToolkit();
			URL lURL = this.getClass().getClassLoader().getResource(mIconFileName);
			if (lURL == null)
				throw new NullPointerException("lURL == null");
			new File(lURL.toString());
			mFrame.setIconImage(lToolkit.getImage(lURL));
		}
	}

	public void setFrameName(String pName)
	{
		mFrame.setTitle(pName);
	}

	public Frame getFrame()
	{
		return mFrame;
	}

	public boolean isImageAccelerated(BufferedImage pImage)
	{
		ImageCapabilities lImageCapabilities = pImage.getCapabilities(mGraphicsDevice.getDefaultConfiguration());
		return lImageCapabilities.isAccelerated();
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