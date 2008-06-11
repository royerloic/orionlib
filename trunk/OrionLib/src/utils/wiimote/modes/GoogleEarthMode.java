package utils.wiimote.modes;

import java.awt.AWTException;
import java.awt.CheckboxMenuItem;
import java.awt.Robot;
import java.awt.event.KeyEvent;
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

	public void activate(final WiiRemote pRemote)
	{
		try
		{
			pRemote.addWiiRemoteListener(this);
			pRemote.setAccelerometerEnabled(true);
		}
		catch (final Throwable e)
		{
			e.printStackTrace();
		}
	}

	public void deactivate(final WiiRemote pRemote)
	{
		try
		{
			pRemote.removeWiiRemoteListener(this);
			pRemote.setAccelerometerEnabled(false);
		}
		catch (final Throwable e)
		{
			e.printStackTrace();
		}
	}

	double xcounter = 0;
	double zcounter = 0;

	HashSet<Integer> mVerticalEventSet = new HashSet<Integer>();
	HashSet<Integer> mHoryzontalEventSet = new HashSet<Integer>();

	double xdd = 0;
	double zdd = 0;

	public void accelerationInputReceived(final WRAccelerationEvent pEvent)
	{
		final double epsilon = 0.05;
		final double zeta = 0.15;
		final double zddoffset = 0.2;
		xdd = xdd * 0.2 + pEvent.getXAcceleration() * 0.8;
		zdd = zdd * 0.2 + (pEvent.getZAcceleration() - zddoffset) * 0.8;

		System.out.println("xdd=" + xdd);
		System.out.println("zdd=" + zdd);

		if (isBpressed)
		{
			if (zdd > epsilon && Math.abs(xdd) < zeta)
			{

				zcounter += zdd;
				if (zcounter > 1)
				{
					zcounter--;
					mRobot.mouseWheel(-1);
				}
			}
			else if (zdd < -epsilon && Math.abs(xdd) < zeta)
			{

				zcounter += zdd;
				if (zcounter < -1)
				{
					zcounter++;
					mRobot.mouseWheel(+1);
				}
			}

			if (xdd > epsilon && Math.abs(zdd) < zeta)
			{

				xcounter += xdd;
				if (xcounter > 1)
				{
					xcounter--;
					mRobot.keyPress(KeyEvent.VK_CONTROL);
					mRobot.mouseWheel(-1);
					mRobot.keyRelease(KeyEvent.VK_CONTROL);
				}

			}
			else if (xdd < -epsilon && Math.abs(zdd) < zeta)
			{

				xcounter += xdd;
				if (xcounter < -1)
				{
					xcounter++;
					mRobot.keyPress(KeyEvent.VK_CONTROL);
					mRobot.mouseWheel(+1);
					mRobot.keyRelease(KeyEvent.VK_CONTROL);
				}
			}
		}

		/***************************************************************************
		 * if (xdd > epsilon) { xcounter += xdd; if (xcounter > 1) {
		 * mRobot.keyPress(KeyEvent.VK_RIGHT); //if (xcounter < 2)
		 * mRobot.keyRelease(KeyEvent.VK_RIGHT); xcounter--; } } else if (xdd <
		 * -epsilon) { xcounter += xdd; if (xcounter < -1) {
		 * mRobot.keyPress(KeyEvent.VK_LEFT); //if (xcounter > -2)
		 * mRobot.keyRelease(KeyEvent.VK_LEFT); xcounter++; } }
		 * 
		 * /***************************************************************************
		 * if (Math.abs(zdd)<epsilon) { for(Integer lInteger :
		 * mHoryzontalEventList) { mRobot.keyRelease(lInteger); }
		 * mHoryzontalEventList.clear(); } else if (xdd>epsilon) {
		 * mRobot.keyPress(KeyEvent.VK_RIGHT);
		 * mHoryzontalEventList.add(KeyEvent.VK_RIGHT); } else if (xdd<epsilon) {
		 * mRobot.keyPress(KeyEvent.VK_LEFT);
		 * mHoryzontalEventList.add(KeyEvent.VK_LEFT); } /
		 **************************************************************************/
	}

	boolean isApressed = false;
	boolean isBpressed = false;

	boolean isDownPressed = false;
	boolean isUpPressed = false;
	boolean isRightPressed = false;
	boolean isLeftPressed = false;

	boolean isMinusPressed = false;
	boolean isPlusPressed = false;

	public void buttonInputReceived(final WRButtonEvent pEvent)
	{
		isApressed = pEvent.isPressed(WRButtonEvent.A);
		isBpressed = pEvent.isPressed(WRButtonEvent.B);

		if (!isDownPressed && pEvent.wasPressed(WRButtonEvent.DOWN))
		{
			isDownPressed = true;
			mRobot.keyPress(KeyEvent.VK_DOWN);
		}
		else if (!isUpPressed && pEvent.wasPressed(WRButtonEvent.UP))
		{
			isUpPressed = true;
			mRobot.keyPress(KeyEvent.VK_UP);
		}
		else if (isDownPressed && pEvent.wasReleased(WRButtonEvent.DOWN))
		{
			isDownPressed = false;
			mRobot.keyRelease(KeyEvent.VK_DOWN);
		}
		else if (isUpPressed && pEvent.wasReleased(WRButtonEvent.UP))
		{
			isUpPressed = false;
			mRobot.keyRelease(KeyEvent.VK_UP);
		}

		if (!isRightPressed && pEvent.wasPressed(WRButtonEvent.RIGHT))
		{
			isRightPressed = true;
			mRobot.keyPress(KeyEvent.VK_RIGHT);
		}
		else if (!isLeftPressed && pEvent.wasPressed(WRButtonEvent.LEFT))
		{
			isLeftPressed = true;
			mRobot.keyPress(KeyEvent.VK_LEFT);
		}
		else if (isRightPressed && pEvent.wasReleased(WRButtonEvent.RIGHT))
		{
			isRightPressed = false;
			mRobot.keyRelease(KeyEvent.VK_RIGHT);
		}
		else if (isLeftPressed && pEvent.wasReleased(WRButtonEvent.LEFT))
		{
			isLeftPressed = false;
			mRobot.keyRelease(KeyEvent.VK_LEFT);
		}

		if (!isMinusPressed && pEvent.wasPressed(WRButtonEvent.MINUS))
		{
			isMinusPressed = true;
			mRobot.keyPress(KeyEvent.VK_PAGE_DOWN);
		}
		else if (!isPlusPressed && pEvent.wasPressed(WRButtonEvent.PLUS))
		{
			isPlusPressed = true;
			mRobot.keyPress(KeyEvent.VK_PAGE_UP);
		}
		else if (isMinusPressed && pEvent.wasReleased(WRButtonEvent.MINUS))
		{
			isMinusPressed = false;
			mRobot.keyRelease(KeyEvent.VK_PAGE_DOWN);
		}
		else if (isPlusPressed && pEvent.wasReleased(WRButtonEvent.PLUS))
		{
			isPlusPressed = false;
			mRobot.keyRelease(KeyEvent.VK_PAGE_UP);
		}

	}
}
