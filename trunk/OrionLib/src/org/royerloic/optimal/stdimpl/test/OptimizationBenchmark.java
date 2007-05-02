/*
 * Created on 06.12.2004 by Dipl.-Inf. MSc. Ing Loic Royer
 */
package org.royerloic.optimal.stdimpl.test;

import org.royerloic.optimal.interf.IDoeStrategy;
import org.royerloic.optimal.interf.IExperiment;
import org.royerloic.optimal.interf.IInterpolator;
import org.royerloic.optimal.interf.IOptimalEngine;
import org.royerloic.optimal.stdimpl.AgrInterpolator;
import org.royerloic.optimal.stdimpl.DoeStrategyClassic;
import org.royerloic.optimal.stdimpl.OptimalEngine;
import org.royerloic.optimal.stdimpl.SvmInterpolator;
import org.royerloic.optimal.stdimpl.test.functions.TestFunctions;

/**
 * @author Dipl.-Inf. MSc. Ing. Loic Royer
 * 
 */
public class OptimizationBenchmark
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
		final OptimizationBenchmark lOptimalEngineTest1 = new OptimizationBenchmark(AgrInterpolator.class,
				DoeStrategyClassic.class, 200, 50);
		final OptimizationBenchmark lOptimalEngineTest2 = new OptimizationBenchmark(SvmInterpolator.class,
				DoeStrategyClassic.class, 200, 50);

		lOptimalEngineTest1.launchBenchmark();
		lOptimalEngineTest2.launchBenchmark();

	}

	/**
	 * 
	 */
	public OptimizationBenchmark(	final Class pInterpolatorClass,
																final Class pDoeStrategyClass,
																final int pMaximumIterations,
																final int pRepeats)
	{
		super();
		this.mInterpolatorClass = pInterpolatorClass;
		this.mDoeStrategyClass = pDoeStrategyClass;
		this.mMaximumIterations = pMaximumIterations;
		this.mRepeats = pRepeats;

	}

	/**
	 * 
	 */
	private void launchBenchmark()
	{
		System.out.println("Fn \tR \tMax \tSpeed");

		for (int i = 1; i <= 10; i++)
			for (int j = 1; j <= this.mRepeats; j++)
			{

				final TestResult lResults = launchTest(i);

				System.out.println(i + "\t" + j + "\t" + lResults.mMaxValue + "\t" + lResults.mSpeed);
			}
	}

	/**
	 * 
	 */
	private TestResult launchTest(final int pIndex)
	{
		final TestFunctions mTestFunction = new TestFunctions(pIndex);

		this.mOptimalEngine = new OptimalEngine();

		try
		{
			final IInterpolator lInterpolator = (IInterpolator) this.mInterpolatorClass.newInstance();
			this.mOptimalEngine.setInterpolator(lInterpolator);
			final IDoeStrategy lDoeStrategy = (IDoeStrategy) this.mDoeStrategyClass.newInstance();
			this.mOptimalEngine.setDoeStrategy(lDoeStrategy);
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

		this.mOptimizationTask = new OptimizationTask(this.mOptimalEngine, mTestFunction, this.mMaximumIterations);

		this.mOptimizationTask.launchTest();

		while (!this.mOptimizationTask.isDone())
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
		lTestResult.mSpeed = this.mOptimizationTask.getSpeed();
		lTestResult.mMaxValue = this.mOptimizationTask.getMaxValue();
		lTestResult.mBestExperiment = this.mOptimizationTask.getBestExperiment();

		return lTestResult;
	}
}