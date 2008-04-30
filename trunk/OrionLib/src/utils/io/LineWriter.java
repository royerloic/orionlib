package utils.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

public class LineWriter
{

	public static final Writer getWriter(final OutputStream pOutputStream)
	{
		// use buffering
		// FileWriter always assumes default encoding is OK!
		final int lBufferSize = 1000000;
		final Writer lWriter = new BufferedWriter(new OutputStreamWriter(pOutputStream),
																							lBufferSize);
		return lWriter;
	}

	public static final Writer getWriter(final File pFile) throws FileNotFoundException
	{
		return getWriter(new FileOutputStream(pFile));
	}

}
