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
import wiiremotej.event.WRAccelerationEvent;
import wiiremotej.event.WRButtonEvent;
import wiiremotej.event.WRIREvent;
import wiiremotej.event.WiiRemoteAdapter;

public class MouseMode  extends WiiRemoteAdapter implements WiiMode
{
	CheckboxMenuItem mMouseModeItem = new CheckboxMenuItem("Mouse Mode", false);

	private static double mX = 0;
	private static double mY = 0;

	private Rectangle mScreenRectangle;

	private boolean mButton1Pressed = false;
	private boolean mButton2Pressed = false;
	private boolean mButton3Pressed = false;

	static Robot mRobot;

	public MouseMode() throws AWTException
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
		return mMouseModeItem;
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
			// TODO Auto-generated catch block
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
			// TODO Auto-generated catch block
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
				/***********************************************************************
				 * System.out.println("Light: " + i); System.out.println("X: " +
				 * light.getX()); System.out.println("Y:" + light.getY());
				 * System.out.println("R:" + light.getSize());
				 **********************************************************************/

				nmX += light.getX();
				nmY += light.getY();
				i++;
			}
		}

		if (i == 2)
		{
			mX = nmX / (double) i;
			mY = nmY / (double) i;

			mX = mX * 1.10 - 0.05;
			mY = mY * 1.10 - 0.05;/**/

			final int x = (int) ((1 - mX) * mScreenRectangle.getWidth());
			final int y = (int) (mY * mScreenRectangle.getHeight());
			mRobot.mouseMove(x, y);
		}

	}

	public void accelerationInputReceived(WRAccelerationEvent pArg0)
	{
		// TODO Auto-generated method stub

	}

	public void buttonInputReceived(WRButtonEvent pWRButtonEvent)
	{
		// System.out.println(evt);
		if (!mButton1Pressed && pWRButtonEvent.isPressed(WRButtonEvent.A))
		{
			mButton1Pressed = true;
			mRobot.mousePress(InputEvent.BUTTON1_MASK);
			mRobot.waitForIdle();
		}
		else if (mButton1Pressed && pWRButtonEvent.wasReleased(WRButtonEvent.A))
		{
			mButton1Pressed = false;
			mRobot.mouseRelease(InputEvent.BUTTON1_MASK);
			mRobot.waitForIdle();
		}
		else if (!mButton3Pressed && pWRButtonEvent.isPressed(WRButtonEvent.B))
		{
			mButton3Pressed = true;
			mRobot.mousePress(InputEvent.BUTTON3_MASK);
			mRobot.waitForIdle();
		}
		else if (mButton3Pressed && pWRButtonEvent.wasReleased(WRButtonEvent.B))
		{
			mButton3Pressed = false;
			mRobot.mouseRelease(InputEvent.BUTTON3_MASK);
			mRobot.waitForIdle();
		}

	}

	

}
