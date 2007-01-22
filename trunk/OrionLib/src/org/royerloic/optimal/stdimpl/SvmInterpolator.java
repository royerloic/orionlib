/*
 * Created on 07.01.2005 by Dipl.-Inf. MSc. Ing Loic Royer
 */
package org.royerloic.optimal.stdimpl;

import org.royerloic.math.INumericalVector;
import org.royerloic.math.IVectorArray;
import org.royerloic.math.ScalarFunctionGridder;
import org.royerloic.ml.svm.ILabelledVectorSet;
import org.royerloic.ml.svm.LabelledVectorSet;
import org.royerloic.ml.svm.SVMRegression;
import org.royerloic.optimal.interf.IExperiment;
import org.royerloic.optimal.interf.IExperimentDatabase;
import org.royerloic.optimal.interf.IInterpolator;
import org.royerloic.optimal.interf.IObjectiveFunction;

/**
 * @author Dipl.-Inf. MSc. Ing. Loic Royer
 * 
 */
public class SvmInterpolator implements IInterpolator, Cloneable
{
	private IObjectiveFunction	mObjectiveFunction;

	private IExperimentDatabase	mExperimentDatabase;

	private SVMRegression				mSVMRegression;

	private boolean							mParameterSearch;

	private double							mGamma;

	private double							mCost;

	private double							mNu;

	private int									mInputDimension;

	/**
	 * 
	 */
	public SvmInterpolator(final double pGamma, final double pCost, final double pNu)
	{
		super();

		mParameterSearch = false;

		mGamma = pGamma;
		mCost = pCost;
		mNu = pNu;

	}

	/**
	 * 
	 */
	public SvmInterpolator()
	{
		super();

		mParameterSearch = true;
	}

	/**
	 * @see org.royerloic.optimal.interf.IInterpolator#setObjectiveFunction(org.royerloic.optimal.interf.IObjectiveFunction)
	 */
	public void setObjectiveFunction(IObjectiveFunction pObjectiveFunction)
	{
		mObjectiveFunction = pObjectiveFunction;
	}

	/**
	 * @see org.royerloic.optimal.interf.IInterpolator#setExperimentDatabase(org.royerloic.optimal.interf.IExperimentDatabase)
	 */
	public void setExperimentDatabase(IExperimentDatabase pExperimentDatabase)
	{
		mExperimentDatabase = pExperimentDatabase;

	}

	public void update()
	{
		synchronized (this)
		{
			if (mParameterSearch)
			{
				doParameterSearch();
			}

			train(mGamma, mCost, mNu);
		}
	}

	private void doParameterSearch()
	{
		ILabelledVectorSet lLabelledVectorSet = getLabbeledVectorSet();

		double lBestGamma = 1;
		double lBestCost = 1;
		double lLeastError = Double.POSITIVE_INFINITY;

		mNu = 0.5;

		mainloop: for (int gammaexp = 0; gammaexp <= 10; gammaexp++)
			for (int Cexp = -5; Cexp <= 5; Cexp++)

			{
				mGamma = Math.pow(2, gammaexp);
				mCost = Math.pow(2, Cexp);

				mSVMRegression = new SVMRegression(mGamma, mCost, mNu);

				double lError = mSVMRegression.checkExactness(lLabelledVectorSet);

				// System.out.println("Tried: gamma="+mGamma+" cost="+mCost+"
				// error="+lError);

				if (lError < lLeastError)
				{
					lBestGamma = mGamma;
					lBestCost = mCost;
					lLeastError = lError;

					// System.out.println("Best: gamma="+lBestGamma+" cost="+lBestCost+"
					// error="+lLeastError);
				}

				if (lError < 0.001)
				{
					break mainloop;
				}

			}

		mGamma = lBestGamma;
		mCost = lBestCost;
		/***************************************************************************
		 * System.out.println("Best: gamma=" + lBestGamma + " cost=" + lBestCost + "
		 * error=" + lLeastError);/
		 **************************************************************************/

	}

	private void train(final double pGamma, final double pCost, final double pNu)
	{
		mSVMRegression = new SVMRegression(pGamma, pCost, pNu);

		int lNumberOfExperiments = mExperimentDatabase.getNumberOfExperiments();
		if (lNumberOfExperiments >= 1)
		{
			ILabelledVectorSet lLabelledVectorSet = getLabbeledVectorSet();
			mSVMRegression.train(lLabelledVectorSet);
		}

	}

