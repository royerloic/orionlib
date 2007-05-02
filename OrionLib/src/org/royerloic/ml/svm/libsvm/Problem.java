package org.royerloic.ml.svm.libsvm;

public class Problem implements java.io.Serializable, Cloneable
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 3843718065256454215L;

	public int			mNumberOfVectors;

	public double[]	mClass;

	public Node[][]	mVectorsTable;

	/**
	 * @see org.royerloic.java.IObject#clone()
	 */
	@Override
	public Object clone()
	{
		try
		{
			final Problem lProblem = (Problem) super.clone();

			for (int i = 0; i < this.mNumberOfVectors; i++)
				lProblem.mVectorsTable[i] = this.mVectorsTable[i].clone();

			return lProblem;
		}
		catch (final CloneNotSupportedException e)
		{
			return null;
		}
	}

}