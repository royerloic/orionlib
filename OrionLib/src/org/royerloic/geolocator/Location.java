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

	@Override
	public String toString()
	{
		String lString = new String();
		if ((this.mType != null) && this.mType.equalsIgnoreCase("city"))
			lString = this.mName + ", ";
		lString += this.mCountry + ", Coordinates: (" + this.mLatitude + ", " + this.mLongitude + ")";
		if (this.mSourceString != null)
			lString += "\n '" + this.mSourceString + "' \n";
		return lString;
	}

	private Location()
	{
		super();
		// TODO Auto-generated constructor stub
	}

	public Location(final String pType, final String pName, final double pLatitude, final double pLongitude, final String pCountry)
	{
		super();
		this.mType = pType;
		this.mName = pName;
		if (!this.mType.matches("@.*"))
			this.mPlaceName = this.mName;
		else
			this.mPlaceName = pCountry;
		this.mLatitude = pLatitude;
		this.mLongitude = pLongitude;
		this.mCountry = pCountry;
	}

	public double getLatitude()
	{
		return this.mLatitude;
	}

	public void setLatitude(final double pLatitude)
	{
		this.mLatitude = pLatitude;
	}

	public double getLongitude()
	{
		return this.mLongitude;
	}

	public void setLongitude(final double pLongitude)
	{
		this.mLongitude = pLongitude;
	}

	public double computeDistanceTo(final Location pLocation)
	{
		final double lLatDiff = this.mLatitude - pLocation.mLatitude;
		final double lLongDiff = this.mLongitude - pLocation.mLongitude;
		final double lDistance = Math.sqrt(lLatDiff * lLatDiff + lLongDiff * lLongDiff);
		return lDistance;
	}

	public String getName()
	{
		return this.mName;
	}

	public void setName(final String pName)
	{
		this.mName = pName;
	}

	public String getType()
	{
		return this.mType;
	}

	public void setType(final String pType)
	{
		this.mType = pType;
	}

	public String getSourceString()
	{
		return this.mSourceString;
	}

	public void setSourceString(final String pSourceString)
	{
		this.mSourceString = pSourceString;
	}

	@Override
	public boolean equals(final Object pObj)
	{
		if (pObj instanceof Location)
		{
			final Location lLocation = (Location) pObj;
			boolean lEquals = true;
			lEquals |= (lLocation.mType.equals(this.mType));
			lEquals |= (lLocation.mName.equals(this.mName));
			lEquals |= (lLocation.mCountry.equals(this.mCountry));
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
		return this.mPlaceName;
	}

	public String getPlaceNameAndCountry()
	{
		String lString;
		if (!this.mType.matches("@.*"))
			lString = this.mName + ", " + this.mCountry;
		else
			lString = this.mCountry;
		return lString;
	}

	public String getPlaceNameCountryAndCoordinates()
	{
		final String lString = getPlaceNameAndCountry() + ", (" + this.mLatitude + "," + this.mLongitude + ")";

		return lString;
	}

	public String getCountry()
	{
		return this.mCountry;
	}

}
