package utils.wiimote.tools;

import java.awt.AWTException;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.io.IOException;
import java.util.ArrayList;

import utils.wiimote.WRLImpl;
import wiiremotej.IRLight;
import wiiremotej.WiiRemote;
import wiiremotej.WiiRemoteExtension;
import wiiremotej.WiiRemoteJ;
import wiiremotej.event.WRAccelerationEvent;
import wiiremotej.event.WRButtonEvent;
import wiiremotej.event.WRCombinedEvent;
import wiiremotej.event.WRExtensionEvent;
import wiiremotej.event.WRIREvent;
import wiiremotej.event.WRStatusEvent;
import wiiremotej.event.WiiRemoteDiscoveredEvent;
import wiiremotej.event.WiiRemoteDiscoveryListener;
import wiiremotej.event.WiiRemoteListener;

public class WiiMode implements WiiRemoteListener
{
	private static WiiRemote			remote;

	private static int						t												= 0;
	private static int						x												= 0;
	private static int						y												= 0;
	private static int						z												= 0;

	private static double					mX											= 0;
	private static double					mY											= 0;

	private Mode									mMode										= null;
	private Rectangle							mScreenRectangle;

	static Robot									mRobot;

	ArrayList<WiiStatusListener>	lWiiStatusListenerList	= new ArrayList<WiiStatusListener>();

	enum Mode
	{
		mouse, pen, slideshow
	}

	public WiiMode() throws AWTException
	{
		mRobot = new Robot();
		final GraphicsEnvironment genv = GraphicsEnvironment.getLocalGraphicsEnvironment();
		mScreenRectangle = genv	.getDefaultScreenDevice()
														.getDefaultConfiguration()
														.getBounds();

	}

	public void connect()
	{
		final WiiMode lWiiMode = this;

		Runnable lRunnable = new Runnable()
			{
				public void run()
				{
					try
					{
						WiiRemoteDiscoveryListener listener = new WiiRemoteDiscoveryListener()
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

						if (remote != null)
						{
							remote.addWiiRemoteListener(lWiiMode);
							remote.setAccelerometerEnabled(false);
							// Wremote.setSpeakerEnabled(false);

							remote.setLEDIlluminated(0, true);

							for (WiiStatusListener lWiiStatusListener : lWiiStatusListenerList)
							{
								lWiiStatusListener.connected();
							}
						}
						else
						{
							// Connection failed
							System.out.println("Connection failed.");
						}

					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			};

		Thread lThread = new Thread(lRunnable);
		lThread.start();
	}

	public void disconnect()
	{
		if (remote != null) remote.disconnect();
		disconnected();
	}

	public void activate(Mode pMode)
	{
		try
		{
			mMode = pMode;
			if (mMode != null) switch (mMode)
			{
				case mouse:
				case pen:

					remote.setIRSensorEnabled(true, WRIREvent.BASIC);

					/***/
					break;

			}
		}
		catch (Throwable e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void IRInputReceived(WRIREvent evt)
	{
		try
		{
			if (mMode != null) switch (mMode)
			{
				case mouse:
				{

					int i = 0;
					double nmX = 0;
					double nmY = 0;

					for (IRLight light : evt.getIRLights())
					{
						if (light != null)
						{
							/*****************************************************************
							 * System.out.println("Light: " + i); System.out.println("X: " +
							 * light.getX()); System.out.println("Y:" + light.getY());
							 * System.out.println("R:" + light.getSize());
							 ****************************************************************/

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
					break;

				case pen:
				{

					int i = 0;
					double nmX = 0;
					double nmY = 0;

					for (IRLight light : evt.getIRLights())
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
					break;

			}
		}
		catch (Throwable e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}/**/

	}

	@Override
	public void accelerationInputReceived(WRAccelerationEvent pArg0)
	{
		// TODO Auto-generated method stub

	}

	static boolean	mButton1Pressed	= false;
	static boolean	mButton2Pressed	= false;
	static boolean	mButton3Pressed	= false;

	@Override
	public void buttonInputReceived(WRButtonEvent evt)
	{
		if (mMode != null) switch (mMode)
		{
			case mouse:
				// System.out.println(evt);
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
				break;
				
			case pen:

		}

	}

	@Override
	public void combinedInputReceived(WRCombinedEvent pArg0)
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void extensionConnected(WiiRemoteExtension pArg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void extensionDisconnected(WiiRemoteExtension pArg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void extensionInputReceived(WRExtensionEvent pArg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void extensionPartiallyInserted()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void extensionUnknown()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void statusReported(WRStatusEvent pArg0)
	{
		System.out.println(pArg0);

	}

	public void addDisconnectListener(WiiStatusListener pWiiStatusListener)
	{
		lWiiStatusListenerList.add(pWiiStatusListener);
	}

	@Override
	public void disconnected()
	{
		for (WiiStatusListener lWiiStatusListener : lWiiStatusListenerList)
		{
			lWiiStatusListener.disconnected();
		}
	}

}
