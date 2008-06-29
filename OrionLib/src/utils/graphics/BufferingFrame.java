package utils.graphics;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;
import java.awt.image.VolatileImage;

public class BufferingFrame extends Frame implements GraphicsProvider
{

	private static final long serialVersionUID = 1L;
	private Graphics2D mDrawGraphics;

	public BufferingFrame(String pTitle)
	{
		super(pTitle);
		this.setSize(800, 600);
		this.setVisible(true);
		this.createBufferStrategy(2);
	}

	public boolean isDecorated()
	{
		return !super.isUndecorated();
	}

	public Component getComponent()
	{
		return this;
	}

	public Graphics2D getDrawGraphics()
	{
		BufferStrategy lBufferStrategy = this.getBufferStrategy();
		mDrawGraphics = (Graphics2D) lBufferStrategy.getDrawGraphics();
		return mDrawGraphics;
	}

	public void showGraphics()
	{
		BufferStrategy lBufferStrategy = this.getBufferStrategy();
		lBufferStrategy.show();		
		Toolkit.getDefaultToolkit().sync();	
		mDrawGraphics.dispose();
	}
}