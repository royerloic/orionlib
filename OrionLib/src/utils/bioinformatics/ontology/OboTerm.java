package utils.bioinformatics.ontology;

import java.io.Serializable;
import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * Loic Royer, Copyright (c) 2005, Some Rights Reserved.
 * 
 */
public class OboTerm implements Serializable
{
	private static final Pattern cSeparatorPattern = Pattern.compile("\\:");

	private Integer mId;
	private String mName;
	private String mDefinition;
	private String mNameSpace;

	public OboTerm(String pNameSpace, final Integer pId)
	{
		mNameSpace = pNameSpace;
		mId = pId;
	}

	public OboTerm(String pOboTermString) throws Exception
	{
		final String[] lOboTermArray = cSeparatorPattern.split(pOboTermString, -1);
		if (lOboTermArray.length == 2)
		{
			mNameSpace = lOboTermArray[0].toUpperCase();
			mId = Integer.parseInt(lOboTermArray[1]);
		}
		else
		{
			throw new Exception("Incorrect format for OBO term: " + pOboTermString
													+ "parsed as: "
													+ Arrays.toString(lOboTermArray));
		}
	}

	public String getDefinition()
	{
		return mDefinition;
	}

	public void setDefinition(final String pDefinition)
	{
		mDefinition = pDefinition;
	}

	public Integer getId()
	{
		return mId;
	}

	public String getName()
	{
		return mName;
	}

	public String getNameSpace()
	{
		return mNameSpace;
	}

	public void setNameSpace(final String pNameSpace)
	{
		mNameSpace = pNameSpace;
	}

	@Override
	public boolean equals(final Object pObj)
	{
		if (this == pObj)
			return true;

		final OboTerm lOboTerm = (OboTerm) pObj;

		return mId.equals(lOboTerm.mId);
	}

	@Override
	public int hashCode()
	{
		return mId;
	}

	@Override
	public String toString()
	{
		return mId + ":" + mName;
	}

	public void setName(final String pName)
	{
		mName = pName;
	}

}
