/**
 * Created on 7 mai 2005 By Dipl.-Inf. MSC. Ing. Loic Royer
 * 
 */
package org.royerloic.date;

import java.util.Date;

public class DateUtils
{
	public static long millisecondsElapsed(Date pDate)
	{
		Date lCurrentDate = new Date();
		long lMillisecondsElapsed = lCurrentDate.getTime() - pDate.getTime();
		return lMillisecondsElapsed;
	}

	public static double secondsElapsed(Date pDate)
	{
		double lSecondsElapsed = ((double) millisecondsElapsed(pDate)) / (1000);
		return lSecondsElapsed;
	}

	public static double minutesElapsed(Date pDate)
	{
		double lMinutesElapsed = ((double) millisecondsElapsed(pDate)) / (1000 * 60);
		return lMinutesElapsed;
	}

	public static double hoursElapsed(Date pDate)
	{
		double lMinutesElapsed = ((double) millisecondsElapsed(pDate)) / (1000 * 60 * 60);
		return lMinutesElapsed;
	}

	public static double daysElapsed(Date pDate)
	{
		double lMinutesElapsed = ((double) millisecondsElapsed(pDate)) / (1000 * 60 * 60 * 24);
		return lMinutesElapsed;
	}

	public static String stringElapsed(Date pDate)
	{
		long lDays = (long) daysElapsed(pDate);
		long lHours = (long) hoursElapsed(pDate);
		long lMinutes = (long) minutesElapsed(pDate);
		long lSeconds = (long) secondsElapsed(pDate);
		long lMilliseconds = (long) millisecondsElapsed(pDate);

		String lString = "";
		if (lDays > 3)
		{
			lString = lDays + " days ago";
		}
		else if (lDays > 0)
		{
			switch ((int) lDays)
			{
				case 1:
					lString = "one day ago";
					break;

				case 2:
					lString = "two days ago";
					break;

				case 3:
					lString = "three days ago";
					break;

			}
		}
		else if (lHours > 3)
		{
			lString = lHours + " hours ago";
		}
		else if (lHours > 0)
		{
			switch ((int) lHours)
			{
				case 1:
					lString = "one hour ago";
					break;

				case 2:
					lString = "two hours ago";
					break;

				case 3:
					lString = "three hours ago";
					break;

			}
		}
		else if (lMinutes > 3)
		{
			lString = lMinutes + " minutes ago";
		}
		else if (lMinutes > 0)
		{
			switch ((int) lMinutes)
			{
				case 1:
					lString = "one minute ago";
					break;

				case 2:
					lString = "two minutes ago";
					break;

				case 3:
					lString = "three minutes ago";
					break;

			}
		}
		else if (lSeconds > 1)
		{
			lString = lSeconds + " seconds ago";
		}
		else if (lSeconds == 1)
		{
			lString = "one second ago";
		}
		else if (lMilliseconds > 0)
		{
			lString = lMilliseconds + " milliseconds ago";
		}
		else if (lMilliseconds == 0)
		{
			lString = "now";
		}

		return lString;
	}

}
