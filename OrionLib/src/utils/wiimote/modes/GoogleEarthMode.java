package utils.wiimote.modes;

import java.awt.AWTException;
import java.awt.CheckboxMenuItem;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashSet;

import wiiremotej.WiiRemote;
import wiiremotej.event.WRAccelerationEvent;
import wiiremotej.event.WRButtonEvent;
import wiiremotej.event.WiiRemoteAdapter;

public class GoogleEarthMode extends WiiRemoteAdapter implements WiiMode

{
	CheckboxMenuItem mGoogleEarthModeItem = new CheckboxMenuItem(	"GoogleEarth Mode",
																																false);

	static Robot mRobot;

	public GoogleEarthMode() throws AWTException
	{
		super();
		mRobot = new Robot();
		mRobot.setAutoWaitForIdle(true);
		mRobot.setAutoDelay(5);
	}

	public CheckboxMenuItem getMenuItem()
	{
		return mGoogleEarthModeItem;
	}

	public void activate(WiiRemote pRemote)
	{
		try
		{
			pRemote.addWiiRemoteListener(this);
			pRemote.setAccelerometerEnabled(true);
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
			pRemote.setAccelerometerEnabled(false);
		}
		catch (Throwable e)
		{
			e.printStackTrace();
		}
	}

	double xcounter = 0;
	double zcounter = 0;

	HashSet<Integer> mVerticalEventSet = new HashSet<Integer>();
	HashSet<Integer> mHoryzontalEventSet = new HashSet<Integer>();

	double xdd=0;
	double zdd=0;
	
	public void accelerationInputReceived(WRAccelerationEvent pEvent)
	{
		final double epsilon = 0.15;
		final double zddoffset = 0.2;
		xdd = xdd*0.2 + pEvent.getXAcceleration()*0.8;
		zdd = zdd*0.2 + (pEvent.getZAcceleration() - zddoffset) *0.8;
		
		System.out.println("xdd=" + xdd);
		System.out.println("zdd=" + zdd);

		if (zdd > epsilon)
		{
			if (!mVerticalEventSet.contains(KeyEvent.VK_UP))
			{
				mRobot.keyPress(KeyEvent.VK_UP);
				mVerticalEventSet.add(KeyEvent.VK_UP);
			}
		}
		else if (zdd < -epsilon )
		{
			if (!mVerticalEventSet.contains(KeyEvent.VK_DOWN))
			{
				mRobot.keyPress(KeyEvent.VK_DOWN);
				mVerticalEventSet.add(KeyEvent.VK_DOWN);
			}
		}
		else if(Math.abs(zdd)<epsilon)
		{
			for (Integer key : mVerticalEventSet)
			{
				mRobot.keyRelease(key);
			}
			mVerticalEventSet.clear();
		}
		
		if (xdd > epsilon)
		{
			if (!mHoryzontalEventSet.contains(KeyEvent.VK_RIGHT))
			{
				mRobot.keyPress(KeyEvent.VK_RIGHT);
				mHoryzontalEventSet.add(KeyEvent.VK_RIGHT);
			}
		}
		else if (xdd < -epsilon )
		{
			if (!mHoryzontalEventSet.contains(KeyEvent.VK_LEFT))
			{
				mRobot.keyPress(KeyEvent.VK_LEFT);
				mHoryzontalEventSet.add(KeyEvent.VK_LEFT);
			}
		}
		else if(Math.abs(xdd)<epsilon)
		{
			for (Integer key : mHoryzontalEventSet)
			{
				mRobot.keyRelease(key);
			}
			mHoryzontalEventSet.clear();
		}

		/*if (xdd > epsilon)
		{
			xcounter += xdd;
			if (xcounter > 1)
			{
				mRobot.keyPress(KeyEvent.VK_RIGHT);
				//if (xcounter < 2)					mRobot.keyRelease(KeyEvent.VK_RIGHT);
				xcounter--;
			}
		}
		else if (xdd < -epsilon)
		{
			xcounter += xdd;
			if (xcounter < -1)
			{
				mRobot.keyPress(KeyEvent.VK_LEFT);
				//if (xcounter > -2)					mRobot.keyRelease(KeyEvent.VK_LEFT);
				xcounter++;
			}
		}

		/***************************************************************************
		 * if (Math.abs(zdd)<epsilon) { for(Integer lInteger :
		 * mHoryzontalEventList) { mRobot.keyRelease(lInteger); }
		 * mHoryzontalEventList.clear(); } else if (xdd>epsilon) {
		 * mRobot.keyPress(KeyEvent.VK_RIGHT);
		 * mHoryzontalEventList.add(KeyEvent.VK_RIGHT); } else if (xdd<epsilon) {
		 * mRobot.keyPress(KeyEvent.VK_LEFT);
		 * mHoryzontalEventList.add(KeyEvent.VK_LEFT); } /
		 **************************************************************************/
	}

	public void buttonInputReceived(WRButtonEvent pEvent)
	{
		/***************************************************************************
		 * if (!isDownPressed && (pEvent.wasPressed(WRButtonEvent.DOWN) ||
		 * pEvent.wasPressed(WRButtonEvent.A))) { isDownPressed = true;
		 * mRobot.keyPress(KeyEvent.VK_DOWN); } else if (!isUpPressed &&
		 * (pEvent.wasPressed(WRButtonEvent.UP) ||
		 * pEvent.wasPressed(WRButtonEvent.B))) { isUpPressed = true;
		 * mRobot.keyPress(KeyEvent.VK_UP); } else if (isDownPressed &&
		 * (pEvent.wasReleased(WRButtonEvent.DOWN) ||
		 * pEvent.wasReleased(WRButtonEvent.A))) { isDownPressed = false;
		 * mRobot.keyRelease(KeyEvent.VK_DOWN); } else if (isUpPressed &&
		 * (pEvent.wasReleased(WRButtonEvent.UP) ||
		 * pEvent.wasReleased(WRButtonEvent.B))) { isUpPressed = false;
		 * mRobot.keyRelease(KeyEvent.VK_UP); } /
		 **************************************************************************/
	}

}
