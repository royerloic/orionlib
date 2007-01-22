/*
 * Created on 04.01.2005
 * by Dipl.-Inf. MSc. Ing Loic Royer
 */
package org.royerloic.optimal.interf;

/**
 * @author Dipl.-Inf. MSc. Ing. Loic Royer
 * 
 */
public interface IOptimalEventListener
{
	void experimentDone(final IExperimentFunctionStub pExperimentFunctionStub, final IExperiment pExperiment);

	void newBestExperiment(final IExperimentDatabase pExperimentDatabase, final IExperiment pExperiment);
}
