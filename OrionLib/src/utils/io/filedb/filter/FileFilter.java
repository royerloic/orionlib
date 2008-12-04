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

	/**
	 * ***************************************************************************
	 * ****************
	 */
	public static final void filterOut(	final File pInputFile,
																			final String pColumn,
																			final String pValue,
																			final File pOutputFile) throws IOException
	{
		filterOut(new FileInputStream(pInputFile),
							FileDB.resolveColumn(pInputFile, pColumn),
							pValue,
							new FileOutputStream(pInputFile));
	}

	public static final void filterOut(	final File pInputFile,
																			final Integer pColumnIndex,
																			final String pValue,
																			final File pOutputFile) throws IOException
	{
		filterOut(new FileInputStream(pInputFile),
							pColumnIndex,
							pValue,
							new FileOutputStream(pOutputFile));
	}

	public static final void filterOut(	final InputStream pInputStream,
																			final int pColumn,
																			final String pValue,
																			final OutputStream pOutputStream) throws IOException
	{
		final BufferedWriter lBufferedWriter = new BufferedWriter(new OutputStreamWriter(pOutputStream));
		for (final String lLine : LineReader.getLines(pInputStream))
		{
			if (lLine.startsWith("//"))
			{
				lBufferedWriter.write(lLine);
				lBufferedWriter.write("\n");
			}
			else if (!(lLine.length() == 0))
			{
				final String lValue = sTabDelPattern.split(lLine, -1)[pColumn];
				if (!lValue.equals(pValue))
				{
					lBufferedWriter.write(lLine);
					lBufferedWriter.write("\n");
				}
			}
		}
		lBufferedWriter.close();
	}

	/**
	 * ***************************************************************************
	 * ****************
	 */
	public static final void filterIn(final File pInputFile,
																		final String pColumn,
																		final String pValue,
																		final File pOutputFile) throws IOException
	{
		filterIn(	new FileInputStream(pInputFile),
							FileDB.resolveColumn(pInputFile, pColumn),
							pValue,
							new FileOutputStream(pInputFile));
	}

	public static final void filterIn(final File pInputFile,
																		final Integer pColumnIndex,
																		final String pValue,
																		final File pOutputFile) throws IOException
	{
		filterIn(	new FileInputStream(pInputFile),
							pColumnIndex,
							pValue,
							new FileOutputStream(pOutputFile));
	}

	public static final void filterIn(final InputStream pInputStream,
																		final int pColumn,
																		final String pValue,
																		final OutputStream pOutputStream) throws IOException
	{
		final BufferedWriter lBufferedWriter = new BufferedWriter(new OutputStreamWriter(pOutputStream));
		for (final String lLine : LineReader.getLines(pInputStream))
		{
			if (lLine.startsWith("//"))
			{
				lBufferedWriter.write(lLine);
				lBufferedWriter.write("\n");
			}
			else if (!(lLine.length() == 0))
			{
				final String lValue = sTabDelPattern.split(lLine, -1)[pColumn];
				if (lValue.equals(pValue))
				{
					lBufferedWriter.write(lLine);
					lBufferedWriter.write("\n");
				}
			}
		}
		lBufferedWriter.close();
	}

	/**
	 * ***************************************************************************
	 * ****************
	 */
	public static final void filterLowerHigher(	final File pInputFile,
																							final String pColumn,
																							final String pValue,
																							final Boolean pHigher,
																							final File pOutputFile) throws IOException
	{
		final int lColumnIndex = FileDB.resolveColumn(pInputFile, pColumn);
		final Class lColumnType = FileDB.getColumnType(	pInputFile,
																										lColumnIndex,
																										1000);
		filterLowerHigher(new FileInputStream(pInputFile),
											lColumnIndex,
											lColumnType.getSimpleName(),
											pValue,
											pHigher,
											new FileOutputStream(pOutputFile));
	}

	public static final void filterLowerHigher(	final File pInputFile,
																							final String pColumn,
																							final Double pValue,
																							final Boolean pHigher,
																							final File pOutputFile) throws IOException
	{
		final int lColumnIndex = FileDB.resolveColumn(pInputFile, pColumn);
		final Class lColumnType = FileDB.getColumnType(	pInputFile,
																										lColumnIndex,
																										1000);
		filterLowerHigher(new FileInputStream(pInputFile),
											lColumnIndex,
											lColumnType.getSimpleName(),
											pValue.toString(),
											pHigher,
											new FileOutputStream(pOutputFile));
	}

	public static final void filterLowerHigher(	final File pInputFile,
																							final Integer pColumnIndex,
																							final String pValue,
																							final Boolean pHigher,
																							final File pOutputFile) throws IOException
	{
		final Class lColumnType = FileDB.getColumnType(	pInputFile,
																										pColumnIndex,
																										1000);
		filterLowerHigher(new FileInputStream(pInputFile),
											pColumnIndex,
											lColumnType.getSimpleName(),
											pValue,
											pHigher,
											new FileOutputStream(pOutputFile));
	}

	public static final void filterLowerHigher(	final File pInputFile,
																							final Integer pColumnIndex,
																							final Double pValue,
																							final Boolean pHigher,
																							final File pOutputFile) throws IOException
	{
		filterLowerHigher(new FileInputStream(pInputFile),
											pColumnIndex,
											Double.class.getSimpleName(),
											pValue.toString(),
											pHigher,
											new FileOutputStream(pOutputFile));
	}

	public static final void filterLowerHigher(	final InputStream pInputStream,
																							final int pColumn,
																							final String pColumnType,
																							final String pValue,
																							final Boolean pHigher,
																							final OutputStream pOutputStream) throws IOException
	{
		final int higher = pHigher ? 1 : -1;

		Comparable lGivenComparableValue;
		if (pColumnType.equals(Double.class.getSimpleName()))
		{
			lGivenComparableValue = Double.parseDouble(pValue);
		}
		else
		{
			lGivenComparableValue = pValue;
		}

		final BufferedWriter lBufferedWriter = new BufferedWriter(new OutputStreamWriter(pOutputStream));
		for (final String lLine : LineReader.getLines(pInputStream))
		{
			if (lLine.startsWith("//"))
			{
				lBufferedWriter.write(lLine);
				lBufferedWriter.write("\n");
			}
			else if (!(lLine.length() == 0))
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
		}
		lBufferedWriter.close();
	}

}
