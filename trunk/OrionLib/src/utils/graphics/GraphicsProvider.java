package utils.graphics;

import java.awt.Component;
import java.awt.Frame;
import java.awt.Graphics2D;

public interface GraphicsProvider
{
	public Frame getFrame();
	public int getHeight();
	public int getWidth();
	public Component getComponent();
	public void showGraphics();
	public Graphics2D getDrawGraphics();
}
