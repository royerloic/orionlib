package utils.bioinformatics.proteome.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.Test;

import utils.bioinformatics.genome.FastaSet;
import utils.bioinformatics.genome.Genome;
import utils.bioinformatics.proteome.InterProScanReader;
import utils.bioinformatics.proteome.Proteome;

public class ProteomeTest
{

	@Test
	public void testProteome() throws IOException
	{
		Proteome lProteome = null;
		try
		{
			Genome lGenome = new Genome(ProteomeTest.class.getResourceAsStream("./test.gff"));
			FastaSet lFastaSet = new FastaSet(ProteomeTest.class.getResourceAsStream("./test.fasta.txt"));
			lProteome = new Proteome(lGenome, lFastaSet);

			lProteome.addInteractions(ProteomeTest.class.getResourceAsStream("./test.int.tab.txt"));

			InterProScanReader.addDomainsTo(lProteome,
																			ProteomeTest.class.getResourceAsStream("./test.interpro.tab.txt"));

			
			System.out.println(lProteome);

		}
		catch (Throwable e)
		{
			e.printStackTrace();
			fail("Exception: " + e);
		}

		assertTrue(lProteome.getProteinSet().getNumberOfProteins() == 2);
		assertNotNull(lProteome.getProteinSet().getProteinById("YAL002W"));
		assertNotNull(lProteome	.getProteinSet()
														.getProteinById("YAL002W")
														.getCorrespondingFastaSequence());
	}

	@Test
	public void testSerialization() throws IOException, ClassNotFoundException
	{
		Proteome lProteome = null;
		try
		{
			Genome lGenome = new Genome(ProteomeTest.class.getResourceAsStream("./test.gff"));
			FastaSet lFastaSet = new FastaSet(ProteomeTest.class.getResourceAsStream("./test.fasta.txt"));
			lProteome = new Proteome(lGenome, lFastaSet);
		}
		catch (Throwable e)
		{
			e.printStackTrace();
			fail("Exception: " + e);
		}
		File lFile = File.createTempFile("pattern", ".suffix");

		FileOutputStream lFileOutputStream = new FileOutputStream(lFile);
		ObjectOutputStream lObjectOutputStream = new ObjectOutputStream(lFileOutputStream);
		lObjectOutputStream.writeObject(lProteome);
		lObjectOutputStream.close();

		FileInputStream lFileInputStream = new FileInputStream(lFile);
		ObjectInputStream lObjectInputStream = new ObjectInputStream(lFileInputStream);
		Proteome lLoadedProteome = (Proteome) lObjectInputStream.readObject();
		lObjectInputStream.close();

		assertEquals(lLoadedProteome, lProteome);
	}

	@Test
	public void testYeastProteome() throws IOException
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
			InterProScanReader.addDomainsTo(lProteome,lInterProScanFile);

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
	}

	

}
