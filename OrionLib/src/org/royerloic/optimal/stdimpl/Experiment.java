/*
 * Optimal Project
 * 
 * M.Sc. Ing. Loic Royer
 * 
 * Fraunhofer IWU Dresden
 *  
 */

package org.royerloic.optimal.stdimpl;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.royerloic.math.INumericalVector;
import org.royerloic.optimal.interf.IExperiment;

/**
 * Experiment is the representation of real experiments as the couple of
 * vectors(MVector): (InputVector,OutputVector) there is also the information of
 * the date and time of the experiment and how much time it lasted.
 * 
 * @see MVector
 * 
 * @author MSc. Ing. Loic Royer
 */
public class Experiment implements IExperiment
{

	/**
	 * <code>mDateFormat</code> is the Date format used to format a date into a
	 * String.
	 */
	private SimpleDateFormat	mDateFormat	= new SimpleDateFormat("yyyy:MM:dd:hh:mm:ss");

	/**
	 * <code>mDate</code> is the Date of the Experiment.
	 */
	private Date							mDate;

	/**
	 * <code>mDuration</code> is the duration of the Experiment in milliseconds.
	 */
	private int								mDuration;

	/**
	 * <code>mInput</code> is the Experiment's input vector.
	 */
	private INumericalVector	mInput;

	/**
	 * <code>mOutput</code> is the Experiment's output vector.
	 */
	private INumericalVector	mOutput;

	/**
	 * Constructs an Experiment given the couple of vectors (pInput,pOutput)
	 * 
	 * @see MVector
	 * 
	 * @param pInput
	 *          input vector.
	 * @param pOutput
	 *          output vector
	 */
	public Experiment(final INumericalVector pInput, final INumericalVector pOutput)
	{
		super();
		this.mInput = pInput;
		this.mOutput = pOutput;
	}

	/**
	 * Constructor
	 */
	public Experiment()
	{
		super();
	}

	/**
	 * This method together with Experiment#end() provides an easy way to set the
	 * start time and duration of an experiment. Call this method just before the
	 * beginning of the experiment, And call end() just after its end.
	 * 
	 */
	public final void begin()
	{
		this.mDate = new Date();

	}

	/**
	 * Specifies that an experiment ended. This method must be called after a call
	 * to Experiment#begin(). It sets the start time and duration of the
	 * Experiment.
	 */
	public final void end()
	{
		final Date lDate = new Date();
		final long lBegin = this.mDate.getTime();
		final long lEnd = lDate.getTime();
		this.mDuration = (int) ((lEnd - lBegin));
	}

	/**
	 * Sets the input and output vectors of this experiment.
	 * 
	 * @param pInput
	 *          input vector.
	 * @param pOutput
	 *          output vector.
	 */
	public final void set(final INumericalVector pInput, final INumericalVector pOutput)
	{
		this.mInput = pInput;
		this.mOutput = pOutput;
	}

	/**
	 * Returns the input vector of this Experiment.
	 * 
	 * @see MVector
	 * 
	 * @return input vector.
	 */
	public final INumericalVector getInput()
	{
		return this.mInput;
	}

	/**
	 * Returns the output Vector of this Experiment.
	 * 
	 * @see MVector
	 * 
	 * @return output vector.
	 */
	public final INumericalVector getOutput()
	{
		return this.mOutput;
	}

	/**
	 * Returns the Date of the Experiment.
	 * 
	 * @return Date of the Experiment
	 */
	public final Date getDate()
	{
		return this.mDate;
	}

