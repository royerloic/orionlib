/*
 * Optimal Project
 * 
 * M.Sc. Ing. Loic Royer
 * 
 * Fraunhofer IWU Dresden
 *  
 */

package org.royerloic.utils;

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

	private boolean	mSuspendRequested	= false;

	private boolean	mStopRequested		= false;

	private boolean	mStarted					= false;

	private boolean	mStopped					= false;

	private final Object	mLock							= new Object();

	public void joinWhenStarted(final int pWaitTime)
	{
		while (isStarted() == false)
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

	public boolean isStarted()
	{
		synchronized (this.mLock)
		{
			return this.mStarted;
		}
	}

	public boolean isStopped()
	{
		synchronized (this.mLock)
		{
			return this.mStopped;
		}
	}

	public boolean isPaused()
	{
		synchronized (this.mLock)
		{
			return this.mSuspendRequested;
		}
	}

	public void setSuspendRequest(final boolean pSuspendRequested)
	{
		synchronized (this.mLock)
		{
			this.mSuspendRequested = pSuspendRequested;
			this.mLock.notifyAll();
		}
	}

	public void setStopRequest(final boolean pStopRequested)
	{
		synchronized (this.mLock)
		{
			this.mStopRequested = pStopRequested;
			this.mLock.notifyAll();
		}
	}

	@Override
	public void run()
	{
		try
		{
			synchronized (this.mLock)
			{
				this.mStopped = false;
			}

			if (initiate())
			{
				synchronized (this.mLock)
				{
					this.mStarted = true;
				}

				while (execute())
					synchronized (this.mLock)
					{
						// if suspended, then wait:
						while (this.mSuspendRequested && !this.mStopRequested)
							this.mLock.wait();

						// id stopped, then get out...
						if (this.mStopRequested)
							break;

					}
				terminate();
			}
		}
		catch (final InterruptedException e)
		{
		}
		finally
		{
			synchronized (this.mLock)
			{
				this.mStopped = true;
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