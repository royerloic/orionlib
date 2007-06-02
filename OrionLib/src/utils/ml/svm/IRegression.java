package utils.ml.svm;

import utils.math.IVectorArray;

/**
 * @author Dipl.-Inf. MSc. Ing. Loic Royer
 * 
 */
public interface IRegression
{
	public double crossValidation(final ILabelledVectorSet pTrainingSet, int pFolds, double pRatio);

	public double checkExactness(final ILabelledVectorSet pTrainingSet);

	public void train(final ILabelledVectorSet pTrainingSet);

	public double predict(final IVectorArray pSet);
}