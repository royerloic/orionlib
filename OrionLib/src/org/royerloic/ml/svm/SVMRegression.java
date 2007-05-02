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
		this.mGamma = pGamma;
		this.mCost = pCost;
		this.mNu = pNu;

		this.mParameters = new Parameter();

		this.mParameters.svm_type = Parameter.NU_SVR;
		this.mParameters.kernel_type = Parameter.RBF;
		this.mParameters.gamma = this.mGamma;
		this.mParameters.C = this.mCost;
		this.mParameters.nu = this.mNu;

		// Other standard parameters:
		this.mParameters.degree = 3;
		this.mParameters.coef0 = 0;
		this.mParameters.p = 0.1;
		this.mParameters.cache_size = 40;
		this.mParameters.eps = 1e-3;
		this.mParameters.shrinking = 1;
		this.mParameters.nr_weight = 0;
		this.mParameters.weight_label = new int[0];
		this.mParameters.weight = new double[0];

		this.mModel = null;
	}

	/**
	 * @see org.royerloic.ml.svm.IRegression#crossValidation(org.royerloic.ml.svm.ILabelledVectorSet)
	 */
	public double crossValidation(final ILabelledVectorSet pSet, final int pFolds, final double pRatio)
	{
		double lError = Double.NEGATIVE_INFINITY;

		for (int i = 0; i < pSet.size(); i++)
		{
			pSet.generateTrainTestRandomSubsets(pRatio);
			final ILabelledVectorSet lTrain = pSet.getTrainSubset();
			final ILabelledVectorSet lTest = pSet.getTestSubset();
			train(lTrain);

			for (int j = 0; j < lTest.size(); j++)
			{
				final INumericalVector lVector = (INumericalVector) lTest.getVector(j);
				final double lValue = lTest.getClass(j);

				final double lPredictedValue = predict(lVector);

				// System.out.println(" value= "+lValue+" predicted
				// value="+lPredictedValue);

				lError = Math.max(lError, Math.abs((lPredictedValue - lValue)));
			}

		}
		if (Double.isNaN(lError))
			lError = Double.POSITIVE_INFINITY;
		return lError;
	}

	/**
	 * @see org.royerloic.ml.svm.IRegression#checkExactness(org.royerloic.ml.svm.ILabelledVectorSet)
	 */
	public double checkExactness(final ILabelledVectorSet pSet)
	{
		double lError = 0;

		final ILabelledVectorSet lTrain = pSet;
		final ILabelledVectorSet lTest = pSet;
		train(lTrain);

		for (int j = 0; j < lTest.size(); j++)
		{
			final INumericalVector lVector = (INumericalVector) lTest.getVector(j);
			final double lValue = lTest.getClass(j);

			final double lPredictedValue = predict(lVector);

			// System.out.println(" value= "+lValue+" predicted
			// value="+lPredictedValue);

			lError += Math.abs(lPredictedValue - lValue);
		}

		return lError / lTest.size();
	}

	/**
	 * @see org.royerloic.ml.svm.IRegression#train(org.royerloic.ml.svm.ILabelledVectorSet)
	 */
	public void train(final ILabelledVectorSet pTrainingSet)
	{
		if (pTrainingSet instanceof LabelledVectorSet)
		{
			this.mProblem = ((LabelledVectorSet) pTrainingSet).toProblem();
			/*************************************************************************
			 * System.out.println("train: gamma="+mParameters.gamma +"
			 * cost="+mParameters.C);/
			 ************************************************************************/

			this.mModel = SVM.svmTrain(this.mProblem, this.mParameters);
		}
	}

	/**
	 * @see org.royerloic.ml.svm.IRegression#predict(org.royerloic.ml.svm.ILabelledVectorSet)
	 */
	public double predict(final IVectorArray pVector)
	{

		final Node[] lVector = new Node[pVector.getDimension()];
		for (int i = 0; i < pVector.getDimension(); i++)
			lVector[i] = new Node(i, pVector.get(i));

		return SVM.svmPredict(this.mModel, lVector);
	}

	/**
	 * @see org.royerloic.java.IObject#clone()
	 */
	@Override
	public Object clone()
	{
		try
		{
			final SVMRegression lSVMRegression = (SVMRegression) super.clone();

			lSVMRegression.mProblem = (Problem) this.mProblem.clone();
			lSVMRegression.mParameters = (Parameter) this.mParameters.clone();
			/* lSVMRegression.mModel = (Model) mModel.clone();/* */

			return lSVMRegression;
		}
		catch (final CloneNotSupportedException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @see org.royerloic.java.IObject#copyFrom(java.lang.Object)
	 */
	public void copyFrom(final Object pObject)
	{
		// TODO Auto-generated method stub

	}

}