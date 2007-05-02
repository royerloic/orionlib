package org.royerloic.graphics;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.Image;

public class BufferingFrame extends Frame implements GraphicsProvider
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1787963798303348822L;
	private Image	mImage;
	
	public BufferingFrame() throws HeadlessException
	{
		super();
		// TODO Auto-generated constructor stub
	}

	public BufferingFrame(final GraphicsConfiguration pGc)
	{
		super(pGc);
		// TODO Auto-generated constructor stub
	}

	public BufferingFrame(final String pTitle, final GraphicsConfiguration pGc)
	{
		super(pTitle, pGc);
		// TODO Auto-generated constructor stub
	}

	public BufferingFrame(final String pTitle) throws HeadlessException
	{
		super(pTitle);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void update(final Graphics g)
	{
		// System.out.println("update");
		paint(g);
	}


	// Buffering by painting into an offscreen image; the
	// paintOffscreen method should be overridden.
	@Override
	public void paint(final Graphics g)
	{
		// System.out.println("ApplicationFrameDB:paint");
		// Clear the offscreen image.
		final Dimension d = getSize();
		if (checkOffscreenImage())
		{
			// It's changed size: must actually redraw it.
			final Graphics offG = this.mImage.getGraphics();
			offG.setColor(getBackground());
			offG.fillRect(0, 0, d.width, d.height);
			// Draw into the offscreen image.
			paintOffscreen(this.mImage.getGraphics());
		}
		// Put the offscreen image on the screen.
		g.drawImage(this.mImage, 0, 0, null);
	}

	
	public void paintOffscreen(final Graphics g)
	{
		System.out.println("paintOffscreen("+g);
	}

	// True if the image has changed size
	private boolean checkOffscreenImage()
	{
		final Dimension d = getSize();
		if ((this.mImage == null) || (this.mImage.getWidth(null) != d.width) || (this.mImage.getHeight(null) != d.height))
		{
			this.mImage = createImage(d.width, d.height);
			return true;
		}
		return false;
	}

	public Graphics2D getDrawGraphics()
	{
		final Dimension d = getSize();
		if (checkOffscreenImage())
		{
			// It's changed size: must actually redraw it.
			final Graphics offG = this.mImage.getGraphics();
			offG.setColor(getBackground());
			offG.fillRect(0, 0, d.width, d.height);
		}
		return (Graphics2D) this.mImage.getGraphics();
	}

	public void showGraphics()
	{
		getGraphics().drawImage(this.mImage, 0, 0, null);
	}

	public Frame getFrame()
	{
		return this;
	}
}