/*
 * Created on 10.11.2004 by Dipl.-Inf. MSc. Ing Loic Royer
 */
package org.royerloic.utils;

import java.io.File;
import java.net.URL;

/**
 * @author Dipl.-Inf. MSc. Ing. Loic Royer
 * 
 */
public class RessourceLocator
{
	static public File getFileFromName(final String pString)
	{
		final ClassLoader lClassLoader = ClassLoader.getSystemClassLoader();
		final URL lURL = lClassLoader.getResource(pString);
		final String lFileName = lURL.getFile();
		final File lFile = new File(lFileName);
		return lFile;
	}
}