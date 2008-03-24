package utils.wiimote.modes;

import java.awt.AWTException;
import java.awt.CheckboxMenuItem;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.io.IOException;

import wiiremotej.IRLight;
import wiiremotej.WiiRemote;
import wiiremotej.event.WRIREvent;
import wiiremotej.event.WiiRemoteAdapter;


public class PenMode  extends WiiRemoteAdapter implements WiiMode
{
	CheckboxMenuItem mPenModeItem = new CheckboxMenuItem(	"Pen Mode",
																													false);
	
	private static double mX = 0;
	private static double mY = 0;

	private Rectangle mScreenRectangle;

	private boolean mButton1Pressed = false;

	static Robot mRobot;
	
	
	
	
	public PenMode() throws AWTException
	{
		super();
		mRobot = new Robot();
		final GraphicsEnvironment genv = GraphicsEnvironment.getLocalGraphicsEnvironment();
		mScreenRectangle = genv	.getDefaultScreenDevice()
														.getDefaultConfiguration()
														.getBounds();
	}


	public CheckboxMenuItem getMenuItem()
	{
		return mPenModeItem;
	}

	
	public void activate(WiiRemote pRemote)
	{
		try
		{
			pRemote.addWiiRemoteListener(this);
			pRemote.setAccelerometerEnabled(false);
			pRemote.setIRSensorEnabled(true, WRIREvent.BASIC);
		}
		catch (Throwable e)
		{
			e.printStackTrace();
		}		
	}

	
	public void deactivate(WiiRemote pRemote)
	{
		try
		{
			pRemote.removeWiiRemoteListener(this);
			pRemote.setIRSensorEnabled(false, WRIREvent.BASIC);
		}
		catch (Throwable e)
		{
			e.printStackTrace();
		}
	}
	
	public void IRInputReceived(WRIREvent pWRIREvent)
	{
		int i = 0;
		double nmX = 0;
		double nmY = 0;

		for (IRLight light : pWRIREvent.getIRLights())
		{
			if (light != null)
			{
				/*****************************************************************
				 * System.out.println("Light: " + i); System.out.println("X: " +
				 * light.getX()); System.out.println("Y:" + light.getY());
				 * System.out.println("R:" + light.getSize());
				 ****************************************************************/

				mX = light.getX();
				mY = light.getY();
				i++;
			}
		}

		if (i == 1)
		{
			final int x = (int) (mX * mScreenRectangle.getWidth());
			final int y = (int) ((1-mY) * mScreenRectangle.getHeight());
			mRobot.mouseMove(x, y);
			
			if(mButton1Pressed==false)
			{
				mRobot.mousePress(InputEvent.BUTTON1_MASK);
				mRobot.waitForIdle();
			}
			mButton1Pressed = true;
		}
		else if (i == 0)
		{
			if(mButton1Pressed)
			{
				mRobot.mouseRelease(InputEvent.BUTTON1_MASK);
				mRobot.waitForIdle();
			}
			
			mButton1Pressed = false;						
		}


	}

	

}
