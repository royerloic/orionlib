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
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
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

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.royerloic.graphics.IMouseInfo;
import org.royerloic.graphics.IOrionGraphics;
import org.royerloic.graphics.IOrionGraphicsMouseListener;
import org.royerloic.graphics.OrionGraphicsFactory;

public class WindowedPixelAndVector implements IOrionGraphics
{

	private static GraphicsEnvironment	mGraphicsEnvironment;

	private static GraphicsDevice				mGraphicsDevice;

	private DisplayMode									mDisplayMode;

	private MemoryImageSource						mMemoryImageSource;

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

	private MyMouseInfo			mMouseInfo;

	private String					mIconFileName;

	private Frame						mFrame;

	private BufferStrategy	mStrategy;

	private KeyAdapter			mKeyListenerAdapter;

	public WindowedPixelAndVector(final int pDevice, final DisplayMode pDisplayMode)
	{
		super();
		this.mIconFileName = null;
		this.mDisplayFramerate = false;
		this.mMaxFramesForFrameRate = 512;
		this.mNumberOfBuffers = 1;
		System.setProperty("sun.java2d.translaccel", "true");
		System.setProperty("sun.java2d.accthreshold", "0");
		System.setProperty("sun.java2d.ddscale", "true");
		System.setProperty("sun.java2d.ddforcevram", "true");
		System.setProperty("sun.java2d.opengl", "True");

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
			this.mFrame = new Frame("OrionGraphics");

			this.mFrame.setResizable(false);
			this.mFrame.setUndecorated(true);

			// mJPanel = new JPanel();
			// mJPanel.setIgnoreRepaint(true);

			this.mFrame.setSize(this.mWidth, this.mHeight);

			final Rectangle lRectangle = mGraphicsDevice.getDefaultConfiguration().getBounds();
			this.mFrame.setBounds(lRectangle);

			this.mFrame.setIgnoreRepaint(true);

			// mFrame.setContentPane(mJPanel);
			this.mFrame.setVisible(true);

			this.mFrame.createBufferStrategy(this.mNumberOfBuffers);
			this.mStrategy = this.mFrame.getBufferStrategy();

			final BufferCapabilities lBufferCapablities = this.mStrategy.getCapabilities();
			System.out.println("isFullScreenRequired: " + lBufferCapablities.isFullScreenRequired());
			System.out.println("isMultiBufferAvailable: " + lBufferCapablities.isMultiBufferAvailable());
			System.out.println("isPageFlipping: " + lBufferCapablities.isPageFlipping());

			System.out.println("Available video memory after starting: "
					+ mGraphicsDevice.getAvailableAcceleratedMemory() / (1024 * 1024) + "MB");

			doSetIconImage();

			System.out.println("Available video memory before starting: "
					+ mGraphicsDevice.getAvailableAcceleratedMemory() / (1024 * 1024) + " MB");
			System.out.println("isfullscreen supported:" + mGraphicsDevice.isFullScreenSupported());

			//

			try
			{
				Thread.sleep(50);
			}
			catch (final InterruptedException e)
			{
			}

		}
		catch (final RuntimeException e)
		{
			e.printStackTrace();
			return false;
		}

		startMouseEventListeners();

		this.mMouseInfo.mMouseX = this.mFrame.getWidth() / 2;
		this.mMouseInfo.mMouseY = this.mFrame.getHeight() / 2;

