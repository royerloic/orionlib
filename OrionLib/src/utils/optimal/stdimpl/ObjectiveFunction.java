/*
 * Created on 06.01.2005 by Dipl.-Inf. MSc. Ing Loic Royer
 */
package utils.optimal.stdimpl;

import org.apache.log4j.Logger;

import utils.math.INumericalVector;
import utils.math.stdimpl.NumericalVector;
import utils.optimal.interf.IExperimentDatabase;
import utils.optimal.interf.IObjectiveFunction;
import bsh.EvalError;
import bsh.Interpreter;

/**
 * @author Dipl.-Inf. MSc. Ing. Loic Royer
 * 
 */
public class ObjectiveFunction implements IObjectiveFunction
{
	static Logger								mLogger	= Logger.getLogger(OptimalEngine.class);

	private Interpreter					mInterpreter;

	private String							mFormula;

	private IExperimentDatabase	mDatabase;

	public void setInterpreter(final Interpreter pInterpreter)
	{
		mInterpreter = pInterpreter;
	}

	/**
	 * @see utils.optimal.interf.IObjectiveFunction#setExperimentDatabase(utils.optimal.interf.IExperimentDatabase)
	 */
	public void setExperimentDatabase(final IExperimentDatabase pDatabase)
	{
		mDatabase = pDatabase;
	}

	/**
	 * 
	 */
	public ObjectiveFunction(final String pFormula)
	{
		super();
		mFormula = pFormula;
	}

	/**
	 * @see utils.optimal.interf.IObjectiveFunction#evaluate(utils.math.INumericalVector)
	 */
	public double evaluate(final INumericalVector pVector)
	{

		Double lResult;

		final int lDimension = pVector.getDimension();

		final INumericalVector lVector = new NumericalVector(lDimension);
		final INumericalVector lMaxVector = mDatabase.getMaximumOutputValuesVector();
		final INumericalVector lMinVector = mDatabase.getMinimumOutputValuesVector();

		for (int i = 0; i < lDimension; i++)
		{
			final double lValue = (pVector.get(i) - lMinVector.get(i)) / (lMaxVector.get(i) - lMinVector.get(i));

			if (Double.isNaN(lValue))
				lVector.set(i, 0);
			else
				lVector.set(i, lValue);

		}

		synchronized (this)
		{
			for (int i = 0; i < lDimension; i++)
				try
				{
					mInterpreter.set("y" + i, pVector.get(i));
					mInterpreter.set("n" + i, lVector.get(i));
				}
				catch (final EvalError e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace(System.out);
				}

			try
			{
				lResult = (Double) mInterpreter.eval(mFormula);
				final double lResultPrimitive = lResult.doubleValue();
				return lResultPrimitive;
			}
			catch (final EvalError e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				mLogger.error(e);
				throw new RuntimeException("Error evaluating Objective formula:" + mFormula);
			}
		}

	}
}