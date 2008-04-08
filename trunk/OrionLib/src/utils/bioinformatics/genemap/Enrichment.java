package utils.bioinformatics.genemap;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class Enrichment
{
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


}
