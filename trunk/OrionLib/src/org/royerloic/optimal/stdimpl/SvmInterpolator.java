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

		this.mParameterSearch = false;

		this.mGamma = pGamma;
		this.mCost = pCost;
		this.mNu = pNu;

	}

	/**
	 * 
	 */
	public SvmInterpolator()
	{
		super();

		this.mParameterSearch = true;
	}

	/**
	 * @see org.royerloic.optimal.interf.IInterpolator#setObjectiveFunction(org.royerloic.optimal.interf.IObjectiveFunction)
	 */
	public void setObjectiveFunction(final IObjectiveFunction pObjectiveFunction)
	{
		this.mObjectiveFunction = pObjectiveFunction;
	}

	/**
	 * @see org.royerloic.optimal.interf.IInterpolator#setExperimentDatabase(org.royerloic.optimal.interf.IExperimentDatabase)
	 */
	public void setExperimentDatabase(final IExperimentDatabase pExperimentDatabase)
	{
		this.mExperimentDatabase = pExperimentDatabase;

	}

	public void update()
	{
		synchronized (this)
		{
			if (this.mParameterSearch)
				doParameterSearch();

			train(this.mGamma, this.mCost, this.mNu);
		}
	}

	private void doParameterSearch()
	{
		final ILabelledVectorSet lLabelledVectorSet = getLabbeledVectorSet();

		double lBestGamma = 1;
		double lBestCost = 1;
		double lLeastError = Double.POSITIVE_INFINITY;

		this.mNu = 0.5;

		mainloop: for (int gammaexp = 0; gammaexp <= 10; gammaexp++)
			for (int Cexp = -5; Cexp <= 5; Cexp++)

			{
				this.mGamma = Math.pow(2, gammaexp);
				this.mCost = Math.pow(2, Cexp);

				this.mSVMRegression = new SVMRegression(this.mGamma, this.mCost, this.mNu);

				final double lError = this.mSVMRegression.checkExactness(lLabelledVectorSet);

				// System.out.println("Tried: gamma="+mGamma+" cost="+mCost+"
				// error="+lError);

				if (lError < lLeastError)
				{
					lBestGamma = this.mGamma;
					lBestCost = this.mCost;
					lLeastError = lError;

					// System.out.println("Best: gamma="+lBestGamma+" cost="+lBestCost+"
					// error="+lLeastError);
				}

				if (lError < 0.001)
					break mainloop;

			}

		this.mGamma = lBestGamma;
		this.mCost = lBestCost;
		/***************************************************************************
		 * System.out.println("Best: gamma=" + lBestGamma + " cost=" + lBestCost + "
		 * error=" + lLeastError);/
		 **************************************************************************/

	}

	private void train(final double pGamma, final double pCost, final double pNu)
	{
		this.mSVMRegression = new SVMRegression(pGamma, pCost, pNu);

		final int lNumberOfExperiments = this.mExperimentDatabase.getNumberOfExperiments();
		if (lNumberOfExperiments >= 1)
		{
			final ILabelledVectorSet lLabelledVectorSet = getLabbeledVectorSet();
			this.mSVMRegression.train(lLabelledVectorSet);
		}

	}

	private ILabelledVectorSet getLabbeledVectorSet()
	{
		synchronized (this)
		{

			final int lNumberOfExperiments = this.mExperimentDatabase.getNumberOfExperiments();

			final ILabelledVectorSet lLabelledVectorSet = new LabelledVectorSet();

			this.mInputDimension = this.mExperimentDatabase.getExperiment(0).getInput().getDimension();

			for (int i = 0; i < lNumberOfExperiments; i++)
			{
				final INumericalVector lInputVector = this.mExperimentDatabase.getExperiment(i).getInput();
				final INumericalVector lOutputVector = this.mExperimentDatabase.getExperiment(i).getOutput();

				final double lValue = this.mObjectiveFunction.evaluate(lOutputVector);
				lLabelledVectorSet.addVector(lInputVector, lValue);
			}

			return lLabelledVectorSet;
		}

	}

	/**
	 * @see org.royerloic.optimal.interf.IInterpolator#evaluate(org.royerloic.math.INumericalVector)
	 */
	public double evaluate(final INumericalVector pVector)
	{
		synchronized (this)
		{
			return this.mSVMRegression.predict(pVector);
		}
	}

	/**
	 * @see org.royerloic.java.IObject#clone()
	 */
	@Override
	public Object clone()
	{
		synchronized (this)
		{
			SvmInterpolator lSvmInterpolator;
			try
			{
				lSvmInterpolator = (SvmInterpolator) super.clone();

				lSvmInterpolator.mObjectiveFunction = this.mObjectiveFunction;
				lSvmInterpolator.mExperimentDatabase = this.mExperimentDatabase;
				lSvmInterpolator.mSVMRegression = (SVMRegression) this.mSVMRegression.clone();
				lSvmInterpolator.mInputDimension = this.mInputDimension;

				return lSvmInterpolator;
			}
			catch (final CloneNotSupportedException e)
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
	public void copyFrom(final Object pObject)
	{
		throw new UnsupportedOperationException("copyFrom not implememented in SvmInterpolator");
	}

	/**
	 * @see org.royerloic.optimal.interf.IInterpolator#getInputDimension()
	 */
	public int getInputDimension()
	{
		return this.mInputDimension;
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
			final int lSize = this.mExperimentDatabase.getNumberOfExperiments();
			final double[][] lPoints = new double[lSize][this.mInputDimension + 1];

			for (int i = 0; i < lSize; i++)
			{
				final IExperiment lExperiment = this.mExperimentDatabase.getExperiment(i);
				for (int k = 0; k < getInputDimension(); k++)
					lPoints[i][k] = lExperiment.getInput().get(k);

				lPoints[i][getInputDimension()] = this.mObjectiveFunction.evaluate(lExperiment.getOutput());
			}

			return lPoints;
		}

	}

	/**
	 * @see org.royerloic.math.IScalarFunction#computePoints(int)
	 */
	public double[][] computePoints(final int pResolution)
	{
		synchronized (this)
		{
			final ScalarFunctionGridder lGridder = new ScalarFunctionGridder(this);
			final double[][] lGridPoints = lGridder.computeGridPoints(pResolution);
			final double[][] lDatabasePoints = computeDatabasePoints(0);

			final double[][] lPoints = new double[lGridPoints.length + lDatabasePoints.length][getInputDimension() + 1];

			for (int i = 0; i < lGridPoints.length; i++)
				lPoints[i] = lGridPoints[i];

			for (int i = 0; i < lDatabasePoints.length; i++)
				lPoints[lGridPoints.length + i] = lDatabasePoints[i];
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
	public double getInputMin(final int pIndex)
	{
		return 0;
	}

	/**
	 * @see org.royerloic.math.IFunction#getInputMax(int)
	 */
	public double getInputMax(final int pIndex)
	{
		return 1;
	}

	/**
	 * @see org.royerloic.math.IFunction#getInputDelta(int)
	 */
	public double getInputDelta(final int pIndex)
	{
		// TODO Auto-generated method stub
		return 0.00001;
	}

	/**
	 * @see org.royerloic.math.IFunction#normalizeInputVector(org.royerloic.math.INumericalVector)
	 */
	public void normalizeInputVector(final INumericalVector pVector)
	{

	}

}