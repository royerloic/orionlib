package utils.io.filedb.filter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.regex.Pattern;

import utils.io.LineReader;
import utils.io.filedb.FileDB;

public class FileFilter
{
	public static Pattern sTabDelPattern = Pattern.compile("\\t");

	public static final void filterOut(	File pInputFile,
																			String pColumn,
																			String pValue,
																			File pOutputFile) throws IOException
	{
		filterOut(new FileInputStream(pInputFile),
							FileDB.resolveColumn(pInputFile, pColumn),
							pValue,
							new FileOutputStream(pInputFile));
	}

	public static final void filterOut(	InputStream pInputStream,
																			int pColumn,
																			String pValue,
																			OutputStream pOutputStream) throws IOException
	{
		BufferedWriter lBufferedWriter = new BufferedWriter(new OutputStreamWriter(pOutputStream));
		for (String lLine : LineReader.getLines(pInputStream))
			if (!lLine.isEmpty())
			{
				final String lValue = sTabDelPattern.split(lLine, -1)[pColumn];
				if (!lValue.equals(pValue))
				{
					lBufferedWriter.write(lLine);
					lBufferedWriter.write("\n");
				}
			}
		lBufferedWriter.close();
	}

	public static final void filterIn(File pInputFile,
																		String pColumn,
																		String pValue,
																		File pOutputFile) throws IOException
	{
		filterIn(	new FileInputStream(pInputFile),
							FileDB.resolveColumn(pInputFile, pColumn),
							pValue,
							new FileOutputStream(pInputFile));
	}

	public static final void filterIn(InputStream pInputStream,
																		int pColumn,
																		String pValue,
																		OutputStream pOutputStream) throws IOException
	{
		BufferedWriter lBufferedWriter = new BufferedWriter(new OutputStreamWriter(pOutputStream));
		for (String lLine : LineReader.getLines(pInputStream))
			if (!lLine.isEmpty())
			{
				final String lValue = sTabDelPattern.split(lLine, -1)[pColumn];
				if (lValue.equals(pValue))
				{
					lBufferedWriter.write(lLine);
					lBufferedWriter.write("\n");
				}
			}
		lBufferedWriter.close();
	}

}
