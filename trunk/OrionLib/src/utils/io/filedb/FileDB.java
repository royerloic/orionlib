package utils.io.filedb;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import utils.io.LineReader;

public class FileDB
{
	public static Pattern sTabDelPattern = Pattern.compile("\\t");
	public static Pattern sIntPattern = Pattern.compile("[0-9]+");
	public static Pattern sFloatPattern = Pattern.compile("[-+]?([0-9]*\\.)?[0-9]+([eE][-+]?[0-9]+)?");

	public static List<String> getColumnNames(File pFile) throws IOException
	{
		return getColumnNames(new FileInputStream(pFile));
	}

	public static List<String> getColumnNames(InputStream pInputStream) throws IOException
	{
		InputStreamReader lInputStreamReader = new InputStreamReader(pInputStream);
		BufferedReader lBufferedReader = new BufferedReader(lInputStreamReader);
		String lHeaderString = lBufferedReader.readLine();
		if (lHeaderString.startsWith("//"))
		{
			lHeaderString = lHeaderString.replace("//", "");
			String[] lStringArray = sTabDelPattern.split(lHeaderString, -1);
			lBufferedReader.close();
			lInputStreamReader.close();
			return Arrays.<String> asList(lStringArray);
		}
		return null;
	}

	public static int resolveColumn(File pFile, String pColumn) throws IOException
	{
		boolean isInteger = sIntPattern.matcher(pColumn).matches();
		if (isInteger)
		{
			return Integer.parseInt(pColumn);
		}
		else
		{
			List<String> lColumnNameList = getColumnNames(pFile);
			if (lColumnNameList != null)
				for (int i = 0; i < lColumnNameList.size(); i++)
					if (lColumnNameList.get(i).equalsIgnoreCase(pColumn.trim()))
					{
						return i;
					}
		}
		throw new IllegalArgumentException("Column name/index not recognized:" + pColumn
																				+ " in "
																				+ pFile.getName());
	}

	public static Class getColumnType(File pFile,
																		int pColumnIndex,
																		int pMaxLinesChecked) throws IOException
	{

		int counter = 0;
		for (String lLine : LineReader.getLines(pFile))
			if (!lLine.isEmpty())
				if (!lLine.startsWith("//"))
				{
					final String lValue = sTabDelPattern.split(lLine, -1)[pColumnIndex];
					boolean isFloatOrInt = sFloatPattern.matcher(lValue).matches();
					if (!isFloatOrInt)
					{
						return String.class;
					}
					else
					{
						counter++;
					}
					if (counter > pMaxLinesChecked)
					{
						return Double.class;
					}
				}

		return Double.class;
	}

}
