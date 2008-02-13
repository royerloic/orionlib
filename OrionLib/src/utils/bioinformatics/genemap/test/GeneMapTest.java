package utils.bioinformatics.genemap.test;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.junit.Test;

import utils.bioinformatics.genemap.Element;
import utils.bioinformatics.genemap.GeneMapBuilder;
import utils.bioinformatics.genemap.GeneSet;
import utils.math.stats.HyperGeometricEnrichement;

public class GeneMapTest
{

	@Test
	public void testHyperGeometricEnrichementTest() throws IOException
	{
		GeneMapBuilder lGeneMap = new GeneMapBuilder();

		lGeneMap.addGene(10, "gene10", "");
		lGeneMap.addGene(11, "gene11", "");
		lGeneMap.addGene(12, "gene12", "");
		lGeneMap.addGene(13, "gene13", "");
		lGeneMap.addGene(14, "gene14", "");

		lGeneMap.addAttribute(0, "att0", "");
		lGeneMap.addAttribute(1, "att1", "");
		lGeneMap.addAttribute(2, "att2", "");
		lGeneMap.addAttribute(3, "att3", "");
		lGeneMap.addAttribute(4, "att4", "");

		lGeneMap.addAnnotation(10, 0);
		lGeneMap.addAnnotation(11, 0);
		lGeneMap.addAnnotation(12, 0);
		lGeneMap.addAnnotation(13, 0);
		lGeneMap.addAnnotation(14, 0);

		lGeneMap.addAnnotation(10, 1);
		lGeneMap.addAnnotation(11, 1);
		lGeneMap.addAnnotation(12, 1);

		lGeneMap.addAnnotation(13, 2);
		lGeneMap.addAnnotation(14, 2);

		lGeneMap.addAnnotation(12, 3);

		HashMap<Element, Double> lEnrichement = lGeneMap.computeEnrichements(	10,
																																					11,
																																					12);

		assertTrue(lEnrichement.get(new Element(0)) == 3.0);
		assertTrue(lEnrichement.get(new Element(1)) == 0.30000000000000004);
		assertTrue(lEnrichement.get(new Element(3)) == 1.8000000000000003);

		// System.out.println(lEnrichement);

		Collection<GeneSet> lGeneSets = lGeneMap.getGeneSubSets(0.5, 10, 11, 12);

		assertTrue(lGeneSets.size()==1);
		assertTrue(lGeneSets.iterator().next().getBestPValue()==0.30000000000000004);
		//System.out.println(lGeneSets);
		
		Collection<GeneSet> lDecomposedGeneSets = lGeneMap.getGeneSubSetsAndDecompose(2, 12, 13, 14);
		System.out.println(lDecomposedGeneSets);
		assertTrue(lDecomposedGeneSets.size()==1);
		assertTrue(lDecomposedGeneSets.iterator().next().getBestPValue()==1.2000000000000002);
		
	}

}
