/*
 * Created on 28.4.2005 by Dipl.-Inf. MSc. Ing Loic Royer
 */
package utils.geolocator;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

public class LocationDatabase
{
	Map<String, Location> mLocationList;

	private static final String cDELIMITERS = "\t";

	boolean mDebug;

	public LocationDatabase()
	{
		super();
		mLocationList = new HashMap<String, Location>();
	}

	public void addListFromFile(final String pFileName)
	{
		try
		{
			final URL lURL = this.getClass().getClassLoader().getResource(pFileName);

			InputStreamReader lInputStreamReader;
			BufferedReader lBufferedReader;
			try
			{
				lInputStreamReader = new InputStreamReader(lURL.openStream());
				lBufferedReader = new BufferedReader(lInputStreamReader);
			}
			catch (final FileNotFoundException e)
			{
				System.out.println("File: " + lURL + " not found.");
				throw e;
			}

			try
			{
				final String lFirstLine = lBufferedReader.readLine();
				final StringTokenizer lFirstLineTokenizer = new StringTokenizer(lFirstLine,
																																				cDELIMITERS,
																																				false);
				final String lType = lFirstLineTokenizer.nextToken();

				String lLineString;
				while ((lLineString = lBufferedReader.readLine()) != null)
				{
					if (!lLineString.matches("#.*"))
					{
						final StringTokenizer lStringTokenizer = new StringTokenizer(	lLineString,
																																					cDELIMITERS,
																																					false);
						String lName = lStringTokenizer.nextToken();
						lName = lName.trim();
						lName = lName.replace('_', ' ');

						final String lLatitudeString = lStringTokenizer.nextToken();
						final String lLongitudeString = lStringTokenizer.nextToken();
						final String lCountry = lStringTokenizer.nextToken();

						final double lLatitude = Double	.valueOf(lLatitudeString)
																						.doubleValue();
						final double lLongitude = Double.valueOf(lLongitudeString)
																						.doubleValue();

						final Location lLocation = new Location(lType,
																										lName,
																										lLatitude,
																										lLongitude,
																										lCountry);
						if (isDebug())
						{
							lLocation.setSourceString(lLineString);
						}

						mLocationList.put(lName, lLocation);

					}
				}
			}
			catch (final IOException e2)
			{
				System.out.println("Error while reading: " + e2.getCause());
			}
			finally
			{
				lInputStreamReader.close();
			}
		}
		catch (final Exception any)
		{
			any.printStackTrace(System.out);

		}

	}

	public Location findLocationByName(final String pName)
	{
		final Location lLocation = mLocationList.get(pName);

		return lLocation;
	}

	public Location findLocationInString(final String pString)
	{
		Location lLocation = null;
		// lLocation = findSingleWordLocationInString(pString);
		if (lLocation == null)
		{
			lLocation = findLocationMatchInString(pString);
		}
		return lLocation;
	}

	public Location findSingleWordLocationInString(final String pString)
	{
		final String lLowerCaseString = pString.toLowerCase(Locale.ENGLISH);
		final Iterator lIterator = mLocationList.keySet().iterator();
		for (; lIterator.hasNext();)
		{
			final String lName = (String) lIterator.next();
			final String lLowerCaseName = lName.toLowerCase(Locale.ENGLISH);
			final String lRefactoredlName = lLowerCaseName.trim();
			final Location lLocation = findLocationByName(lRefactoredlName);
			return lLocation;
		}

		return null;

	}

	public Location findLocationMatchInString(final String pString)
	{

		String lLowerCaseString = pString.toLowerCase();

		// System.out.println("Before lLowerCaseString="+lLowerCaseString);

		lLowerCaseString = lLowerCaseString.replaceAll("[\\p{Punct}]", " ");
		lLowerCaseString = lLowerCaseString.replaceAll("[\\p{Blank}]", " ");
		lLowerCaseString = lLowerCaseString.replaceAll("[\\p{Cntrl}]", " ");
		lLowerCaseString = lLowerCaseString.replaceAll("[\\p{Space}]", " ");

		lLowerCaseString = " " + lLowerCaseString + " ";

		// System.out.println("After lLowerCaseString= '"+lLowerCaseString+"'");

		Location lLocationFound = null;
		int lMinimalIndex = Integer.MAX_VALUE;
		final Iterator lIterator = mLocationList.keySet().iterator();
		for (; lIterator.hasNext();)
		{
			final String lName = (String) lIterator.next();
			final String lLowerCaseName = lName.toLowerCase();
			final String lRefactoredlName = " " + lLowerCaseName + " ";
			final int lIndex = lLowerCaseString.indexOf(lRefactoredlName);
			// System.out.println("Searching for : '"+lRefactoredlName+"', found it
			// at: "+lIndex);
			if (lIndex < lMinimalIndex && lIndex >= 0)
			{
				lMinimalIndex = lIndex;
				lLocationFound = mLocationList.get(lName);
			}
		}

		return lLocationFound;
	}

	public Location findLocationByCoordinates(final double pLong,
																						final double pLat)
	{
		Location lLocationFound = null;
		try
		{
			final Iterator lIterator = mLocationList.entrySet().iterator();
			double lMinDistance = Double.POSITIVE_INFINITY;
			for (; lIterator.hasNext();)
			{
				final Map.Entry lEntry = (Map.Entry) lIterator.next();
				final Location lLocation = (Location) lEntry.getValue();
				final double lLong = lLocation.getLongitude();
				final double lLat = lLocation.getLatitude();

				final double lDifferenceLong = lLong - pLong;
				final double lDifferenceLat = lLat - pLat;

				final double lDistance = Math.sqrt(lDifferenceLong * lDifferenceLong
																						+ lDifferenceLat
																						* lDifferenceLat);

				if (lDistance < lMinDistance)
				{
					lMinDistance = lDistance;
					lLocationFound = lLocation;
				}

			}
		}
		catch (final RuntimeException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return lLocationFound;
	}

	public boolean isDebug()
	{
		return mDebug;
	}

	public void setDebug(final boolean pDebug)
	{
		mDebug = pDebug;
	}

}
