package utils.bioinformatics.ontology.go.test;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.junit.Test;

import utils.bioinformatics.ontology.go.GoOntology;
import utils.io.StreamToFile;
import utils.io.filedb.FileDB;
import utils.io.filedb.index.FileIndex;

public class GoOntologyTest
{

	@Test
	public void testGoOntology() throws IOException
	{
		GoOntology lGoOntology = GoOntology.getUniqueInstance();

		System.out.println(lGoOntology.getNodeSet());
	}

}
