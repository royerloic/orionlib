package org.royerloic.bioinformatics.interpro;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import org.royerloic.bioinformatics.ids.GoIdConversion;
import org.royerloic.bioinformatics.ids.InterproIdConversion;
import org.royerloic.io.MatrixFile;
import org.royerloic.structures.ArrayListMap;
import org.royerloic.structures.ListMap;
import org.royerloic.structures.Matrix;

/**
 * Loic Royer, Copyright (c) 2005, Some Rights Reserved.
 * 
 */
public class InterproToGo
{
	private static ListMap<Integer, Integer>	InterproToGoSetMap	= new ArrayListMap<Integer, Integer>();

	static
	{
		try
		{
			InputStream lInputStream = MatrixFile.getInputStreamFromRessource(new InterproToGo().getClass(),
					"InterproToGo.tab.txt");

			Matrix<String> lMatrix = MatrixFile.readMatrixFromStream(lInputStream);

			for (List<String> lList : lMatrix)
			{
				String lInterproIdString = lList.get(0);
				String lGoIdString = lList.get(1);
				Integer lInterproId = InterproIdConversion.getIdFromString(lInterproIdString);
				Integer lGoId = GoIdConversion.getIdFromString(lGoIdString);
				InterproToGoSetMap.put(lInterproId, lGoId);
			}
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static List<Integer> getGoIdsForInterproId(Integer pInterproId)
	{
		final List<Integer> lGoIdList = InterproToGoSetMap.get(pInterproId);
		return lGoIdList == null ? Collections.<Integer> emptyList() : lGoIdList;
	}

}
