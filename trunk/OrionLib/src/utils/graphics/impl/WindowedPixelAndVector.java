/*
 * Created on 21.4.2005 by Dipl.-Inf. MSc. Ing Loic Royer
 */
package utils.graphics.impl;

import java.awt.BufferCapabilities;
import java.awt.Color;
import java.awt.Component;
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

import javax.swing.SwingUtilities;

import utils.graphics.IMouseInfo;
import utils.graphics.IOrionGraphics;
import utils.graphics.IOrionGraphicsMouseListener;
import utils.graphics.OrionGraphicsFactory;

public class WindowedPixelAndVector implements IOrionGraphics
{

	private static GraphicsEnvironment mGraphicsEnvironment;

	private static GraphicsDevice mGraphicsDevice;

	private final DisplayMode mDisplayMode;

	private MemoryImageSource mMemoryImageSource;

	private Image mPixelOffscreen;

	private final int mWidth;

	private final int mHeight;

	private boolean mTimerStarted;

	private long mInitialTime;

	private int mFrameCounter;

	private boolean mDisplayFramerate;

	private int mNumberOfBuffers;

	private double mFrameRate;

	private int mMaxFramesForFrameRate;

	private IOrionGraphicsMouseListener mOrionGraphicsMouseListener;

	private MouseAdapter mMouseListenerAdapter;

	private MouseMotionAdapter mMouseMotionListenerAdapter;

	private MouseWheelListener mMouseWheelListenerAdapter;

	public class MyMouseInfo implements IMouseInfo
	{
		protected boolean mMouseLeft;

		protected boolean mMouseMiddle;

		protected boolean mMouseRight;

		protected int mMouseX;

		protected int mMouseY;

		protected int mMouseDeltaZ;

		protected boolean mShift;

		protected boolean mCtrl;

		protected boolean mAlt;

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

	private MyMouseInfo mMouseInfo;

	private String mIconFileName;

	private Frame mFrame;

	private BufferStrategy mStrategy;

	private KeyAdapter mKeyListenerAdapter;

	public WindowedPixelAndVector(final int pDevice,
																final DisplayMode pDisplayMode)
	{
		super();
		mIconFileName = null;
		mDisplayFramerate = false;
		mMaxFramesForFrameRate = 512;
		mNumberOfBuffers = 1;
		System.setProperty("sun.java2d.translaccel", "true");
		System.setProperty("sun.java2d.accthreshold", "0");
		System.setProperty("sun.java2d.ddscale", "true");
		System.setProperty("sun.java2d.ddforcevram", "true");
		System.setProperty("sun.java2d.opengl", "True");

		mGraphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();

		final GraphicsDevice[] lAllGraphicsDevices = mGraphicsEnvironment.getScreenDevices();

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

			mFrame.setResizable(false);
			mFrame.setUndecorated(true);

			// mJPanel = new JPanel();
			// mJPanel.setIgnoreRepaint(true);

			mFrame.setSize(mWidth, mHeight);

			final Rectangle lRectangle = mGraphicsDevice.getDefaultConfiguration()
																									.getBounds();
			mFrame.setBounds(lRectangle);

			mFrame.setIgnoreRepaint(true);

			// mFrame.setContentPane(mJPanel);
			mFrame.setVisible(true);

			mFrame.createBufferStrategy(mNumberOfBuffers);
			mStrategy = mFrame.getBufferStrategy();

			final BufferCapabilities lBufferCapablities = mStrategy.getCapabilities();
			System.out.println("isFullScreenRequired: " + lBufferCapablities.isFullScreenRequired());
			System.out.println("isMultiBufferAvailable: " + lBufferCapablities.isMultiBufferAvailable());
			System.out.println("isPageFlipping: " + lBufferCapablities.isPageFlipping());

			System.out.println("Available video memory after starting: " + mGraphicsDevice.getAvailableAcceleratedMemory()
													/ (1024 * 1024)
													+ "MB");

			doSetIconImage();

			System.out.println("Available video memory before starting: " + mGraphicsDevice.getAvailableAcceleratedMemory()
													/ (1024 * 1024)
													+ " MB");
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

		mMouseInfo.mMouseX = mFrame.getWidth() / 2;
		mMouseInfo.mMouseY = mFrame.getHeight() / 2;

		return true;
	}

	public void stopGraphics()
	{
		stopMouseEventListeners();
		mFrame.dispose();
	}

	public void setPixelArray(final int[] pPixelArray,
														final int pOffset,
														final int pScan)
	{
		final int lScreenWidth = mFrame.getWidth();
		final int lScreenHeight = mFrame.getHeight();

		if (lScreenWidth * lScreenHeight != pPixelArray.length)
		{
			throw new IllegalArgumentException("Wrong dimension for the Pixel Array.");
		}
		final DirectColorModel lColorModel = new DirectColorModel(32,
																															0x00FF0000,
																															0x0000FF00,
																															0x000000FF,
																															0);
		mMemoryImageSource = new MemoryImageSource(	lScreenWidth,
																								lScreenHeight,
																								lColorModel,
																								pPixelArray,
																								pOffset,
																								pScan);

		mMemoryImageSource.setAnimated(true);

		mPixelOffscreen = Toolkit	.getDefaultToolkit()
															.createImage(mMemoryImageSource);
	}

