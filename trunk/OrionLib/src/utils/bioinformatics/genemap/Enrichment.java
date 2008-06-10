package utils.bioinformatics.genemap;

import java.io.Serializable;
import java.util.Collection;

public class Enrichment implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	public double mUniverseSize;
	public double mSet1Size;
	public double mSet2Size;
	public double mIntersectionSize;

	public double mPValue;
	public double mCoverage;
	public double mCorrectedPValue;
	public Collection<Element> mSet1;
	public Collection<Element> mSet2;
	public Collection<Element> mIntersectionSet;
	
	
	@Override
	public String toString()
	{
		return "("+mCorrectedPValue+"\t"+mCoverage+")";
	}

	
	
}
