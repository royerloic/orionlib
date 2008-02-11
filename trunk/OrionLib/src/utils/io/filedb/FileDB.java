package utils.io.filedb;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class FileDB
{

	public static final void filter(File pInputFile,
																	int pColumn,
																	String pValue,
																	File pOutputFile) throws FileNotFoundException
	{
		filter(	new FileInputStream(pInputFile),
						pColumn,
						pValue,
						new FileOutputStream(pInputFile));
	}

	public static final void filter(InputStream pInputStream,
																	int pColumn,
																	String pValue,
																	OutputStream pOutputStream)
	{

	}

}
