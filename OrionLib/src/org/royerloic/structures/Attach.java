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
		return this.mO;
	}

	/**
	 * @param pO
	 */
	public void setO(final O pO)
	{
		this.mO = pO;
	}

	/**
	 * @return
	 */
	public V getV()
	{
		return this.mV;
	}

	/**
	 * @param pV
	 */
	public void setV(final V pV)
	{
		this.mV = pV;
	}

	/**
	 * @param pO
	 * @param pV
	 */
	public Attach(final O pO, final V pV)
	{
		this.mO = pO;
		this.mV = pV;
	}

	@SuppressWarnings("unchecked")
	@Override
	public final boolean equals(final Object pObj)
	{
		final Attach<O, V> lAttachedValue = (Attach<O, V>) pObj;
		return (this.mO.equals(lAttachedValue.mO));
	}

	public final boolean fastEquals(final Attach<O, V> pAttachedValue)
	{
		if (this == pAttachedValue)
			return true;
		else if (this.hashCode() != pAttachedValue.hashCode())
			return false;
		else
			return (this.mO.equals(pAttachedValue.mO));
	}

	@Override
	public final int hashCode()
	{
		return this.mO.hashCode();
	}

	@Override
	public final String toString()
	{
		return this.mO.toString() + "(" + this.mV.toString() + ")";
	}

	public static final <O, V> Set<Attach<O, V>> attachValueToAll(final Set<O> pOSet, final V pValue)
	{
		final Set<Attach<O, V>> lAttachedValueSet = new HashSet<Attach<O, V>>();
		for (final O lO : pOSet)
		{
			final Attach<O, V> lAttachedValue = new Attach<O, V>(lO, pValue);
			lAttachedValueSet.add(lAttachedValue);
		}
		return lAttachedValueSet;
	}

}
