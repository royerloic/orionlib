package utils.graphics;

import java.awt.Frame;
import java.awt.Graphics2D;

public interface GraphicsProvider
{
	public void showGraphics();
	public Graphics2D getDrawGraphics();
	public Frame getFrame();
	public int getHeight();
	public int getWidth();
}
