/*
 * Created on 09.11.2004 by Dipl.-Inf. MSc. Ing Loic Royer
 */
package org.royerloic.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author Dipl.-Inf. MSc. Ing. Loic Royer
 * 
 */
public class FileToString
{
	public static String read(File pFile) throws IOException
	{
		String lineSep = System.getProperty("line.separator");
		BufferedReader br = new BufferedReader(new FileReader(pFile));
		String nextLine = "";
		StringBuffer sb = new StringBuffer();
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
}