package utils.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SerializationUtils
{
	public static final void write(final Object pObject, final File pProteomeFile) throws IOException
	{
		final FileOutputStream lFileOutputStream = new FileOutputStream(pProteomeFile);
		final BufferedOutputStream lBufferedOutputStream = new BufferedOutputStream(lFileOutputStream);
		final ObjectOutputStream lObjectOuputStream = new ObjectOutputStream(lBufferedOutputStream);
		lObjectOuputStream.writeObject(pObject);
		lObjectOuputStream.close();
	}

	public static final Object read(final File pProteomeFile)	throws IOException,
																														ClassNotFoundException
	{
		if (pProteomeFile.exists())
		{
			final FileInputStream lFileInputStream = new FileInputStream(pProteomeFile);
			final BufferedInputStream lBufferedInputStream = new BufferedInputStream(lFileInputStream);
			final ObjectInputStream lObjectInputStream = new ObjectInputStream(lBufferedInputStream);
			final Object lObject = lObjectInputStream.readObject();
			lObjectInputStream.close();
			return lObject;
		}
		else
		{
			return null;
		}
	}
}
