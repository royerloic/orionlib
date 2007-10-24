package utils.bioinformatics.proteome.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.junit.Test;

import utils.bioinformatics.genome.FastaSet;
import utils.bioinformatics.genome.Genome;
import utils.bioinformatics.proteome.Domain;
import utils.bioinformatics.proteome.InterProScanReaderYeast;
import utils.bioinformatics.proteome.Protein;
import utils.bioinformatics.proteome.Proteome;

public class ProteomeYeastTest
{


	@Test
	public void testQueryYeastProteome() throws IOException
	{
		
		Proteome lProteome = null;
		try
		{
			Genome lGenome = null;
			final File lGenomeFile = new File("G:/Projects/PowerGraphs/Data/Yeast/saccharomyces_cerevisiae.gff");
			lGenome = new Genome(lGenomeFile);

			final File lYeastProteinSequencesFile = new File("G:/Projects/PowerGraphs/Data/Yeast/orf_trans_all.fasta");
			FastaSet lFastaSet = new FastaSet(lYeastProteinSequencesFile);
			lProteome = new Proteome(lGenome, lFastaSet);

			final File lBioGridInteractionsFile = new File("G:/Projects/PowerGraphs/Data/Yeast/Yeast.biogrid.tab.txt");
			lProteome.addInteractions(lBioGridInteractionsFile);
			
			final File lInterProScanFile = new File("G:/Projects/PowerGraphs/Data/Yeast/domains.tab");
			InterProScanReaderYeast.addDomainsTo(lProteome,lInterProScanFile);
			
			
			System.out.println(lProteome);
		}
		catch (Throwable e)
		{
			e.printStackTrace();
			fail("Exception: " + e);
		}

		assertTrue(lProteome.getProteinSet().getNumberOfProteins() == 6609);
		assertNotNull(lProteome.getProteinSet()
													.getProteinById("YAL065C")
													.getCorrespondingFastaSequence());

		System.out.println(lProteome.getInteractionGraph().getNumberOfNodes());
		System.out.println(lProteome.getInteractionGraph().getNumberOfEdges());
		
		DomainIndex lDomainIndex = new DomainIndex(lProteome);
		lDomainIndex.index();
		Set<Protein> lProteinSet =  lDomainIndex.getProteinByDomainInterproId("IPR001452");
		assertNotNull(lProteinSet);
		System.out.println(lProteinSet);
		
		for (Protein lProtein : lProteinSet)
		{
			Set<Domain> lDomainSet = lProtein.getDomainsByInterProId("IPR001452");
			System.out.println("protein has: "+lProtein.getDomainMap().keySet().size()+" distinct domains");
			for (Domain lDomain : lDomainSet)
				if(lDomain.getEValue()<1E-10)
			{
				System.out.println("Domain:");
				System.out.println(lDomain);
				System.out.println(lDomain.getCorrespondingFastaSequence());
				System.out.println("");
			}
			
		}
	}

	

}
