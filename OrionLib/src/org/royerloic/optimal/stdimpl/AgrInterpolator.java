/*
 * Created on 07.01.2005 by Dipl.-Inf. MSc. Ing Loic Royer
 */
package org.royerloic.optimal.stdimpl;

import org.royerloic.math.INumericalVector;
import org.royerloic.math.RInterpolator;
import org.royerloic.math.ScalarFunctionGridder;
import org.royerloic.optimal.interf.IExperiment;
import org.royerloic.optimal.interf.IExperimentDatabase;
import org.royerloic.optimal.interf.IInterpolator;
import org.royerloic.optimal.interf.IObjectiveFunction;

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
			this.mInputDimension = this.mExperimentDatabase.getBestExperiment().getInput().getDimension();
			this.mRInterpolator = new RInterpolator(this.mInputDimension);
			for (int i = 0; i < this.mExperimentDatabase.getNumberOfExperiments(); i++)
			{
				final INumericalVector lInputVector = this.mExperimentDatabase.getExperiment(i).getInput();
				final INumericalVector lOutputputVector = this.mExperimentDatabase.getExperiment(i).getOutput();
				final double lValue = this.mObjectiveFunction.evaluate(lOutputputVector);

				this.mRInterpolator.addPointWithoutUpdate(lInputVector, lValue);
			}
			this.mRInterpolator.update();

		}
	}

	/**
	 * @see org.royerloic.optimal.interf.IInterpolator#evaluate(org.royerloic.math.INumericalVector)
	 */
	public double evaluate(final INumericalVector pVector)
	{
		synchronized (this)
		{
			return this.mRInterpolator.evaluate(pVector);
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
			AgrInterpolator lAgrInterpolator;
			try
			{
				lAgrInterpolator = (AgrInterpolator) super.clone();

				lAgrInterpolator.mObjectiveFunction = this.mObjectiveFunction;
				lAgrInterpolator.mExperimentDatabase = this.mExperimentDatabase;
				lAgrInterpolator.mRInterpolator = (RInterpolator) this.mRInterpolator.clone();

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