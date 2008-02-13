package utils.structures.set;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import utils.structures.Pair;
import utils.structures.map.HashSetMap;

public class FirstInPrioritizedSetDecomposer<E, A>
{
	private HashSetMap<Set<E>, A> mSetMap = new HashSetMap<Set<E>, A>();

	public HashMap<Set<E>, Set<A>> getSetsAndAttributes()
	{
		return mSetMap;
	}

	public void addSet(A pAttribute, E... lSetAsArray)
	{
		HashSet<E> lSet = new HashSet<E>();
		for (E lE : lSetAsArray)
		{
			lSet.add(lE);
		}
		addSet(lSet, pAttribute);
	}

	public void addSet(Set<E> lSet, A pAttribute)
	{
		final Set<E> lCuttingSet = ifNotCompatibleReturnCuttingSet(lSet);
		if (lCuttingSet == null)
		{
			mSetMap.put(lSet, pAttribute);
		}
		else
		{
			Pair<Set<E>> lPairOfCuttedSets = cutFirstWithSecond(lSet, lCuttingSet);

			for (Set<E> lSetPart : lPairOfCuttedSets)
			{
				addSet(lSetPart,pAttribute);
			}
		}
	}

	private Pair<Set<E>> cutFirstWithSecond(Set<E> pSet, Set<E> pCuttingSet)
	{
		HashSet<E> lFirstPart = new HashSet<E>();
		lFirstPart.addAll(pSet);
		lFirstPart.removeAll(pCuttingSet);

		HashSet<E> lSecondPart = new HashSet<E>();
		lSecondPart.addAll(pSet);
		lSecondPart.retainAll(pCuttingSet);

		Pair<Set<E>> lPairOfCuttedSets = new Pair<Set<E>>(lFirstPart, lSecondPart);
		return lPairOfCuttedSets;
	}

	private Set<E> ifNotCompatibleReturnCuttingSet(Set<E> pSet)
	{
		for (Set<E> lSet : mSetMap.keySet())
			if (strictlyIntersecting(lSet, pSet))
				return lSet;
		return null;
	}

	private boolean strictlyIntersecting(Set<E> pSet1, Set<E> pSet2)
	{
		if (pSet1.containsAll(pSet2))
			return false;
		if (pSet2.containsAll(pSet1))
			return false;

		HashSet<E> lIntersection = new HashSet<E>();
		lIntersection.addAll(pSet1);
		lIntersection.retainAll(pSet2);

		return !lIntersection.isEmpty();
	}
}
