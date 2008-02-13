package utils.bioinformatics.ontology.go;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipInputStream;

import utils.bioinformatics.ontology.OboOntology;
import utils.structures.map.IntegerMap;

public class GoOntology extends OboOntology
{

	private static GoOntology sGoOntology = null;

	public static final GoOntology getUniqueInstance() throws FileNotFoundException,
																										IOException
	{
		if (sGoOntology == null)
		{
			final InputStream lInputStream = GoOntology.class.getResourceAsStream("go.obo.txt.zip");
			final ZipInputStream lZipInputStream = new ZipInputStream(lInputStream);
			lZipInputStream.getNextEntry();
			sGoOntology = new GoOntology(lZipInputStream);
		}
		return sGoOntology;
	}

	public GoOntology(final InputStream pInputStream)	throws FileNotFoundException,
																										IOException
	{
		super(pInputStream);
	}

	public IntegerMap<Integer> getAncestors(final Integer pGoId)
	{
		return null;
	}

}
