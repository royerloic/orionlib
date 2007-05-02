/*
 * Created on 10.01.2005 by Dipl.-Inf. MSc. Ing Loic Royer
 */
package org.royerloic.ml.svm;

import java.util.ArrayList;
import java.util.List;

import org.royerloic.math.INumericalVector;
import org.royerloic.math.IVectorArray;
import org.royerloic.ml.svm.libsvm.Node;
import org.royerloic.ml.svm.libsvm.Problem;

/**
 * @author Dipl.-Inf. MSc. Ing. Loic Royer
 * 
 */
public class LabelledVectorSet implements ILabelledVectorSet
{
	List							mVectorList;

	List							mClassList;

	LabelledVectorSet	mTrainSubset;

	LabelledVectorSet	mTestSubset;

	/**
	 * 
	 */
	public LabelledVectorSet()
	{
		super();
		this.mVectorList = new ArrayList();
		this.mClassList = new ArrayList();
	}

	/**
	 * @see org.royerloic.ml.svm.ILabelledVectorSet#size()
	 */
	public int size()
	{
		// TODO Auto-generated method stub
		return this.mVectorList.size();
	}

	/**
	 * @see org.royerloic.ml.svm.ILabelledVectorSet#addVector(org.royerloic.math.IVectorArray,
	 *      double)
	 */
	public void addVector(final IVectorArray pVector, final double pClass)
	{
		this.mVectorList.add(pVector);
		this.mClassList.add(new Double(pClass));
	}

	/**
	 * @see org.royerloic.ml.svm.ILabelledVectorSet#delVector(int)
	 */
	public void delVector(final int pIndex)
	{
		this.mVectorList.remove(pIndex);
		this.mClassList.remove(pIndex);
	}

	/**
	 * @see org.royerloic.ml.svm.ILabelledVectorSet#getVector(int)
	 */
	public IVectorArray getVector(final int pIndex)
	{
		return (IVectorArray) this.mVectorList.get(pIndex);
	}

	/**
	 * @see org.royerloic.ml.svm.ILabelledVectorSet#getClass(int)
	 */
	public double getClass(final int pIndex)
	{
		return ((Double) this.mClassList.get(pIndex)).doubleValue();
	}

	public Problem toProblem()
	{
		final Problem lResult = new Problem();

		lResult.mNumberOfVectors = this.mVectorList.size();

		lResult.mClass = new double[this.mVectorList.size()];
		lResult.mVectorsTable = new Node[this.mVectorList.size()][];
		for (int i = 0; i < this.mVectorList.size(); i++)
		{
			final IVectorArray lVectorArray = (IVectorArray) this.mVectorList.get(i);
			final int lVectorSize = lVectorArray.getDimension();
			lResult.mVectorsTable[i] = new Node[lVectorSize];
			for (int j = 0; j < lVectorSize; j++)
				lResult.mVectorsTable[i][j] = new Node(j, lVectorArray.get(j));

			lResult.mClass[i] = ((Double) this.mClassList.get(i)).doubleValue();
		}

		return lResult;
	}

	/**
	 * @see org.royerloic.ml.svm.ILabelledVectorSet#generateTrainTestRandomSubsets(double)
	 */
	public void generateTrainTestRandomSubsets(final double pRatio)
	{
		if (this.mTrainSubset == null)
		{
			this.mTrainSubset = new LabelledVectorSet();
			this.mTestSubset = new LabelledVectorSet();
		}
		else
		{
			this.mTrainSubset.clear();
			this.mTestSubset.clear();
		}

		INumericalVector lVector = null;
		double lClass = 0;

		for (int i = 0; i < size(); i++)
		{
			lVector = (INumericalVector) getVector(i);
			lClass = getClass(i);

			if (Math.random() < pRatio)
				this.mTrainSubset.addVector(lVector, lClass);
			else
				this.mTestSubset.addVector(lVector, lClass);

		}

		if (this.mTestSubset.size() == 0)
		{
			this.mTestSubset.addVector(lVector, lClass);
			this.mTrainSubset.delVector(size() - 1);
		}

	}

	/**
	 * @see org.royerloic.ml.svm.ILabelledVectorSet#generateTrainTestRandomSubsets(double)
	 */
	public void generateTrainTestUnitarySubsets(final int pIndex)
	{
		if (this.mTrainSubset == null)
		{
			this.mTrainSubset = new LabelledVectorSet();
			this.mTestSubset = new LabelledVectorSet();
		}
		else
		{
			this.mTrainSubset.clear();
			this.mTestSubset.clear();
		}

		INumericalVector lVector = null;
		double lClass = 0;

		for (int i = 0; i < size(); i++)
		{
			lVector = (INumericalVector) getVector(i);
			lClass = getClass(i);
			if (pIndex != i)
				this.mTrainSubset.addVector(lVector, lClass);
			else
				this.mTestSubset.addVector(lVector, lClass);

		}

	}

	/**
	 * @see org.royerloic.ml.svm.ILabelledVectorSet#getTrainSubset()
	 */
	public ILabelledVectorSet getTrainSubset()
	{
		return this.mTrainSubset;
	}

	/**
	 * @see org.royerloic.ml.svm.ILabelledVectorSet#getTestSubset()
	 */
	public ILabelledVectorSet getTestSubset()
	{
		return this.mTestSubset;
	}

	/**
	 * @see org.royerloic.ml.svm.ILabelledVectorSet#clear()
	 */
	public void clear()
	{
		this.mVectorList.clear();
		this.mClassList.clear();
		if (this.mTrainSubset != null)
		{
			this.mTrainSubset.clear();
			this.mTestSubset.clear();
		}
	}

}