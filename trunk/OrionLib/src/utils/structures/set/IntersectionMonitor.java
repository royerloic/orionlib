package utils.structures.set;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Loic Royer, Copyright (c) 2005, Some Rights Reserved.
 * 
 * @param <E>
 */
public class IntersectionMonitor<E>
{
	private final HashSet<E> mSetA = new HashSet<E>();
	private final HashSet<E> mSetB = new HashSet<E>();
	private boolean mIsIntersecting = false;

	/**
	 * @param pCollection
	 * @return
	 */
	public boolean addToA(final Collection<E> pCollection)
	{
		this.mSetA.addAll(pCollection);

		if (!Collections.disjoint(pCollection, this.mSetB))
			this.mIsIntersecting = true;

		return isIntersecting();
	}

	/**
	 * @param pCollection
	 * @return
	 */
	public boolean addToB(final Collection<E> pCollection)
	{
		this.mSetB.addAll(pCollection);

		if (!Collections.disjoint(pCollection, this.mSetA))
			this.mIsIntersecting = true;

		return isIntersecting();
	}

	/**
	 * @return
	 */
	public boolean isIntersecting()
	{
		return this.mIsIntersecting;
	}

	/**
	 * @return
	 */
	public Set<E> getIntersection()
	{
		final Set<E> lIntersectionSet = new HashSet<E>();
		if (this.mIsIntersecting)
		{
			lIntersectionSet.addAll(this.mSetA);
			lIntersectionSet.retainAll(this.mSetB);
		}
		return lIntersectionSet;
	}

}
