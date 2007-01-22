package org.royerloic.bioinformatics.ontology.go;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.royerloic.bioinformatics.ontology.OboOntology;
import org.royerloic.structures.IntegerMap;

public class GoOntology extends OboOntology
{

	private static GoOntology	sGoOntology	= null;

	public static final GoOntology getUniqueInstance() throws FileNotFoundException, IOException
	{
		if (sGoOntology == null)
		{
			final InputStream lInputStream = GoOntology.class.getResourceAsStream("go.obo.txt");
			sGoOntology = new GoOntology(lInputStream);
		}
		return sGoOntology;
	}

	public GoOntology(InputStream pInputStream) throws FileNotFoundException, IOException
	{
		super(pInputStream);
	}

	public IntegerMap<Integer> getAncestors(Integer pGoId)
	{
		return null;
	}

}
