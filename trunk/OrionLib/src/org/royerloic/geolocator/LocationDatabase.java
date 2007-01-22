/*
 * Created on 28.4.2005 by Dipl.-Inf. MSc. Ing Loic Royer
 */
package org.royerloic.geolocator;

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
	Map<String, Location>				mLocationList;

	private static final String	cDELIMITERS	= "\t";

	boolean											mDebug;

	public LocationDatabase()
	{
		super();
		mLocationList = new HashMap<String, Location>();
	}

	public void addListFromFile(String pFileName)
	{
		try
		{
			URL lURL = this.getClass().getClassLoader().getResource(pFileName);

			InputStreamReader lInputStreamReader;
			BufferedReader lBufferedReader;
			try
			{
				lInputStreamReader = new InputStreamReader(lURL.openStream());
				lBufferedReader = new BufferedReader(lInputStreamReader);
			}
			catch (FileNotFoundException e)
			{
				System.out.println("File: " + lURL + " not found.");
				throw e;
			}

			try
			{
				String lFirstLine = lBufferedReader.readLine();
				StringTokenizer lFirstLineTokenizer = new StringTokenizer(lFirstLine, cDELIMITERS, false);
				String lType = lFirstLineTokenizer.nextToken();

				String lLineString;
				while ((lLineString = lBufferedReader.readLine()) != null)
				{
					if (!lLineString.matches("#.*"))
					{
						StringTokenizer lStringTokenizer = new StringTokenizer(lLineString, cDELIMITERS, false);
						String lName = lStringTokenizer.nextToken();
						lName = lName.trim();
						lName = lName.replace('_', ' ');

						String lLatitudeString = lStringTokenizer.nextToken();
						String lLongitudeString = lStringTokenizer.nextToken();
						String lCountry = lStringTokenizer.nextToken();

						double lLatitude = (Double.valueOf(lLatitudeString)).doubleValue();
						double lLongitude = (Double.valueOf(lLongitudeString)).doubleValue();

						Location lLocation = new Location(lType, lName, lLatitude, lLongitude, lCountry);
						if (isDebug())
						{
							lLocation.setSourceString(lLineString);
						}

						mLocationList.put(lName, lLocation);

					}

				}
			}
			catch (IOException e2)
			{
				System.out.println("Error while reading: " + e2.getCause());
			}
			finally
			{
				lInputStreamReader.close();
			}
		}
		catch (Exception any)
		{
			any.printStackTrace(System.out);

		}

	}

	public Location findLocationByName(final String pName)
	{
		Location lLocation = (Location) mLocationList.get(pName);

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

	public Location findSingleWordLocationInString(String pString)
	{
		String lLowerCaseString = pString.toLowerCase(Locale.ENGLISH);
		Iterator lIterator = mLocationList.keySet().iterator();
		for (; lIterator.hasNext();)
		{
			String lName = (String) lIterator.next();
			String lLowerCaseName = lName.toLowerCase(Locale.ENGLISH);
			String lRefactoredlName = lLowerCaseName.trim();
			Location lLocation = findLocationByName(lRefactoredlName);
			return lLocation;
		}

		return null;

	}

	public Location findLocationMatchInString(String pString)
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
		Iterator lIterator = mLocationList.keySet().iterator();
		for (; lIterator.hasNext();)
		{
			String lName = (String) lIterator.next();
			String lLowerCaseName = lName.toLowerCase();
			String lRefactoredlName = " " + lLowerCaseName + " ";
			int lIndex = lLowerCaseString.indexOf(lRefactoredlName);
			// System.out.println("Searching for : '"+lRefactoredlName+"', found it
			// at: "+lIndex);
			if ((lIndex < lMinimalIndex) && (lIndex >= 0))
			{
				lMinimalIndex = lIndex;
				lLocationFound = (Location) mLocationList.get(lName);
			}
		}

		return lLocationFound;
	}

	public Location findLocationByCoordinates(double pLong, double pLat)
	{
		Location lLocationFound = null;
		try
		{
			Iterator lIterator = mLocationList.entrySet().iterator();
			double lMinDistance = Double.POSITIVE_INFINITY;
			for (; lIterator.hasNext();)
			{
				Map.Entry lEntry = (Map.Entry) lIterator.next();
				Location lLocation = (Location) lEntry.getValue();
				double lLong = lLocation.getLongitude();
				double lLat = lLocation.getLatitude();

				double lDifferenceLong = lLong - pLong;
				double lDifferenceLat = lLat - pLat;

				double lDistance = Math.sqrt(lDifferenceLong * lDifferenceLong + lDifferenceLat * lDifferenceLat);

				if (lDistance < lMinDistance)
				{
					lMinDistance = lDistance;
					lLocationFound = lLocation;
				}

			}
		}
		catch (RuntimeException e)
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

	public void setDebug(boolean pDebug)
	{
		mDebug = pDebug;
	}

}
