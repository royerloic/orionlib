/*
 * Created on 07.01.2005 by Dipl.-Inf. MSc. Ing Loic Royer
 */
package utils.optimal.stdimpl;

import utils.math.INumericalVector;
import utils.math.ScalarFunctionGridder;
import utils.ml.svm.ILabelledVectorSet;
import utils.ml.svm.LabelledVectorSet;
import utils.ml.svm.SVMRegression;
import utils.optimal.interf.IExperiment;
import utils.optimal.interf.IExperimentDatabase;
import utils.optimal.interf.IInterpolator;
import utils.optimal.interf.IObjectiveFunction;

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
	 * @see utils.optimal.interf.IInterpolator#setObjectiveFunction(utils.optimal.interf.IObjectiveFunction)
	 */
	public void setObjectiveFunction(final IObjectiveFunction pObjectiveFunction)
	{
		mObjectiveFunction = pObjectiveFunction;
	}

	/**
	 * @see utils.optimal.interf.IInterpolator#setExperimentDatabase(utils.optimal.interf.IExperimentDatabase)
	 */
	public void setExperimentDatabase(final IExperimentDatabase pExperimentDatabase)
	{
		mExperimentDatabase = pExperimentDatabase;

	}

	public void update()
	{
		synchronized (this)
		{
			if (mParameterSearch)
				doParameterSearch();

			train(mGamma, mCost, mNu);
		}
	}

	private void doParameterSearch()
	{
		final ILabelledVectorSet lLabelledVectorSet = getLabbeledVectorSet();

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

				final double lError = mSVMRegression.checkExactness(lLabelledVectorSet);

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
					break mainloop;

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

		final int lNumberOfExperiments = mExperimentDatabase.getNumberOfExperiments();
		if (lNumberOfExperiments >= 1)
		{
			final ILabelledVectorSet lLabelledVectorSet = getLabbeledVectorSet();
			mSVMRegression.train(lLabelledVectorSet);
		}

	}

	private ILabelledVectorSet getLabbeledVectorSet()
	{
		synchronized (this)
		{

			final int lNumberOfExperiments = mExperimentDatabase.getNumberOfExperiments();

			final ILabelledVectorSet lLabelledVectorSet = new LabelledVectorSet();

			mInputDimension = mExperimentDatabase.getExperiment(0).getInput().getDimension();

			for (int i = 0; i < lNumberOfExperiments; i++)
			{
				final INumericalVector lInputVector = mExperimentDatabase.getExperiment(i).getInput();
				final INumericalVector lOutputVector = mExperimentDatabase.getExperiment(i).getOutput();

				final double lValue = mObjectiveFunction.evaluate(lOutputVector);
				lLabelledVectorSet.addVector(lInputVector, lValue);
			}

			return lLabelledVectorSet;
		}

	}

	/**
	 * @see utils.optimal.interf.IInterpolator#evaluate(utils.math.INumericalVector)
	 */
	public double evaluate(final INumericalVector pVector)
	{
		synchronized (this)
		{
			return mSVMRegression.predict(pVector);
		}
	}

	/**
	 * @see utils.java.IObject#clone()
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

				lSvmInterpolator.mObjectiveFunction = mObjectiveFunction;
				lSvmInterpolator.mExperimentDatabase = mExperimentDatabase;
				lSvmInterpolator.mSVMRegression = (SVMRegression) mSVMRegression.clone();
				lSvmInterpolator.mInputDimension = mInputDimension;

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
	 * @see utils.java.IObject#copyFrom(java.lang.Object)
	 */
	public void copyFrom(final Object pObject)
	{
		throw new UnsupportedOperationException("copyFrom not implememented in SvmInterpolator");
	}

	/**
	 * @see utils.optimal.interf.IInterpolator#getInputDimension()
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
			final int lSize = mExperimentDatabase.getNumberOfExperiments();
			final double[][] lPoints = new double[lSize][mInputDimension + 1];

			for (int i = 0; i < lSize; i++)
			{
				final IExperiment lExperiment = mExperimentDatabase.getExperiment(i);
				for (int k = 0; k < getInputDimension(); k++)
					lPoints[i][k] = lExperiment.getInput().get(k);

				lPoints[i][getInputDimension()] = mObjectiveFunction.evaluate(lExperiment.getOutput());
			}

			return lPoints;
		}

	}

	/**
	 * @see utils.math.IScalarFunction#computePoints(int)
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
	 * @see utils.math.IFunction#getOutputDimension()
	 */
	public int getOutputDimension()
	{
		return 1;
	}

	/**
	 * @see utils.math.IFunction#getInputMin(int)
	 */
	public double getInputMin(final int pIndex)
	{
		return 0;
	}

	/**
	 * @see utils.math.IFunction#getInputMax(int)
	 */
	public double getInputMax(final int pIndex)
	{
		return 1;
	}

	/**
	 * @see utils.math.IFunction#getInputDelta(int)
	 */
	public double getInputDelta(final int pIndex)
	{
		// TODO Auto-generated method stub
		return 0.00001;
	}

	/**
	 * @see utils.math.IFunction#normalizeInputVector(utils.math.INumericalVector)
	 */
	public void normalizeInputVector(final INumericalVector pVector)
	{

	}

}