	/**
	 * Returns the duration of the Experiment .
	 * 
	 * @return duration
	 */
	public final int getDuration()
	{
		return this.mDuration;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public final String toString()
	{
		return " Date: " + this.mDate.toString() + "\n" + " Duration: " + this.mDuration + "\n" + " Input: "
				+ this.mInput.toString() + "\n" + " Output: " + this.mOutput.toString();
	}

	/**
	 * Returns a text description of the Experiment.
	 * 
	 * @return text description
	 */
	public final String toText()
	{
		String lTextString = "";

		final INumericalVector lInputVector = getInput();
		final INumericalVector lOutputVector = getOutput();

		for (int k = 0; k < lInputVector.getDimension(); k++)
		{
			final double lValue = lInputVector.get(k);
			final String lValueString = Double.toString(lValue);
			lTextString += lValueString + "\t";
		}

		for (int k = 0; k < lOutputVector.getDimension(); k++)
		{
			final double lValue = lOutputVector.get(k);
			final String lValueString = Double.toString(lValue);
			lTextString += lValueString + "\t";
		}

		return lTextString;
	}

	/**
	 * Returns a Html formated String describing the Experiment.
	 * 
	 * @return Html formated description of the Experiment.
	 */
	public final String toHtml()
	{
		String lHtmlString = "";

		lHtmlString += "Date and Time: " + getDate().toString() + " :<br>\n";
		lHtmlString += "Duration: " + getDuration() / 1000 + " seconds:<br>\n";

		final INumericalVector lInputVector = getInput();

		lHtmlString += "Input Vector:<br>\n";
		lHtmlString += "<table border=\"1\">\n";
		lHtmlString += "<tr>";

		for (int k = 0; k < lInputVector.getDimension(); k++)
		{
			final double lValue = lInputVector.get(k);
			final String lValueString = Double.toString(lValue);
			lHtmlString += "<td>" + lValueString + "</td>\n";
		}
		lHtmlString += "</tr>\n";
		lHtmlString += "</table>\n";

		final INumericalVector lOutputVector = getOutput();

		lHtmlString += "Output Vector:<br>\n";
		lHtmlString += "<table border=\"1\">\n";
		lHtmlString += "<tr>";
		for (int k = 0; k < lOutputVector.getDimension(); k++)
		{
			final double lValue = lOutputVector.get(k);
			final String lValueString = Double.toString(lValue);
			lHtmlString += "<td>" + lValueString + "</td>\n";
		}
		lHtmlString += "</tr>\n";
		lHtmlString += "</table>\n";

		return lHtmlString;
	}

	/**
	 * @see org.royerloic.java.IObject#clone()
	 */
	@Override
	public Object clone()
	{
		final Experiment lExperiment = new Experiment();
		lExperiment.mDate = (Date) this.mDate.clone();
		lExperiment.mDateFormat = (SimpleDateFormat) this.mDateFormat.clone();
		lExperiment.mDuration = this.mDuration;
		lExperiment.mInput = (INumericalVector) this.mInput.clone();
		lExperiment.mOutput = (INumericalVector) this.mOutput.clone();

		return lExperiment;
	}

	/**
	 * @see org.royerloic.java.IObject#copyFrom(java.lang.Object)
	 */
	public void copyFrom(final Object pObject)
	{
		if (pObject instanceof Experiment)
		{
			final Experiment lExperiment = (Experiment) pObject;

			lExperiment.mDate = (Date) this.mDate.clone();
			lExperiment.mDateFormat = (SimpleDateFormat) this.mDateFormat.clone();
			lExperiment.mDuration = this.mDuration;
			lExperiment.mInput = (INumericalVector) this.mInput.clone();
			lExperiment.mOutput = (INumericalVector) this.mOutput.clone();
		}

	}

	@Override
	public boolean equals(final Object pObject)
	{
		boolean lEquals = false;
		if (pObject instanceof Experiment)
		{
			final Experiment lExperiment = (Experiment) pObject;

			lEquals = true;
			lEquals &= lExperiment.mDate.equals(this.mDate);
			lEquals &= lExperiment.mDuration == this.mDuration;
			lEquals &= lExperiment.mInput.equals(this.mInput);
			lEquals &= lExperiment.mOutput.equals(this.mOutput);
		}
		return lEquals;
	}

	@Override
	public int hashCode()
	{
		int lCode = 1;
		lCode *= this.mDate.hashCode();
		lCode *= this.mDuration;
		lCode *= this.mInput.hashCode();
		lCode *= this.mOutput.hashCode();
		return lCode;
	}
}