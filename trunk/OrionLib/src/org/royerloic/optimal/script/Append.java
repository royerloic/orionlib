/*
 * Optimal Project
 * 
 * M.Sc. Ing. Loic Royer
 * 
 * Fraunhofer IWU Dresden
 *  
 */

package org.royerloic.optimal.script;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import bsh.CallStack;
import bsh.Interpreter;

/**
 * @author MSc. Ing. Loic Royer
 * 
 */
public final class Append
{

	/**
	 * Hiding default constructor.
	 */
	private Append()
	{
		super();
		// TODO Auto-generated constructor stub
	}

	public static void invoke(final Interpreter pInterpreter,
														final CallStack pCallStack,
														final String pAccumulatorFile,
														final String pAppendFile)
	{
		final File lInputFile = new File(pAppendFile);
		final File lOutputFile = new File(pAccumulatorFile);

		FileReader lFileReader;
		try
		{
			lFileReader = new FileReader(lInputFile);
		}
		catch (final FileNotFoundException e)
		{
			System.out.println("File: " + lInputFile + " not found.");
			return;
		}

		FileWriter lFileWriter;
		try
		{
			lFileWriter = new FileWriter(lOutputFile, true);
		}
		catch (final IOException e1)
		{
			System.out.println("File: " + lOutputFile + " not found.");
			return;
		}
		int lFileReaderResult;

		try
		{
			while ((lFileReaderResult = lFileReader.read()) != -1)
				lFileWriter.write(lFileReaderResult);
		}
		catch (final IOException e2)
		{
			System.out.println("Error while appending: " + e2.getCause());

		}
		finally
		{

			try
			{
				lFileReader.close();
			}
			catch (final IOException e3)
			{
				// TODO Auto-generated catch block
				e3.printStackTrace(System.out);
			}

			try
			{
				lFileWriter.close();
			}
			catch (final IOException e4)
			{
				// TODO Auto-generated catch block
				e4.printStackTrace(System.out);
			}

		}

	}

}