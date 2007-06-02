package utils.bioinformatics.ids;

public class InterproIdConversion
{
	public static final Integer getIdFromString(final String pInterproId)
	{
		final String lInterproIdString = pInterproId.substring(3);
		final Integer lInterproId = Integer.parseInt(lInterproIdString.trim());
		return lInterproId;
	}
}