	public void updateAllPixels()
	{
		mMemoryImageSource.newPixels();
	}

	public void updatePixelArea(final int pX,
															final int pY,
															final int pW,
															final int pH)
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
		// return (Graphics2D) mFrame.getGraphics();
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
		if (!mTimerStarted || mFrameCounter > mMaxFramesForFrameRate)
		{
			mTimerStarted = true;
			mInitialTime = System.currentTimeMillis();
			mFrameCounter = 1;
		}
		else
		{
			mFrameCounter++;
			final long lCurrentTime = System.currentTimeMillis();
			final long lDifference = lCurrentTime - mInitialTime;
			final long lDeltaInSeconds = lDifference / 1000;
			if (lDeltaInSeconds != 0)
			{
				mFrameRate = mFrameCounter / lDeltaInSeconds;

				if (mDisplayFramerate)
				{
					final Graphics2D lGraphics = getDrawGraphics();
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

	public void setDisplayFramerate(final boolean pDisplayFramerate)
	{
		mDisplayFramerate = pDisplayFramerate;
	}

	public void startMouseEventListeners()
	{
		mMouseInfo = new MyMouseInfo();

		mMouseListenerAdapter = new MouseAdapter()
		{

			@Override
			public void mousePressed(final MouseEvent pMouseEvent)
			{
				mMouseInfo.mMouseLeft = SwingUtilities.isLeftMouseButton(pMouseEvent);
				mMouseInfo.mMouseMiddle = SwingUtilities.isMiddleMouseButton(pMouseEvent);
				mMouseInfo.mMouseRight = SwingUtilities.isRightMouseButton(pMouseEvent);
				if (mOrionGraphicsMouseListener != null)
				{
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
			}

			@Override
			public void mouseReleased(final MouseEvent pMouseEvent)
			{
				if (SwingUtilities.isLeftMouseButton(pMouseEvent))
				{
					mMouseInfo.mMouseLeft = false;
				}
				if (SwingUtilities.isMiddleMouseButton(pMouseEvent))
				{
					mMouseInfo.mMouseMiddle = false;
				}
				if (SwingUtilities.isRightMouseButton(pMouseEvent))
				{
					mMouseInfo.mMouseRight = false;
				}
			}

			@Override
			public void mouseClicked(final MouseEvent pMouseEvent)
			{
				if (mOrionGraphicsMouseListener != null)
				{
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

			@Override
			public void mouseDragged(final MouseEvent pMouseEvent)
			{
				mMouseInfo.mMouseX = pMouseEvent.getX();
				mMouseInfo.mMouseY = pMouseEvent.getY();
				if (mOrionGraphicsMouseListener != null)
				{
					mOrionGraphicsMouseListener.onMove(mMouseInfo);
				}
			}

			@Override
			public void mouseMoved(final MouseEvent pMouseEvent)
			{
				mMouseInfo.mMouseX = pMouseEvent.getX();
				mMouseInfo.mMouseY = pMouseEvent.getY();
				if (mOrionGraphicsMouseListener != null)
				{
					mOrionGraphicsMouseListener.onMove(mMouseInfo);
				}
			}
		};
		mFrame.addMouseMotionListener(mMouseMotionListenerAdapter);

		mMouseWheelListenerAdapter = new MouseWheelListener()
		{
			public void mouseWheelMoved(final MouseWheelEvent pE)
			{
				mMouseInfo.mMouseDeltaZ = pE.getWheelRotation();
				if (mOrionGraphicsMouseListener != null)
				{
					mOrionGraphicsMouseListener.onWheel(mMouseInfo);
				}
			}
		};

		mFrame.addMouseWheelListener(mMouseWheelListenerAdapter);

		mKeyListenerAdapter = new KeyAdapter()
		{
			@Override
			public void keyTyped(final KeyEvent pE)
			{
				super.keyTyped(pE);
				if (mOrionGraphicsMouseListener != null)
				{
					mOrionGraphicsMouseListener.onKeyTyped(pE);
				}
			}

			@Override
			public void keyPressed(final KeyEvent pE)
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
	}

	public void setMouseListener(final IOrionGraphicsMouseListener pOrionGraphicsMouseListener)
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

	public void setNumberOfBuffers(final int pNumberOfBuffers)
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

	public void setMaxFramesForFrameRate(final int pMaxFramesForFrameRate)
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

	public void setIconImage(final String pIconFileName)
	{
		mIconFileName = pIconFileName;
	}

	public void doSetIconImage()
	{
		if (mIconFileName != null)
		{
			final Toolkit lToolkit = Toolkit.getDefaultToolkit();
			final URL lURL = ClassLoader.getSystemResource(mIconFileName);
			if (lURL == null)
			{
				throw new NullPointerException("lURL == null");
			}
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

	public Component getComponent()
	{
		return mFrame;
	}

	public void dispose()
	{
		mFrame.dispose();		
	}

	public boolean isDecorated()
	{
		return !mFrame.isUndecorated();
	}

}