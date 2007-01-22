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
		ClassLoader lClassLoader = ClassLoader.getSystemClassLoader();
		URL lURL = lClassLoader.getResource(pString);
		String lFileName = lURL.getFile();
		File lFile = new File(lFileName);
		return lFile;
	}
}