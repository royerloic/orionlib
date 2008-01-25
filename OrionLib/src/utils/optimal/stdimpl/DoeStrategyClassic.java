/*
 * Created on 07.01.2005 by Dipl.-Inf. MSc. Ing Loic Royer
 */
package utils.optimal.stdimpl;

import utils.math.INumericalVector;
import utils.math.IScalarFunction;
import utils.math.ScalarFunctionGridder;
import utils.optimal.interf.IDoeStrategy;
import utils.optimal.interf.IExperimentDatabase;
import utils.optimal.interf.IInterpolator;
import utils.optimal.interf.IObjectiveFunction;

/**
 * @author Dipl.-Inf. MSc. Ing. Loic Royer
 * 
 */
public class DoeStrategyClassic implements IDoeStrategy
{
	IExperimentDatabase mExperimentDatabase;

	IObjectiveFunction mObjectiveFunction;

	/**
	 * <code>mProxyFunction</code> is the proxy function used to hide the fact
	 * that we use the difference between the interpolated function (modeler) and
	 * the reference (or base) function.
	 */
	public IScalarFunction mProxyFunction;

	/**
	 * <code>mModelerFunction</code> is the function which interpolates the
	 * objective function.
	 */
	private IInterpolator mInterpolator;

	private IScalarFunction mStochFillFunction;

	/**
	 * <code>mBaseFunction</code> is the function used to "remenber" which
	 * maxima we have allready found.
	 */
	private IScalarFunction mBaseFunction;

	private int mStochMaxIterationsMinimum;

	private int mStochMaxIterationsMaximum;

	private int mStagnationStart;

	private int mStagnationLatency;

	private int mStochFillIterationsMinimum;

	private int mStochFillIterationsMaximum;

	private double mStochMaxActivationThreshold;

	private double mLastMaximumValue;

	private double mLastValue;

	/**
	 * <code>cMODE_START</code> is the constant representing the Designer in
	 * start mode.
	 */
	private static final int cMODE_START = 0;

	/**
	 * <code>cMODE_STOCHMAX</code> is the constant representing the Designer in
	 * Stochastic Search Mode.
	 */
	private static final int cMODE_STOCHMAX = 1;

	/**
	 * <code>cMODE_STOCHFILL</code> is the constant representing the Designer in
	 * Stochastic Fill Mode.
	 */
	private static final int cMODE_STOCHFILL = 2;

	/**
	 * <code>mMode</code> stores the current mode of the Designer.
	 */
	private int mMode;

	/**
	 * <code>mModeCounter</code> stores the counter value of the Designer. The
	 * counter is used to keep track of how many iterations have elapsed since the
	 * last change of Designer´s mode.
	 */
	private int mModeCounter;

	/**
	 * <code>mResetTime</code> stores the Reset Counter Period value.
	 */
	private int mResetTime;

	/**
	 * <code>mResetCounter</code> stores the counter value that is decremented
	 * since the Designer was in Start Mode. This counter is used to reset the
	 * Designer every <code>mResetCounterPeriod</code> iterations.
	 */
	private int mResetCounter;

	/**
	 * 
	 */
	public DoeStrategyClassic()
	{
		super();

		mResetTime = 1000;
		mResetCounter = mResetTime;
		mMode = cMODE_START;
		mModeCounter = 0;

		mStochMaxIterationsMinimum = 10;
		mStochMaxIterationsMaximum = 30;

		mStagnationLatency = 5;
		mStochFillIterationsMinimum = 10;
		mStochFillIterationsMaximum = 30;
		mStochMaxActivationThreshold = 0.01;

	}

	/**
	 * @see utils.optimal.interf.IDoeStrategy#setExperimentDatabase(utils.optimal.interf.IExperimentDatabase)
	 */
	public void setExperimentDatabase(final IExperimentDatabase pExperimentDatabase)
	{
		mExperimentDatabase = pExperimentDatabase;
	}

	/**
	 * @see utils.optimal.interf.IDoeStrategy#setObjectiveFunction(utils.optimal.interf.IObjectiveFunction)
	 */
	public void setObjectiveFunction(final IObjectiveFunction pObjectiveFunction)
	{
		mObjectiveFunction = pObjectiveFunction;
	}

	/**
	 * @see utils.optimal.interf.IDoeStrategy#setInterpolator(utils.optimal.interf.IInterpolator)
	 */
	public void setInterpolator(final IInterpolator pInterpolator)
	{
		mInterpolator = pInterpolator;
		mBaseFunction = getNullBaseFunction();
		mProxyFunction = getProxyFunction();
	}

