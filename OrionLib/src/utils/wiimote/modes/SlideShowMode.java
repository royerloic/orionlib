package utils.wiimote.modes;

import java.awt.AWTException;
import java.awt.CheckboxMenuItem;
import java.awt.Robot;
import java.awt.event.KeyEvent;

import wiiremotej.WiiRemote;
import wiiremotej.event.WRAccelerationEvent;
import wiiremotej.event.WRButtonEvent;
import wiiremotej.event.WiiRemoteAdapter;
import wiiremotej.event.WiiRemoteListener;

public class SlideShowMode extends WiiRemoteAdapter	implements
																										WiiMode,
																										WiiRemoteListener
{
	CheckboxMenuItem mSlideShowModeItem = new CheckboxMenuItem(	"SlideShow Mode",
																															false);

	static Robot mRobot;

	public SlideShowMode() throws AWTException
	{
		super();
		mRobot = new Robot();
		mRobot.setAutoWaitForIdle(true);
		mRobot.setAutoDelay(100);
	}

	public CheckboxMenuItem getMenuItem()
	{
		return mSlideShowModeItem;
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

	boolean isDownPressed = false;
	boolean isUpPressed = false;

	public void accelerationInputReceived(final WRAccelerationEvent pEvent)
	{
		final double accel = pEvent.getXAcceleration();

		final double max = 2;
		final double zero = 0.125;

		final boolean downaccelpressed = accel < -max;
		final boolean downaccelreleased = accel > max * 0.8;

		final boolean upaccelpressed = accel > max;
		final boolean upaccelreleased = accel < -max * 0.8;

		/***************************************************************************
		 * System.out.println("accel="+accel);
		 * System.out.println("downaccelpressed="+downaccelpressed);
		 * System.out.println("downaccelreleased="+downaccelreleased);
		 * System.out.println("upaccelpressed="+upaccelpressed);
		 * System.out.println("upaccelreleased="+upaccelreleased);/
		 **************************************************************************/

		if (Math.abs(accel) < zero)
		{
			isDownPressed = false;
			isUpPressed = false;
		}

		if (!isDownPressed && !isUpPressed && downaccelpressed)
		{
			isDownPressed = true;
			isUpPressed = false;
			System.out.println("!isDownPressed && !isUpPressed && downaccelpressed");

			// mRobot.keyRelease(KeyEvent.VK_DOWN);
		}
		else if (!isUpPressed && !isDownPressed && upaccelpressed)
		{
			System.out.println("!isUpPressed && !isDownPressed && upaccelpressed");
			isUpPressed = true;
			isDownPressed = false;

			// mRobot.keyRelease(KeyEvent.VK_UP);
		}
		else if (isDownPressed && downaccelreleased)
		{
			System.out.println("isDownPressed && downaccelreleased");
			isDownPressed = false;
			mRobot.keyPress(KeyEvent.VK_DOWN);
		}
		else if (isUpPressed && upaccelreleased)
		{
			System.out.println("isUpPressed && upaccelreleased");
			isUpPressed = false;
			mRobot.keyPress(KeyEvent.VK_UP);
		}
		/***/
	}

	public void buttonInputReceived(final WRButtonEvent pEvent)
	{

		if (!isDownPressed && (pEvent.wasPressed(WRButtonEvent.DOWN) || pEvent.wasPressed(WRButtonEvent.A)))
		{
			isDownPressed = true;
			mRobot.keyPress(KeyEvent.VK_DOWN);
		}
		else if (!isUpPressed && (pEvent.wasPressed(WRButtonEvent.UP) || pEvent.wasPressed(WRButtonEvent.B)))
		{
			isUpPressed = true;
			mRobot.keyPress(KeyEvent.VK_UP);
		}
		else if (isDownPressed && (pEvent.wasReleased(WRButtonEvent.DOWN) || pEvent.wasReleased(WRButtonEvent.A)))
		{
			isDownPressed = false;
			mRobot.keyRelease(KeyEvent.VK_DOWN);
		}
		else if (isUpPressed && (pEvent.wasReleased(WRButtonEvent.UP) || pEvent.wasReleased(WRButtonEvent.B)))
		{
			isUpPressed = false;
			mRobot.keyRelease(KeyEvent.VK_UP);
		}
	}

	/*****************************************************************************
	 * public void combinedInputReceived(WRCombinedEvent pEvent) {
	 * buttonInputReceived(pEvent.getButtonEvent()); }/
	 ****************************************************************************/

}
