package utils.geolocator;

public class GeoLocator implements Locator
{

	LocationDatabase mCountryDatabase;

	private LocationDatabase mBigCityDatabase;

	private LocationDatabase mAllCityDatabase;

	private LocationDatabase mUSSDatabase;

	public GeoLocator()
	{
		super();

		mCountryDatabase = new LocationDatabase();
		mCountryDatabase.setDebug(true);

		mCountryDatabase.addListFromFile("org/royerloic/geolocator/data/country-lat-long-country.txt");
		mCountryDatabase.addListFromFile("org/royerloic/geolocator/data/nationality-lat-long-country.txt");

		mBigCityDatabase = new LocationDatabase();
		mBigCityDatabase.setDebug(true);
		mBigCityDatabase.addListFromFile("org/royerloic/geolocator/data/city-lat-long-country-pop.txt");

		mAllCityDatabase = new LocationDatabase();
		mAllCityDatabase.setDebug(true);
		mAllCityDatabase.addListFromFile("org/royerloic/geolocator/data/city-lat-long-country.txt");

		mUSSDatabase = new LocationDatabase();
		mUSSDatabase.setDebug(true);
		mUSSDatabase.addListFromFile("org/royerloic/geolocator/data/USState-lat-long-code.txt");

	}

	public Location locateString(final String pString)
	{
		Location lLocation = null;

		lLocation = mBigCityDatabase.findLocationInString(pString);
		if (lLocation == null)
		{
			lLocation = mCountryDatabase.findLocationInString(pString);

			if (lLocation == null)
				lLocation = mUSSDatabase.findLocationInString(pString);
		}

		return lLocation;
	}
}
