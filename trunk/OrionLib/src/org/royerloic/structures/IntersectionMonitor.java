package org.royerloic.structures;

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
	private HashSet<E>	mSetA						= new HashSet<E>();
	private HashSet<E>	mSetB						= new HashSet<E>();
	private boolean			mIsIntersecting	= false;

	/**
	 * @param pCollection
	 * @return
	 */
	public boolean addToA(Collection<E> pCollection)
	{
		mSetA.addAll(pCollection);

		if (!Collections.disjoint(pCollection, mSetB))
			mIsIntersecting = true;

		return isIntersecting();
	}

	/**
	 * @param pCollection
	 * @return
	 */
	public boolean addToB(Collection<E> pCollection)
	{
		mSetB.addAll(pCollection);

		if (!Collections.disjoint(pCollection, mSetA))
			mIsIntersecting = true;

		return isIntersecting();
	}

	/**
	 * @return
	 */
	public boolean isIntersecting()
	{
		return mIsIntersecting;
	}

	/**
	 * @return
	 */
	public Set<E> getIntersection()
	{
		Set<E> lIntersectionSet = new HashSet<E>();
		if (mIsIntersecting)
		{
			lIntersectionSet.addAll(mSetA);
			lIntersectionSet.retainAll(mSetB);
		}
		return lIntersectionSet;
	}

}
