package org.royerloic.geolocator;

public class GeoLocator implements Locator
{

	LocationDatabase					mCountryDatabase;

	private LocationDatabase	mBigCityDatabase;

	private LocationDatabase	mAllCityDatabase;

	private LocationDatabase	mUSSDatabase;

	public GeoLocator()
	{
		super();

		this.mCountryDatabase = new LocationDatabase();
		this.mCountryDatabase.setDebug(true);

		this.mCountryDatabase.addListFromFile("org/royerloic/geolocator/data/country-lat-long-country.txt");
		this.mCountryDatabase.addListFromFile("org/royerloic/geolocator/data/nationality-lat-long-country.txt");

		this.mBigCityDatabase = new LocationDatabase();
		this.mBigCityDatabase.setDebug(true);
		this.mBigCityDatabase.addListFromFile("org/royerloic/geolocator/data/city-lat-long-country-pop.txt");

		this.mAllCityDatabase = new LocationDatabase();
		this.mAllCityDatabase.setDebug(true);
		this.mAllCityDatabase.addListFromFile("org/royerloic/geolocator/data/city-lat-long-country.txt");

		this.mUSSDatabase = new LocationDatabase();
		this.mUSSDatabase.setDebug(true);
		this.mUSSDatabase.addListFromFile("org/royerloic/geolocator/data/USState-lat-long-code.txt");

	}

	public Location locateString(final String pString)
	{
		Location lLocation = null;

		lLocation = this.mBigCityDatabase.findLocationInString(pString);
		if (lLocation == null)
		{
			lLocation = this.mCountryDatabase.findLocationInString(pString);

			if (lLocation == null)
				lLocation = this.mUSSDatabase.findLocationInString(pString);
		}

		return lLocation;
	}
}
