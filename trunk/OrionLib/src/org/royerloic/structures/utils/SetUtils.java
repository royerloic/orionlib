package org.royerloic.structures.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SetUtils<E>
{
	public List<Set<E>> hierarchicalDecomposition(final Collection<Set<E>> pSetCollection)
	{
		final Set<Set<E>> lSetSet = new HashSet<Set<E>>(pSetCollection);
		final List<Set<E>> lResultSetList = new ArrayList<Set<E>>(lSetSet);

		// Map<Edge<Set<E>>, Boolean> lCutCacheMap = new HashMap<Edge<Set<E>>,
		// Boolean>();

		boolean cutFound;
		do
		{
			cutFound = false;
			Set<E> lSetA = null;
			Set<E> lSetB = null;
			Set<E> lNodeSetAIB = null;
			Set<E> lNodeSetAMB = null;
			Set<E> lNodeSetBMA = null;

			outer: for (int i = 0; i < lResultSetList.size(); i++)
				for (int j = 0; j < i; j++)
				{
					lSetA = lResultSetList.get(i);
					lSetB = lResultSetList.get(j);

					lNodeSetAIB = new HashSet<E>(lSetA);
					lNodeSetAIB.retainAll(lSetB);
					if (lNodeSetAIB.isEmpty())
						continue;
					lNodeSetAMB = new HashSet<E>(lSetA);
					lNodeSetAMB.removeAll(lSetB);
					if (lNodeSetAMB.isEmpty())
						continue;
					lNodeSetBMA = new HashSet<E>(lSetB);
					lNodeSetBMA.removeAll(lSetA);
					if (lNodeSetBMA.isEmpty())
						continue;

					cutFound = true;
					break outer;

				}

			if (cutFound)
			{
				lResultSetList.remove(lSetA);
				lResultSetList.remove(lSetB);
				if (!lResultSetList.contains(lNodeSetAMB))
					lResultSetList.add(lNodeSetAMB);
				if (!lResultSetList.contains(lNodeSetBMA))
					lResultSetList.add(lNodeSetBMA);
				if (!lResultSetList.contains(lNodeSetAIB))
					lResultSetList.add(lNodeSetAIB);

			}
			// System.out.println(lResultSetList.size());
		}
		while (cutFound);

		return lResultSetList;
	}

}
