/*
 * Created on 07.01.2005 by Dipl.-Inf. MSc. Ing Loic Royer
 */
package org.royerloic.optimal.stdimpl;

import java.util.ArrayList;
import java.util.List;

import org.royerloic.math.INumericalVector;
import org.royerloic.math.ScalarFunctionGridder;
import org.royerloic.math.stdimpl.NumericalVector;
import org.royerloic.optimal.interf.IExperiment;
import org.royerloic.optimal.interf.IExperimentDatabase;
import org.royerloic.optimal.interf.IInterpolator;
import org.royerloic.optimal.interf.IObjectiveFunction;

import bsh.Interpreter;

/**
 * @author Dipl.-Inf. MSc. Ing. Loic Royer
 * 
 */
public class PiInterpolator implements IInterpolator, Cloneable
{
	private IObjectiveFunction	mObjectiveFunction;

	private IExperimentDatabase	mExperimentDatabase;

	private int									mInputDimension;

	private int									mOutputDimension;

	private List								mInterpolatorList;

	private Class								mSubInterpolator;

	/**
	 * 
	 */
	public PiInterpolator(final Class pSubInterpolator)
	{
		super();
		mSubInterpolator = pSubInterpolator;

	}

	public PiInterpolator()
	{
		super();
		mSubInterpolator = AgrInterpolator.class;

	}

	/**
	 * @see org.royerloic.optimal.interf.IInterpolator#setObjectiveFunction(org.royerloic.optimal.interf.IObjectiveFunction)
	 */
	public void setObjectiveFunction(final IObjectiveFunction pObjectiveFunction)
	{
		mObjectiveFunction = pObjectiveFunction;
	}

	/**
	 * @see org.royerloic.optimal.interf.IInterpolator#setExperimentDatabase(org.royerloic.optimal.interf.IExperimentDatabase)
	 */
	public void setExperimentDatabase(final IExperimentDatabase pExperimentDatabase)
	{
		mExperimentDatabase = pExperimentDatabase;
	}

	public void update()
	{
		synchronized (this)
		{
			if (mInterpolatorList == null)
			{
				mInterpolatorList = new ArrayList();
				mInputDimension = mExperimentDatabase.getExperiment(0).getInput().getDimension();/**/
				mOutputDimension = mExperimentDatabase.getExperiment(0).getOutput().getDimension();/**/

				for (int i = 0; i < mOutputDimension; i++)
					try
					{
						IInterpolator lInterpolator;
						lInterpolator = (IInterpolator) mSubInterpolator.newInstance();
						lInterpolator.setExperimentDatabase(mExperimentDatabase);

						final int lIndex = i;
						final IObjectiveFunction lObjectiveFunction = new IObjectiveFunction()
						{
							private final Integer	mIndex	= new Integer(lIndex);

							public double evaluate(INumericalVector pVector)
							{
								return pVector.get(mIndex.intValue());
							}

							public void setInterpreter(Interpreter pInterpreter)
							{
							}

							public void setExperimentDatabase(IExperimentDatabase pDatabase)
							{
							}
						};

						lInterpolator.setObjectiveFunction(lObjectiveFunction);
						mInterpolatorList.add(lInterpolator);

					}
					catch (final InstantiationException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					catch (final IllegalAccessException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}

			for (int i = 0; i < mOutputDimension; i++)
			{
				final IInterpolator lInterpolator = (IInterpolator) mInterpolatorList.get(i);
				lInterpolator.update();
			}
		}
	}

	/**
	 * @see org.royerloic.optimal.interf.IInterpolator#evaluate(org.royerloic.math.INumericalVector)
	 */
	public double evaluate(final INumericalVector pVector)
	{
		synchronized (this)
		{
			final double[] lArray = new double[mOutputDimension];
			for (int i = 0; i < mOutputDimension; i++)
			{
				final IInterpolator lInterpolator = (IInterpolator) mInterpolatorList.get(i);
				final double lValue = lInterpolator.evaluate(pVector);
				lArray[i] = lValue;
			}
			final INumericalVector lResultVector = new NumericalVector(lArray);
			final double lObjectiveValue = mObjectiveFunction.evaluate(lResultVector);
			return lObjectiveValue;
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
			try
			{
				final PiInterpolator lPiInterpolator = (PiInterpolator) super.clone();
				final List lClonedInterpolatorList = (List) ((ArrayList) mInterpolatorList).clone();
				for (int i = 0; i < mOutputDimension; i++)
				{
					final IInterpolator lInterpolator = (IInterpolator) ((IInterpolator) mInterpolatorList.get(i)).clone();
					lPiInterpolator.mInterpolatorList.set(i, lInterpolator);
				}
				return lPiInterpolator;
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