	private ILabelledVectorSet getLabbeledVectorSet()
	{
		synchronized (this)
		{

			int lNumberOfExperiments = mExperimentDatabase.getNumberOfExperiments();

			ILabelledVectorSet lLabelledVectorSet = new LabelledVectorSet();

			mInputDimension = mExperimentDatabase.getExperiment(0).getInput().getDimension();

			for (int i = 0; i < lNumberOfExperiments; i++)
			{
				INumericalVector lInputVector = mExperimentDatabase.getExperiment(i).getInput();
				INumericalVector lOutputVector = mExperimentDatabase.getExperiment(i).getOutput();

				double lValue = mObjectiveFunction.evaluate(lOutputVector);
				lLabelledVectorSet.addVector((IVectorArray) lInputVector, lValue);
			}

			return lLabelledVectorSet;
		}

	}

	/**
	 * @see org.royerloic.optimal.interf.IInterpolator#evaluate(org.royerloic.math.INumericalVector)
	 */
	public double evaluate(INumericalVector pVector)
	{
		synchronized (this)
		{
			return mSVMRegression.predict(pVector);
		}
	}

	/**
	 * @see org.royerloic.java.IObject#clone()
	 */
	public Object clone()
	{
		synchronized (this)
		{
			SvmInterpolator lSvmInterpolator;
			try
			{
				lSvmInterpolator = (SvmInterpolator) super.clone();

				lSvmInterpolator.mObjectiveFunction = mObjectiveFunction;
				lSvmInterpolator.mExperimentDatabase = mExperimentDatabase;
				lSvmInterpolator.mSVMRegression = (SVMRegression) mSVMRegression.clone();
				lSvmInterpolator.mInputDimension = mInputDimension;

				return lSvmInterpolator;
			}
			catch (CloneNotSupportedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}
	}

	/**
	 * @see org.royerloic.java.IObject#copyFrom(java.lang.Object)
	 */
	public void copyFrom(Object pObject)
	{
		throw new UnsupportedOperationException("copyFrom not implememented in SvmInterpolator");
	}

	/**
	 * @see org.royerloic.optimal.interf.IInterpolator#getInputDimension()
	 */
	public int getInputDimension()
	{
		return mInputDimension;
	}

	/**
	 * Computes the objective function values for the points in the associated
	 * Database and returns an array containing these values in a format
	 * compatible with the plotting class.
	 * 
	 * @see PlotScalarFuctionFactory
	 * 
	 * @param pDummy
	 *          this is a dummy parameter used for some developpement purposes, it
	 *          is ignored.
	 * @return a PlotScalarFuctionFactory compatible (vector,value) array.
	 */
	public final double[][] computeDatabasePoints(final int pDummy)
	{
		synchronized (this)
		{
			int lSize = mExperimentDatabase.getNumberOfExperiments();
			double[][] lPoints = new double[lSize][mInputDimension + 1];

			for (int i = 0; i < lSize; i++)
			{
				IExperiment lExperiment = (IExperiment) mExperimentDatabase.getExperiment(i);
				for (int k = 0; k < getInputDimension(); k++)
				{
					lPoints[i][k] = lExperiment.getInput().get(k);
				}

				lPoints[i][getInputDimension()] = mObjectiveFunction.evaluate(lExperiment.getOutput());
			}

			return lPoints;
		}

	}

	/**
	 * @see org.royerloic.math.IScalarFunction#computePoints(int)
	 */
	public double[][] computePoints(int pResolution)
	{
		synchronized (this)
		{
			ScalarFunctionGridder lGridder = new ScalarFunctionGridder(this);
			double[][] lGridPoints = lGridder.computeGridPoints(pResolution);
			double[][] lDatabasePoints = computeDatabasePoints(0);

			double[][] lPoints = new double[lGridPoints.length + lDatabasePoints.length][getInputDimension() + 1];

			for (int i = 0; i < lGridPoints.length; i++)
			{
				lPoints[i] = lGridPoints[i];
			}

			for (int i = 0; i < lDatabasePoints.length; i++)
			{
				lPoints[lGridPoints.length + i] = lDatabasePoints[i];
			}
			return lPoints;
		}
	}

	/**
	 * @see org.royerloic.math.IFunction#getOutputDimension()
	 */
	public int getOutputDimension()
	{
		return 1;
	}

	/**
	 * @see org.royerloic.math.IFunction#getInputMin(int)
	 */
	public double getInputMin(int pIndex)
	{
		return 0;
	}

	/**
	 * @see org.royerloic.math.IFunction#getInputMax(int)
	 */
	public double getInputMax(int pIndex)
	{
		return 1;
	}

	/**
	 * @see org.royerloic.math.IFunction#getInputDelta(int)
	 */
	public double getInputDelta(int pIndex)
	{
		// TODO Auto-generated method stub
		return 0.00001;
	}

	/**
	 * @see org.royerloic.math.IFunction#normalizeInputVector(org.royerloic.math.INumericalVector)
	 */
	public void normalizeInputVector(INumericalVector pVector)
	{

	}

}