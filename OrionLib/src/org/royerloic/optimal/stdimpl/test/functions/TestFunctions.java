/*
 * Created on 04.01.2005
 * by Dipl.-Inf. MSc. Ing Loic Royer
 */
package org.royerloic.optimal.stdimpl.test.functions;

import org.royerloic.math.INumericalVector;
import org.royerloic.math.stdimpl.NumericalVector;
import org.royerloic.optimal.interf.IExperimentFunction;

/**
 * @author Dipl.-Inf. MSc. Ing. Loic Royer
 * 
 */
public class TestFunctions implements IExperimentFunction
{

	private String	mName;
	private int			mFunctionIndex;

	/**
	 * 
	 */
	public TestFunctions(final int pFunctionIndex)
	{
		super();
		mFunctionIndex = pFunctionIndex;
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

	/**
	 * @see org.royerloic.optimal.interf.IExperimentFunction#evaluate(org.royerloic.math.INumericalVector)
	 */
	public INumericalVector evaluate(INumericalVector pExperimentInputVector)
	{
		// System.out.println(mName);
		/***************************************************************************
		 * try { Thread.sleep(100); } catch (InterruptedException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); }/
		 **************************************************************************/

		double px = pExperimentInputVector.get(0);
		double py = pExperimentInputVector.get(1);

		// 0.9/(1+sq(0.3*(y0-5.2))+sq(0.3*(y1-5.7)))"
		// + " +1/(1+sq(0.9*(y0+4.3))+sq(0.9*(y1+7.8)))"

		double x = 2 * px - 1;
		double y = 2 * py - 1;

		double z = 0;

		switch (mFunctionIndex)
		{
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

		return new NumericalVector(new double[]
		{ z });
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
		return 1;
	}

}
