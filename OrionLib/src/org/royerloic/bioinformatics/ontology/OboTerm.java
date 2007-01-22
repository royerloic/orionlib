package org.royerloic.bioinformatics.ontology;


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

	public OboTerm(Integer pId)
	{
		mId = pId;
	}

	public String getDefinition()
	{
		return mDefinition;
	}

	public void setDefinition(String pDefinition)
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

	public void setNameSpace(String pNameSpace)
	{
		mNameSpace = pNameSpace;
	}

	@Override
	public boolean equals(Object pObj)
	{
		if (this == pObj)
			return true;

		OboTerm lOboTerm = (OboTerm) pObj;

		return this.mId.equals(lOboTerm.mId);
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

	public void setName(String pName)
	{
		mName = pName;
	}

}
