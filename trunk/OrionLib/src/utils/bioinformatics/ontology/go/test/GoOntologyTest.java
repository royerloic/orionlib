package utils.bioinformatics.ontology.go.test;

import java.io.IOException;

import org.junit.Test;

import utils.bioinformatics.ontology.go.GoOntology;

public class GoOntologyTest
{

	@Test
	public void testGoOntology() throws IOException
	{
		final GoOntology lGoOntology = GoOntology.getUniqueInstance();

		System.out.println(lGoOntology.getNodeSet());
	}

}
