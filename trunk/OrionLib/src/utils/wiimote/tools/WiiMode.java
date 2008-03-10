package utils.wiimote.tools;

import java.awt.AWTException;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.io.IOException;

import utils.wiimote.DisconnectionListener;
import utils.wiimote.IrCameraMode;
import utils.wiimote.IrCameraSensitivity;
import utils.wiimote.Mote;
import utils.wiimote.MoteFinder;
import utils.wiimote.StatusInformationReport;
import utils.wiimote.event.CoreButtonEvent;
import utils.wiimote.event.CoreButtonListener;
import utils.wiimote.event.IrCameraEvent;
import utils.wiimote.event.IrCameraListener;
import utils.wiimote.event.StatusInformationListener;
import utils.wiimote.request.ReportModeRequest;

public class WiiMode implements
										IrCameraListener,
										CoreButtonListener,
										StatusInformationListener,
										DisconnectionListener
{

	private Mote			mMote;
	private Mode			mMode;
	private Rectangle	mScreenRectangle;

	static Robot			mRobot;

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
					mMote = MoteFinder.getMoteFinder().findMote();
					mMote.setPlayerLeds(new boolean[] { true, false, false, false });
					mMote.addCoreButtonListener(lWiiMode);
					mMote.addStatusInformationListener(lWiiMode);
					mMote.addDisconnectionListener(lWiiMode);
				}
			};

		Thread lThread = new Thread(lRunnable);
		lThread.start();
	}

	public void disconnect()
	{
		if (mMote != null) mMote.disconnect();
	}

	public void activate(Mode pMode)
	{
		mMode = pMode;
		switch (mMode)
		{
			case mouse:
				mMote.requestStatusInformation();
				mMote.enableIrCamera(IrCameraMode.BASIC, IrCameraSensitivity.MARCAN);
				mMote.addIrCameraListener(this);
				mMote.setReportMode(ReportModeRequest.DATA_REPORT_0x36);
				/***/
				break;

		}

	}

	@Override
	public void irImageChanged(IrCameraEvent pEvt)
	{
		System.out.println(pEvt);
		switch (mMode)
		{
			case mouse:
			{
				if (pEvt.getSlot() == 0)
				{
					final int x = (int) ((1 - pEvt.getNormalizedX()) * mScreenRectangle.getWidth());
					final int y = (int) (pEvt.getNormalizedY() * mScreenRectangle.getHeight());
					mRobot.mouseMove(x, y);
				}
			}
				break;

		}

	}

	static boolean	mButton1Pressed	= false;
	static boolean	mButton2Pressed	= false;
	static boolean	mButton3Pressed	= false;

	@Override
	public void buttonPressed(CoreButtonEvent pEvt)
	{
		System.out.println("button: " + pEvt.getButton());

		if (!mButton1Pressed && pEvt.isButtonAPressed())
		{
			mButton1Pressed = true;
			mRobot.mousePress(InputEvent.BUTTON1_MASK);
			mRobot.waitForIdle();
		}
		else if (mButton1Pressed && pEvt.isNoButtonPressed())
		{
			mButton1Pressed = false;
			mRobot.mouseRelease(InputEvent.BUTTON1_MASK);
			mRobot.waitForIdle();
		}
		else if (!mButton3Pressed && pEvt.isButtonBPressed())
		{
			mButton3Pressed = true;
			mRobot.mousePress(InputEvent.BUTTON3_MASK);
			mRobot.waitForIdle();
		}
		else if (mButton3Pressed && pEvt.isNoButtonPressed())
		{
			mButton3Pressed = false;
			mRobot.mouseRelease(InputEvent.BUTTON3_MASK);
			mRobot.waitForIdle();
		}

	}

	@Override
	public void statusInformationReceived(StatusInformationReport pReport)
	{
		System.out.println(pReport);
	}

	@Override
	public void disconnected(IOException pEx)
	{
		System.out.println("disconnected...");

	}

}
