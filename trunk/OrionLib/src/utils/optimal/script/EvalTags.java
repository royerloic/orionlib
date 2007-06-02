/*
 * Optimal Project
 * 
 * M.Sc. Ing. Loic Royer
 * 
 * Fraunhofer IWU Dresden
 *  
 */

package utils.optimal.script;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bsh.CallStack;
import bsh.EvalError;
import bsh.Interpreter;

/**
 * @author MSc. Ing. Loic Royer
 * 
 */
public final class EvalTags
{
	/**
	 * Hiding default constructor.
	 */
	private EvalTags()
	{
		super();
		// TODO Auto-generated constructor stub
	}

	private static final Pattern	sPattern	= Pattern.compile("##([^#]+)##");

	private static String evalTagsInLine(	final Interpreter pInterpreter,
																				final Pattern pPattern,
																				final String pLine)
	{
		final Matcher lMatcher = pPattern.matcher(pLine);
		final StringBuffer lStringBuffer = new StringBuffer();

		while (lMatcher.find())
		{
			final String lExpression = lMatcher.group(1);

			Object lObject = null;
			String lEvaluatedExpresssion = new String("");
			try
			{
				pInterpreter.set("tagValue", lEvaluatedExpresssion);
				lObject = pInterpreter.eval(lExpression);
				lEvaluatedExpresssion = (String) pInterpreter.get("tagValue");
			}
			catch (final EvalError e)
			{
				lEvaluatedExpresssion += "<Evaluation Error: " + e.getErrorText() + ">";
			}

			if (lObject == null)
				lEvaluatedExpresssion += "";
			else
				lEvaluatedExpresssion += lObject.toString();

			lMatcher.appendReplacement(lStringBuffer, lEvaluatedExpresssion);
		}

		lMatcher.appendTail(lStringBuffer);

		return lStringBuffer.toString();
	}

	public static void invoke(final Interpreter pInterpreter,
														final CallStack pCallStack,
														final String pMasterFile,
														final String pResultFile)
	{
		try
		{

			final File lInputFile = new File(pMasterFile);
			final File lOutputFile = new File(pResultFile);

			try
			{
				lOutputFile.createNewFile();
			}
			catch (final IOException e4)
			{
				// TODO Auto-generated catch block
				e4.printStackTrace(System.out);
			}

			FileReader lFileReader;
			BufferedReader lBufferedReader;
			try
			{
				lFileReader = new FileReader(lInputFile);
				lBufferedReader = new BufferedReader(lFileReader);
			}
			catch (final FileNotFoundException e)
			{
				System.out.println("File: " + lInputFile + " not found.");
				return;
			}

			FileWriter lFileWriter;
			BufferedWriter lBufferedWriter;
			try
			{
				lFileWriter = new FileWriter(lOutputFile);
				lBufferedWriter = new BufferedWriter(lFileWriter);
			}
			catch (final IOException e1)
			{
				System.out.println("Output File: " + lOutputFile + " not found.");
				return;
			}

			String lLineString;
			String lProcessedLineString;
			try
			{
				while ((lLineString = lBufferedReader.readLine()) != null)
				{
					lProcessedLineString = evalTagsInLine(pInterpreter, sPattern, lLineString);
					lBufferedWriter.write(lProcessedLineString);
					lBufferedWriter.newLine();
				}
				lBufferedWriter.flush();
			}
			catch (final IOException e2)
			{
				System.out.println("Error while writing: " + e2.getCause());

			}
			finally
			{
				lFileReader.close();
				lFileWriter.close();
			}
		}
		catch (final Exception any)
		{
			any.printStackTrace(System.out);
		}

	}

}