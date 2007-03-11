package org.royerloic.graphics;

import java.awt.Frame;
import java.awt.*;
import java.awt.event.*;

public class BufferingFrame extends Frame
{
	public void update(Graphics g)
	{
		// System.out.println("update");
		paint(g);
	}

	private int		mX, mY;
	private Image	mImage;

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

	// Override this, not the paint method
	public void paintOffscreen(Graphics g)
	{
		System.out.println("Override me");
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
}