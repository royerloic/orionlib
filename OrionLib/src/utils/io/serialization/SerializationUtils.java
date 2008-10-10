package utils.io.serialization;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class SerializationUtils
{
	public static final void write(final Object pObject, final File pFile) throws IOException
	{
		final FileOutputStream lFileOutputStream = new FileOutputStream(pFile);
		final BufferedOutputStream lBufferedOutputStream = new BufferedOutputStream(lFileOutputStream);
		final ObjectOutputStream lObjectOuputStream = new ObjectOutputStream(lBufferedOutputStream);
		lObjectOuputStream.writeObject(pObject);
		lObjectOuputStream.close();
	}

	public static final Object read(final File pFile)	throws IOException,
																										ClassNotFoundException
	{
		if (pFile.exists())
		{
			final FileInputStream lFileInputStream = new FileInputStream(pFile);
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

	public static final void writegzipped(final Object pObject, final File pFile) throws IOException
	{
		final FileOutputStream lFileOutputStream = new FileOutputStream(pFile);
		final BufferedOutputStream lBufferedOutputStream = new BufferedOutputStream(lFileOutputStream);
		final GZIPOutputStream lGZIPOutputStream = new GZIPOutputStream(lBufferedOutputStream);
		final ObjectOutputStream lObjectOuputStream = new ObjectOutputStream(lGZIPOutputStream);
		lObjectOuputStream.writeObject(pObject);
		lObjectOuputStream.close();
	}

	public static final Object readgzipped(final File pFile) throws IOException,
																													ClassNotFoundException
	{
		if (pFile.exists())
		{
			final FileInputStream lFileInputStream = new FileInputStream(pFile);
			final BufferedInputStream lBufferedInputStream = new BufferedInputStream(lFileInputStream);
			final GZIPInputStream lGZIPInputStream = new GZIPInputStream(lBufferedInputStream);
			final ObjectInputStream lObjectInputStream = new ObjectInputStream(lGZIPInputStream);
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
