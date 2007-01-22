/*
 * Created on 21.4.2005 by Dipl.-Inf. MSc. Ing Loic Royer
 */
package org.royerloic.memory;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Class to have the current application relaunched with enough memory.
 * <p>
 * Usage:
 * 
 * public static void main(String[] argv) { try { new
 * needMemory().needMemory(400, argv); // 400Mb limit, 200Mb initial.
 * .......................
 * 
 * @author J.F.Lanting
 * @since 07-Oct-2002
 */
public final class NeedMemory
{
	/**
	 * Environment variable to check for recursive re-launching
	 * (->'needMemory()').
	 */
	private final static String	launchToken	= "RELAUNCHEDFORHEAP";

	/**
	 * Checks if enough memory is available to the VM for this application and
	 * optionally re-launches it.
	 * <p>
	 * If not, the application is re-launched from this copy with:<BR> - the same
	 * commandline and<BR> - the same JAVA release.<BR>
	 * The operation fails or is blocked because:<BR>
	 * 1) this is already a re-launch (something must be wrong),<BR>
	 * 2) the amount of memory asked for is > 1500Mb which is out of reach.<BR>
	 * The first situation is controlled by a property: 'launchToken'.<BR>
	 * We relaunch the application with initially 50% of the heap limit allocated.
	 * </p>
	 * 
	 * @param needed #
	 *          of Mb to establish.
	 * @param commandLine
	 *          the command line as appearing in 'main()'.
	 * @throws Exception
	 *           for excess needs or recursive launches (a bug?).
	 */
	public void needMemory(int needed, String[] programCommandLine) throws Exception
	{
		long available; // What we've got.
		String javaPath; // The path to java or java.exe
		URL u; // The URL to this class from the classloader.
		String programPath = null; // The path to this .jar or .class if any.
		File directory; // Directory of the .jar or .class file.
		boolean isJarFile; // If this is run from a .jar file.
		String[] commandVector; // The new commandline for re-launch.
		String s;
		int i;

		if ((available = toMegaBytes(Runtime.getRuntime().maxMemory())) < needed)
		{
			if (needed > 1500)
			{
				throw new Exception("IMPOSSIBLE TO MEET REQUEST FOR " + needed + " Mb MEMORY.");
			}
			if (System.getProperty(launchToken) != null)
			{
				throw new Exception("FATAL RECURSION IN RE-LAUNCH");
			}

			// Where did I come from ?

			u = getClass().getResource("NeedMemory.class");
			if (u == null)
			{
				throw new Exception("CAN'T FIND MY OWN CLASS FILE.");
			}

			// Determine the path to a class-file or to a jar-file from the
			// classloader URL:

			s = u.getPath();
			if (s.startsWith("file:/")) // It's a jar-file.
			{
				isJarFile = true;
				if (File.separatorChar == '/') // UNIX|LINUX
				{
					s = s.substring(5); // Leaves a / alone.
				}
				else
				{
					s = s.substring(6); // Strips the / away.
				}

				if ((i = s.indexOf("!/")) > 0)
				{
					s = s.substring(0, i); // Deletes the internal jar path.
				}
				commandVector = new String[6 + programCommandLine.length];
			}
			else
			// In a class file.
			{
				isJarFile = false;
				if (s.startsWith("/"))
				{
					s = s.substring(1); // Waste the /.
				}
				if ((i = s.indexOf(".class")) > 0)
				{
					s = s.substring(0, i); // Drop the extension.
				}
				commandVector = new String[5 + programCommandLine.length];
			}

			if (s.indexOf("%20") >= 0) // Incase the URL contained them.
			{
				s = s.replaceAll("%20", " ");
			}
			programPath = s;

			// Where to find java[w] :

			javaPath = System.getProperty("java.home");
			if (File.separatorChar == '/') // UNIX|LINUX
			{
				javaPath += "/bin/java";
			}
			else
			{
				javaPath += "\\bin\\javaw.exe";
			}

			// Prepare new commandline :

			commandVector[0] = javaPath;

			needed = (int) (needed * 1.01 + 1); // Allow 1% more plus one.

			commandVector[1] = "-Xms" + (needed / 2) + "m"; // Initial heap.
			commandVector[2] = "-Xmx" + needed + "m"; // Maximum heap.
			commandVector[3] = "-D" + "RELAUNCHEDFORHEAP" + "=1";

			directory = new File(programPath);
			if (isJarFile)
			{
				commandVector[4] = "-jar";
				commandVector[5] = directory.getName();
				i = 6;
			}
			else
			{
				commandVector[4] = directory.getName();
				i = 5;
			}
			directory = directory.getParentFile();

			for (int j = 0; j < programCommandLine.length; ++j, ++i)
			{
				commandVector[i] = programCommandLine[j];
			}

			// Log the action :

			for (i = 0; i < commandVector.length; ++i)
			{
				if (i == 0)
				{
					s = commandVector[0];
				}
				else
				{
					s += " " + commandVector[i];
				}
			}
			System.out.println("+++ Re-launch, heap=" + available + "Mb, requested=" + needed
					+ "Mb, from directory " + directory.getAbsolutePath() + ", commandline=");
			System.out.println(s);

			// Re-launch :

			i = launch(commandVector, directory);
			if (i < 0)
			{
				System.out.println("*** RE-LAUNCH ATTEMPT FAILED.");
			}
			else
			{
				System.out.println("### End of re-launch.");
			}
			System.exit(i);
		}
	}

	/**
	 * Executes an application and waits for completion.
	 * <p>
	 * For execution <u>without</u> waiting see also <b>Sy.execute()</b>,<BR>
	 * for execution with tethers (pipes) see also <b>Sy.tether()</b>.<BR>
	 * <u>NOTE:</u> a failing execution just returns <b>-1</b>.
	 * <p>
	 * A sample call is:<BR>
	 * <b>result = launch(new String[] { "df", "-k", fileName}, "OUTPUT" );</b>
	 * </p>
	 * 
	 * @param commandVector
	 *          Command line as array.
	 * @param dir
	 *          directory in which the process runs.
	 * @return Exit status from the command.
	 */
	public int launch(String[] commandVector, File dir)
	{
		Process p;
		int exitValue = -1;

		if ((p = execute(commandVector, dir)) != null)
		{
			try
			{
				exitValue = p.waitFor();
			}
			catch (InterruptedException e)
			{
				exitValue = -1;
			}
		}
		return (exitValue);
	}

	/**
	 * Executes an application in a directory, without waiting for it and returns
	 * the Process object.
	 * 
	 * @param commandVector
	 *          command line as String array.
	 * @param dir
	 *          directory in which the process runs.
	 * @return Process object for the child process.
	 */
	public static Process execute(String[] commandVector, File dir)
	{
		Process p;

		try
		{
			p = Runtime.getRuntime().exec(commandVector, null, dir);
		}
		catch (IOException e)
		{
			p = null;
		}
		return (p);
	}

	/**
	 * Returns a number of bytes as Mb, rounded up.
	 * 
	 * @param m #
	 *          of bytes
	 * @return the # of Mb.
	 */
	public static long toMegaBytes(long m)
	{
		return ((m / 1024 + 512) / 1024);
	}
}
