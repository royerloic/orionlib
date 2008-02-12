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
import java.util.Iterator;

public class LineReader
{

	public static final Writer getWriterFromStream(final OutputStream pOutputStream)
	{
		// use buffering
		// FileWriter always assumes default encoding is OK!
		final int lBufferSize = 10000000;
		final Writer lWriter = new BufferedWriter(new OutputStreamWriter(pOutputStream),
																							lBufferSize);
		return lWriter;
	}

	public static final Writer getWriterFromFile(final File pFile) throws FileNotFoundException
	{
		return getWriterFromStream(new FileOutputStream(pFile));
	}

	public static final BufferedReader getBufferedReaderFromStream(final InputStream pInputStream)
	{
		final int lBufferSize = 10000000;
		final BufferedReader lReader = new BufferedReader(new InputStreamReader(pInputStream),
																											lBufferSize);
		return lReader;
	}

	public static final BufferedReader getBufferedReaderFromFile(final File pFile) throws FileNotFoundException
	{
		return getBufferedReaderFromStream(new FileInputStream(pFile));
	}

	public static InputStream getInputStreamFromRessource(final Class pClass,
																												final String pRessourceName)
	{
		final InputStream lInputStream = pClass.getResourceAsStream(pRessourceName);
		return lInputStream;
	}

	public static final LineIterator getLines(String pString) throws IOException
	{
		return getLines(Object.class.getResourceAsStream(pString));
	}

	public static final LineIterator getLines(final InputStream pInputStream,
																						int pSkipLines) throws IOException
	{
		return new LineIterator(pInputStream, pSkipLines);
	}

	public static final LineIterator getLines(final InputStream pInputStream) throws IOException
	{
		return new LineIterator(pInputStream, 0);
	}

	public static final LineIterator getLines(final File pFile) throws IOException
	{
		return new LineIterator(new FileInputStream(pFile), 0);
	}

	public final static class LineIterator implements
																				Iterable<String>,
																				Iterator<String>
	{
		private BufferedReader mBufferedReader = null;
		private String mLineString = null;

		public LineIterator(final InputStream pInputStream, final int pSkipLines) throws IOException
		{
			// We choose the buffer to be 10% of the file, therefore, a File will be
			// block read in about 10 steps.
			int lBufferSize = Math.min(10000000, (pInputStream.available() / 10));
			lBufferSize = lBufferSize == 0 ? 1000 : lBufferSize;
			mBufferedReader = new BufferedReader(	new InputStreamReader(pInputStream),
																						lBufferSize);
			for (int i = 0; i < pSkipLines; i++)
			{
				final String lString = mBufferedReader.readLine();
				System.out.println("##skipped: " + lString);
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
