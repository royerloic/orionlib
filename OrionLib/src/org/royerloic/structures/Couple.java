package org.royerloic.structures;

public class Couple<A, B>
{
	public A	mA;
	public B	mB;

	public Couple(A pA, B pB)
	{
		super();
		mA = pA;
		mB = pB;

	}

	@Override
	public boolean equals(Object pObj)
	{
		Couple<A, B> lPair = (Couple<A, B>) pObj;
		return (this.mA.equals(lPair.mA) && this.mB.equals(lPair.mB));
	}

	@Override
	public int hashCode()
	{
		return mA.hashCode() ^ mB.hashCode();
	}

	@Override
	public String toString()
	{
		return "(" + mA.toString() + "," + mB.toString() + ")";
	}

}
