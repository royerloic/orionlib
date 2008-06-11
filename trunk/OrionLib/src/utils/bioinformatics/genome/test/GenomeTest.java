package utils.bioinformatics.genome.test;

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

import utils.bioinformatics.genome.Genome;

public class GenomeTest
{

	@Test
	public void testGenome() throws IOException
	{
		Genome lGenome = null;
		try
		{
			lGenome = new Genome(GenomeTest.class.getResourceAsStream("./Test.gff"));
			lGenome.addSequencesFromStream(GenomeTest.class.getResourceAsStream("./Test.fasta.txt"));
			System.out.println(lGenome);

		}
		catch (final Throwable e)
		{
			e.printStackTrace();
			fail("Exception: " + e);
		}

		assertTrue(lGenome.getGeneSet().getNumberOfGenes() == 17);
		assertNotNull(lGenome.getGeneSet().getGeneById("YAL065C"));
		assertNotNull(lGenome	.getGeneSet()
													.getGeneById("YAL065C")
													.getCorrespondingFastaSequence());
	}

	@Test
	public void testSerialization() throws IOException, ClassNotFoundException
	{
		Genome lGenome = null;
		try
		{
			lGenome = new Genome(GenomeTest.class.getResourceAsStream("./Test.gff"));
		}
		catch (final Throwable e)
		{
			e.printStackTrace();
			fail("Exception: " + e);
		}
		final File lFile = File.createTempFile("pattern", ".suffix");

		final FileOutputStream lFileOutputStream = new FileOutputStream(lFile);
		final ObjectOutputStream lObjectOutputStream = new ObjectOutputStream(lFileOutputStream);
		lObjectOutputStream.writeObject(lGenome);
		lObjectOutputStream.close();

		final FileInputStream lFileInputStream = new FileInputStream(lFile);
		final ObjectInputStream lObjectInputStream = new ObjectInputStream(lFileInputStream);
		final Genome lLoadedGenome = (Genome) lObjectInputStream.readObject();
		lObjectInputStream.close();

		assertEquals(lLoadedGenome, lGenome);
	}

	/*****************************************************************************
	 * @Test public void testYeastGenome() throws IOException { Genome lGenome =
	 *       null; try { final File lGenomeFile = new
	 *       File("G:/Projects/PowerGraphs/Data/Yeast/saccharomyces_cerevisiae.gff");
	 *       lGenome = new Genome(lGenomeFile);
	 * 
	 * final File lGeneSequencesFile = new
	 * File("G:/Projects/PowerGraphs/Data/Yeast/orf_coding_all.fasta");
	 * lGenome.addSequencesFromFile(lGeneSequencesFile); //
	 * System.out.println(lGenome); } catch (Throwable e) { e.printStackTrace();
	 * fail("Exception: " + e); }
	 * 
	 * assertTrue(lGenome.getGeneSet().getNumberOfGenes() == 6609);
	 * assertNotNull(lGenome .getGeneSet() .getGeneById("YAL065C")
	 * .getCorrespondingFastaSequence()); }/
	 ****************************************************************************/

}
