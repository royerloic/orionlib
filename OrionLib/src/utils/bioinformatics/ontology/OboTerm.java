package utils.bioinformatics.ontology;


/**
 * Loic Royer, Copyright (c) 2005, Some Rights Reserved.
 * 
 */
public class OboTerm
{

	private Integer	mId;
	private String	mName;
	private String	mDefinition;
	private String	mNameSpace;

	public OboTerm(final Integer pId)
	{
		mId = pId;
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
