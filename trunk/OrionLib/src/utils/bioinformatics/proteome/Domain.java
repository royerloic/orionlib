package utils.bioinformatics.proteome;

import java.util.HashSet;

import utils.bioinformatics.ontology.OboTerm;

public class Domain
{

	protected final String mId;
	protected final String mInterProId;
	protected final String mDescription;
	protected final String mSource;
	protected final int mStart; //inclusive
	protected final int mEnd; //exclusive
	protected final double mEValue;
	
	protected final HashSet<OboTerm> mOBOTermSet = new HashSet<OboTerm>();

	
	
	
	
	public Domain(String pId,
	              String pInterProId,
								String pDescription,
								String pSource,
								int pStart,
								int pEnd,
								double pEValue)
	{
		super();
		mId = pId;
		mInterProId = pInterProId;
		mSource = pSource;
		mStart = pStart;
		mEnd = pEnd;
		mEValue = pEValue;
		mDescription = pDescription;
	}



	public String getId()
	{
		return mId;
	}

}