	/**
	 * @see utils.optimal.interf.IDoeStrategy#designNewExperiment()
	 */
	public INumericalVector designNewExperiment()
	{
		INumericalVector lVector = null;

		if (mExperimentDatabase.getNumberOfExperiments() != 0)
		{
			mModeCounter++;

			/**
			 * decrements the counter <code>mResetCounter</code> until 0. When it
			 * reaches 0 we reset the Base Function.
			 */
			if (mResetCounter > 0)
				mResetCounter--;
			else
			{
				mResetCounter = mResetTime;
				mBaseFunction = getNullBaseFunction();
			}

			if (mMode == cMODE_START)
			{
				mStochFillFunction = getStochFillFunction();
				mMode = cMODE_STOCHMAX;
				mStagnationStart = mExperimentDatabase.getNumberOfExperiments();
			}
			else if ((mMode == cMODE_STOCHMAX) && ((mExperimentDatabase.stagnating(	mStagnationStart,
																																							mStagnationLatency) && (mModeCounter > mStochMaxIterationsMinimum)) || (mModeCounter > mStochMaxIterationsMaximum)))
			{
				/**
				 * The Database is stagnating we switch to StochFill mode.
				 */
				mMode = cMODE_STOCHFILL;
				setBaseFunction(mInterpolator);
				mModeCounter = 0;
				mLastMaximumValue = mLastValue;
				mLastValue = 0;

			}
			else if ((mMode == cMODE_STOCHFILL) && (((mLastValue > mStochMaxActivationThreshold * mLastMaximumValue) && (mModeCounter > mStochFillIterationsMinimum)) || (mModeCounter > mStochFillIterationsMaximum)))
			{
				mMode = cMODE_STOCHMAX;
				mModeCounter = 0;
			}

			if (mMode == cMODE_STOCHMAX)
				/**
				 * We are in StochMax Mode.
				 */
				lVector = DoeStrategyHelper.genetic(mProxyFunction, 4, 1000);
			else if (mMode == cMODE_STOCHFILL)
				/**
				 * We are in StochFill Mode.
				 */
				lVector = DoeStrategyHelper.multiStepsStochmax(	mStochFillFunction,
																												1,
																												100);

			// System.out.println("DOE Mode: " + mMode);
			mLastValue = mProxyFunction.evaluate(lVector);
		}

		return lVector;
	}

	private IScalarFunction getStochFillFunction()
	{
		return new IScalarFunction()
		{

			/**
			 * @see de.fhg.iwu.utils.math.IFunction#getInputDimension()
			 */
			public final int getInputDimension()
			{
				return mInterpolator.getInputDimension();
			}

			/**
			 * @see de.fhg.iwu.utils.math.IFunction#getOutputDimension()
			 */
			public final int getOutputDimension()
			{
				return mInterpolator.getOutputDimension();
			}

			/**
			 * @see de.fhg.iwu.utils.math.IFunction#getInputMin(int)
			 */
			public final double getInputMin(final int pIndex)
			{
				return mInterpolator.getInputMin(pIndex);
			}

			/**
			 * @see de.fhg.iwu.utils.math.IFunction#getInputMax(int)
			 */
			public final double getInputMax(final int pIndex)
			{
				return mInterpolator.getInputMax(pIndex);
			}

			/**
			 * @see de.fhg.iwu.utils.math.IFunction#getInputDelta(int)
			 */
			public final double getInputDelta(final int pIndex)
			{
				return mInterpolator.getInputDelta(pIndex);
			}

			/**
			 * @see de.fhg.iwu.utils.math.IFunction#normalizeInputVector(de.fhg.iwu.utils.math.MVector)
			 */
			public final void normalizeInputVector(final INumericalVector pVector)
			{

			}

			/**
			 * @see de.fhg.iwu.utils.math.IScalarFunction#evaluate(de.fhg.iwu.utils.math.MVector)
			 */
			public final double evaluate(final INumericalVector pVector)
			{
				final INumericalVector lNVector = mExperimentDatabase	.getNeighboor(pVector)
																															.getInput();
				final double lDistance = lNVector.euclideanDistanceTo(pVector);
				return lDistance;
			}

			/**
			 * @see de.fhg.iwu.utils.math.IScalarFunction#computePoints(int)
			 */
			public final double[][] computePoints(final int pResolution)
			{
				return null;
			}
		};
	}

