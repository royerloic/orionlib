/*
 * Created on 01.12.2004 by Dipl.-Inf. MSc. Ing Loic Royer
 */
package org.royerloic.optimal.interf;

import java.util.List;

import org.royerloic.java.IObject;
import org.royerloic.math.INumericalVector;

/**
 * @author Dipl.-Inf. MSc. Ing. Loic Royer
 * 
 */
public interface IExperimentDatabase extends IObject
{

	public void setObjectiveFunction(IObjectiveFunction pObjectiveFunction);

	public int getNumberOfExperiments();

	public boolean addExperiment(final IExperiment pExperiment);

	public IExperiment getExperiment(final int pIndex);

	public int findExperiment(final IExperiment pExperiment);

	public boolean contains(final IExperiment pExperiment);

	public boolean containsInputVector(final INumericalVector pVector);

	public INumericalVector getMinimumInputValuesVector();

	public INumericalVector getMaximumInputValuesVector();

	public INumericalVector getMinimumOutputValuesVector();

	public INumericalVector getMaximumOutputValuesVector();

	public IExperiment getNeighboor(final INumericalVector pVector);

	public IExperiment getBestExperiment();

	public List getListOfBestExperiments();

	public List getListOfBestExperimentValues();

	public boolean stagnating(final int pStart, final int pTime);

}