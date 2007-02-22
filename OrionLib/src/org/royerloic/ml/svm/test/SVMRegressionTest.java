/*
 * Created on 12.01.2005
 * by Dipl.-Inf. MSc. Ing Loic Royer
 */
package org.royerloic.ml.svm.test;

import junit.framework.TestCase;

import org.royerloic.math.IVectorArray;
import org.royerloic.math.stdimpl.NumericalVector;
import org.royerloic.ml.svm.ILabelledVectorSet;
import org.royerloic.ml.svm.LabelledVectorSet;
import org.royerloic.ml.svm.SVMRegression;

/**
 * @author Dipl.-Inf. MSc. Ing. Loic Royer
 * 
 */
public class SVMRegressionTest extends TestCase
{
	SVMRegression	mSVMRegression;

	/**
	 * 
	 */
	public SVMRegressionTest()
	{
		super();

		mSVMRegression = new SVMRegression(0.001, 10000, 0.5);

		ILabelledVectorSet lLabelledVectorSet = new LabelledVectorSet();

		IVectorArray lVector1 = new NumericalVector(new double[]
		{ 0 });
		IVectorArray lVector2 = new NumericalVector(new double[]
		{ 0.5 });
		IVectorArray lVector3 = new NumericalVector(new double[]
		{ 1 });
		IVectorArray lVector4 = new NumericalVector(new double[]
		{ 0.15 });

		lLabelledVectorSet.addVector(lVector1, 0);
		lLabelledVectorSet.addVector(lVector2, 0.5);
		lLabelledVectorSet.addVector(lVector3, 1);

		mSVMRegression.train(lLabelledVectorSet);

		double lResult = mSVMRegression.predict(lVector4);

		System.out.println("Result: " + lResult);

	}

	public static void main(String[] args)
	{
		SVMRegressionTest lSVMRegressionTest = new SVMRegressionTest();
	}

	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception
	{
		super.setUp();

	}

	/*
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception
	{
		super.tearDown();
	}

}