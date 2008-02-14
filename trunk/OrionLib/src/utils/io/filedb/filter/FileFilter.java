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

	/** ******************************************************************************************* */
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

	public static final void filterOut(	File pInputFile,
																			Integer pColumnIndex,
																			String pValue,
																			File pOutputFile) throws IOException
	{
		filterOut(new FileInputStream(pInputFile),
							pColumnIndex,
							pValue,
							new FileOutputStream(pOutputFile));
	}

	public static final void filterOut(	InputStream pInputStream,
																			int pColumn,
																			String pValue,
																			OutputStream pOutputStream) throws IOException
	{
		BufferedWriter lBufferedWriter = new BufferedWriter(new OutputStreamWriter(pOutputStream));
		for (String lLine : LineReader.getLines(pInputStream))
			if (lLine.startsWith("//"))
			{
				lBufferedWriter.write(lLine);
				lBufferedWriter.write("\n");
			}
			else if (!lLine.isEmpty())
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

	/** ******************************************************************************************* */
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

	public static final void filterIn(File pInputFile,
																		Integer pColumnIndex,
																		String pValue,
																		File pOutputFile) throws IOException
	{
		filterIn(	new FileInputStream(pInputFile),
							pColumnIndex,
							pValue,
							new FileOutputStream(pOutputFile));
	}

	public static final void filterIn(InputStream pInputStream,
																		int pColumn,
																		String pValue,
																		OutputStream pOutputStream) throws IOException
	{
		BufferedWriter lBufferedWriter = new BufferedWriter(new OutputStreamWriter(pOutputStream));
		for (String lLine : LineReader.getLines(pInputStream))
			if (lLine.startsWith("//"))
			{
				lBufferedWriter.write(lLine);
				lBufferedWriter.write("\n");
			}
			else if (!lLine.isEmpty())
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

	/** ******************************************************************************************* */
	public static final void filterLowerHigher(	File pInputFile,
																							String pColumn,
																							String pValue,
																							Boolean pHigher,
																							File pOutputFile) throws IOException
	{
		final int lColumnIndex = FileDB.resolveColumn(pInputFile, pColumn);
		Class lColumnType = FileDB.getColumnType(pInputFile, lColumnIndex, 1000);
		filterLowerHigher(new FileInputStream(pInputFile),
											lColumnIndex,
											lColumnType.getSimpleName(),
											pValue,
											pHigher,
											new FileOutputStream(pOutputFile));
	}

	public static final void filterLowerHigher(	File pInputFile,
																							String pColumn,
																							Double pValue,
																							Boolean pHigher,
																							File pOutputFile) throws IOException
	{
		final int lColumnIndex = FileDB.resolveColumn(pInputFile, pColumn);
		Class lColumnType = FileDB.getColumnType(pInputFile, lColumnIndex, 1000);
		filterLowerHigher(new FileInputStream(pInputFile),
											lColumnIndex,
											lColumnType.getSimpleName(),
											pValue.toString(),
											pHigher,
											new FileOutputStream(pOutputFile));
	}

	public static final void filterLowerHigher(	File pInputFile,
																							Integer pColumnIndex,
																							String pValue,
																							Boolean pHigher,
																							File pOutputFile) throws IOException
	{
		Class lColumnType = FileDB.getColumnType(pInputFile, pColumnIndex, 1000);
		filterLowerHigher(new FileInputStream(pInputFile),
											pColumnIndex,
											lColumnType.getSimpleName(),
											pValue,
											pHigher,
											new FileOutputStream(pOutputFile));
	}

	public static final void filterLowerHigher(	File pInputFile,
																							Integer pColumnIndex,
																							Double pValue,
																							Boolean pHigher,
																							File pOutputFile) throws IOException
	{
		filterLowerHigher(new FileInputStream(pInputFile),
											pColumnIndex,
											Double.class.getSimpleName(),
											pValue.toString(),
											pHigher,
											new FileOutputStream(pOutputFile));
	}

	public static final void filterLowerHigher(	InputStream pInputStream,
																							int pColumn,
																							String pColumnType,
																							String pValue,
																							Boolean pHigher,
																							OutputStream pOutputStream) throws IOException
	{
		int higher = pHigher ? 1 : -1;

		Comparable lGivenComparableValue;
		if (pColumnType.equals(Double.class.getSimpleName()))
		{
			lGivenComparableValue = Double.parseDouble(pValue);
		}
		else
		{
			lGivenComparableValue = pValue;
		}

		BufferedWriter lBufferedWriter = new BufferedWriter(new OutputStreamWriter(pOutputStream));
		for (String lLine : LineReader.getLines(pInputStream))
			if (lLine.startsWith("//"))
			{
				lBufferedWriter.write(lLine);
				lBufferedWriter.write("\n");
			}
			else if (!lLine.isEmpty())
			{
				final String lValue = sTabDelPattern.split(lLine, -1)[pColumn];
				Comparable lComparableValue;
				if (pColumnType.equals(Double.class.getSimpleName()))
				{
					lComparableValue = Double.parseDouble(lValue);
				}
				else
				{
					lComparableValue = lValue;
				}

				if (lComparableValue.compareTo(lGivenComparableValue) * higher >= 0)
				{
					lBufferedWriter.write(lLine);
					lBufferedWriter.write("\n");
				}
			}
		lBufferedWriter.close();
	}

}
