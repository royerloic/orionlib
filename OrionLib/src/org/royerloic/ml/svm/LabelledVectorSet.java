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
		mVectorList = new ArrayList();
		mClassList = new ArrayList();
	}

	/**
	 * @see org.royerloic.ml.svm.ILabelledVectorSet#size()
	 */
	public int size()
	{
		// TODO Auto-generated method stub
		return mVectorList.size();
	}

	/**
	 * @see org.royerloic.ml.svm.ILabelledVectorSet#addVector(org.royerloic.math.IVectorArray,
	 *      double)
	 */
	public void addVector(IVectorArray pVector, double pClass)
	{
		mVectorList.add(pVector);
		mClassList.add(new Double(pClass));
	}

	/**
	 * @see org.royerloic.ml.svm.ILabelledVectorSet#delVector(int)
	 */
	public void delVector(int pIndex)
	{
		mVectorList.remove(pIndex);
		mClassList.remove(pIndex);
	}

	/**
	 * @see org.royerloic.ml.svm.ILabelledVectorSet#getVector(int)
	 */
	public IVectorArray getVector(int pIndex)
	{
		return (IVectorArray) mVectorList.get(pIndex);
	}

	/**
	 * @see org.royerloic.ml.svm.ILabelledVectorSet#getClass(int)
	 */
	public double getClass(int pIndex)
	{
		return ((Double) mClassList.get(pIndex)).doubleValue();
	}

	public Problem toProblem()
	{
		Problem lResult = new Problem();

		lResult.mNumberOfVectors = mVectorList.size();

		lResult.mClass = new double[mVectorList.size()];
		lResult.mVectorsTable = new Node[mVectorList.size()][];
		for (int i = 0; i < mVectorList.size(); i++)
		{
			IVectorArray lVectorArray = (IVectorArray) mVectorList.get(i);
			int lVectorSize = lVectorArray.getDimension();
			lResult.mVectorsTable[i] = new Node[lVectorSize];
			for (int j = 0; j < lVectorSize; j++)
			{
				lResult.mVectorsTable[i][j] = new Node(j, lVectorArray.get(j));
			}

			lResult.mClass[i] = ((Double) mClassList.get(i)).doubleValue();
		}

		return lResult;
	}

	/**
	 * @see org.royerloic.ml.svm.ILabelledVectorSet#generateTrainTestRandomSubsets(double)
	 */
	public void generateTrainTestRandomSubsets(double pRatio)
	{
		if (mTrainSubset == null)
		{
			mTrainSubset = new LabelledVectorSet();
			mTestSubset = new LabelledVectorSet();
		}
		else
		{
			mTrainSubset.clear();
			mTestSubset.clear();
		}

		INumericalVector lVector = null;
		double lClass = 0;

		for (int i = 0; i < size(); i++)
		{
			lVector = (INumericalVector) getVector(i);
			lClass = getClass(i);

			if (Math.random() < pRatio)
			{
				mTrainSubset.addVector(lVector, lClass);
			}
			else
			{
				mTestSubset.addVector(lVector, lClass);
			}

		}

		if (mTestSubset.size() == 0)
		{
			mTestSubset.addVector(lVector, lClass);
			mTrainSubset.delVector(size() - 1);
		}

	}

	/**
	 * @see org.royerloic.ml.svm.ILabelledVectorSet#generateTrainTestRandomSubsets(double)
	 */
	public void generateTrainTestUnitarySubsets(int pIndex)
	{
		if (mTrainSubset == null)
		{
			mTrainSubset = new LabelledVectorSet();
			mTestSubset = new LabelledVectorSet();
		}
		else
		{
			mTrainSubset.clear();
			mTestSubset.clear();
		}

		INumericalVector lVector = null;
		double lClass = 0;

		for (int i = 0; i < size(); i++)
		{
			lVector = (INumericalVector) getVector(i);
			lClass = getClass(i);
			if (pIndex != i)
			{
				mTrainSubset.addVector(lVector, lClass);
			}
			else
			{
				mTestSubset.addVector(lVector, lClass);
			}

		}

	}

	/**
	 * @see org.royerloic.ml.svm.ILabelledVectorSet#getTrainSubset()
	 */
	public ILabelledVectorSet getTrainSubset()
	{
		return mTrainSubset;
	}

	/**
	 * @see org.royerloic.ml.svm.ILabelledVectorSet#getTestSubset()
	 */
	public ILabelledVectorSet getTestSubset()
	{
		return mTestSubset;
	}

	/**
	 * @see org.royerloic.ml.svm.ILabelledVectorSet#clear()
	 */
	public void clear()
	{
		mVectorList.clear();
		mClassList.clear();
		if (mTrainSubset != null)
		{
			mTrainSubset.clear();
			mTestSubset.clear();
		}
	}

}