package org.royerloic.ml.svm.libsvm;

public class Problem implements java.io.Serializable, Cloneable
{
	public int			mNumberOfVectors;

	public double[]	mClass;

	public Node[][]	mVectorsTable;

	/**
	 * @see org.royerloic.java.IObject#clone()
	 */
	public Object clone()
	{
		try
		{
			Problem lProblem = (Problem) super.clone();

			for (int i = 0; i < mNumberOfVectors; i++)
			{
				lProblem.mVectorsTable[i] = (Node[]) mVectorsTable[i].clone();
			}

			return lProblem;
		}
		catch (CloneNotSupportedException e)
		{
			return null;
		}
	}

}