/*
 * Created on 01.12.2004 by Dipl.-Inf. MSc. Ing Loic Royer
 */
package org.royerloic.optimal.interf;

import java.util.Date;

import org.royerloic.java.IObject;
import org.royerloic.math.INumericalVector;

/**
 * @author Dipl.-Inf. MSc. Ing. Loic Royer
 * 
 */
public interface IExperiment extends IObject
{
	/**
	 * Returns the Date of the Experiment.
	 * 
	 * @return Date of the Experiment
	 */
	public abstract Date getDate();

	/**
	 * Returns the duration of the Experiment .
	 * 
	 * @return duration
	 */
	public abstract int getDuration();

	/**
	 * Returns the input vector of this Experiment.
	 * 
	 * @see MVector
	 * 
	 * @return input vector.
	 */
	public abstract INumericalVector getInput();

	/**
	 * Returns the output Vector of this Experiment.
	 * 
	 * @see MVector
	 * 
	 * @return output vector.
	 */
	public abstract INumericalVector getOutput();

	/**
	 * This method together with Experiment#end() provides an easy way to set the
	 * start time and duration of an experiment. Call this method just before the
	 * beginning of the experiment, And call end() just after its end.
	 * 
	 */
	public abstract void begin();

	/**
	 * Specifies that an experiment ended. This method must be called after a call
	 * to Experiment#begin(). It sets the start time and duration of the
	 * Experiment.
	 */
	public abstract void end();

	/**
	 * Sets the input and output vectors of this experiment.
	 * 
	 * @param pInput
	 *          input vector.
	 * @param pOutput
	 *          output vector.
	 */
	public abstract void set(final INumericalVector pInput, final INumericalVector pOutput);
}