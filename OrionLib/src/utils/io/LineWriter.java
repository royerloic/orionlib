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

import utils.structures.matrix.ArrayMatrix;
import utils.structures.matrix.Matrix;

public class LineWriter
{

	public static final Writer getWriter(final OutputStream pOutputStream)
	{
		// use buffering
		// FileWriter always assumes default encoding is OK!
		final int lBufferSize = 10000000;
		final Writer lWriter = new BufferedWriter(new OutputStreamWriter(pOutputStream),
																							lBufferSize);
		return lWriter;
	}

	public static final Writer getWriter(final File pFile) throws FileNotFoundException
	{
		return getWriter(new FileOutputStream(pFile));
	}

}
