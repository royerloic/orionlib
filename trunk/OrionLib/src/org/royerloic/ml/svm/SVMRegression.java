/*
 * Created on 29.10.2004 by Dipl.-Inf. MSc. Ing Loic Royer
 */
package org.royerloic.ml.svm;

import org.royerloic.java.IObject;
import org.royerloic.math.INumericalVector;
import org.royerloic.math.IVectorArray;
import org.royerloic.ml.svm.libsvm.Model;
import org.royerloic.ml.svm.libsvm.Node;
import org.royerloic.ml.svm.libsvm.Parameter;
import org.royerloic.ml.svm.libsvm.Problem;
import org.royerloic.ml.svm.libsvm.SVM;

/**
 * @author Dipl.-Inf. MSc. Ing. Loic Royer
 * 
 */
public class SVMRegression implements IRegression, IObject, Cloneable
{
	private Problem		mProblem;

	private Parameter	mParameters;

	private Model			mModel;

	private double		mGamma;

	private double		mCost;

	private double		mNu;

	/**
	 * 
	 */
	public SVMRegression(final double pGamma, final double pCost, final double pNu)
	{
		super();
		mGamma = pGamma;
		mCost = pCost;
		mNu = pNu;

		mParameters = new Parameter();

		mParameters.svm_type = Parameter.NU_SVR;
		mParameters.kernel_type = Parameter.RBF;
		mParameters.gamma = mGamma;
		mParameters.C = mCost;
		mParameters.nu = mNu;

		// Other standard parameters:
		mParameters.degree = 3;
		mParameters.coef0 = 0;
		mParameters.p = 0.1;
		mParameters.cache_size = 40;
		mParameters.eps = 1e-3;
		mParameters.shrinking = 1;
		mParameters.nr_weight = 0;
		mParameters.weight_label = new int[0];
		mParameters.weight = new double[0];

		mModel = null;
	}

	/**
	 * @see org.royerloic.ml.svm.IRegression#crossValidation(org.royerloic.ml.svm.ILabelledVectorSet)
	 */
	public double crossValidation(ILabelledVectorSet pSet, int pFolds, double pRatio)
	{
		double lError = Double.NEGATIVE_INFINITY;

		for (int i = 0; i < pSet.size(); i++)
		{
			pSet.generateTrainTestRandomSubsets(pRatio);
			ILabelledVectorSet lTrain = pSet.getTrainSubset();
			ILabelledVectorSet lTest = pSet.getTestSubset();
			train(lTrain);

			for (int j = 0; j < lTest.size(); j++)
			{
				INumericalVector lVector = (INumericalVector) lTest.getVector(j);
				double lValue = lTest.getClass(j);

				double lPredictedValue = predict(lVector);

				// System.out.println(" value= "+lValue+" predicted
				// value="+lPredictedValue);

				lError = Math.max(lError, Math.abs((lPredictedValue - lValue)));
			}

		}
		if (Double.isNaN(lError))
		{
			lError = Double.POSITIVE_INFINITY;
		}
		return lError;
	}

	/**
	 * @see org.royerloic.ml.svm.IRegression#checkExactness(org.royerloic.ml.svm.ILabelledVectorSet)
	 */
	public double checkExactness(ILabelledVectorSet pSet)
	{
		double lError = 0;

		ILabelledVectorSet lTrain = pSet;
		ILabelledVectorSet lTest = pSet;
		train(lTrain);

		for (int j = 0; j < lTest.size(); j++)
		{
			INumericalVector lVector = (INumericalVector) lTest.getVector(j);
			double lValue = lTest.getClass(j);

			double lPredictedValue = predict(lVector);

			// System.out.println(" value= "+lValue+" predicted
			// value="+lPredictedValue);

			lError += Math.abs(lPredictedValue - lValue);
		}

		return lError / lTest.size();
	}

	/**
	 * @see org.royerloic.ml.svm.IRegression#train(org.royerloic.ml.svm.ILabelledVectorSet)
	 */
	public void train(ILabelledVectorSet pTrainingSet)
	{
		if (pTrainingSet instanceof LabelledVectorSet)
		{
			mProblem = ((LabelledVectorSet) pTrainingSet).toProblem();
			/*************************************************************************
			 * System.out.println("train: gamma="+mParameters.gamma +"
			 * cost="+mParameters.C);/
			 ************************************************************************/

			mModel = SVM.svmTrain(mProblem, mParameters);
		}
	}

	/**
	 * @see org.royerloic.ml.svm.IRegression#predict(org.royerloic.ml.svm.ILabelledVectorSet)
	 */
	public double predict(IVectorArray pVector)
	{

		Node[] lVector = new Node[pVector.getDimension()];
		for (int i = 0; i < pVector.getDimension(); i++)
		{
			lVector[i] = new Node(i, pVector.get(i));
		}

		return SVM.svmPredict(mModel, lVector);
	}

	/**
	 * @see org.royerloic.java.IObject#clone()
	 */
	public Object clone()
	{
		try
		{
			SVMRegression lSVMRegression = (SVMRegression) super.clone();

			lSVMRegression.mProblem = (Problem) mProblem.clone();
			lSVMRegression.mParameters = (Parameter) mParameters.clone();
			/* lSVMRegression.mModel = (Model) mModel.clone();/* */

			return lSVMRegression;
		}
		catch (CloneNotSupportedException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @see org.royerloic.java.IObject#copyFrom(java.lang.Object)
	 */
	public void copyFrom(Object pObject)
	{
		// TODO Auto-generated method stub

	}

}