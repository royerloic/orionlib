/*
 * Created on 28.4.2005 by Dipl.-Inf. MSc. Ing Loic Royer
 */
package org.royerloic.geolocator;

public class Location
{
	String					mType;

	String					mName;

	double					mLatitude;

	double					mLongitude;

	String					mCountry;

	String					mSourceString;

	private String	mPlaceName;

	public String toString()
	{
		String lString = new String();
		if ((mType != null) && mType.equalsIgnoreCase("city"))
		{
			lString = mName + ", ";
		}
		lString += mCountry + ", Coordinates: (" + mLatitude + ", " + mLongitude + ")";
		if (mSourceString != null)
		{
			lString += "\n '" + mSourceString + "' \n";
		}
		return lString;
	}

	private Location()
	{
		super();
		// TODO Auto-generated constructor stub
	}

	public Location(String pType, String pName, double pLatitude, double pLongitude, String pCountry)
	{
		super();
		mType = pType;
		mName = pName;
		if (!mType.matches("@.*"))
		{
			mPlaceName = mName;
		}
		else
		{
			mPlaceName = pCountry;
		}
		mLatitude = pLatitude;
		mLongitude = pLongitude;
		mCountry = pCountry;
	}

	public double getLatitude()
	{
		return mLatitude;
	}

	public void setLatitude(double pLatitude)
	{
		mLatitude = pLatitude;
	}

	public double getLongitude()
	{
		return mLongitude;
	}

	public void setLongitude(double pLongitude)
	{
		mLongitude = pLongitude;
	}

	public double computeDistanceTo(Location pLocation)
	{
		double lLatDiff = mLatitude - pLocation.mLatitude;
		double lLongDiff = mLongitude - pLocation.mLongitude;
		double lDistance = Math.sqrt(lLatDiff * lLatDiff + lLongDiff * lLongDiff);
		return lDistance;
	}

	public String getName()
	{
		return mName;
	}

	public void setName(String pName)
	{
		mName = pName;
	}

	public String getType()
	{
		return mType;
	}

	public void setType(String pType)
	{
		mType = pType;
	}

	public String getSourceString()
	{
		return mSourceString;
	}

	public void setSourceString(String pSourceString)
	{
		mSourceString = pSourceString;
	}

	public boolean equals(Object pObj)
	{
		if (pObj instanceof Location)
		{
			Location lLocation = (Location) pObj;
			boolean lEquals = true;
			lEquals |= (lLocation.mType.equals(mType));
			lEquals |= (lLocation.mName.equals(mName));
			lEquals |= (lLocation.mCountry.equals(mCountry));
			return lEquals;
		}
		else
		{
			return false;
		}

	}

	public int hashCode()
	{
		Double lLongitude = new Double(getLongitude());
		Double lLatitude = new Double(getLatitude());
		return lLongitude.hashCode() ^ lLatitude.hashCode();
	}

	public String getPlaceName()
	{
		return mPlaceName;
	}

	public String getPlaceNameAndCountry()
	{
		String lString;
		if (!mType.matches("@.*"))
		{
			lString = mName + ", " + mCountry;
		}
		else
		{
			lString = mCountry;
		}
		return lString;
	}

	public String getPlaceNameCountryAndCoordinates()
	{
		String lString = getPlaceNameAndCountry() + ", (" + mLatitude + "," + mLongitude + ")";

		return lString;
	}

	public String getCountry()
	{
		return mCountry;
	}

}
