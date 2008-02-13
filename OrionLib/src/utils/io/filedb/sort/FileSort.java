package utils.io.filedb.sort;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.regex.Pattern;

import utils.io.LineReader;
import utils.io.filedb.FileDB;

public class FileSort
{
	public static Pattern sTabDelPattern = Pattern.compile("\\t");

	private static final class KeyedLine<O extends Comparable<O>>
	{
		String mLine;
		O mKey;

		public KeyedLine(String pLine, O pKey)
		{
			super();
			mLine = pLine;
			mKey = pKey;
		}

		@Override
		public String toString()
		{
			return "[" + mKey + "]'" + mLine + "'";
		}

		public String toLineString()
		{
			return mLine;
		}

	}

	private static class AscendingComparator<O extends Comparable<O>> implements
																																		Comparator<KeyedLine<O>>
	{

		@Override
		public int compare(KeyedLine<O> pO1, KeyedLine<O> pO2)
		{
			return pO1.mKey.compareTo(pO2.mKey);
		}

	}

	private static class DescendingComparator<O extends Comparable<O>>	implements
																																			Comparator<KeyedLine<O>>
	{
		@Override
		public int compare(KeyedLine<O> pO1, KeyedLine<O> pO2)
		{
			return -pO1.mKey.compareTo(pO2.mKey);
		}
	}

	public static final void sort(File pInputFile,
																String pColumn,
																boolean pAscending,
																File pOutputFile) throws IOException
	{
		final int lColumnIndex = FileDB.resolveColumn(pInputFile, pColumn);
		// check 100 lines, might be dangerous but too slow otherwise
		Class lColumnType = FileDB.getColumnType(pInputFile, lColumnIndex, 1000);
		sort(	new FileInputStream(pInputFile),
					lColumnIndex,
					lColumnType.getSimpleName(),
					pAscending,
					new FileOutputStream(pInputFile));
	}

	public static final void sort(InputStream pInputStream,
																int pColumnIndex,
																String pColumnType,
																boolean pAscending,
																OutputStream pOutputStream) throws IOException
	{
		ArrayList<KeyedLine<? extends Comparable<?>>> lList = new ArrayList<KeyedLine<? extends Comparable<?>>>();

		for (String lLine : LineReader.getLines(pInputStream))
			if (!lLine.isEmpty() && !lLine.startsWith("//"))
			{
				final String lValue = sTabDelPattern.split(lLine, -1)[pColumnIndex];
				KeyedLine<? extends Comparable<?>> lKeyedLine = null;
				if (pColumnType.equals(Double.class.getSimpleName()))
				{
					final double lDoubleValue = Double.parseDouble(lValue);
					lKeyedLine = new KeyedLine<Double>(lLine, lDoubleValue);
				}
				else
				{
					lKeyedLine = new KeyedLine<String>(lLine, lValue);
				}
				lList.add(lKeyedLine);
			}

		if (pAscending)
		{
			Collections.sort(lList, new AscendingComparator());
		}
		else
		{
			Collections.sort(lList, new DescendingComparator());
		}

		BufferedWriter lBufferedWriter = new BufferedWriter(new OutputStreamWriter(pOutputStream));
		for (KeyedLine<? extends Comparable<?>> lKeyedLine : lList)
		{
			lBufferedWriter.append(lKeyedLine.toLineString() + "\n");
		}
		lBufferedWriter.close();

	}

}