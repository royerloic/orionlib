package org.royerloic.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.royerloic.structures.ArrayMatrix;
import org.royerloic.structures.Matrix;

public class MatrixFile
{

	public static <O> void writeMatrixToFile(List<List<O>> pVectorList, File pFile)
			throws FileNotFoundException, IOException
	{
		writeMatrixToFile(pVectorList, pFile, "\t");
	}

	public static <O> void writeMatrixToFile(List<List<O>> pVectorList, File pFile, String lSeparator)
			throws FileNotFoundException, IOException
	{
		writeMatrixToStream(pVectorList, new FileOutputStream(pFile), lSeparator);
	}

	public static <O> void writeMatrixToStream(	List<List<O>> pVectorList,
																							OutputStream pOutputStream,
																							String pSeparator) throws FileNotFoundException, IOException
	{
		{
			if (pOutputStream == null)
			{
				throw new IllegalArgumentException("OutputStream should not be null.");
			}

			// declared here only to make visible to finally clause; generic reference
			Writer lWriter = null;
			try
			{
				lWriter = getWriterFromStream(pOutputStream);
				for (List<O> lList : pVectorList)
				{
					writeListToStream(lWriter, lList, pSeparator);
					lWriter.append("\r\n");
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				// flush and close both "output" and its underlying FileWriter
				if (lWriter != null)
					lWriter.close();
			}
		}
	}

	public static final Writer getWriterFromStream(OutputStream pOutputStream)
	{
		// use buffering
		// FileWriter always assumes default encoding is OK!
		final int lBufferSize = 10000000;
		Writer lWriter = new BufferedWriter(new OutputStreamWriter(pOutputStream), lBufferSize);
		return lWriter;
	}

	public static final Writer getWriterFromFile(File pFile) throws FileNotFoundException
	{
		return getWriterFromStream(new FileOutputStream(pFile));
	}

	public static final <O> void writeListToStream(Writer pWriter, List<O> pList, String pSeparator)
			throws IOException
	{
		int i = 0;
		for (O lEntry : pList)
		{
			boolean isLast = i == pList.size() - 1;
			if (!(lEntry instanceof Double && ((Double) lEntry).isNaN()))
			{
				pWriter.append(lEntry.toString());
			}

			if (!isLast)
				pWriter.append(pSeparator);
			i++;
		}
	}

	public static Matrix<String> readMatrixFromFile(File pFile) throws FileNotFoundException, IOException
	{
		return readMatrixFromFile(pFile, false, "\\t");
	}

	public static Matrix<String> readMatrixFromFile(File pFile, boolean pFileHasHeader)
			throws FileNotFoundException, IOException
	{
		return readMatrixFromFile(pFile, pFileHasHeader, "\\t");
	}

	public static Matrix<String> readMatrixFromFile(File pFile, String pSeparator)
			throws FileNotFoundException, IOException
	{
		return readMatrixFromStream(new FileInputStream(pFile), false, pSeparator);
	}

	public static Matrix<String> readMatrixFromFile(File pFile, boolean pFileHasHeader, String pSeparator)
			throws FileNotFoundException, IOException
	{
		return readMatrixFromStream(new FileInputStream(pFile), pFileHasHeader, pSeparator);
	}

	public static Matrix<String> readMatrixFromZipFile(File pFile, boolean pFileHasHeader, String pSeparator)
			throws FileNotFoundException, IOException
	{
		return readMatrixFromStream(new FileInputStream(pFile), pFileHasHeader, pSeparator);
	}

	public static Matrix<String> readMatrixFromStream(InputStream pInputStream) throws FileNotFoundException,
			IOException
	{
		return readMatrixFromStream(pInputStream, "\\t");
	}

	public static Matrix<String> readMatrixFromStream(InputStream pInputStream, String pSeparator)
			throws FileNotFoundException, IOException
	{
		return readMatrixFromStream(pInputStream, false, pSeparator);
	}

	public static Matrix<String> readMatrixFromStream(InputStream pInputStream, boolean pFileHasHeader)
			throws FileNotFoundException, IOException
	{
		return readMatrixFromStream(pInputStream, pFileHasHeader, "\\t");
	}

	public static Matrix<String> readMatrixFromStream(InputStream pInputStream,
																										boolean pFileHasHeader,
																										String pSeparator) throws FileNotFoundException,
			IOException
	{
		Matrix<String> lMatrix = new ArrayMatrix<String>();
		BufferedReader lBufferedReader = null;
		try
		{
			// We choose the buffer to be 10% of the file, therefore, a File will be
			// block read in about 100 steps.
			int lBufferSize = Math.min(10000000, (pInputStream.available() / 10));
			lBufferSize = lBufferSize == 0 ? 1000 : lBufferSize;
			lBufferedReader = new BufferedReader(new InputStreamReader(pInputStream), lBufferSize);
			Pattern lColumnSplitPattern = Pattern.compile(pSeparator);

			String lLineString;
			if (pFileHasHeader)
			{
				lLineString = lBufferedReader.readLine();
			}

			while ((lLineString = lBufferedReader.readLine()) != null)
			{
				String[] lColumnsStringArray = lColumnSplitPattern.split(lLineString, -1);
				List<String> lColumnsStringList = Arrays.asList(lColumnsStringArray);
				lMatrix.add(lColumnsStringList);
			}

			return lMatrix;
		}
		catch (IOException exception)
		{
			System.err.println(exception);
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
				catch (IOException exception)
				{
					System.err.println(exception);
				}
			}
		}
	}

	public static final BufferedReader getBufferedReaderFromStream(InputStream pInputStream)
	{
		final int lBufferSize = 10000000;
		BufferedReader lReader = new BufferedReader(new InputStreamReader(pInputStream), lBufferSize);
		return lReader;
	}

	public static final BufferedReader getBufferedReaderFromFile(File pFile) throws FileNotFoundException
	{
		return getBufferedReaderFromStream(new FileInputStream(pFile));
	}

	public static final <O> List<String> readListFromStream(BufferedReader pReader, String pSeparator)
			throws IOException
	{
		String lLineString = pReader.readLine();
		if (lLineString != null)
		{
			String[] lColumnsStringArray = lLineString.split(pSeparator, -1);
			List<String> lColumnsStringList = Arrays.asList(lColumnsStringArray);
			return lColumnsStringList;
		}
		return null;
	}

	public static InputStream getInputStreamFromRessource(Class pClass, String pRessourceName)
			throws IOException
	{
		InputStream lInputStream = pClass.getResourceAsStream(pRessourceName);
		return lInputStream;
	}
}
