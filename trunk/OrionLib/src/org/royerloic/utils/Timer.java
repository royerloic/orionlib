package org.royerloic.utils;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * A useful timer for java routines.
 * 
 * @version $Revision: 1.2 $ , $Date: 2005/03/16 17:20:51 $
 * @author Andreas Doms ( <A HREF="mailto:coding@adoms.de">coding@adoms.de </A>)
 * @author Mirko Seifert ( <A
 *         HREF="mailto:ms53@inf.tu-dresden.de">ms53@inf.tu-dresden.de </A>)
 */
public class Timer
{
	protected long				startTime;
	protected long				endTime;
	private NumberFormat	nf			= null;
	private String				prefix	= "(";
	private String				suffix	= ")";

	/**
	 * @return Returns the prefix.
	 */
	public String getPrefix()
	{
		return prefix;
	}

	/**
	 * @param prefix
	 *          The prefix to set.
	 */
	public void setPrefix(final String prefix)
	{
		this.prefix = prefix;
	}

	/**
	 * @return Returns the suffix.
	 */
	public String getSuffix()
	{
		return suffix;
	}

	/**
	 * @param suffix
	 *          The suffix to set.
	 */
	public void setSuffix(final String suffix)
	{
		this.suffix = suffix;
	}

	/**
	 * Constructs a new Timer without a name.
	 */
	public Timer()
	{
		this("");
	}

	/**
	 * Constructs a new Timer with the specified name.
	 * 
	 * @param name
	 *          String
	 */
	public Timer(final String name)
	{
		super();
		nf = NumberFormat.getInstance(Locale.US);
		nf.setMinimumIntegerDigits(2);
	}

	/**
	 * Insert the method's description here.
	 * 
	 * @return double
	 * @param reference
	 *          de.acid.util.Timer
	 */
	public double getSpeedUp(final Timer reference)
	{
		return (double) reference.getTotalTime() / (double) getTotalTime();
	}

	/**
	 * Formats the specified time into a string.
	 * 
	 * @param time
	 * @return timer string
	 */
	private String getTimeString(final long time)
	{

		final long hour = time / 1000 / 60 / 60;
		final long minute = time / 1000 / 60 % 60;
		final long second = time / 1000 % 60;
		final long tsecond = time % 1000;

		return hour + ":" + minute + ":" + second + "." + tsecond;
	}

	/**
	 * Returns the time between starting the timer and now.
	 * 
	 * @return long
	 */
	private long getTimeTillNow()
	{
		return System.currentTimeMillis() - startTime;
	}

	/**
	 * Returns the time between starting and stopping the timer.
	 * 
	 * @return long
	 */
	public long getTotalTime()
	{
		return endTime - startTime;
	}

	/**
	 * 
	 * @see de.tud.biotec.protein.helper.ProgressListener#notifyProgress(double)
	 */
	public void notifyProgress(final double pProgress)
	{
		printEST(pProgress);
	}

	/**
	 * Prints the estimed time.
	 * 
	 * @param percentFinished
	 *          double
	 */
	public void printEST(final double percentFinished)
	{
		System.out.println(getESTString(percentFinished));
	}

	/**
	 * Prints the estimed time.
	 * 
	 * @param percentFinished
	 *          double
	 */
	public String getESTString(final double percentFinished)
	{
		return getPrefix() + getTimeString((long) (getTimeTillNow() * (1 / percentFinished - 1))) + getSuffix();
	}

	/**
	 * Prints the total time.
	 * 
	 */
	public void printTime()
	{
		System.out.println(getPrefix() + getTimeString(getTotalTime()) + getSuffix());
	}

	/**
	 * Returns the total time.
	 * 
	 * @return the total time.
	 */
	public String getTime()
	{
		return getPrefix() + getTimeString(getTotalTime()) + getSuffix();
	}

	/**
	 * Prints the total time.
	 */
	public void printTimeTillNow()
	{
		System.out.println(getPrefix() + getTimeString(getTimeTillNow()) + getSuffix());
	}

	/**
	 * @return time till now
	 */
	public String timeTillNow()
	{
		return getPrefix() + getTimeString(getTimeTillNow()) + getSuffix();
	}

	/**
	 * Resumes the timer after stopping it.
	 */
	public void resume()
	{
		startTime = System.currentTimeMillis() - (endTime - startTime);
	}

	/**
	 * Starts the timer.
	 */
	public void start()
	{
		startTime = System.currentTimeMillis();
	}

	/**
	 * Stops the timer.
	 * 
	 * @return timer reference
	 */
	public Timer stop()
	{
		endTime = System.currentTimeMillis();
		return this;
	}

	/**
	 * @param timer
	 * @return time difference between the two timers
	 */
	public String difference(final Timer timer)
	{
		final Timer t = new Timer();
		t.startTime = 0;
		t.endTime = this.getTotalTime() - timer.getTotalTime();
		return t.getTime();
	}

	/**
	 * Calulates the percentage of time passed. This timer is assumed to be the
	 * total time.
	 * 
	 * @param timer
	 *          running timer
	 * @return percentage of time of this timer that is passed on timer given.
	 */
	public double percentage(final Timer timer)
	{
		return 100 * (double) timer.getTotalTime() / this.getTotalTime();
	}

}