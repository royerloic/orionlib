package utils.bioinformatics.genome;

import java.io.Serializable;

public class ParseException extends RuntimeException implements Serializable
{

	public ParseException(String pString)
	{
		super(pString);
	}

}
