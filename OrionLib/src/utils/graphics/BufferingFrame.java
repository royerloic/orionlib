package utils.graphics;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;

public class BufferingFrame extends JFrame implements GraphicsProvider
{

	private static final long serialVersionUID = 1L;
	private Graphics2D mDrawGraphics;
	private BufferStrategy mBufferStrategy;

	public BufferingFrame(String pTitle)
	{
		super(pTitle);
		this.setSize(800, 600);
		this.setVisible(true);

		this.setIgnoreRepaint(true);
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsConfiguration gc = ge	.getDefaultScreenDevice()
																	.getDefaultConfiguration();

		this.createBufferStrategy(2);
		mBufferStrategy = this.getBufferStrategy();
		System.out.println("getBackBufferCapabilities().isAccelerated()" + String.valueOf(mBufferStrategy	.getCapabilities()
																																																			.getBackBufferCapabilities()
																																																			.isAccelerated()));
		System.out.println("getFrontBufferCapabilities().isAccelerated()" + String.valueOf(mBufferStrategy.getCapabilities()
																																																			.getFrontBufferCapabilities()
																																																			.isAccelerated()));
		System.out.println("getBackBufferCapabilities().isTrueVolatile()" + String.valueOf(mBufferStrategy.getCapabilities()
																																																			.getBackBufferCapabilities()
																																																			.isTrueVolatile()));
		System.out.println("getFrontBufferCapabilities().isTrueVolatile()" + String.valueOf(mBufferStrategy	.getCapabilities()
																																																				.getFrontBufferCapabilities()
																																																				.isTrueVolatile()));

	}

	public boolean isDecorated()
	{
		return !super.isUndecorated();
	}

	public Component getComponent()
	{
		return this;
	}

	private void validate(java.lang.IllegalStateException e)
	{
		System.out.println("Switched screen, buffer configuration not valid anymore:");
		System.out.println(e.getMessage());
		this.createBufferStrategy(2);
		mBufferStrategy = this.getBufferStrategy();
	}

	public Graphics2D getDrawGraphics()
	{
		mDrawGraphics = null;
		try
		{
			mDrawGraphics = (Graphics2D) mBufferStrategy.getDrawGraphics();
		}
		catch (java.lang.IllegalStateException e)
		{
			validate(e);
		}
		return mDrawGraphics;
	}

	public void showGraphics()
	{
		try
		{
			mBufferStrategy.show();
			Toolkit.getDefaultToolkit().sync();
		}
		catch (java.lang.IllegalStateException e)
		{
			validate(e);
		}
		finally
		{
			mDrawGraphics.dispose();
		}

	}
}