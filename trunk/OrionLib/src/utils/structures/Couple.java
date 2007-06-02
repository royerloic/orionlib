package utils.structures;

public class Couple<A, B>
{
	public A	mA;
	public B	mB;

	public Couple(final A pA, final B pB)
	{
		super();
		this.mA = pA;
		this.mB = pB;

	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(final Object pObj)
	{
		final Couple<A, B> lPair = (Couple<A, B>) pObj;
		return (this.mA.equals(lPair.mA) && this.mB.equals(lPair.mB));
	}

	@Override
	public int hashCode()
	{
		return this.mA.hashCode() ^ this.mB.hashCode();
	}

	@Override
	public String toString()
	{
		return "(" + this.mA.toString() + "," + this.mB.toString() + ")";
	}

}