		return true;
	}

	public void stopGraphics()
	{
		stopMouseEventListeners();
		this.mFrame.dispose();
	}

	public void setPixelArray(final int[] pPixelArray, final int pOffset, final int pScan)
	{
		final int lScreenWidth = this.mFrame.getWidth();
		final int lScreenHeight = this.mFrame.getHeight();

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
		// return (Graphics2D) mFrame.getGraphics();
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
				WindowedPixelAndVector.this.mMouseInfo.mMouseLeft = SwingUtilities.isLeftMouseButton(pMouseEvent);
				WindowedPixelAndVector.this.mMouseInfo.mMouseMiddle = SwingUtilities.isMiddleMouseButton(pMouseEvent);
				WindowedPixelAndVector.this.mMouseInfo.mMouseRight = SwingUtilities.isRightMouseButton(pMouseEvent);
				if (WindowedPixelAndVector.this.mOrionGraphicsMouseListener != null)
					if (WindowedPixelAndVector.this.mMouseInfo.mMouseLeft)
						WindowedPixelAndVector.this.mOrionGraphicsMouseListener.onLeftPress(WindowedPixelAndVector.this.mMouseInfo);
					else if (WindowedPixelAndVector.this.mMouseInfo.mMouseRight)
						WindowedPixelAndVector.this.mOrionGraphicsMouseListener.onRightPress(WindowedPixelAndVector.this.mMouseInfo);
					else if (WindowedPixelAndVector.this.mMouseInfo.mMouseMiddle)
						WindowedPixelAndVector.this.mOrionGraphicsMouseListener.onMiddlePress(WindowedPixelAndVector.this.mMouseInfo);
			}

			@Override
			public void mouseReleased(final MouseEvent pMouseEvent)
			{
				if (SwingUtilities.isLeftMouseButton(pMouseEvent))
					WindowedPixelAndVector.this.mMouseInfo.mMouseLeft = false;
				if (SwingUtilities.isMiddleMouseButton(pMouseEvent))
					WindowedPixelAndVector.this.mMouseInfo.mMouseMiddle = false;
				if (SwingUtilities.isRightMouseButton(pMouseEvent))
					WindowedPixelAndVector.this.mMouseInfo.mMouseRight = false;
			}

			@Override
			public void mouseClicked(final MouseEvent pMouseEvent)
			{
				if (WindowedPixelAndVector.this.mOrionGraphicsMouseListener != null)
				{
					WindowedPixelAndVector.this.mMouseInfo.mMouseLeft = SwingUtilities.isLeftMouseButton(pMouseEvent);
					WindowedPixelAndVector.this.mMouseInfo.mMouseMiddle = SwingUtilities.isMiddleMouseButton(pMouseEvent);
					WindowedPixelAndVector.this.mMouseInfo.mMouseRight = SwingUtilities.isRightMouseButton(pMouseEvent);/**/
					if (WindowedPixelAndVector.this.mMouseInfo.mMouseLeft)
						WindowedPixelAndVector.this.mOrionGraphicsMouseListener.onLeftClick(WindowedPixelAndVector.this.mMouseInfo);
					else if (WindowedPixelAndVector.this.mMouseInfo.mMouseRight)
						WindowedPixelAndVector.this.mOrionGraphicsMouseListener.onRightClick(WindowedPixelAndVector.this.mMouseInfo);
					else if (WindowedPixelAndVector.this.mMouseInfo.mMouseMiddle)
						WindowedPixelAndVector.this.mOrionGraphicsMouseListener.onMiddleClick(WindowedPixelAndVector.this.mMouseInfo);
				}
			}

		};
		this.mFrame.addMouseListener(this.mMouseListenerAdapter);

		this.mMouseMotionListenerAdapter = new MouseMotionAdapter()
		{

			@Override
			public void mouseDragged(final MouseEvent pMouseEvent)
			{
				WindowedPixelAndVector.this.mMouseInfo.mMouseX = pMouseEvent.getX();
				WindowedPixelAndVector.this.mMouseInfo.mMouseY = pMouseEvent.getY();
				if (WindowedPixelAndVector.this.mOrionGraphicsMouseListener != null)
					WindowedPixelAndVector.this.mOrionGraphicsMouseListener.onMove(WindowedPixelAndVector.this.mMouseInfo);
			}

			@Override
			public void mouseMoved(final MouseEvent pMouseEvent)
			{
				WindowedPixelAndVector.this.mMouseInfo.mMouseX = pMouseEvent.getX();
				WindowedPixelAndVector.this.mMouseInfo.mMouseY = pMouseEvent.getY();
				if (WindowedPixelAndVector.this.mOrionGraphicsMouseListener != null)
					WindowedPixelAndVector.this.mOrionGraphicsMouseListener.onMove(WindowedPixelAndVector.this.mMouseInfo);
			}
		};
		this.mFrame.addMouseMotionListener(this.mMouseMotionListenerAdapter);

		this.mMouseWheelListenerAdapter = new MouseWheelListener()
		{
			public void mouseWheelMoved(final MouseWheelEvent pE)
			{
				WindowedPixelAndVector.this.mMouseInfo.mMouseDeltaZ = pE.getWheelRotation();
				if (WindowedPixelAndVector.this.mOrionGraphicsMouseListener != null)
					WindowedPixelAndVector.this.mOrionGraphicsMouseListener.onWheel(WindowedPixelAndVector.this.mMouseInfo);
			}
		};

		this.mFrame.addMouseWheelListener(this.mMouseWheelListenerAdapter);

		this.mKeyListenerAdapter = new KeyAdapter()
		{
			@Override
			public void keyTyped(final KeyEvent pE)
			{
				super.keyTyped(pE);
				if (WindowedPixelAndVector.this.mOrionGraphicsMouseListener != null)
					WindowedPixelAndVector.this.mOrionGraphicsMouseListener.onKeyTyped(pE);
			}

			@Override
			public void keyPressed(final KeyEvent pE)
			{
				super.keyPressed(pE);
				if (WindowedPixelAndVector.this.mOrionGraphicsMouseListener != null)
					WindowedPixelAndVector.this.mOrionGraphicsMouseListener.onKeyPressed(pE);
			}
		};
		this.mFrame.addKeyListener(this.mKeyListenerAdapter);
	}

	public void stopMouseEventListeners()
	{
		this.mFrame.removeMouseListener(this.mMouseListenerAdapter);
		this.mFrame.removeMouseMotionListener(this.mMouseMotionListenerAdapter);
		this.mFrame.removeMouseWheelListener(this.mMouseWheelListenerAdapter);
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
		this.mFrame.toBack();
		this.mFrame.setExtendedState(Frame.ICONIFIED);
	}

	public void maximize()
	{
		this.mFrame.toFront();
		this.mFrame.setExtendedState(Frame.MAXIMIZED_BOTH);
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
			final URL lURL = ClassLoader.getSystemResource(this.mIconFileName);
			if (lURL == null)
				throw new NullPointerException("lURL == null");
			new File(lURL.toString());
			this.mFrame.setIconImage(lToolkit.getImage(lURL));
		}
	}

	public void setFrameName(final String pName)
	{
		this.mFrame.setTitle(pName);
	}

	public Frame getFrame()
	{
		return this.mFrame;
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

}