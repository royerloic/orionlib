package utils.bioinformatics.interpro;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import utils.bioinformatics.ids.GoIdConversion;
import utils.bioinformatics.ids.InterproIdConversion;
import utils.io.MatrixFile;
import utils.structures.ArrayListMap;
import utils.structures.ListMap;
import utils.structures.Matrix;

/**
 * Loic Royer, Copyright (c) 2005, Some Rights Reserved.
 * 
 */
public class InterproToGo
{
	private static final ListMap<Integer, Integer>	InterproToGoSetMap	= new ArrayListMap<Integer, Integer>();

	static
	{
		try
		{
			final InputStream lInputStream = MatrixFile.getInputStreamFromRessource(new InterproToGo().getClass(),
					"InterproToGo.tab.txt");

			final Matrix<String> lMatrix = MatrixFile.readMatrixFromStream(lInputStream);

			for (final List<String> lList : lMatrix)
			{
				final String lInterproIdString = lList.get(0);
				final String lGoIdString = lList.get(1);
				final Integer lInterproId = InterproIdConversion.getIdFromString(lInterproIdString);
				final Integer lGoId = GoIdConversion.getIdFromString(lGoIdString);
				InterproToGoSetMap.put(lInterproId, lGoId);
			}
		}
		catch (final IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static List<Integer> getGoIdsForInterproId(final Integer pInterproId)
	{
		final List<Integer> lGoIdList = InterproToGoSetMap.get(pInterproId);
		return lGoIdList == null ? Collections.<Integer> emptyList() : lGoIdList;
	}

}
