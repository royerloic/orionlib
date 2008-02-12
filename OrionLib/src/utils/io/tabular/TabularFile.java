package utils.io.tabular;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import utils.io.MatrixFile;
import utils.structures.BijectiveBidiHashMap;
import utils.structures.Matrix;

public class TabularFile
{
	static final Pattern cDoublePattern = Pattern.compile("[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?");
	static final Pattern cIntegerPattern = Pattern.compile("[-+]?[0-9]+");

	private final InputStream mInputStream;

	@SuppressWarnings("unchecked")
	Map<String, Column> mNameToColumnMap = new LinkedHashMap<String, Column>();

	@SuppressWarnings("unchecked")
	BijectiveBidiHashMap<Integer, String> mIndexToNameMap = new BijectiveBidiHashMap<Integer, String>();

	private final boolean mHasHeader;
	private final String mName;

	public TabularFile(File pFile, boolean pHasHeader) throws IOException
	{
		this(pFile.getName(), new FileInputStream(pFile), pHasHeader);
	}

	public TabularFile(String pName, InputStream pInputStream, boolean pHasHeader) throws FileNotFoundException,
																																								IOException
	{
		super();
		mName = pName;
		mInputStream = pInputStream;
		mHasHeader = pHasHeader;

	}

	public final void read() throws FileNotFoundException, IOException
	{
		Matrix<String> lMatrix = MatrixFile.readMatrixFromStream(mInputStream);

		int lMaxColumns = 0;
		int lMinColumns = Integer.MAX_VALUE;
		for (int line = 0; line < lMatrix.size(); line++)
		{

			int lNumberOfColumns = lMatrix.get(line).size();
			if (lNumberOfColumns != 0)
			{
				lMaxColumns = Math.max(lMaxColumns, lNumberOfColumns);
				lMinColumns = Math.min(lMinColumns, lNumberOfColumns);
			}
		}

		for (int line = 0; line < lMatrix.size(); line++)
		{
			int lNumberOfColumns = lMatrix.get(line).size();
			if (lNumberOfColumns < lMaxColumns)
			{
				// we get rid of lines that don't have enough columns...
				lMatrix.remove(line);
				line--;
			}
			else
			{
				boolean isAllEmpty = true;
				for (int index = 0; index < lMatrix.get(line).size(); index++)
				{
					isAllEmpty &= lMatrix.get(line).get(index).isEmpty();
				}
				if (isAllEmpty)
				{
					lMatrix.remove(line);
					line--;
				}
			}
		}

		if (mHasHeader)
		{
			List<String> lHeaderList = lMatrix.get(0);
			for (int index = 0; index < lHeaderList.size(); index++)
			{
				mIndexToNameMap.put(index, lHeaderList.get(index));
			}
		}
		else
		{
			for (int index = 0; index < lMaxColumns; index++)
			{
				mIndexToNameMap.put(index, mName + "[col=" + index + "]");
			}
		}

		for (int index = 0; index < lMaxColumns; index++)
		{
			Column<?> lColumn = parseColumn(lMatrix, index);
			final String lColumnName = mIndexToNameMap.get(index);
			mNameToColumnMap.put(lColumnName, lColumn);
		}
	}

	private Column<?> parseColumn(final Matrix<String> pMatrix, final int pColumn)
	{
		final int lStartLine = mHasHeader ? 1 : 0;

		boolean isDouble = true;
		boolean isInteger = true;

		for (int line = lStartLine; line < pMatrix.size(); line++)
		{
			final String lItem = pMatrix.get(line).get(pColumn);
			if (isDouble)
				isDouble &= cDoublePattern.matcher(lItem).matches();
			if (isInteger)
				isInteger &= cIntegerPattern.matcher(lItem).matches();

			if (!isDouble && !isInteger)
				break;
		}

		Column lColumn = null;
		if (isDouble && !isInteger)
		{
			lColumn = new Column<Double>(Double.class);
		}
		else if (isInteger)
		{
			lColumn = new Column<Integer>(Integer.class);
		}
		else
		{
			lColumn = new Column<String>(String.class);
		}

		for (int line = lStartLine; line < pMatrix.size(); line++)
		{
			final String lItem = pMatrix.get(line).get(pColumn);
			if (isDouble && !isInteger)
			{
				final Double lDouble = Double.parseDouble(lItem);
				lColumn.getList().add(lDouble);
			}
			else if (isInteger)
			{
				final Integer lInteger = Integer.parseInt(lItem);
				lColumn.getList().add(lInteger);
			}
			else
			{
				lColumn.getList().add(lItem);
			}
		}

		lColumn.normalise();

		return lColumn;
	}

	public Column getColumnByIndex(final int pIndex)
	{
		final String lName = mIndexToNameMap.get(pIndex);
		if (lName != null)
		{
			return mNameToColumnMap.get(lName);
		}
		else
			return null;
	}

	public Column getColumnByName(final String pName)
	{
		return mNameToColumnMap.get(pName);
	}

	@Override
	public String toString()
	{
		StringBuilder lStringBuilder = new StringBuilder();
		lStringBuilder.append("Number of columns" + mNameToColumnMap.size() + "\n");
		for (Map.Entry<String, Column> lEntry : mNameToColumnMap.entrySet())
		{
			final String lName = lEntry.getKey();
			final Column lColumn = lEntry.getValue();

			lStringBuilder.append("name      : '" + lName + "'\n");
			lStringBuilder.append("contents  : " + lColumn.getList() + "\n");
			lStringBuilder.append("normalized: " + lColumn.getNormalisedList() + "\n");
			lStringBuilder.append("\n");
		}

		return lStringBuilder.toString();
	}

	public int getNumberOfColumns()
	{
		return mNameToColumnMap.size();
	}

	public String getColumnNameForIndex(int pColumnIndex)
	{
		return mIndexToNameMap.get(pColumnIndex);
	}

}
