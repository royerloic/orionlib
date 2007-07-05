package utils.io;

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
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import utils.structures.ArrayMatrix;
import utils.structures.Matrix;

public class MatrixFile
{

	public static <O> void writeMatrixToFile(final List<List<O>> pVectorList, final File pFile)
			throws FileNotFoundException, IOException
	{
		writeMatrixToFile(pVectorList, pFile, "\t");
	}

	public static <O> void writeMatrixToFile(	final List<List<O>> pVectorList,
																						final File pFile,
																						final String lSeparator) throws FileNotFoundException,
			IOException
	{
		writeMatrixToStream(pVectorList, new FileOutputStream(pFile), lSeparator);
	}

	public static <O> void writeMatrixToStream(	final List<List<O>> pVectorList,
																							final OutputStream pOutputStream,
																							final String pSeparator) throws FileNotFoundException,
			IOException
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
				for (final List<O> lList : pVectorList)
				{
					writeListToStream(lWriter, lList, pSeparator);
					lWriter.append("\r\n");
				}
			}
			catch (final Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				// flush and close both "output" and its underlying FileWriter
				if (lWriter != null)
				{
					lWriter.close();
				}
			}
		}
	}

	public static final Writer getWriterFromStream(final OutputStream pOutputStream)
	{
		// use buffering
		// FileWriter always assumes default encoding is OK!
		final int lBufferSize = 10000000;
		final Writer lWriter = new BufferedWriter(new OutputStreamWriter(pOutputStream), lBufferSize);
		return lWriter;
	}

	public static final Writer getWriterFromFile(final File pFile) throws FileNotFoundException
	{
		return getWriterFromStream(new FileOutputStream(pFile));
	}

	public static final <O> void writeListToStream(	final Writer pWriter,
																									final List<O> pList,
																									final String pSeparator) throws IOException
	{
		int i = 0;
		for (final O lEntry : pList)
		{
			boolean isLast = i == pList.size() - 1;
			if (!((lEntry instanceof Double) && ((Double) lEntry).isNaN()))
			{
				pWriter.append(lEntry.toString());
			}

			if (!isLast)
			{
				pWriter.append(pSeparator);
			}
			i++;
		}
	}

	public static Matrix<String> readMatrixFromFile(final File pFile) throws FileNotFoundException, IOException
	{
		return readMatrixFromFile(pFile, false, "\\t");
	}

	public static Matrix<String> readMatrixFromFile(final File pFile, final boolean pFileHasHeader)
			throws FileNotFoundException, IOException
	{
		return readMatrixFromFile(pFile, pFileHasHeader, "\\t");
	}

	public static Matrix<String> readMatrixFromFile(final File pFile, final String pSeparator)
			throws FileNotFoundException, IOException
	{
		return readMatrixFromStream(new FileInputStream(pFile), false, pSeparator);
	}

	public static Matrix<String> readMatrixFromFile(final File pFile,
																									final boolean pFileHasHeader,
																									final String pSeparator) throws FileNotFoundException,
			IOException
	{
		return readMatrixFromStream(new FileInputStream(pFile), pFileHasHeader, pSeparator);
	}

	public static Matrix<String> readMatrixFromZipFile(	final File pFile,
																											final boolean pFileHasHeader,
																											final String pSeparator) throws FileNotFoundException,
			IOException
	{
		return readMatrixFromStream(new FileInputStream(pFile), pFileHasHeader, pSeparator);
	}

	public static Matrix<String> readMatrixFromStream(final InputStream pInputStream)
			throws FileNotFoundException, IOException
	{
		return readMatrixFromStream(pInputStream, "\\t");
	}

	public static Matrix<String> readMatrixFromStream(final InputStream pInputStream, final String pSeparator)
			throws FileNotFoundException, IOException
	{
		return readMatrixFromStream(pInputStream, false, pSeparator);
	}

	public static Matrix<String> readMatrixFromStream(final InputStream pInputStream,
																										final boolean pFileHasHeader)
			throws FileNotFoundException, IOException
	{
		return readMatrixFromStream(pInputStream, pFileHasHeader, "\\t");
	}

	public static Matrix<String> readMatrixFromStream(final InputStream pInputStream,
																										final boolean pFileHasHeader,
																										final String pSeparator) throws FileNotFoundException,
			IOException
	{
		final Matrix<String> lMatrix = new ArrayMatrix<String>();
		BufferedReader lBufferedReader = null;
		try
		{
			// We choose the buffer to be 10% of the file, therefore, a File will be
			// block read in about 100 steps.
			int lBufferSize = Math.min(10000000, (pInputStream.available() / 10));
			lBufferSize = lBufferSize == 0 ? 1000 : lBufferSize;
			lBufferedReader = new BufferedReader(new InputStreamReader(pInputStream), lBufferSize);
			final Pattern lColumnSplitPattern = Pattern.compile(pSeparator);

			String lLineString;
			if (pFileHasHeader)
			{
				lLineString = lBufferedReader.readLine();
			}

			while ((lLineString = lBufferedReader.readLine()) != null)
			{
				final String[] lColumnsStringArray = lColumnSplitPattern.split(lLineString, -1);
				final List<String> lColumnsStringList = Arrays.asList(lColumnsStringArray);
				lMatrix.add(lColumnsStringList);
			}

			return lMatrix;
		}
		catch (final IOException exception)
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
				catch (final IOException exception)
				{
					System.err.println(exception);
				}
			}
		}
	}

	public static final BufferedReader getBufferedReaderFromStream(final InputStream pInputStream)
	{
		final int lBufferSize = 10000000;
		final BufferedReader lReader = new BufferedReader(new InputStreamReader(pInputStream), lBufferSize);
		return lReader;
	}

	public static final BufferedReader getBufferedReaderFromFile(final File pFile) throws FileNotFoundException
	{
		return getBufferedReaderFromStream(new FileInputStream(pFile));
	}

	public static final <O> List<String> readListFromStream(final BufferedReader pReader,
																													final String pSeparator) throws IOException
	{
		final String lLineString = pReader.readLine();
		if (lLineString != null)
		{
			final String[] lColumnsStringArray = lLineString.split(pSeparator, -1);
			final List<String> lColumnsStringList = Arrays.asList(lColumnsStringArray);
			return lColumnsStringList;
		}
		return null;
	}

	public static InputStream getInputStreamFromRessource(final Class pClass, final String pRessourceName)
	{
		final InputStream lInputStream = pClass.getResourceAsStream(pRessourceName);
		return lInputStream;
	}
	
	public static final LineIterator getLines(String pString) throws IOException
	{
		return getLines(Object.class.getResourceAsStream(pString));
	}

	public static final LineIterator getLines(final InputStream pInputStream, int pSkipLines) throws IOException
	{
		return new LineIterator(pInputStream,pSkipLines);
	}
	
	public static final LineIterator getLines(final InputStream pInputStream) throws IOException
	{
		return new LineIterator(pInputStream,0);
	}
	
	public static final LineIterator getLines(final File pFile) throws IOException
	{
		return new LineIterator(new FileInputStream(pFile),0);
	}

	public final static class LineIterator implements Iterable<String>, Iterator<String>
	{
		private BufferedReader	mBufferedReader	= null;
		private String					mLineString			= null;

		public LineIterator(final InputStream pInputStream, final int pSkipLines) throws IOException
		{
			// We choose the buffer to be 10% of the file, therefore, a File will be
			// block read in about 100 steps.
			int lBufferSize = Math.min(10000000, (pInputStream.available() / 10));
			lBufferSize = lBufferSize == 0 ? 1000 : lBufferSize;
			mBufferedReader = new BufferedReader(new InputStreamReader(pInputStream), lBufferSize);
			for(int i=0; i<pSkipLines; i++)
			{
				final String lString = mBufferedReader.readLine();
				System.out.println("##skipped: "+lString);
			}
		}

		public Iterator<String> iterator()
		{
			return this;
		}

		public boolean hasNext()
		{
			if (mLineString != null)
			{
				return true;
			}
			else
			{
				try
				{
					mLineString = mBufferedReader.readLine();
					if (mLineString == null)
					{
						mBufferedReader.close();
						return false;
					}
					else
					{
						return true;
					}
				}
				catch (IOException e)
				{
					throw new RuntimeException(e);
				}
				
			}
		}

		public String next()
		{
			final String lLineString = mLineString;
			mLineString = null;
			return lLineString;
		}

		public void remove()
		{
			throw new UnsupportedOperationException("Cannot remove from this iterator");

		}

	}





}
