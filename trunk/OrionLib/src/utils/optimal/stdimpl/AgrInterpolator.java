/*
 * Created on 07.01.2005 by Dipl.-Inf. MSc. Ing Loic Royer
 */
package utils.optimal.stdimpl;

import utils.math.INumericalVector;
import utils.math.RInterpolator;
import utils.math.ScalarFunctionGridder;
import utils.optimal.interf.IExperiment;
import utils.optimal.interf.IExperimentDatabase;
import utils.optimal.interf.IInterpolator;
import utils.optimal.interf.IObjectiveFunction;

/**
 * @author Dipl.-Inf. MSc. Ing. Loic Royer
 * 
 */
public class AgrInterpolator implements IInterpolator, Cloneable
{
	private IObjectiveFunction	mObjectiveFunction;

	private IExperimentDatabase	mExperimentDatabase;

	private RInterpolator				mRInterpolator;

	private int									mInputDimension;

	/**
	 * 
	 */
	public AgrInterpolator()
	{
		super();

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
			mInputDimension = mExperimentDatabase.getBestExperiment().getInput().getDimension();
			mRInterpolator = new RInterpolator(mInputDimension);
			for (int i = 0; i < mExperimentDatabase.getNumberOfExperiments(); i++)
			{
				final INumericalVector lInputVector = mExperimentDatabase.getExperiment(i).getInput();
				final INumericalVector lOutputputVector = mExperimentDatabase.getExperiment(i).getOutput();
				final double lValue = mObjectiveFunction.evaluate(lOutputputVector);

				mRInterpolator.addPointWithoutUpdate(lInputVector, lValue);
			}
			mRInterpolator.update();

		}
	}

	/**
	 * @see utils.optimal.interf.IInterpolator#evaluate(utils.math.INumericalVector)
	 */
	public double evaluate(final INumericalVector pVector)
	{
		synchronized (this)
		{
			return mRInterpolator.evaluate(pVector);
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
			AgrInterpolator lAgrInterpolator;
			try
			{
				lAgrInterpolator = (AgrInterpolator) super.clone();

				lAgrInterpolator.mObjectiveFunction = mObjectiveFunction;
				lAgrInterpolator.mExperimentDatabase = mExperimentDatabase;
				lAgrInterpolator.mRInterpolator = (RInterpolator) mRInterpolator.clone();

				return lAgrInterpolator;
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