/*
 * Created on 09.11.2004 by Dipl.-Inf. MSc. Ing Loic Royer
 */
package utils.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author Dipl.-Inf. MSc. Ing. Loic Royer
 * 
 */
public class FileToString
{
	public static String read(final File pFile) throws IOException
	{
		final String lineSep = System.getProperty("line.separator");
		final BufferedReader br = new BufferedReader(new FileReader(pFile));
		String nextLine = "";
		final StringBuffer sb = new StringBuffer();
		while ((nextLine = br.readLine()) != null)
		{
			sb.append(nextLine);
			//
			// note:
			// BufferedReader strips the EOL character.
			//
			sb.append(lineSep);
		}
		return sb.toString();
	}

	public static void write(final String pString, final File pFile) throws IOException
	{
		FileWriter out = new FileWriter(pFile);
		out.write(pString);
		out.close();
	}
	
	
}