/*
 * Created on 04.01.2005 by Dipl.-Inf. MSc. Ing Loic Royer
 */
package org.royerloic.optimal.stdimpl.test.functions;

import org.royerloic.math.INumericalVector;
import org.royerloic.math.stdimpl.NumericalVector;
import org.royerloic.optimal.interf.IExperimentFunction;

/**
 * @author Dipl.-Inf. MSc. Ing. Loic Royer
 * 
 */
public class DoubleTestFunctions implements IExperimentFunction
{

	private String	mName;

	private int			mFunctionIndex1;

	private int			mFunctionIndex2;

	/**
	 * 
	 */
	public DoubleTestFunctions(final int pFunctionIndex1, final int pFunctionIndex2)
	{
		super();
		mFunctionIndex1 = pFunctionIndex1;
		mFunctionIndex2 = pFunctionIndex2;
	}

	static double dist(double x, double y)
	{
		return Math.sqrt(x * x + y * y);
	}

	static double sync(double x, double y)
	{
		return (1 / 9.9998) * Math.sin(10 * dist(x, y)) / (dist(x, y) + 0.0000000001);
	}

	static double simplequad(double x, double y)
	{
		return 1.0 / (1 + 4 * (x * x + y * y));
	}

	static double spike(double x, double y)
	{
		return 1.0 - Math.pow((x * x + y * y), 0.25);
	}

	static double gridspike(double x, double y)
	{
		return spike(x, y) * ((Math.cos(10 * x) * Math.cos(10 * y)) - (x + y));
	}

	static double flattop(double x, double y)
	{
		return 0.05 * (Math.cos(10 * x) * Math.cos(10 * y)) + 0.95 / (1 + Math.pow(2 * dist(x, y), 20));
	}

	static double multiquad(double x, double y)
	{
		return (1 / 1.1439)
				* (simplequad(x - 0.5, y - 0.8) + 0.8 * simplequad(x + 0.7, y + 0.2) + 0.5 * simplequad(x + 0.7,
						y - 0.4));
	}

	static double multispike(double x, double y)
	{
		return (1 / 0.7378)
				* (spike(x - 0.5, y - 0.8) + 0.8 * spike(x + 0.7, y + 0.2) + 0.5 * spike(x + 0.7, y - 0.4));
	}

	static double multisync(double x, double y)
	{
		return (1 / 1.0081)
				* (sync(x - 0.5, y - 0.8) + 0.8 * sync(x + 0.7, y + 0.2) + 0.5 * sync(x + 0.7, y - 0.4));
	}

	static double multigridspike(double x, double y)
	{
		return (1 / 1.7153)
				* (gridspike(x - 0.5, y - 0.8) + 0.8 * gridspike(x + 0.7, y + 0.2) + 0.5 * gridspike(x + 0.7, y - 0.4));
	}

	static double multiflattop(double x, double y)
	{
		return (1 / 1.2675)
				* (flattop(x - 0.5, y - 0.8) + 0.8 * flattop(x + 0.7, y + 0.2) + 0.5 * flattop(x + 0.7, y - 0.4));
	}

	private double evaluateOneFunction(final int pFunctionIndex, final INumericalVector pVector)
	{
		double px = pVector.get(0);
		double py = pVector.get(1);

		double x = 2 * px - 1;
		double y = 2 * py - 1;

		double z = 0;

		switch (pFunctionIndex)
		{
			case 0:
			{
				z = 0;
			}
				break;

			case 1:
			{
				z = simplequad(x, y);
			}
				break;

			case 2:
			{
				z = spike(x, y);
			}
				break;

			case 3:
			{
				z = sync(x, y);
			}
				break;

			case 4:
			{
				z = gridspike(x, y);
			}
				break;

			case 5:
			{
				z = flattop(x, y);
			}
				break;

			case 6:
			{
				z = multiquad(x, y);
			}
				break;

			case 7:
			{
				z = multispike(x, y);
			}
				break;

			case 8:
			{
				z = multisync(x, y);
			}
				break;

			case 9:
			{
				z = multigridspike(x, y);
			}
				break;

			case 10:
			{
				z = multiflattop(x, y);
			}
				break;

		}
		return z;
	}

	private INumericalVector evaluateFunction(final int pFunctionIndex1,
																						final int pFunctionIndex2,
																						INumericalVector pVector)
	{

		double z1 = evaluateOneFunction(pFunctionIndex1, pVector);
		double z2 = evaluateOneFunction(pFunctionIndex2, pVector);

		return new NumericalVector(new double[]
		{ z1, z2 });
	}

	/**
	 * @see org.royerloic.optimal.interf.IExperimentFunction#evaluate(org.royerloic.math.INumericalVector)
	 */
	public INumericalVector evaluate(INumericalVector pExperimentInputVector)
	{
		return evaluateFunction(mFunctionIndex1, mFunctionIndex2, pExperimentInputVector);
	}

	/**
	 * @see org.royerloic.optimal.interf.IExperimentFunction#getInputDimension()
	 */
	public int getInputDimension()
	{
		// TODO Auto-generated method stub
		return 2;
	}

	/**
	 * @see org.royerloic.optimal.interf.IExperimentFunction#getOutputDimension()
	 */
	public int getOutputDimension()
	{
		// TODO Auto-generated method stub
		return 2;
	}

}