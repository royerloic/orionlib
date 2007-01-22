package org.royerloic.structures;

public class Triple<A, B, C>
{
	public A	mA;
	public B	mB;
	public C	mC;

	public Triple(A pA, B pB, C pC)
	{
		super();
		mA = pA;
		mB = pB;
		mC = pC;
	}

	@Override
	public boolean equals(Object pObj)
	{
		Triple<A, B, C> lTriple = (Triple<A, B, C>) pObj;
		return (this.mA.equals(lTriple.mA) && this.mB.equals(lTriple.mB) && this.mC.equals(lTriple.mC));

	}

	@Override
	public int hashCode()
	{
		return mA.hashCode() ^ mB.hashCode() ^ mC.hashCode();
	}

	@Override
	public String toString()
	{
		return "(" + mA.toString() + "," + mB.toString() + "," + mC.toString() + ")";
	}
}
