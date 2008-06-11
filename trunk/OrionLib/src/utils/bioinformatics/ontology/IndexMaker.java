package utils.bioinformatics.ontology;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import utils.structures.lattice.Lattice;

public class IndexMaker<O>
{
	private Lattice<O> mLattice = null;
	Map<O, Integer> mIndexMap = new HashMap<O, Integer>();
	private int mIndex = 0;

	public IndexMaker(final Lattice<O> pLattice)
	{
		super();
		// TODO Auto-generated constructor stub
		this.mLattice = pLattice;
	}

	public final Map<O, Integer> computeMapBreathFirst(final Collection<O> pRoots)
	{
		this.mIndex = 0;
		this.mIndexMap.clear();
		for (final O lO : pRoots)
		{
			if (!this.mIndexMap.containsKey(lO))
			{
				this.mIndexMap.put(lO, this.mIndex);
				this.mIndex++;
			}
		}
		for (final O lO : pRoots)
		{
			breathFirstIndexRecursive(lO);
		}

		return this.mIndexMap;
	}

	private final void breathFirstIndexRecursive(final O pNode)
	{
		final Set<O> lChildrenSet = this.mLattice.getChildren(pNode);
		for (final O lO : lChildrenSet)
		{
			if (!this.mIndexMap.containsKey(lO))
			{
				this.mIndexMap.put(lO, this.mIndex);
				this.mIndex++;
			}
		}
		for (final O lO : lChildrenSet)
		{
			breathFirstIndexRecursive(lO);
		}
	}

	public final Map<O, Integer> computeMapDepthFirst(final Collection<O> pRoots)
	{
		this.mIndex = 0;
		this.mIndexMap.clear();
		for (final O lO : pRoots)
		{
			if (!this.mIndexMap.containsKey(lO))
			{
				this.mIndexMap.put(lO, this.mIndex);
				this.mIndex++;
				deapthFirstIndexRecursive(lO);
			}
		}

		return this.mIndexMap;
	}

	private final void deapthFirstIndexRecursive(final O pNode)
	{
		final Set<O> lChildrenSet = this.mLattice.getChildren(pNode);
		for (final O lO : lChildrenSet)
		{
			if (!this.mIndexMap.containsKey(lO))
			{
				this.mIndexMap.put(lO, this.mIndex);
				this.mIndex++;
				deapthFirstIndexRecursive(lO);
			}
		}
	}

}
