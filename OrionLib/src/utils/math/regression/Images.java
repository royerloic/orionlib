/*
 * Drej
 * Copyright (c) 2005 Greg Dennis
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

package utils.math.regression;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.vecmath.GVector;

/**
 * Utility class for manipulating images.
 * 
 * @author Greg Dennis (gdennis@mit.edu)
 */
public final class Images
{

	private static final Toolkit TOOLKIT = Toolkit.getDefaultToolkit();

	private Images()
	{
	}

	/**
	 * Returns the image located at the specified path.
	 * 
	 * @throws FileNotFoundException
	 *           if file does not exist at path
	 * @throws IOException
	 *           if error reading from file
	 * @throws NotImageException
	 *           if file is not an image
	 */
	public static Image imageFromFile(final String path) throws FileNotFoundException,
																											IOException,
																											NotImageException
	{
		final File imageFile = new File(path);
		if (!imageFile.exists())
		{
			throw new FileNotFoundException("file " + path + " does not exist");
		}
		else if (!imageFile.canRead())
		{
			throw new IOException("error reading file");
		}

		final Image image = TOOLKIT.getImage(path);
		ensureImage(image);
		return image;
	}

	/**
	 * Returns the image located at the specified URL.
	 * 
	 * @throws IOException
	 *           if error reading from URL
	 * @throws NotImageException
	 *           if URL does not reference an image
	 */
	public static Image imageFromURL(final URL url)	throws IOException,
																									NotImageException
	{
		url.openStream().close(); // throws IOException if url bad
		final Image image = TOOLKIT.getImage(url);
		ensureImage(image);
		return image;
	}

	/**
	 * Returns a vector of data from the specified image. The returned vector is
	 * of length (width * height * 3). The intensity of red in pixel (i, j) is
	 * located at index p = (3 * (i + j * width)) in the vector. The intensity of
	 * green is at index p + 1 and blue is at p + 2.
	 * 
	 * @throws NotImageException
	 *           if image is empty
	 */
	public static GVector imageData(final Image image) throws NotImageException
	{
		ensureImage(image);
		return getData(image, 0, 0, image.getWidth(null), image.getHeight(null));
	}

	/**
	 * Returns a vector of data from the (x, y, width, height) rectangular section
	 * of the specified image. The returned vector is of length (width * height *
	 * 3). The intensity of red in pixel (i, j) is located at index p = (3 * ((i -
	 * x) + (j - y) * width)) in the vector. The intensity of green is at index p
	 * + 1 and blue is at p + 2.
	 * 
	 * @throws NotImageException
	 *           if image is empty
	 */
	public static GVector imageData(final Image image,
																	final int x,
																	final int y,
																	final int width,
																	final int height) throws NotImageException
	{
		ensureImage(image);
		return getData(image, x, y, width, height);
	}

	/**
	 * Returns a vector of the data in the given image.
	 */
	private static GVector getData(	final Image image,
																	final int x,
																	final int y,
																	final int width,
																	final int height)
	{
		final int[] pixels = new int[width * height];
		final PixelGrabber pg = new PixelGrabber(	image,
																							x,
																							y,
																							width,
																							height,
																							pixels,
																							0,
																							width);
		try
		{
			pg.grabPixels();
		}
		catch (final InterruptedException e)
		{
			throw new RuntimeException(e);
		}

		final GVector data = new GVector(3 * pixels.length);
		for (int i = 0; i < pixels.length; i++)
		{
			final int red = pixels[i] >> 16 & 0xff;
			final int green = pixels[i] >> 8 & 0xff;
			final int blue = pixels[i] & 0xff;
			data.setElement(3 * i, red);
			data.setElement(3 * i + 1, green);
			data.setElement(3 * i + 2, blue);
		}

		return data;
	}

	/**
	 * Ensures that the image has been loaded and that it is non-empty.
	 */
	private static void ensureImage(final Image image) throws NotImageException
	{
		new ImageIcon(image);
		if (image.getWidth(null) < 0)
		{
			throw new NotImageException(image);
		}
	}

}
