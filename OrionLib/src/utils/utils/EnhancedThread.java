/*
 * Optimal Project
 * 
 * M.Sc. Ing. Loic Royer
 * 
 * Fraunhofer IWU Dresden
 *  
 */

package utils.utils;

/**
 * @author MSc. Ing. Loic Royer
 * 
 */
public abstract class EnhancedThread extends Thread
{

	public abstract boolean initiate();

	public boolean execute()
	{
		yield();
		return true;
	};

	public abstract boolean terminate();

	private boolean mSuspendRequested = false;

	private boolean mStopRequested = false;

	private boolean mStarted = false;

	private boolean mStopped = false;

	private final Object mLock = new Object();

	public void joinWhenStarted(final int pWaitTime)
	{
		while (isStarted() == false)
		{
			try
			{
				sleep(pWaitTime);
			}
			catch (final InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace(System.out);
			}
		}
	}

	public boolean isStarted()
	{
		synchronized (mLock)
		{
			return mStarted;
		}
	}

	public boolean isStopped()
	{
		synchronized (mLock)
		{
			return mStopped;
		}
	}

	public boolean isPaused()
	{
		synchronized (mLock)
		{
			return mSuspendRequested;
		}
	}

	public void setSuspendRequest(final boolean pSuspendRequested)
	{
		synchronized (mLock)
		{
			mSuspendRequested = pSuspendRequested;
			mLock.notifyAll();
		}
	}

	public void setStopRequest(final boolean pStopRequested)
	{
		synchronized (mLock)
		{
			mStopRequested = pStopRequested;
			mLock.notifyAll();
		}
	}

	@Override
	public void run()
	{
		try
		{
			synchronized (mLock)
			{
				mStopped = false;
			}

			if (initiate())
			{
				synchronized (mLock)
				{
					mStarted = true;
				}

				while (execute())
				{
					synchronized (mLock)
					{
						// if suspended, then wait:
						while (mSuspendRequested && !mStopRequested)
						{
							mLock.wait();
						}

						// id stopped, then get out...
						if (mStopRequested)
						{
							break;
						}

					}
				}
				terminate();
			}
		}
		catch (final InterruptedException e)
		{
		}
		finally
		{
			synchronized (mLock)
			{
				mStopped = true;
			}
		}
	}

	public void doStart()
	{
		setSuspendRequest(false);
		setStopRequest(false);
		super.start();
	}

	public void doPause()
	{
		setSuspendRequest(true);
	}

	public void doResume()
	{
		setSuspendRequest(false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#start()
	 */
	public void doStop()
	{
		setStopRequest(true);
	}

}