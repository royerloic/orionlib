package org.royerloic.graphics;

import java.awt.*;
import java.awt.event.*;

public class BufferingFrame extends Frame implements GraphicsProvider
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1787963798303348822L;
	private int		mX, mY;
	private Image	mImage;
	
	public BufferingFrame() throws HeadlessException
	{
		super();
		// TODO Auto-generated constructor stub
	}

	public BufferingFrame(GraphicsConfiguration pGc)
	{
		super(pGc);
		// TODO Auto-generated constructor stub
	}

	public BufferingFrame(String pTitle, GraphicsConfiguration pGc)
	{
		super(pTitle, pGc);
		// TODO Auto-generated constructor stub
	}

	public BufferingFrame(String pTitle) throws HeadlessException
	{
		super(pTitle);
		// TODO Auto-generated constructor stub
	}

	public void update(Graphics g)
	{
		// System.out.println("update");
		paint(g);
	}


	// Buffering by painting into an offscreen image; the
	// paintOffscreen method should be overridden.
	public void paint(Graphics g)
	{
		// System.out.println("ApplicationFrameDB:paint");
		// Clear the offscreen image.
		Dimension d = getSize();
		if (checkOffscreenImage())
		{
			// It's changed size: must actually redraw it.
			Graphics offG = mImage.getGraphics();
			offG.setColor(getBackground());
			offG.fillRect(0, 0, d.width, d.height);
			// Draw into the offscreen image.
			paintOffscreen(mImage.getGraphics());
		}
		// Put the offscreen image on the screen.
		g.drawImage(mImage, 0, 0, null);
	}

	
	public void paintOffscreen(Graphics g)
	{
		System.out.println("paintOffscreen("+g);
	}

	// True if the image has changed size
	private boolean checkOffscreenImage()
	{
		Dimension d = getSize();
		if (mImage == null || mImage.getWidth(null) != d.width || mImage.getHeight(null) != d.height)
		{
			mImage = createImage(d.width, d.height);
			return true;
		}
		return false;
	}

	public Graphics2D getDrawGraphics()
	{
		Dimension d = getSize();
		if (checkOffscreenImage())
		{
			// It's changed size: must actually redraw it.
			Graphics offG = mImage.getGraphics();
			offG.setColor(getBackground());
			offG.fillRect(0, 0, d.width, d.height);
		}
		return (Graphics2D) mImage.getGraphics();
	}

	public void showGraphics()
	{
		getGraphics().drawImage(mImage, 0, 0, null);
	}

	public Frame getFrame()
	{
		return this;
	}
}