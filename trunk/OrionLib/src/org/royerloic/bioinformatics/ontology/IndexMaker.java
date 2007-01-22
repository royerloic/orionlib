package org.royerloic.bioinformatics.ontology;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.royerloic.structures.lattice.Lattice;

public class IndexMaker<O>
{
	private Lattice<O>	mLattice	= null;
	Map<O, Integer>			mIndexMap	= new HashMap<O, Integer>();
	private int					mIndex		= 0;

	private IndexMaker()
	{
		super();
		// TODO Auto-generated constructor stub
	}

	public IndexMaker(Lattice<O> pLattice)
	{
		super();
		// TODO Auto-generated constructor stub
		mLattice = pLattice;
	}

	public final Map<O, Integer> computeMapBreathFirst(Collection<O> pRoots)
	{
		mIndex = 0;
		mIndexMap.clear();
		for (O lO : pRoots)
			if (!mIndexMap.containsKey(lO))
			{
				mIndexMap.put(lO, mIndex);
				mIndex++;
			}
		for (O lO : pRoots)
		{
			breathFirstIndexRecursive(lO);
		}

		return mIndexMap;
	}

	private final void breathFirstIndexRecursive(O pNode)
	{
		Set<O> lChildrenSet = mLattice.getChildren(pNode);
		for (O lO : lChildrenSet)
			if (!mIndexMap.containsKey(lO))
			{
				mIndexMap.put(lO, mIndex);
				mIndex++;
			}
		for (O lO : lChildrenSet)
		{
			breathFirstIndexRecursive(lO);
		}
	}

	public final Map<O, Integer> computeMapDepthFirst(Collection<O> pRoots)
	{
		mIndex = 0;
		mIndexMap.clear();
		for (O lO : pRoots)
			if (!mIndexMap.containsKey(lO))
			{
				mIndexMap.put(lO, mIndex);
				mIndex++;
				deapthFirstIndexRecursive(lO);
			}

		return mIndexMap;
	}

	private final void deapthFirstIndexRecursive(O pNode)
	{
		Set<O> lChildrenSet = mLattice.getChildren(pNode);
		for (O lO : lChildrenSet)
			if (!mIndexMap.containsKey(lO))
			{
				mIndexMap.put(lO, mIndex);
				mIndex++;
				deapthFirstIndexRecursive(lO);
			}
	}

}
