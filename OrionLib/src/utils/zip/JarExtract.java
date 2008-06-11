/*
 * Created on 22.4.2005 by Dipl.-Inf. MSc. Ing Loic Royer
 */
package utils.zip;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JarExtract
{
	/**
	 * main()
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException
	{
		// Get the jar name and the entry name.

		final String jarName = args[0];
		final String entryName = args[1];

		// Open the jar.

		final JarFile jar = new JarFile(jarName);
		System.out.println(jarName + " opened.");

		try
		{
			// Get the entry and its input stream.

			final JarEntry entry = jar.getJarEntry(entryName);

			// If the entry is not null, extract it. Otherwise, print a
			// message.

			if (entry != null)
			{
				// Get an input stream for the entry.

				final InputStream entryStream = jar.getInputStream(entry);

				try
				{
					// Create the output file (clobbering the file if it exists).

					final FileOutputStream file = new FileOutputStream(entry.getName());

					try
					{
						// Allocate a buffer for reading the entry data.

						final byte[] buffer = new byte[1024];
						int bytesRead;

						// Read the entry data and write it to the output file.

						while ((bytesRead = entryStream.read(buffer)) != -1)
						{
							file.write(buffer, 0, bytesRead);
						}

						System.out.println(entry.getName() + " extracted.");
					}
					finally
					{
						file.close();
					}
				}
				finally
				{
					entryStream.close();
				}
			}
			else
			{
				System.out.println(entryName + " not found.");
			}
		}
		finally
		{
			jar.close();
			System.out.println(jarName + " closed.");
		}
	}
}