	/**
	 * Sets the base function.
	 * 
	 * @param pModeler
	 *          Modeler function
	 */
	private void setBaseFunction(final IInterpolator pInterpolator)
	{
		mBaseFunction = (IScalarFunction) (pInterpolator.clone());
	}

	/**
	 * Sets the Base function to a constant null function.
	 */
	private IScalarFunction getNullBaseFunction()
	{
		return new IScalarFunction()
		{

			/**
			 * @see de.fhg.iwu.utils.math.IScalarFunction#evaluate(de.fhg.iwu.utils.math.MVector)
			 */
			public final double evaluate(final INumericalVector pVector)
			{
				return 0;
			}

			/**
			 * @see de.fhg.iwu.utils.math.IScalarFunction#computePoints(int)
			 */
			public final double[][] computePoints(final int pResolution)
			{
				return null;
			}

			/**
			 * @see de.fhg.iwu.utils.math.IFunction#getInputDimension()
			 */
			public final int getInputDimension()
			{
				return mInterpolator.getInputDimension();
			}

			/**
			 * @see de.fhg.iwu.utils.math.IFunction#getOutputDimension()
			 */
			public final int getOutputDimension()
			{
				return mInterpolator.getOutputDimension();
			}

			/**
			 * @see de.fhg.iwu.utils.math.IFunction#getInputMin(int)
			 */
			public final double getInputMin(final int pIndex)
			{
				return mInterpolator.getInputMin(pIndex);
			}

			/**
			 * @see de.fhg.iwu.utils.math.IFunction#getInputMax(int)
			 */
			public final double getInputMax(final int pIndex)
			{
				return mInterpolator.getInputMax(pIndex);
			}

			/**
			 * @see de.fhg.iwu.utils.math.IFunction#getInputDelta(int)
			 */
			public final double getInputDelta(final int pIndex)
			{
				return mInterpolator.getInputDelta(pIndex);
			}

			/**
			 * @see de.fhg.iwu.utils.math.IFunction#normalizeInputVector(de.fhg.iwu.utils.math.MVector)
			 */
			public final void normalizeInputVector(final INumericalVector pVector)
			{
				mInterpolator.normalizeInputVector(pVector);
			}
		};

	}

	private IScalarFunction getProxyFunction()
	{
		return new IScalarFunction()
		{

			/**
			 * @see de.fhg.iwu.utils.math.IScalarFunction#evaluate(de.fhg.iwu.utils.math.MVector)
			 */
			public final double evaluate(final INumericalVector pVector)
			{
				return mInterpolator.evaluate(pVector) - mBaseFunction.evaluate(pVector);
			}

			/**
			 * @see de.fhg.iwu.utils.math.IScalarFunction#computePoints(int)
			 */
			public final double[][] computePoints(final int pResolution)
			{
				final ScalarFunctionGridder lGridder = new ScalarFunctionGridder(this);
				final double[][] lGridPoints = lGridder.computeGridPoints(pResolution);
				return lGridPoints;
			}

			/**
			 * @see de.fhg.iwu.utils.math.IFunction#getInputDimension()
			 */
			public final int getInputDimension()
			{
				return mInterpolator.getInputDimension();
			}

			/**
			 * @see de.fhg.iwu.utils.math.IFunction#getOutputDimension()
			 */
			public final int getOutputDimension()
			{
				return mInterpolator.getOutputDimension();
			}

			/**
			 * @see de.fhg.iwu.utils.math.IFunction#getInputMin(int)
			 */
			public final double getInputMin(final int pIndex)
			{
				return mInterpolator.getInputMin(pIndex);
			}

			/**
			 * @see de.fhg.iwu.utils.math.IFunction#getInputMax(int)
			 */
			public final double getInputMax(final int pIndex)
			{
				return mInterpolator.getInputMax(pIndex);
			}

			/**
			 * @see de.fhg.iwu.utils.math.IFunction#getInputDelta(int)
			 */
			public final double getInputDelta(final int pIndex)
			{
				return mInterpolator.getInputDelta(pIndex);
			}

			/**
			 * @see de.fhg.iwu.utils.math.IFunction#normalizeInputVector(de.fhg.iwu.utils.math.MVector)
			 */
			public final void normalizeInputVector(final INumericalVector pVector)
			{
				mInterpolator.normalizeInputVector(pVector);
			}
		};
	}

}