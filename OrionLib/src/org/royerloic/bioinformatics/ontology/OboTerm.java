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

	public OboTerm(final Integer pId)
	{
		this.mId = pId;
	}

	public String getDefinition()
	{
		return this.mDefinition;
	}

	public void setDefinition(final String pDefinition)
	{
		this.mDefinition = pDefinition;
	}

	public Integer getId()
	{
		return this.mId;
	}

	public String getName()
	{
		return this.mName;
	}

	public String getNameSpace()
	{
		return this.mNameSpace;
	}

	public void setNameSpace(final String pNameSpace)
	{
		this.mNameSpace = pNameSpace;
	}

	@Override
	public boolean equals(final Object pObj)
	{
		if (this == pObj)
			return true;

		final OboTerm lOboTerm = (OboTerm) pObj;

		return this.mId.equals(lOboTerm.mId);
	}

	@Override
	public int hashCode()
	{
		return this.mId;
	}

	@Override
	public String toString()
	{
		return this.mId + ":" + this.mName;
	}

	public void setName(final String pName)
	{
		this.mName = pName;
	}

}
