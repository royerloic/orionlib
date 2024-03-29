// / import classes
package utils.graphics.impl;

import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.Graphics;
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

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public abstract class OrionGraphicsOld extends JFrame	implements
																											Runnable,
																											ImageProducer,
																											MouseListener,
																											MouseMotionListener,
																											KeyListener
{

	// data
	private GraphicsDevice mGraphicsDevice;

	private final boolean mFullScreen = true;

	private int mWidth;

	private int mHeight;

	private Image mImage;

	private Thread mThread;

	private Graphics mGraphics;

	private ImageConsumer mImageConsumer;

	private DirectColorModel mColorModel;

	private int mScreenIndex;

	protected volatile int mMouseX;

	protected volatile int mMouseY;

	protected boolean mMouseLeft;

	protected boolean mMouseMiddle;

	protected boolean mMouseRight;

	private static final DisplayMode[] BEST_DISPLAY_MODES = new DisplayMode[]
	{ new DisplayMode(400, 300, 32, 0),
		new DisplayMode(512, 384, 32, 0),
		new DisplayMode(640, 400, 32, 0),
		new DisplayMode(640, 480, 32, 0) };

	public static final int cLAST_DEVICE = -1;

	private static DisplayMode getBestDisplayMode(final GraphicsDevice pGraphicsDevice)
	{
		for (final DisplayMode element : BEST_DISPLAY_MODES)
		{
			final DisplayMode[] modes = pGraphicsDevice.getDisplayModes();
			for (final DisplayMode element0 : modes)
			{
				if (element0.getWidth() == element.getWidth() && element0.getHeight() == element.getHeight()
						&& element0.getBitDepth() == element.getBitDepth())
				{
					return element;
				}
			}
		}
		return null;
	}

	public void activateBestDisplayMode(final GraphicsDevice pGraphicsDevice)
	{
		final DisplayMode lBestDisplayMode = getBestDisplayMode(pGraphicsDevice);
		if (lBestDisplayMode != null)
		{
			pGraphicsDevice.setDisplayMode(lBestDisplayMode);
		}

	}

	public abstract void main(final int width, final int height);

	public synchronized void update(final Object pixels)
	{
		// check consumer
		if (mImageConsumer != null)
		{
			// copy integer pixel data to image consumer
			mImageConsumer.setPixels(	0,
																0,
																mWidth,
																mHeight,
																mColorModel,
																(int[]) pixels,
																0,
																mWidth);

			done();
		}

	}

	public synchronized void done()
	{
		// notify image consumer that the frame is done
		mImageConsumer.imageComplete(ImageConsumer.SINGLEFRAMEDONE);
	}

	public void start()
	{
		// check thread
		if (mThread == null)
		{
			// create thread
			mThread = new Thread(this);

			// start thread
			mThread.start();
		}

	}

	synchronized void setFullscreen()
	{
		if (isDisplayable())
		{
			dispose();
		}

		setVisible(false);

		setUndecorated(true);
		setIgnoreRepaint(true);
		setResizable(false);
		setFocusable(true);

		mGraphicsDevice.setFullScreenWindow(this);

		activateBestDisplayMode(mGraphicsDevice);

		validate();

		addNotify();

		getBufferStrategy().getDrawGraphics().dispose();
		createBufferStrategy(2);

		setVisible(true);

		toFront();

	}

	synchronized void setWindowed()
	{
		if (isDisplayable())
		{
			dispose();
		}

		setVisible(false);

		setUndecorated(false);
		setIgnoreRepaint(false);
		setResizable(false);

		mGraphicsDevice.setFullScreenWindow(null);

		final DisplayMode lBestDisplayMode = getBestDisplayMode(mGraphicsDevice);
		mHeight = lBestDisplayMode.getHeight();
		mWidth = lBestDisplayMode.getWidth();

		setSize(mWidth, mHeight);

		validate();

		addNotify();

		getBufferStrategy().getDrawGraphics().dispose();
		createBufferStrategy(2);

		setVisible(true);

		toFront();

	}

	public void run()
	{
		final GraphicsEnvironment lGraphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
		final GraphicsDevice[] lAllGraphicsDevices = lGraphicsEnvironment.getScreenDevices();
		if (mScreenIndex == cLAST_DEVICE)
		{
			mScreenIndex = lAllGraphicsDevices.length - 1;
		}

		mGraphicsDevice = lAllGraphicsDevices[mScreenIndex];

		removeAll();
		addMouseMotionListener(this);
		addMouseListener(this);
		addKeyListener(this);

		try
		{
			if (mFullScreen)
			{
				setFullscreen();
			}
			else
			{
				setWindowed();
			}

			// get component size
			final Dimension lComponentSize = getSize();

			// setup data
			mWidth = lComponentSize.width;
			mHeight = lComponentSize.height;

			// setup color model
			mColorModel = new DirectColorModel(	32,
																					0x00FF0000,
																					0x000FF00,
																					0x000000FF,
																					0);

			// create image using default toolkit
			mImage = Toolkit.getDefaultToolkit().createImage(this);

			// call user main
			main(mWidth, mHeight);
		}
		catch (final Exception lExeption)
		{
			lExeption.printStackTrace();
		}
		finally
		{
			mGraphicsDevice.setFullScreenWindow(null);
		}
	}

	public void stop()
	{
		// check thread is valid and alive
		if (mThread != null && mThread.isAlive())
		{
			// stop thread
			mThread.stop();
		}

		// null thread
		mThread = null;
	}

	public synchronized void paint()
	{

		// get component graphics object
		mGraphics = getBufferStrategy().getDrawGraphics();
		// draw image to graphics context
		mGraphics.drawImage(mImage, 0, 0, mWidth, mHeight, null);

		// getGraphics().drawImage(backBuffer, 0, 0, mWidth, mHeight,
		// null);
	}

	public synchronized void addConsumer(final ImageConsumer ic)
	{
		// register image consumer
		mImageConsumer = ic;

		// set image dimensions
		mImageConsumer.setDimensions(mWidth, mHeight);

		// set image consumer hints for speed
		mImageConsumer.setHints(ImageConsumer.TOPDOWNLEFTRIGHT | ImageConsumer.COMPLETESCANLINES
														| ImageConsumer.SINGLEPASS
														| ImageConsumer.SINGLEFRAME);

		// set image color model
		mImageConsumer.setColorModel(mColorModel);
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

	public synchronized Graphics getDrawGraphics()
	{
		return getBufferStrategy().getDrawGraphics();
	}

	public synchronized void refresh()
	{
		getBufferStrategy().show();
	}

	/**
	 * @param pScreenIndex
	 * @throws java.awt.HeadlessException
	 */
	public OrionGraphicsOld(final int pScreenIndex) throws HeadlessException
	{
		super();
		mScreenIndex = pScreenIndex;
	}

	/**
	 * @param pFrameTitle
	 * @param pScreenIndex
	 * @throws java.awt.HeadlessException
	 */
	public OrionGraphicsOld(final String pFrameTitle, final int pScreenIndex) throws HeadlessException
	{
		super(pFrameTitle);
		// TODO Auto-generated constructor stub
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
		// TODO Auto-generated method stub

	}

	/**
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	public void mouseExited(final MouseEvent arg0)
	{
		// TODO Auto-generated method stub

	}

	/**
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	public void mousePressed(final MouseEvent pMouseEvent)
	{
		mMouseLeft = SwingUtilities.isLeftMouseButton(pMouseEvent);
		mMouseMiddle = SwingUtilities.isMiddleMouseButton(pMouseEvent);
		mMouseRight = SwingUtilities.isRightMouseButton(pMouseEvent);

	}

	/**
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	public void mouseReleased(final MouseEvent pMouseEvent)
	{

		if (SwingUtilities.isLeftMouseButton(pMouseEvent))
		{
			mMouseLeft = false;
		}

		if (SwingUtilities.isMiddleMouseButton(pMouseEvent))
		{
			mMouseMiddle = false;
		}

		if (SwingUtilities.isRightMouseButton(pMouseEvent))
		{
			mMouseRight = false;
		}

	}

	/**
	 * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
	 */
	public void mouseDragged(final MouseEvent pMouseEvent)
	{
		mMouseX = pMouseEvent.getX();
		mMouseY = pMouseEvent.getY();

	}

	/**
	 * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
	 */
	public void mouseMoved(final MouseEvent pMouseEvent)
	{
		mMouseX = pMouseEvent.getX();
		mMouseY = pMouseEvent.getY();

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
		// TODO Auto-generated method stub

	}

	/**
	 * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
	 */
	public void keyTyped(final KeyEvent pKeyEvent)
	{
		// TODO Auto-generated method stub

	}
}

/**
 * if(mMouseMiddle) { if(mFullScreen) { System.out.println("switching to
 * Windowed"); mFullScreen = false; setWindowed(); } else {
 * System.out.println("switching to FullScreen"); mFullScreen = true;
 * setFullscreen(); } }
 * 
 */
