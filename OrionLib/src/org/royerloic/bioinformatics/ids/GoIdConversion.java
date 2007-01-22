package org.royerloic.bioinformatics.ids;

public class GoIdConversion
{
	public static final Integer getIdFromString(String pGoId)
	{
		final String lGoIdString = pGoId.substring(3);
		final Integer lGoId = Integer.parseInt(lGoIdString.trim());
		return lGoId;
	}
}
