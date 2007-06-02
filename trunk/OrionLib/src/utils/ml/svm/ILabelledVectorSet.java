/*
 * Created on 29.10.2004 by Dipl.-Inf. MSc. Ing Loic Royer
 */
package utils.ml.svm;

import utils.math.IVectorArray;

/**
 * @author Dipl.-Inf. MSc. Ing. Loic Royer
 * 
 */
public interface ILabelledVectorSet
{
	public int size();

	public void clear();

	public void addVector(final IVectorArray pVector, final double pClass);

	public void delVector(final int pIndex);

	public IVectorArray getVector(final int pIndex);

	public double getClass(final int pIndex);

	public void generateTrainTestRandomSubsets(final double pRatio);

	public ILabelledVectorSet getTrainSubset();

	public ILabelledVectorSet getTestSubset();

}