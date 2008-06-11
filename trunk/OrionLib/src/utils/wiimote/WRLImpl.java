package utils.wiimote;

import java.awt.AWTException;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.InputEvent;

import wiiremotej.ButtonMap;
import wiiremotej.IRLight;
import wiiremotej.WiiRemote;
import wiiremotej.WiiRemoteExtension;
import wiiremotej.WiiRemoteJ;
import wiiremotej.event.WRAccelerationEvent;
import wiiremotej.event.WRButtonEvent;
import wiiremotej.event.WRExtensionEvent;
import wiiremotej.event.WRIREvent;
import wiiremotej.event.WRNunchukExtensionEvent;
import wiiremotej.event.WRStatusEvent;
import wiiremotej.event.WiiRemoteAdapter;
import wiiremotej.event.WiiRemoteDiscoveredEvent;
import wiiremotej.event.WiiRemoteDiscoveryListener;

/**
 * Implements WiiRemoteListener and acts as a general test class. Note that you
 * can ignore the main method pretty much, as it mostly has to do with the
 * graphs and GUIs. At the very end though, there's an example of how to connect
 * to a remote and how to prebuffer audio files.
 * 
 * @author Michael Diamond
 * @version 1/05/07
 */

public class WRLImpl extends WiiRemoteAdapter
{
	private static boolean accelerometerSource = true; // true = wii

	private static WiiRemote remote;

	private static int t = 0;
	private static int x = 0;
	private static int y = 0;
	private static int z = 0;

	private static double mX = 0;
	private static double mY = 0;
	static java.awt.Robot mRobot;
	static boolean mButton1Pressed = false;
	static boolean mButton2Pressed = false;
	static boolean mButton3Pressed = false;
	static Rectangle mScreenRectangle;

	public static void main(final String args[]) throws AWTException
	{
		mRobot = new java.awt.Robot();

		final GraphicsEnvironment genv = GraphicsEnvironment.getLocalGraphicsEnvironment();

		mScreenRectangle = genv.getMaximumWindowBounds();

		WiiRemoteJ.setConsoleLoggingAll();

		try
		{
			final WiiRemoteDiscoveryListener listener = new WiiRemoteDiscoveryListener()
			{
				public void wiiRemoteDiscovered(WiiRemoteDiscoveredEvent evt)
				{
					evt	.getWiiRemote()
							.addWiiRemoteListener(new WRLImpl(evt.getWiiRemote()));
				}

				public void findFinished(int numberFound)
				{
					System.out.println("Found " + numberFound + " remotes!");
				}
			};

			// Find and connect to a Wii Remote
			remote = WiiRemoteJ.findRemote();
			remote.addWiiRemoteListener(new WRLImpl(remote));
			remote.setAccelerometerEnabled(false);
			remote.setSpeakerEnabled(true);
			remote.setIRSensorEnabled(true, WRIREvent.BASIC);
			remote.setLEDIlluminated(0, true);
			remote.setLEDIlluminated(1, true);

			remote.getButtonMaps()
						.add(new ButtonMap(	WRButtonEvent.HOME,
																ButtonMap.NUNCHUK,
																WRNunchukExtensionEvent.C,
																new int[]
																{ java.awt.event.KeyEvent.VK_CONTROL },
																java.awt.event.InputEvent.BUTTON1_MASK,
																0,
																-1));

		}
		catch (final Exception e)
		{
			e.printStackTrace();
		}
	}

	public WRLImpl(final WiiRemote remote)
	{
		WRLImpl.remote = remote;
	}

	public void disconnected()
	{
		System.out.println("Remote disconnected... Please Wii again.");
		System.exit(0);
	}

	public void statusReported(final WRStatusEvent evt)
	{
		System.out.println("Battery level: " + evt.getBatteryLevel() / 2 + "%");
		System.out.println("Continuous: " + evt.isContinuousEnabled());
		System.out.println("Remote continuous: " + remote.isContinuousEnabled());
	}

	public void IRInputReceived(final WRIREvent evt)
	{

		try
		{
			int i = 0;
			double nmX = 0;
			double nmY = 0;

			for (final IRLight light : evt.getIRLights())
			{
				if (light != null)
				{
					System.out.println("Light: " + i);
					System.out.println("X: " + light.getX());
					System.out.println("Y:" + light.getY());
					// System.out.println("R:" + light.getSize());

					nmX += light.getX();
					nmY += light.getY();
					i++;
				}
			}

			if (i > 0)
			{
				mX = nmX / i;
				mY = nmY / i;

				final int x = (int) ((1 - mX) * mScreenRectangle.getWidth());
				final int y = (int) (mY * mScreenRectangle.getHeight());
				mRobot.mouseMove(x, y);

				/***********************************************************************
				 * if (y < mScreenRectangle.getHeight() / 2) {
				 * remote.startModulatedVibrating(x); } else {
				 * remote.stopModulatedVibrating(); }/
				 **********************************************************************/
			}
		}
		catch (final Throwable e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}/**/

	}

	public void accelerationInputReceived(final WRAccelerationEvent evt)
	{
		if (accelerometerSource)
		{
			x = (int) (evt.getXAcceleration() / 5 * 300) + 300;
			y = (int) (evt.getYAcceleration() / 5 * 300) + 300;
			z = (int) (evt.getZAcceleration() / 5 * 300) + 300;

			t++;

			// System.out.println("x=" + x + "y=" + y + "z=" + z + "");
		}

	}

	public void extensionInputReceived(final WRExtensionEvent evt)
	{

	}

	public void extensionConnected(final WiiRemoteExtension extension)
	{
		System.out.println("Extension connected!");
		try
		{
			remote.setExtensionEnabled(true);
		}
		catch (final Exception e)
		{
			e.printStackTrace();
		}
	}

	public void extensionPartiallyInserted()
	{
		System.out.println("Extension partially inserted. Push it in more next time, jerk!");
	}

	public void extensionUnknown()
	{
		System.out.println("Extension unknown. Did you try to plug in a toaster or something?");
	}

	public void extensionDisconnected(final WiiRemoteExtension extension)
	{
		System.out.println("Extension disconnected. Why'd you unplug it, retard?");
	}

	public void buttonInputReceived(final WRButtonEvent evt)
	{

		System.out.println(evt);
		if (!mButton1Pressed && evt.isPressed(WRButtonEvent.A))
		{
			mButton1Pressed = true;
			mRobot.mousePress(InputEvent.BUTTON1_MASK);
			mRobot.waitForIdle();
		}
		else if (mButton1Pressed && evt.wasReleased(WRButtonEvent.A))
		{
			mButton1Pressed = false;
			mRobot.mouseRelease(InputEvent.BUTTON1_MASK);
			mRobot.waitForIdle();
		}
		else if (!mButton3Pressed && evt.isPressed(WRButtonEvent.B))
		{
			mButton3Pressed = true;
			mRobot.mousePress(InputEvent.BUTTON3_MASK);
			mRobot.waitForIdle();
		}
		else if (mButton3Pressed && evt.wasReleased(WRButtonEvent.B))
		{
			mButton3Pressed = false;
			mRobot.mouseRelease(InputEvent.BUTTON3_MASK);
			mRobot.waitForIdle();
		}
		/***************************************************************************
		 * else if (evt.isPressed(WRButtonEvent.B)) {
		 * mRobot.mousePress(InputEvent.BUTTON2_MASK); mRobot.waitForIdle(); } else
		 * if (evt.wasReleased(WRButtonEvent.B)) {
		 * mRobot.mouseRelease(InputEvent.BUTTON2_MASK); mRobot.waitForIdle(); }/
		 **************************************************************************/

	}

}