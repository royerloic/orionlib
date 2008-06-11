package utils.structures;

public class Triple<A, B, C>
{
	public A mA;
	public B mB;
	public C mC;

	public Triple(final A pA, final B pB, final C pC)
	{
		super();
		this.mA = pA;
		this.mB = pB;
		this.mC = pC;
	}

	@Override
	public boolean equals(final Object pObj)
	{
		final Triple<A, B, C> lTriple = (Triple<A, B, C>) pObj;
		return this.mA.equals(lTriple.mA) && this.mB.equals(lTriple.mB)
						&& this.mC.equals(lTriple.mC);

	}

	@Override
	public int hashCode()
	{
		return this.mA.hashCode() ^ this.mB.hashCode() ^ this.mC.hashCode();
	}

	@Override
	public String toString()
	{
		return "(" + this.mA.toString()
						+ ","
						+ this.mB.toString()
						+ ","
						+ this.mC.toString()
						+ ")";
	}
}
