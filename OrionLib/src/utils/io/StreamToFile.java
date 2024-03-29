package utils.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StreamToFile
{
	public static final void streamToFile(final InputStream pInputStream,
																				final File pFile) throws IOException
	{
		int lBufferSize = Math.min(10000000, (pInputStream.available() / 10));
		lBufferSize = lBufferSize == 0 ? 1000 : lBufferSize;
		InputStreamReader lInputStreamReader = new InputStreamReader(pInputStream);
		final BufferedReader lBufferedReader = new BufferedReader(lInputStreamReader,
																															lBufferSize);

		final BufferedWriter lBufferedFileWriter = new BufferedWriter(new FileWriter(pFile),
																																	lBufferSize);

		
		int c;
		while (((c = lBufferedReader.read()) != -1))
		{
			lBufferedFileWriter.write(c);
		}
		lBufferedReader.close();
		lBufferedFileWriter.close();
	}
}
