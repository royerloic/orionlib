// / import classes
package org.royerloic.graphics.impl;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.DirectColorModel;
import java.awt.image.ImageConsumer;
import java.awt.image.ImageProducer;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class OrionGraphics extends JFrame implements ImageProducer, MouseListener, MouseMotionListener,
		KeyListener
{

	// Constants:
	private static final long	serialVersionUID	= 1L;

	public static final int		cLAST_DEVICE			= -1;

	public static final int		cFIRST_DEVICE			= 0;

	// Fields
	private GraphicsDevice		mGraphicsDevice;

	private boolean						mFullScreen				= true;

	private int								mWidth;

	private int								mHeight;

	private Image							mImage;

	private Graphics					mGraphics;

	private ImageConsumer			mImageConsumer;

	private DirectColorModel	mColorModel;

	private List<DisplayMode>	mDisplayModeWishList;

	private int								mScreenIndex;

	public volatile int				mMouseX;

	public volatile int				mMouseY;

	public boolean						mMouseLeft;

	public boolean						mMouseMiddle;

	public boolean						mMouseRight;

	private boolean						mTimerStarted;

	private long							mInitialTime;

	private int								mFrameCounter;

	// methods:

	/**
	 * @param pFrameTitle
	 * @param pScreenIndex
	 * @throws java.awt.HeadlessException
	 */
	public OrionGraphics(final String pFrameTitle, final int pScreenIndex) throws HeadlessException
	{
		super(pFrameTitle);
		System.setProperty("sun.java2d.translaccel", "true");
		System.setProperty("sun.java2d.accthreshold", "0");
		System.setProperty("sun.java2d.ddscale", "true");
		System.setProperty("sun.java2d.ddforcevram", "true");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.mScreenIndex = pScreenIndex;
		this.mDisplayModeWishList = new ArrayList<DisplayMode>();
	}

	/**
	 * @param pFrameTitle
	 * @throws java.awt.HeadlessException
	 */
	public OrionGraphics(final String pFrameTitle) throws HeadlessException
	{
		super(pFrameTitle);
		this.mDisplayModeWishList = new ArrayList<DisplayMode>();
		this.mFullScreen = false;
	}

	/**
	 */
	public void clearModeWishList()
	{
		this.mDisplayModeWishList.clear();
	}

	/**
	 * @param pDisplayMode
	 */
	public void addModeInWishList(final DisplayMode pDisplayMode)
	{
		this.mDisplayModeWishList.add(pDisplayMode);
	}

	private DisplayMode getBestDisplayMode(final GraphicsDevice pGraphicsDevice)
	{

		for (int x = 0; x < this.mDisplayModeWishList.size(); x++)
		{
			final DisplayMode[] modes = pGraphicsDevice.getDisplayModes();
			for (DisplayMode element : modes)
				if ((element.getWidth() == (this.mDisplayModeWishList.get(x)).getWidth())
						&& (element.getHeight() == (this.mDisplayModeWishList.get(x)).getHeight())
						&& (element.getBitDepth() == (this.mDisplayModeWishList.get(x)).getBitDepth()))
					return (this.mDisplayModeWishList.get(x));
		}
		return null;
	}

	public boolean activateBestDisplayMode(final GraphicsDevice pGraphicsDevice)
	{
		boolean lResult = false;
		final DisplayMode lBestDisplayMode = getBestDisplayMode(pGraphicsDevice);
		if (lBestDisplayMode != null)
		{
			pGraphicsDevice.setDisplayMode(lBestDisplayMode);
			lResult = true;
		}
		return lResult;
	}

	synchronized void setFullscreen()
	{
		if (isDisplayable())
			dispose();

		setVisible(false);

		setUndecorated(true);
		setIgnoreRepaint(true);
		setResizable(false);
		setFocusable(true);

		this.mGraphicsDevice.setFullScreenWindow(this);

		activateBestDisplayMode(this.mGraphicsDevice);

		validate();

		addNotify();

		// getBufferStrategy().getDrawGraphics().dispose();
		createBufferStrategy(2);
		// System.out.println(getBufferStrategy().getCapabilities().isPageFlipping());

		// show();

		setVisible(true);

		toFront();

	}

	synchronized void setWindowed()
	{
		if (isDisplayable())
			dispose();

		setVisible(false);

		setUndecorated(false);
		setIgnoreRepaint(false);
		setResizable(false);

		this.mGraphicsDevice.setFullScreenWindow(null);

		final DisplayMode lBestDisplayMode = getBestDisplayMode(this.mGraphicsDevice);
		this.mHeight = lBestDisplayMode.getHeight();
		this.mWidth = lBestDisplayMode.getWidth();

		setSize(this.mWidth, this.mHeight);

		validate();

		addNotify();

		getBufferStrategy().getDrawGraphics().dispose();
		createBufferStrategy(2);

		// show();

		setVisible(true);

		toFront();

	}

	/**
	 * @see org.royerloic.graphics.IOrionGraphics#startGraphics()
	 */
	public void startGraphics()
	{
		final GraphicsEnvironment lGraphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
		final GraphicsDevice[] lAllGraphicsDevices = lGraphicsEnvironment.getScreenDevices();

		if (this.mScreenIndex == cLAST_DEVICE)
			this.mScreenIndex = lAllGraphicsDevices.length - 1;

		this.mGraphicsDevice = lAllGraphicsDevices[this.mScreenIndex];

		removeAll();
		addMouseMotionListener(this);
		addMouseListener(this);
		addKeyListener(this);

		try
		{
			if (this.mFullScreen && (getBestDisplayMode(this.mGraphicsDevice) != null))
				setFullscreen();
			else
				setWindowed();

			// get component size
			final Dimension lComponentSize = getSize();

			// setup data
			this.mWidth = lComponentSize.width;
			this.mHeight = lComponentSize.height;

			// setup color model
			this.mColorModel = new DirectColorModel(32, 0x00FF0000, 0x0000FF00, 0x000000FF, 0);

			// create image using default toolkit
			this.mImage = Toolkit.getDefaultToolkit().createImage(this);

		}
		catch (final Exception lExeption)
		{
			lExeption.printStackTrace();
		}
	}

	public synchronized void update(final Object pixels)
	{
		// check consumer
		if (this.mImageConsumer != null)
		{
			// copy integer pixel data to image consumer
			this.mImageConsumer.setPixels(0, 0, this.mWidth, this.mHeight, this.mColorModel, (int[]) pixels, 0, this.mWidth);

			// done(); notify image consumer that the frame is done
			this.mImageConsumer.imageComplete(ImageConsumer.SINGLEFRAMEDONE);
		}
	}

	/**
	 * @see org.royerloic.graphics.IOrionGraphics#paintPixels()
	 */
	public synchronized void paintPixels()
	{
		// get component graphics object
		this.mGraphics = getBufferStrategy().getDrawGraphics();
		// draw image to graphics context
		this.mGraphics.drawImage(this.mImage, 0, 0, this.mWidth, this.mHeight, null);
	}

	/**
	 */
	public synchronized void refresh()
	{
		if (!this.mTimerStarted)
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
				final long lFrameRate = (this.mFrameCounter) / (lDeltaInSeconds);

				final Graphics2D lGraphics = getDrawGraphics();
				lGraphics.setColor(Color.BLACK);
				lGraphics.fillRect(0, 0, 150, 15);
				lGraphics.setColor(Color.WHITE);
				lGraphics.drawString("Images per second: " + lFrameRate, 10, 10);
				lGraphics.dispose();
			}
		}

		// show buffer
		getBufferStrategy().show();
	}

	/**
	 * @see org.royerloic.graphics.IOrionGraphics#stopGraphics()
	 */
	public synchronized void stopGraphics()
	{
		this.mGraphicsDevice.setFullScreenWindow(null);
		// hide();
		// setVisible(false);
	}

	/**
	 * @return Graphics2D that can be used to draw.
	 * @see org.royerloic.graphics.IOrionGraphics#getDrawGraphics()
	 */
	public synchronized Graphics2D getDrawGraphics()
	{
		return (Graphics2D) getBufferStrategy().getDrawGraphics();
	}

	public synchronized void addConsumer(final ImageConsumer ic)
	{
		// register image consumer
		this.mImageConsumer = ic;

		// set image dimensions
		this.mImageConsumer.setDimensions(this.mWidth, this.mHeight);

		// set image consumer hints for speed
		this.mImageConsumer.setHints(ImageConsumer.TOPDOWNLEFTRIGHT | ImageConsumer.COMPLETESCANLINES
				| ImageConsumer.SINGLEPASS | ImageConsumer.SINGLEFRAME);

		// set image color model
		this.mImageConsumer.setColorModel(this.mColorModel);
	}

	public synchronized boolean isConsumer(final ImageConsumer ic)
	{
		// check if consumer is registered
		return true;
	}

	public synchronized void removeConsumer(final ImageConsumer ic)
	{
		// remove image consumer
	}

	public void startProduction(final ImageConsumer ic)
	{
		// add consumer
		addConsumer(ic);
	}

	public void requestTopDownLeftRightResend(final ImageConsumer ic)
	{
		// ignore resend request
	}

	/**
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	public void mouseClicked(final MouseEvent pMouseEvent)
	{
	}

	/**
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	public void mouseEntered(final MouseEvent arg0)
	{
	}

	/**
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	public void mouseExited(final MouseEvent arg0)
	{
	}

	/**
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	public void mousePressed(final MouseEvent pMouseEvent)
	{
		this.mMouseLeft = SwingUtilities.isLeftMouseButton(pMouseEvent);
		this.mMouseMiddle = SwingUtilities.isMiddleMouseButton(pMouseEvent);
		this.mMouseRight = SwingUtilities.isRightMouseButton(pMouseEvent);
	}

	/**
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	public void mouseReleased(final MouseEvent pMouseEvent)
	{
		if (SwingUtilities.isLeftMouseButton(pMouseEvent))
			this.mMouseLeft = false;
		if (SwingUtilities.isMiddleMouseButton(pMouseEvent))
			this.mMouseMiddle = false;
		if (SwingUtilities.isRightMouseButton(pMouseEvent))
			this.mMouseRight = false;
	}

	/**
	 * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
	 */
	public void mouseDragged(final MouseEvent pMouseEvent)
	{
		this.mMouseX = pMouseEvent.getX();
		this.mMouseY = pMouseEvent.getY();
	}

	/**
	 * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
	 */
	public void mouseMoved(final MouseEvent pMouseEvent)
	{
		this.mMouseX = pMouseEvent.getX();
		this.mMouseY = pMouseEvent.getY();
	}

	/**
	 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
	 */
	public void keyPressed(final KeyEvent pKeyEvent)
	{
		if (pKeyEvent.getKeyCode() == (InputEvent.ALT_MASK | KeyEvent.VK_ENTER))
		{
		}
	}

	/**
	 * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
	 */
	public void keyReleased(final KeyEvent pKeyEvent)
	{
	}

	/**
	 * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
	 */
	public void keyTyped(final KeyEvent pKeyEvent)
	{
	}

	/**
	 * @return Returns the fullScreen.
	 */
	public boolean isFullScreen()
	{
		return this.mFullScreen;
	}

	/**
	 * @param pFullScreen
	 *          The fullScreen to set.
	 */
	public void setFullScreen(final boolean pFullScreen)
	{
		this.mFullScreen = pFullScreen;
	}
}
