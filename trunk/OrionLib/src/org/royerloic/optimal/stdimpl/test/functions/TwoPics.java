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
public class TwoPics implements IExperimentFunction
{

	private String	mName;

	/**
	 * 
	 */
	public TwoPics(String pName)
	{
		super();
		mName = pName;
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

		double x = pExperimentInputVector.get(0);
		double y = pExperimentInputVector.get(1);
		// double z = pExperimentInputVector.get(2)-0.5 ;

		// 0.9/(1+sq(0.3*(y0-5.2))+sq(0.3*(y1-5.7)))"
		// + " +1/(1+sq(0.9*(y0+4.3))+sq(0.9*(y1+7.8)))"

		double x1 = (x - 0.52);
		double y1 = (y - 0.57);

		double d1 = Math.sqrt(x1 * x1 + y1 * y1);

		double x2 = (x - 0.23);
		double y2 = (y - 0.78);

		double d2 = Math.sqrt(x2 * x2 + y2 * y2);

		double u = 0.9 / (1 + 10 * d1) + 1 / (1 + 13 * d2);
		double v = u;

		return new NumericalVector(new double[]
		{ u, v });
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
