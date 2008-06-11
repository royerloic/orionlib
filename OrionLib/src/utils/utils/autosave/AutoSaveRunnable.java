package utils.utils.autosave;

import java.awt.AWTException;
import java.awt.Robot;

public class AutoSaveRunnable implements Runnable
{

	public boolean mKeepAlive = true;

	int mDelayInSeconds;

	public boolean mActive = false;

	int[] mKeyEvents;

	Robot mRobot;

	private AutoSaveRunnable() throws AWTException
	{
		super();
		mRobot = new Robot();
		mRobot.setAutoWaitForIdle(false);
		mRobot.setAutoDelay(0);
	}

	public AutoSaveRunnable(final int pDelayInSeconds, final int[] pKeyEvents) throws AWTException
	{
		this();
		mDelayInSeconds = pDelayInSeconds;
		mKeyEvents = pKeyEvents;
	}

	public void run()
	{
		while (mKeepAlive)
		{
			try
			{
				Thread.sleep(mDelayInSeconds * 1000);
				if (mActive)
				{
					mRobot.waitForIdle();
					for (final int lKeyEvent : mKeyEvents)
					{
						mRobot.keyPress(lKeyEvent);
					}
					for (int i = mKeyEvents.length - 1; i >= 0; i--)
					{
						mRobot.keyRelease(mKeyEvents[i]);
					}
				}
			}
			catch (final Throwable e)
			{
				e.printStackTrace();
			}
		}
	}

}
