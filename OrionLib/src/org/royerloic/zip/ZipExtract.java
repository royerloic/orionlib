/*
 * Created on 22.4.2005 by Dipl.-Inf. MSc. Ing Loic Royer
 */
package org.royerloic.zip;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipExtract
{
	/**
	 * main()
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException
	{
		doExtract(args[0], args[1]);
	}

	public static void doExtract(String pZipFileName, String pEntryFileName) throws IOException
	{
		// Get the zip name and the entry name.

		String zipName = pZipFileName;
		String entryName = pEntryFileName;

		// Open the zip.

		ZipFile zip = new ZipFile(zipName);
		System.out.println(zipName + " opened.");

		try
		{
			// Get the entry and its input stream.

			ZipEntry entry = zip.getEntry(entryName);

			// If the entry is not null, extract it. Otherwise, print a
			// message.

			if (entry != null)
			{
				// Get an input stream for the entry.

				InputStream entryStream = zip.getInputStream(entry);

				try
				{
					// Create the output file (clobbering the file if it exists).

					FileOutputStream file = new FileOutputStream(entry.getName());

					try
					{
						// Allocate a buffer for reading the entry data.

						byte[] buffer = new byte[1024];
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
			} // end if
		}
		finally
		{
			zip.close();
			System.out.println(zipName + " closed.");
		}
	}
}