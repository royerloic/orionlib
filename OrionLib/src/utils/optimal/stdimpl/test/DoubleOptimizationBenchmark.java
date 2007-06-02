/*
 * Created on 06.12.2004 by Dipl.-Inf. MSc. Ing Loic Royer
 */
package utils.optimal.stdimpl.test;

import utils.optimal.interf.IDoeStrategy;
import utils.optimal.interf.IExperiment;
import utils.optimal.interf.IInterpolator;
import utils.optimal.interf.IOptimalEngine;
import utils.optimal.stdimpl.AgrInterpolator;
import utils.optimal.stdimpl.DoeStrategyClassic;
import utils.optimal.stdimpl.OptimalEngine;
import utils.optimal.stdimpl.PiInterpolator;
import utils.optimal.stdimpl.test.functions.DoubleTestFunctions;

/**
 * @author Dipl.-Inf. MSc. Ing. Loic Royer
 * 
 */
public class DoubleOptimizationBenchmark
{
	private IOptimalEngine		mOptimalEngine;

	// private IInterpolator mInterpolator;
	//
	// private DoeStrategyClassic mDoeStrategy;
	//
	// private TestFunctions mTestFunction;

	private OptimizationTask	mOptimizationTask;

	private int								mMaximumIterations;

	private int								mRepeats;

	private Class							mInterpolatorClass;

	private Class							mDoeStrategyClass;

	class TestResult
	{
		public double				mSpeed;

		public double				mMaxValue;

		public IExperiment	mBestExperiment;
	}

	public static void main(final String[] args)
	{

		final DoubleOptimizationBenchmark lOptimalEngineTest1 = new DoubleOptimizationBenchmark(AgrInterpolator.class,
				DoeStrategyClassic.class, 100, 10);
		final DoubleOptimizationBenchmark lOptimalEngineTest2 = new DoubleOptimizationBenchmark(PiInterpolator.class,
				DoeStrategyClassic.class, 100, 10);

		lOptimalEngineTest1.launchBenchmark();
		lOptimalEngineTest2.launchBenchmark();

	}

	/**
	 * 
	 */
	public DoubleOptimizationBenchmark(	final Class pInterpolatorClass,
																			final Class pDoeStrategyClass,
																			final int pMaximumIterations,
																			final int pRepeats)
	{
		super();
		mInterpolatorClass = pInterpolatorClass;
		mDoeStrategyClass = pDoeStrategyClass;
		mMaximumIterations = pMaximumIterations;
		mRepeats = pRepeats;

	}

	/**
	 * 
	 */
	private void launchBenchmark()
	{
		System.out.println("Fn1 \t Fn2 \tR \tMax \tSpeed");

		for (int i1 = 0; i1 < 10; i1++)
			for (int i2 = i1 + 1; i2 <= 10; i2++)
				for (int j = 1; j <= mRepeats; j++)
				{

					final TestResult lResults = launchTest(i1, i2);

					System.out.println(i1 + "\t" + i2 + "\t" + j + "\t" + lResults.mMaxValue + "\t" + lResults.mSpeed);
				}
	}

	/**
	 * 
	 */
	private TestResult launchTest(final int pIndex1, final int pIndex2)
	{
		final DoubleTestFunctions mTestFunction = new DoubleTestFunctions(pIndex1, pIndex2);

		mOptimalEngine = new OptimalEngine();

		try
		{
			final IInterpolator lInterpolator = (IInterpolator) mInterpolatorClass.newInstance();
			mOptimalEngine.setInterpolator(lInterpolator);
			final IDoeStrategy lDoeStrategy = (IDoeStrategy) mDoeStrategyClass.newInstance();
			mOptimalEngine.setDoeStrategy(lDoeStrategy);
		}
		catch (final InstantiationException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		catch (final IllegalAccessException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		mOptimizationTask = new OptimizationTask(mOptimalEngine, mTestFunction, mMaximumIterations);

		mOptimizationTask.launchTest();

		while (!mOptimizationTask.isDone())
			try
			{
				Thread.sleep(100);
			}
			catch (final InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		final TestResult lTestResult = new TestResult();
		lTestResult.mSpeed = mOptimizationTask.getSpeed();
		lTestResult.mMaxValue = mOptimizationTask.getMaxValue();
		lTestResult.mBestExperiment = mOptimizationTask.getBestExperiment();

		return lTestResult;
	}
}