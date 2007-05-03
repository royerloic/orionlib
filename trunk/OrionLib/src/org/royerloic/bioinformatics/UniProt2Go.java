package org.royerloic.bioinformatics;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.royerloic.io.MatrixFile;
import org.royerloic.structures.ArrayListMap;
import org.royerloic.structures.ListMap;

public class UniProt2Go
{
	private final ListMap<Integer, Integer>	mUniProt2GoListMap	= new ArrayListMap<Integer, Integer>();

	public UniProt2Go(final File pUniprot2GoFile) throws FileNotFoundException, IOException
	{
		super();

		{
			final List<List<String>> lUniprot2GoMatrix = MatrixFile.readMatrixFromFile(pUniprot2GoFile, false);

			for (final List<String> lList : lUniprot2GoMatrix)
			{
				final String lUniProtId = lList.get(0);
				final Integer lUniProtIdInteger = UniProtIdConverter.convertUniProtIdToInteger(lUniProtId);
				final String lGoIdString = lList.get(1);
				final Integer lGoIdInteger = Integer.parseInt(lGoIdString);
				mUniProt2GoListMap.put(lUniProtIdInteger, lGoIdInteger);
			}
		}
	}

}
