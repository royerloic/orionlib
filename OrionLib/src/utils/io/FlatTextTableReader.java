package utils.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author loic (<href>royer@biotec.tu-dresden.de</href>) Nov 4, 2005 Reads a
 *         Flat Text File having a table structure (lines and arbitrarily
 *         separated columns) and stores it into a generic structure
 *         implementing: List<List<Set<String>>>.
 */
public class FlatTextTableReader
{
	private String mColumnSplitRegex = "\t";

	private String mSetSplitRegex = "[|;]";

	private String mNullRegex = "[-]";

	private final Set<Integer> mExcludedColumns;

	private FlatTextTableReaderHandler mHandler;

	/**
	 * @author loic (<href>royer@biotec.tu-dresden.de</href>) Nov 8, 2005
	 *         Classes implementing this handler manage the content of a cell.
	 */
	public interface FlatTextTableReaderHandler
	{
		/**
		 * @param pLineCounter
		 *          Line position of cell
		 * @param pColumnCounter
		 *          Column posotion of cell
		 * @param pSetCounter
		 *          Set position of cell
		 * @param pCellString
		 *          Cell string
		 * @return TODO
		 */
		boolean handleCell(	int pLineCounter,
												int pColumnCounter,
												int pSetCounter,
												String pCellString);

		boolean handleEndOfCell(int pLineCounter);
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		final String lFileNameString = "Handler name= " + mHandler;
		return "(" + lFileNameString + ")";
	}

	/**
	 */
	private FlatTextTableReader()
	{
		super();
		mExcludedColumns = new HashSet<Integer>();
	}

	public FlatTextTableReader(final FlatTextTableReaderHandler pHandler)
	{
		this();
		mHandler = pHandler;
	}

	/**
	 * Adds indices of columns to be ignored.
	 * 
	 * @param pIntegerArray
	 * @param pMaximumNumberOfColumns
	 */
	public void setIncludedColumns(	final int[] pIntegerArray,
																	int pMaximumNumberOfColumns)
	{
		pMaximumNumberOfColumns = Math.max(	pMaximumNumberOfColumns,
																				pIntegerArray.length);
		for (int i = 1; i < pMaximumNumberOfColumns; i++)
		{
			mExcludedColumns.add(i);
		}
		for (final int lI : pIntegerArray)
		{
			mExcludedColumns.remove(lI);
		}
	}

	/**
	 * Reads the <code>File</code> provided in the constructor and feels a
	 * structure: List<List<Set<String>>>.
	 * 
	 * @param pFile
	 *          File to be read,
	 * @param pFileHasHeader
	 *          true if first line should be omitted.
	 * 
	 * 
	 * @throws IOException
	 *           if problem while reading file.
	 * 
	 */
	public void readFile(final File pFile, final boolean pFileHasHeader) throws IOException
	{

		BufferedReader lBufferedReader = null;
		try
		{
			// We choose the buffer to be 10% of the file, therefore, a File will be
			// block read in about 100 steps.
			final int lBufferSize = Math.min(10000000, (int) (pFile.length() / 10));
			final FileReader lFileReader = new FileReader(pFile);
			lBufferedReader = new BufferedReader(lFileReader, lBufferSize);
			readStream(lBufferedReader, pFileHasHeader);

		}
		catch (final IOException exception)
		{
			exception.printStackTrace();
			throw exception;
		}
		finally
		{
			if (lBufferedReader != null)
			{
				try
				{
					lBufferedReader.close();
				}
				catch (final IOException exception)
				{
					exception.printStackTrace();
				}
			}
		}
	}

	/**
	 * @param pBufferedReader
	 * @param pFileHasHeader
	 * @throws IOException
	 */
	public void readStream(	final BufferedReader pBufferedReader,
													final boolean pFileHasHeader) throws IOException
	{
		final Pattern lColumnSplitPattern = Pattern.compile(mColumnSplitRegex);
		final Pattern lSetSplitPattern = Pattern.compile(mSetSplitRegex);
		final Pattern lNullPattern = Pattern.compile(mNullRegex);

		String lLineString;
		if (pFileHasHeader)
		{
			lLineString = pBufferedReader.readLine();
		}
		int lLineCounter = 0;
		lineloop: while ((lLineString = pBufferedReader.readLine()) != null)
		{
			lLineCounter++;
			final String[] lColumnsStringArray = lColumnSplitPattern.split(	lLineString,
																																			-1);
			int lColumnCounter = 0;
			for (final String lColumnsElementString : lColumnsStringArray)
			{
				if (!mExcludedColumns.contains(lColumnCounter))
				{
					final String[] lSetStringArray = lSetSplitPattern.split(lColumnsElementString,
																																	-1);
					int lSetCounter = 0;
					for (final String element : lSetStringArray)
					{
						final Matcher lMatcher = lNullPattern.matcher(element);
						if (!lMatcher.matches())
						{
							final boolean lSkipCell = !mHandler.handleCell(	lLineCounter,
																															lColumnCounter,
																															lSetCounter,
																															element);
							if (lSkipCell)
							{
								continue lineloop;
							}
							lSetCounter++;
						}
					}
				}
				lColumnCounter++;
			}

			if (!mHandler.handleEndOfCell(lLineCounter))
			{
				return;
			}
		}
	}

	public void readRessource(final Class pClass,
														final String pRessourceName,
														final boolean pFileHasHeader) throws IOException
	{
		final InputStream lStream = pClass.getClassLoader()
																			.getResourceAsStream(pRessourceName);
		final InputStreamReader lInputStreamReader = new InputStreamReader(lStream);
		final BufferedReader lBufferedReader = new BufferedReader(lInputStreamReader);
		readStream(lBufferedReader, pFileHasHeader);
	}

	/**
	 * @return the regex string representing the separating between columns.
	 */
	public String getColumnSplitRegex()
	{
		return mColumnSplitRegex;
	}

	/**
	 * Sets the regex string representing the separation between columns.
	 * 
	 * @param pColumnSplitRegex
	 *          regular expression.
	 */
	public void setColumnSplitRegex(final String pColumnSplitRegex)
	{
		mColumnSplitRegex = pColumnSplitRegex;
	}

	/**
	 * @return the regex string representing the null element.
	 */
	public String getNullRegex()
	{
		return mNullRegex;
	}

	/**
	 * Sets the regex string representing the separation between elements of a
	 * set.
	 * 
	 * @param pNullRegex
	 *          null element regular expression.
	 */
	public void setNullRegex(final String pNullRegex)
	{
		mNullRegex = pNullRegex;
	}

	/**
	 * @return the regex string representing the separating between set elements.
	 */
	public String getSetSplitRegex()
	{
		return mSetSplitRegex;
	}

	/**
	 * Sets the regex string representing the separation between elements of a
	 * set.
	 * 
	 * @param pSetSplitRegex
	 *          regular expression.
	 */
	public void setSetSplitRegex(final String pSetSplitRegex)
	{
		mSetSplitRegex = pSetSplitRegex;
	}
}
