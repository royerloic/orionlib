package utils.ml.svm.libsvm;

public class Node implements java.io.Serializable
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= -3731797707743471399L;
	public int		mIndex;
	public double	mValue;

	/**
	 * @param pIndex
	 * @param pValue
	 */
	public Node()
	{
		super();
	}

	/**
	 * @param pIndex
	 * @param pValue
	 */
	public Node(final int pIndex, final double pValue)
	{
		super();
		mIndex = pIndex;
		mValue = pValue;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException
	{
		return super.clone();
	}
}