package utils.bioinformatics.genome;

import java.io.Serializable;

public class ParseException extends RuntimeException implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ParseException(final String pString)
	{
		super(pString);
	}

}
