package org.royerloic.ml.svm.libsvm;

public class Node implements java.io.Serializable
{
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
	public Node(int pIndex, double pValue)
	{
		super();
		mIndex = pIndex;
		mValue = pValue;
	}

	protected Object clone() throws CloneNotSupportedException
	{
		return super.clone();
	}
}