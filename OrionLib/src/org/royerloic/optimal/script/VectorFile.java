/*
 * Optimal Project
 * 
 * M.Sc. Ing. Loic Royer
 * 
 * Fraunhofer IWU Dresden
 *   
 */

package org.royerloic.optimal.script;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

/**
 * @author MSc. Ing. Loic Royer
 * 
 */
public final class VectorFile
{

	/**
	 * Hiding default constructor.
	 */
	private VectorFile()
	{
		super();
		// TODO Auto-generated constructor stub
	}

	public static double[] getVector(final String pFileName)
	{
		Vector lVector = new Vector();

		try
		{
			File lInputFile = new File(pFileName);

			FileReader lFileReader;
			BufferedReader lBufferedReader;
			try
			{
				lFileReader = new FileReader(lInputFile);
				lBufferedReader = new BufferedReader(lFileReader);
			}
			catch (FileNotFoundException e)
			{
				System.out.println("File: " + lInputFile + " not found.");
				throw e;
			}

			String lLineString;
			try
			{
				while ((lLineString = lBufferedReader.readLine()) != null)
				{
					lVector.add(Double.valueOf(lLineString));
				}
			}
			catch (IOException e2)
			{
				System.out.println("Error while reading: " + e2.getCause());
			}
			finally
			{
				lFileReader.close();
			}
		}
		catch (Exception any)
		{
			any.printStackTrace(System.out);

		}

		double[] lResult = new double[lVector.size()];
		for (int i = 0; i < lVector.size(); i++)
		{
			lResult[i] = ((Double) lVector.elementAt(i)).doubleValue();
		}
		return lResult;
	}

}
