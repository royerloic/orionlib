/*
 * Created on 28.4.2005 by Dipl.-Inf. MSc. Ing Loic Royer
 */
package utils.geolocator;

public class Location
{
	String mType;

	String mName;

	double mLatitude;

	double mLongitude;

	String mCountry;

	String mSourceString;

	private String mPlaceName;

	@Override
	public String toString()
	{
		String lString = new String();
		if ((mType != null) && mType.equalsIgnoreCase("city"))
			lString = mName + ", ";
		lString += mCountry + ", Coordinates: ("
								+ mLatitude
								+ ", "
								+ mLongitude
								+ ")";
		if (mSourceString != null)
			lString += "\n '" + mSourceString + "' \n";
		return lString;
	}

	private Location()
	{
		super();
		// TODO Auto-generated constructor stub
	}

	public Location(final String pType,
									final String pName,
									final double pLatitude,
									final double pLongitude,
									final String pCountry)
	{
		super();
		mType = pType;
		mName = pName;
		if (!mType.matches("@.*"))
			mPlaceName = mName;
		else
			mPlaceName = pCountry;
		mLatitude = pLatitude;
		mLongitude = pLongitude;
		mCountry = pCountry;
	}

	public double getLatitude()
	{
		return mLatitude;
	}

	public void setLatitude(final double pLatitude)
	{
		mLatitude = pLatitude;
	}

	public double getLongitude()
	{
		return mLongitude;
	}

	public void setLongitude(final double pLongitude)
	{
		mLongitude = pLongitude;
	}

	public double computeDistanceTo(final Location pLocation)
	{
		final double lLatDiff = mLatitude - pLocation.mLatitude;
		final double lLongDiff = mLongitude - pLocation.mLongitude;
		final double lDistance = Math.sqrt(lLatDiff * lLatDiff
																				+ lLongDiff
																				* lLongDiff);
		return lDistance;
	}

	public String getName()
	{
		return mName;
	}

	public void setName(final String pName)
	{
		mName = pName;
	}

	public String getType()
	{
		return mType;
	}

	public void setType(final String pType)
	{
		mType = pType;
	}

	public String getSourceString()
	{
		return mSourceString;
	}

	public void setSourceString(final String pSourceString)
	{
		mSourceString = pSourceString;
	}

	@Override
	public boolean equals(final Object pObj)
	{
		if (pObj instanceof Location)
		{
			final Location lLocation = (Location) pObj;
			boolean lEquals = true;
			lEquals |= (lLocation.mType.equals(mType));
			lEquals |= (lLocation.mName.equals(mName));
			lEquals |= (lLocation.mCountry.equals(mCountry));
			return lEquals;
		}
		else
			return false;

	}

	@Override
	public int hashCode()
	{
		final Double lLongitude = new Double(getLongitude());
		final Double lLatitude = new Double(getLatitude());
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
			lString = mName + ", " + mCountry;
		else
			lString = mCountry;
		return lString;
	}

	public String getPlaceNameCountryAndCoordinates()
	{
		final String lString = getPlaceNameAndCountry() + ", ("
														+ mLatitude
														+ ","
														+ mLongitude
														+ ")";

		return lString;
	}

	public String getCountry()
	{
		return mCountry;
	}

}
