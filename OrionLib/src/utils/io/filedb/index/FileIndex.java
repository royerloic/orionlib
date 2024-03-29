package utils.io.filedb.index;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.regex.Pattern;

public class FileIndex implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final File mFile;
	private final String mColumnSeparatorRegex;
	private final Pattern mColumnSeparatorPattern;
	private final HashMap<String, Long> mIndexMap = new HashMap<String, Long>();

	private RandomAccessFile mRandomAccessFile;

	private final HashMap<String, SoftReference<String[]>> mCacheMap = new HashMap<String, SoftReference<String[]>>();

	public FileIndex(final File pFile, final String pColumnSeparatorRegex)
	{
		super();
		mFile = pFile;
		mColumnSeparatorRegex = pColumnSeparatorRegex;
		mColumnSeparatorPattern = Pattern.compile(mColumnSeparatorRegex);
	}

	public void buildIndex(final Integer pColumnIndex) throws IOException
	{
		final String lineSep = System.getProperty("line.separator");
		final FileReader lFileReader = new FileReader(mFile);
		final BufferedReader lBufferedReader = new BufferedReader(lFileReader);
		String nextLine = "";
		long lPosition = 0;
		while ((nextLine = lBufferedReader.readLine()) != null)
		{
			final String[] lColumnArray = mColumnSeparatorPattern.split(nextLine, -1);
			if (lColumnArray.length > pColumnIndex)
			{
				final String lIndexColumnValue = lColumnArray[pColumnIndex];
				mIndexMap.put(lIndexColumnValue, lPosition);
				System.out.println(lIndexColumnValue);
			}
			lPosition += nextLine.length() + lineSep.length();
		}
		if (mFile.length() != lPosition)
		{
			throw new RuntimeException("Line Separator of wrong size -> index corrupt");
		}
		lBufferedReader.close();
	}

	public final void open() throws FileNotFoundException
	{
		mRandomAccessFile = new RandomAccessFile(mFile, "r");
	}

	public final void close() throws IOException
	{
		mRandomAccessFile.close();
	}

	public final String[] getLine(final String pValue) throws IOException
	{
		final long lLinePosition = mIndexMap.get(pValue);
		mRandomAccessFile.seek(lLinePosition);
		final String lLine = mRandomAccessFile.readLine();
		final String[] lColumnArray = mColumnSeparatorPattern.split(lLine, -1);
		return lColumnArray;
	}

	public final String[] getLineAndCache(final String pValue) throws IOException
	{
		SoftReference<String[]> lSoftRef = mCacheMap.get(pValue);
		if (lSoftRef == null || lSoftRef.get() == null)
		{
			final String[] lColumnArray = getLine(pValue);
			lSoftRef = new SoftReference<String[]>(lColumnArray);
		}
		return lSoftRef.get();
	}

}
