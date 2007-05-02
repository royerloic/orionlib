package org.royerloic.ml.svm.libsvm;

//
// // Model
//
public class Model implements java.io.Serializable, Cloneable
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 2711592896037300561L;

	Parameter		mParameter;											// parameter

	int					mNumberOfClasses;								// number of classes, = 2 in

	// regression/one class SVM

	int					mNumberOfSupportVectors;					// total #mSupportVectorsTable

	Node[][]		mSupportVectorsTable;						// SVs

	// (mSupportVectorsTable[mNumberOfSupportVectors])

	double[][]	mSupportVectorCoeficientsTable;	// coefficients for SVs in
																							// decision

	// functions

	// (mSupportVectorCoeficientsTable[n-1][mNumberOfSupportVectors])

	double[]		mRho;														// constants in decision functions

	// (mRho[n*(n-1)/2])

	double[]		mPairwiseProbability;						// pariwise probability
																							// information

	double[]		probB;

	// for classification only

	int[]				mSVCLabelsTable;									// mSVCLabelsTable of each class

	// (mSVCLabelsTable[n])

	int[]				mNumberOfSupportVectorsPerClass;	// number of SVs for each class

	// (mNumberOfSupportVectorsPerClass[n])
	// mNumberOfSupportVectorsPerClass[0] + mNumberOfSupportVectorsPerClass[1] +
	// ... + mNumberOfSupportVectorsPerClass[n-1] = mNumberOfSupportVectors

	/**
	 * @see org.royerloic.java.IObject#clone()
	 */
	@Override
	public Object clone()
	{
		try
		{
			final Model lModel = (Model) super.clone();

			for (int i = 0; i < this.mNumberOfSupportVectors; i++)
			{
				for (int j = 0; j < this.mSupportVectorsTable[i].length; j++)
					lModel.mSupportVectorsTable[i][j] = (Node) this.mSupportVectorsTable[i][j].clone();
				lModel.mSupportVectorCoeficientsTable[i] = this.mSupportVectorCoeficientsTable[i].clone();
			}

			return lModel;
		}
		catch (final CloneNotSupportedException e)
		{
			return null;
		}
	}

};
