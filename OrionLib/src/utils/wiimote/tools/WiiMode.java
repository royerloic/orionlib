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
	private static WiiRemote	remote;

	private static int				t			= 0;
	private static int				x			= 0;
	private static int				y			= 0;
	private static int				z			= 0;

	private static double			mX		= 0;
	private static double			mY		= 0;

	private Mode							mMode	= null;
	private Rectangle					mScreenRectangle;

	static Robot							mRobot;

	enum Mode
	{
		mouse, pen, slideshow
	}

	public WiiMode() throws AWTException
	{
		mRobot = new Robot();
		final GraphicsEnvironment genv = GraphicsEnvironment.getLocalGraphicsEnvironment();
		mScreenRectangle = genv.getMaximumWindowBounds();

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
	}

	public void activate(Mode pMode)
	{
		mMode = pMode;
		if (mMode != null) switch (mMode)
		{
			case mouse:
				try
				{
					remote.setIRSensorEnabled(true, WRIREvent.BASIC);
				}
				catch (IllegalArgumentException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch (IllegalStateException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				/***/
				break;

		}

	}

	@Override
	public void IRInputReceived(WRIREvent evt)
	{
		if (mMode != null) switch (mMode)
		{
			case mouse:
				try
				{
					int i = 0;
					double nmX = 0;
					double nmY = 0;

					for (IRLight light : evt.getIRLights())
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
						mX = nmX / (double) i;
						mY = nmY / (double) i;

						final int x = (int) ((1 - mX) * mScreenRectangle.getWidth());
						final int y = (int) (mY * mScreenRectangle.getHeight());
						mRobot.mouseMove(x, y);

						/*******************************************************************
						 * if (y < mScreenRectangle.getHeight() / 2) {
						 * remote.startModulatedVibrating(x); } else {
						 * remote.stopModulatedVibrating(); }/
						 ******************************************************************/
					}
				}
				catch (Throwable e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}/**/
				break;

		}

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
				break;

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

	
	
	
	ArrayList<DisconnectListener>	lDisconnectListenerList	= new ArrayList<DisconnectListener>();

	public void addDisconnectListener(DisconnectListener pDisconnectListener)
	{
		lDisconnectListenerList.add(pDisconnectListener);
	}
	
	@Override
	public void disconnected()
	{
		System.out.println("disconnected...");
		for (DisconnectListener lDisconnectListener : lDisconnectListenerList)
		{
			lDisconnectListener.disconnected();
		}
	}

}
