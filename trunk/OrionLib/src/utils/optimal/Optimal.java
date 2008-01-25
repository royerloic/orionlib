/*
 * Optimal Project
 * 
 * M.Sc. Ing. Loic Royer
 * 
 * Fraunhofer IWU Dresden
 *  
 */

package utils.optimal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;

import bsh.ConsoleInterface;
import bsh.EvalError;
import bsh.Interpreter;
import bsh.NameSpace;

/**
 * @author MSc. Ing. Loic Royer
 * 
 */
public class Optimal extends Interpreter
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5334339857551841098L;

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @param arg3
	 * @param arg4
	 * @param arg5
	 * @param arg6
	 */
	public Optimal(	final Reader arg0,
									final PrintStream arg1,
									final PrintStream arg2,
									final boolean arg3,
									final NameSpace arg4,
									final Interpreter arg5,
									final String arg6)
	{
		super(arg0, arg1, arg2, arg3, arg4, arg5, arg6);
		initialize();
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @param arg3
	 * @param arg4
	 */
	public Optimal(	final Reader arg0,
									final PrintStream arg1,
									final PrintStream arg2,
									final boolean arg3,
									final NameSpace arg4)
	{
		super(arg0, arg1, arg2, arg3, arg4);
		initialize();
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @param arg3
	 */
	public Optimal(	final Reader arg0,
									final PrintStream arg1,
									final PrintStream arg2,
									final boolean arg3)
	{
		super(arg0, arg1, arg2, arg3);
		initialize();
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public Optimal(final ConsoleInterface arg0, final NameSpace arg1)
	{
		super(arg0, arg1);
		initialize();
	}

	/**
	 * @param arg0
	 */
	public Optimal(final ConsoleInterface arg0)
	{
		super(arg0);
		initialize();
	}

	/**
	 * @param arg0
	 */
	public Optimal(final int pServerPort)
	{
		super();
		initialize();
		try
		{
			eval("server(" + pServerPort + ")");
		}
		catch (final EvalError e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace(System.out);
		}
	}

	/**
	 * 
	 */
	public Optimal()
	{
		super();
		initialize();
	}

	/**
	 * 
	 */
	public void initialize()
	{

		try
		{
			// eval("importCommands(\"org.royerloic.optimal.script\")");
			eval("import org.royerloic.optimal.script.*");
			eval("import org.royerloic.optimal.stdimpl.*");
			eval("import org.royerloic.utils.math.*");
			eval("public double sq(double p){return p*p;}");
			eval("public double boost(double p){return (1-Math.sqrt(1-p*p));}");
		}
		catch (final EvalError e1)
		{
			e1.printStackTrace(System.out);
		}
	}

	public void startConsole()
	{
		// setConsole(new OptimalConsole());

		final BufferedReader lBufferedReader = new BufferedReader(new InputStreamReader(System.in));

		System.out.println(getBanner());

		String lString = null;
		do
		{
			try
			{
				System.out.print(getPrompt());
				lString = lBufferedReader.readLine();
			}
			catch (final IOException e2)
			{
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			try
			{
				eval(lString);
			}
			catch (final EvalError e1)
			{
				// TODO Auto-generated catch block
				System.err.println(e1.getMessage() + "\n");
			}
			// System.out.println(lString);
		}
		while (!(((lString != null) ? (lString.equals("exit")) : false)));

	}

	/**
	 * @return
	 */
	private String getBanner()
	{
		String lBanner = "";
		lBanner += "Optimal 2.0 \n";
		lBanner += "Dipl.-Inf Loic Royer, Fraunhoffer IWU (Dresden) \n";
		lBanner += "Console Interface \n";
		lBanner += "\n";
		return lBanner;
	}

	private String getPrompt()
	{
		final File lLocalDir = new File(".");
		return "[" + lLocalDir.getAbsolutePath() + "] Optimal>";
	}

	public static void main(final String[] pArguments)
	{
		final Optimal lOScriptInterpreter = new Optimal(1001);
		try
		{
			lOScriptInterpreter.set("tmpCmdLine", pArguments);
			lOScriptInterpreter.eval("String[] Arguments = (String[])tmpCmdLine");

			if (!(pArguments.length == 0))
				lOScriptInterpreter.source(pArguments[0]);
			else
				lOScriptInterpreter.startConsole();
		}
		catch (final EvalError e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (final FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (final IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}