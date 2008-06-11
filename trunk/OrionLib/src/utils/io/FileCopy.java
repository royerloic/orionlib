package utils.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileCopy
{
	public static void copy(final File pSource, final File pDest) throws IOException
	{
		final BufferedReader in = new BufferedReader(new FileReader(pSource));
		final BufferedWriter out = new BufferedWriter(new FileWriter(pSource));
		int c;
		while ((c = in.read()) != -1)
		{
			out.write(c);
		}
		in.close();
		out.close();
	}
}
