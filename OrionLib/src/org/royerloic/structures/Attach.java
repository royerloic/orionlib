package org.royerloic.structures;

import java.util.HashSet;
import java.util.Set;

public class Attach<O, V>
{
	O	mO;
	V	mV;

	/**
	 * @return
	 */
	public O getO()
	{
		return mO;
	}

	/**
	 * @param pO
	 */
	public void setO(O pO)
	{
		mO = pO;
	}

	/**
	 * @return
	 */
	public V getV()
	{
		return mV;
	}

	/**
	 * @param pV
	 */
	public void setV(V pV)
	{
		mV = pV;
	}

	/**
	 * @param pO
	 * @param pV
	 */
	public Attach(O pO, V pV)
	{
		mO = pO;
		mV = pV;
	}

	@SuppressWarnings("unchecked")
	@Override
	public final boolean equals(Object pObj)
	{
		final Attach<O, V> lAttachedValue = (Attach<O, V>) pObj;
		return (this.mO.equals(lAttachedValue.mO));
	}

	public final boolean fastEquals(Attach<O, V> pAttachedValue)
	{
		if (this == pAttachedValue)
			return true;
		else if (this.hashCode() != pAttachedValue.hashCode())
			return false;
		else
		{
			return (this.mO.equals(pAttachedValue.mO));
		}
	}

	@Override
	public final int hashCode()
	{
		return mO.hashCode();
	}

	@Override
	public final String toString()
	{
		return mO.toString() + "(" + mV.toString() + ")";
	}

	public static final <O, V> Set<Attach<O, V>> attachValueToAll(Set<O> pOSet, V pValue)
	{
		Set<Attach<O, V>> lAttachedValueSet = new HashSet<Attach<O, V>>();
		for (O lO : pOSet)
		{
			Attach<O, V> lAttachedValue = new Attach<O, V>(lO, pValue);
			lAttachedValueSet.add(lAttachedValue);
		}
		return lAttachedValueSet;
	}

}
