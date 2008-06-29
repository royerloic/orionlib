package utils.graphics;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.image.VolatileImage;

public class BufferingFrame extends Frame implements GraphicsProvider
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1787963798303348822L;

	private VolatileImage mImage;
	private volatile boolean painting;

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
		//paint(g);
	}

	@Override
	public void paint(final Graphics pGraphics)
	{
		/*synchronized (this)
		{
			painting = true;
			mypaint(pGraphics);
			painting = false;
		}/**/
	}


	public void mypaint(final Graphics pGraphics)
	{
		if (!checkOffscreenImage())
			pGraphics.drawImage(mImage, 0, 0, null);
	}

	// True if the image has changed size
	private boolean checkOffscreenImage()
	{
		final Dimension d = getSize();
		if (mImage == null || mImage.getWidth(null) != d.width
				|| mImage.getHeight(null) != d.height)
		{
			mImage = createVolatileImage(d.width, d.height);

			return true;
		}
		else if (mImage.contentsLost())
		{
			mImage.validate(getGraphicsConfiguration());
		}

		return false;
	}

	public Graphics2D getDrawGraphics()
	{
		final Dimension d = getSize();
		if (checkOffscreenImage())
		{
			// It's changed size: must actually redraw it.
			final Graphics offG = mImage.getGraphics();
			offG.setColor(getBackground());
			offG.fillRect(0, 0, d.width, d.height);
		}

		Graphics2D lGraphics = (Graphics2D) mImage.getGraphics();
		return lGraphics;
	}

	public void showGraphics()
	{
		if (!painting)
			mypaint(getGraphics());
	}

	public Frame getFrame()
	{
		return this;
	}

	public Component getComponent()
	{
		return this;
	}

	public boolean isDecorated()
	{
		return !super.isUndecorated();
	}
}