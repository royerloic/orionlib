/*
 * Created on 02.12.2004 by Dipl.-Inf. MSc. Ing Loic Royer
 */
package org.royerloic.optimal.stdimpl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.royerloic.math.INumericalVector;
import org.royerloic.math.io.MatrixFile;
import org.royerloic.math.stdimpl.NumericalVector;
import org.royerloic.optimal.interf.IExperiment;
import org.royerloic.optimal.interf.IExperimentDatabase;
import org.royerloic.optimal.interf.IExperimentDatabaseStore;
import org.royerloic.optimal.interf.IObjectiveFunction;

/**
 * @author Dipl.-Inf. MSc. Ing. Loic Royer
 * 
 */
public class CsvDatabaseStore implements IExperimentDatabaseStore
{

	private File								mCsvFile;

	private IExperimentDatabase	mExperimentDatabase;

	private IObjectiveFunction	mObjectiveFunction;

	/**
	 * 
	 */
	public CsvDatabaseStore(File pCsvFile)
	{
		super();
		mCsvFile = pCsvFile;
	}

	/**
	 * @see org.royerloic.optimal.interf.IExperimentDatabaseStore#setExperimentDatabase(org.royerloic.optimal.interf.IExperimentDatabase)
	 */
	public void setExperimentDatabase(IExperimentDatabase pExperimentDatabase)
	{
		mExperimentDatabase = pExperimentDatabase;

	}

	/**
	 * @see org.royerloic.optimal.interf.IExperimentDatabaseStore#setObjectiveFunction(org.royerloic.optimal.interf.IObjectiveFunction)
	 */
	public void setObjectiveFunction(IObjectiveFunction pObjectiveFunction)
	{
		mObjectiveFunction = pObjectiveFunction;

	}

	/**
	 * @see org.royerloic.optimal.interf.IExperimentDatabaseStore#loadTo(org.royerloic.optimal.interf.IExperimentDatabase)
	 */
	public void load()
	{
		MatrixFile lMatrixFile = new MatrixFile(mCsvFile);
		INumericalVector lHeaderVector = lMatrixFile.getVector(0);

		final int lNumberOfExperiments = (int) lHeaderVector.get(0);
		final int lInputDimension = (int) lHeaderVector.get(1);
		final int lOutputDimension = (int) lHeaderVector.get(1);

		for (int i = 1; i <= lNumberOfExperiments; i++)
		{
			INumericalVector lLine = lMatrixFile.getVector(i);
			INumericalVector lInput = new NumericalVector(lInputDimension);
			int j;
			for (j = 0; j < lInputDimension; j++)
			{
				double lValue = lLine.get(j);
				lInput.set(j, lValue);
			}

			INumericalVector lOutput = new NumericalVector(lOutputDimension);
			for (j = 0; j < lOutputDimension; j++)
			{
				double lValue = lLine.get(lInputDimension + j);
				lOutput.set(j, lValue);
			}

			IExperiment lExperiment = new Experiment();
			lExperiment.begin();
			lExperiment.end();

			lExperiment.set(lInput, lOutput);
			mExperimentDatabase.addExperiment(lExperiment);
		}

	}

	/**
	 * @see org.royerloic.optimal.interf.IExperimentDatabaseStore#saveFrom(org.royerloic.optimal.interf.IExperimentDatabase)
	 */
	public void save()
	{
		try
		{
			File lOutputFile = mCsvFile;

			try
			{
				lOutputFile.createNewFile();
			}
			catch (IOException e4)
			{
				// TODO Auto-generated catch block
				e4.printStackTrace(System.out);
			}

			FileWriter lFileWriter;
			BufferedWriter lBufferedWriter;
			try
			{
				lFileWriter = new FileWriter(lOutputFile);
				lBufferedWriter = new BufferedWriter(lFileWriter);
			}
			catch (IOException e1)
			{
				System.out.println("Output File: " + lOutputFile + " not found.");
				return;
			}

			try
			{
				final IExperimentDatabase lDatabase = mExperimentDatabase;
				final int lSize = lDatabase.getNumberOfExperiments();

				String lValueString = Integer.toString(lSize);
				lBufferedWriter.write(lValueString + "\t");

				if (lSize == 0)
				{

					return;
				}

				IExperiment lExperiment = lDatabase.getExperiment(0);
				INumericalVector lInputVector = lExperiment.getInput();
				INumericalVector lOutputVector = lExperiment.getOutput();
				final int lInputDimension = lInputVector.getDimension();
				final int lOutputDimension = lOutputVector.getDimension();

				lValueString = Integer.toString(lInputDimension);
				lBufferedWriter.write(lValueString + "\t");
				lValueString = Integer.toString(lOutputDimension);
				lBufferedWriter.write(lValueString + "\t");
				lBufferedWriter.newLine();

				for (int i = 0; i < lSize; i++)
				{
					lExperiment = lDatabase.getExperiment(i);
					lInputVector = lExperiment.getInput();
					lOutputVector = lExperiment.getOutput();

					for (int k = 0; k < lInputDimension; k++)
					{
						final double lValue = lInputVector.get(k);
						lValueString = Double.toString(lValue);
						lBufferedWriter.write(lValueString + "\t");
					}

					for (int k = 0; k < lOutputDimension; k++)
					{
						final double lValue = lOutputVector.get(k);
						lValueString = Double.toString(lValue);
						lBufferedWriter.write(lValueString + "\t");
					}

					final double lObjectiveValue = mObjectiveFunction.evaluate(lOutputVector);

					final String lObjectiveValueString = Double.toString(lObjectiveValue);
					lBufferedWriter.write(lObjectiveValueString + "\t");
					lBufferedWriter.newLine();
				}

				lBufferedWriter.flush();
			}
			catch (IOException e2)
			{
				System.out.println("Error while writing: " + e2.getCause());
			}
			finally
			{
				lFileWriter.close();
			}
		}
		catch (Exception any)
		{
			any.printStackTrace(System.out);
		}

	}

}