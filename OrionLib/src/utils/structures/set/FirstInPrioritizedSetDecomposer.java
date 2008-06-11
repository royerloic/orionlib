package utils.structures.set;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import utils.structures.Pair;
import utils.structures.map.HashSetMap;

public class FirstInPrioritizedSetDecomposer<E, A>
{
	private final HashSetMap<Set<E>, A> mSetMap = new HashSetMap<Set<E>, A>();

	public HashMap<Set<E>, Set<A>> getSetsAndAttributes()
	{
		return mSetMap;
	}

	public void addSet(final A pAttribute, final E... lSetAsArray)
	{
		final HashSet<E> lSet = new HashSet<E>();
		for (final E lE : lSetAsArray)
		{
			lSet.add(lE);
		}
		addSet(lSet, pAttribute);
	}

	public void addSet(final Set<E> lSet, final A pAttribute)
	{
		final Set<E> lCuttingSet = ifNotCompatibleReturnCuttingSet(lSet);
		if (lCuttingSet == null)
		{
			mSetMap.put(lSet, pAttribute);
		}
		else
		{
			final Pair<Set<E>> lPairOfCuttedSets = cutFirstWithSecond(lSet,
																																lCuttingSet);

			for (final Set<E> lSetPart : lPairOfCuttedSets)
			{
				addSet(lSetPart, pAttribute);
			}
		}
	}

	private Pair<Set<E>> cutFirstWithSecond(final Set<E> pSet,
																					final Set<E> pCuttingSet)
	{
		final HashSet<E> lFirstPart = new HashSet<E>();
		lFirstPart.addAll(pSet);
		lFirstPart.removeAll(pCuttingSet);

		final HashSet<E> lSecondPart = new HashSet<E>();
		lSecondPart.addAll(pSet);
		lSecondPart.retainAll(pCuttingSet);

		final Pair<Set<E>> lPairOfCuttedSets = new Pair<Set<E>>(lFirstPart,
																														lSecondPart);
		return lPairOfCuttedSets;
	}

	private Set<E> ifNotCompatibleReturnCuttingSet(final Set<E> pSet)
	{
		for (final Set<E> lSet : mSetMap.keySet())
		{
			if (strictlyIntersecting(lSet, pSet))
			{
				return lSet;
			}
		}
		return null;
	}

	private boolean strictlyIntersecting(final Set<E> pSet1, final Set<E> pSet2)
	{
		if (pSet1.containsAll(pSet2))
		{
			return false;
		}
		if (pSet2.containsAll(pSet1))
		{
			return false;
		}

		final HashSet<E> lIntersection = new HashSet<E>();
		lIntersection.addAll(pSet1);
		lIntersection.retainAll(pSet2);

		return !lIntersection.isEmpty();
	}
}
