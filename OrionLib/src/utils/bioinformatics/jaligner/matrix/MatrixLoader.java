/*
 * $Id: MatrixLoader.java,v 1.2 2005/01/25 11:54:30 ahmed Exp $
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package utils.bioinformatics.jaligner.matrix;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

import utils.bioinformatics.jaligner.ui.filechooser.NamedInputStream;
import utils.bioinformatics.jaligner.util.Commons;

/**
 * Scoring matrices loader from a jar file or a file system.
 * 
 * @author Ahmed Moustafa (ahmed@users.sf.net)
 */

public class MatrixLoader
{
	/**
	 * The starter character of a comment line.
	 */
	private static final char COMMENT_STARTER = '#';

	/**
	 * The size of the scoring matrix. It is the number of the characters in the
	 * ASCII table. It is more than the 20 amino acids just to save the processing
	 * time of the mapping.
	 */
	private static final int SIZE = 127;

	/**
	 * The path to the matrices within the package.
	 */
	private static final String MATRICES_HOME = "utils/bioinformatics/jaligner/matrix/matrices/";

	/**
	 * Logger
	 */
	private static final Logger logger = Logger.getLogger(MatrixLoader.class.getName());

	/**
	 * Loads scoring matrix from Jar file or file system.
	 * 
	 * @param matrix
	 *          to load
	 * @return loaded matrix
	 * @throws MatrixLoaderException
	 * @see Matrix
	 */
	public static Matrix load(final String matrix) throws MatrixLoaderException
	{
		InputStream is = null;

		if (new StringTokenizer(matrix, Commons.getFileSeparator()).countTokens() == 1)
		{
			// Matrix does not include the path
			// Load the matrix from matrices.jar
			is = MatrixLoader.class	.getClassLoader()
															.getResourceAsStream(MATRICES_HOME + matrix);
		}
		else
		{
			// Matrix includes the path information
			// Load the matrix from the file system
			try
			{
				is = new FileInputStream(matrix);
			}
			catch (final Exception e)
			{
				final String message = "Failed opening input stream: " + e.getMessage();
				logger.log(Level.SEVERE, message, e);
				throw new MatrixLoaderException(message);
			}
		}

		return load(new NamedInputStream(matrix, is));
	}

	/**
	 * Loads scoring matrix from {@link InputStream}
	 * 
	 * @param nis
	 *          named input stream
	 * @return loaded matrix
	 * @throws MatrixLoaderException
	 * @see Matrix
	 * @see NamedInputStream
	 */
	public static Matrix load(final NamedInputStream nis) throws MatrixLoaderException
	{
		logger.info("Loading scoring matrix...");
		final char[] acids = new char[SIZE];

		// Initialize the acids array to null values (ascii = 0)
		for (int i = 0; i < SIZE; i++)
		{
			acids[i] = 0;
		}

		final float[][] scores = new float[SIZE][SIZE];

		final BufferedReader reader = new BufferedReader(new InputStreamReader(nis.getInputStream()));

		String line;

		try
		{
			// Skip the comment lines
			while ((line = reader.readLine()) != null && line.trim().charAt(0) == COMMENT_STARTER)
			{
				;
			}
		}
		catch (final Exception e)
		{
			final String message = "Failed reading from input stream: " + e.getMessage();
			logger.log(Level.SEVERE, message, e);
			throw new MatrixLoaderException(message);
		}

		// Read the headers line (the letters of the acids)
		StringTokenizer tokenizer;
		tokenizer = new StringTokenizer(line.trim());
		for (int j = 0; tokenizer.hasMoreTokens(); j++)
		{
			acids[j] = tokenizer.nextToken().charAt(0);
		}

		try
		{
			// Read the scores
			while ((line = reader.readLine()) != null)
			{
				tokenizer = new StringTokenizer(line.trim());
				final char acid = tokenizer.nextToken().charAt(0);
				for (int i = 0; i < SIZE; i++)
				{
					if (acids[i] != 0)
					{
						scores[acid][acids[i]] = Float.parseFloat(tokenizer.nextToken());
					}
				}
			}
		}
		catch (final Exception e)
		{
			final String message = "Failed reading from input stream: " + e.getMessage();
			logger.log(Level.SEVERE, message, e);
			throw new MatrixLoaderException(message);
		}
		logger.info("Finished loading scoring matrix");
		return new Matrix(nis.getName(), scores);
	}

	/**
	 * Returns a list of the scoring matrices in the matrices home directory
	 * 
	 * @param sort
	 *          flag to sort the list or not
	 * @return sorted array of scoring matrices
	 * @throws MatrixLoaderException
	 */
	public static Collection list(final boolean sort) throws MatrixLoaderException
	{
		logger.info("Loading list of scoring matrices...");
		final ArrayList matrices = new ArrayList();
		final URL url = MatrixLoader.class.getClassLoader()
																			.getResource(MATRICES_HOME);
		if (url.getFile().toString().indexOf("!") != -1)
		{
			// Load from Jar
			JarURLConnection connection = null;
			JarFile jar = null;
			try
			{
				connection = (JarURLConnection) url.openConnection();
				jar = connection.getJarFile();
			}
			catch (final Exception e)
			{
				final String message = "Failed opening a connection to jar: " + e.getMessage();
				logger.log(Level.SEVERE, message, e);
				throw new MatrixLoaderException(message);
			}
			final Enumeration entries = jar.entries();
			JarEntry entry;
			String entryName;
			final int length = MATRICES_HOME.length();
			while (entries.hasMoreElements())
			{
				entry = (JarEntry) entries.nextElement();
				if (!entry.isDirectory())
				{
					entryName = entry.getName();
					if (entryName.startsWith(MATRICES_HOME))
					{
						matrices.add(entryName.substring(length));
					}
				}
			}
		}
		else
		{
			// Load from file system
			final String home = url.getFile();
			final File dir = new File(home);
			final String files[] = dir.list();
			File file;
			for (final String lElement : files)
			{
				file = new File(home + lElement);
				if (file.isFile() && file.canRead())
				{
					matrices.add(file.getName());
				}
			}
		}
		if (sort)
		{
			Collections.sort(matrices, new MatricesCompartor());
		}
		logger.info("Finished loading list of scoring matrices");
		return matrices;
	}

	/**
	 * Returns a list of the scoring matrices in the matrices home directory
	 * 
	 * @return sorted array of scoring matrices
	 * @throws MatrixLoaderException
	 */
	public static Collection list() throws MatrixLoaderException
	{
		return list(false);
	}
}