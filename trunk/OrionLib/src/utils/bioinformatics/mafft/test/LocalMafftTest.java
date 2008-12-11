package utils.bioinformatics.mafft.test;

import static org.junit.Assert.*;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

import utils.bioinformatics.fasta.Conservation;
import utils.bioinformatics.fasta.FastaSet;
import utils.bioinformatics.fasta.Randomize;
import utils.bioinformatics.mafft.LocalMafft;
import utils.bioinformatics.mafft.MultipleSequenceAlignment;
import utils.bioinformatics.mafft.SignificantMultipleAlignment;

public class LocalMafftTest
{

	@Test
	public void testLocalMafft() throws IOException
	{
		String result = "AFEGQTN--TEIPGLPKK---------PEII";

		try
		{
			InputStream lInputStream = LocalMafftTest.class.getResourceAsStream("test.fasta");

			FastaSet lInput = new FastaSet(lInputStream);

			MultipleSequenceAlignment lLocalMafft = new LocalMafft();

			FastaSet lOutput = lLocalMafft.run(lInput);

			System.out.println(lOutput.toAlignmentString());

			//assertTrue(lOutput.toAlignmentString().contains(result));

		}
		catch (Throwable e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testLocalMafftOnRandomizedSequences() throws IOException
	{

		try
		{
			InputStream lInputStream = LocalMafftTest.class.getResourceAsStream("test.many.fasta");

			FastaSet lInput = new FastaSet(lInputStream);

			MultipleSequenceAlignment lLocalMafft = new LocalMafft();
			SignificantMultipleAlignment lSignificantMultipleAlignment = new SignificantMultipleAlignment(lLocalMafft);
			lSignificantMultipleAlignment.setRandomizationRuns(10);
			FastaSet lFastaSet = lSignificantMultipleAlignment.run(lInput);
			double lComputedAverageRelativeNegentropy = Conservation.computeAverageRelativeNegentropy(lFastaSet);
			System.out.println("lComputedAverageRelativeNegentropy for randomized: "+lComputedAverageRelativeNegentropy);

			//System.out.println(lFastaSet.toAlignmentString());
			double lSignificanceForOriginalSequences = lSignificantMultipleAlignment.getSignificance();
			double[] lSignificantConservationScore = lSignificantMultipleAlignment.getSignificantConservationScore();

			FastaSet lRandomizedInput = Randomize.randomize("1gram", lInput);
			FastaSet lRandomizedAligment = lSignificantMultipleAlignment.run(lRandomizedInput);
			double lComputedAverageRelativeNegentropyRand = Conservation.computeAverageRelativeNegentropy(lRandomizedAligment);
			System.out.println("lComputedAverageRelativeNegentropy for randomized: "+lComputedAverageRelativeNegentropyRand);
			double lSignificanceForRandomizedSequences = lSignificantMultipleAlignment.getSignificance();
			double[] lSignificantConservationScoreForRandomizedSequences = lSignificantMultipleAlignment.getSignificantConservationScore();

			System.out.println("lSignificanceForOriginalSequences=" + lSignificanceForOriginalSequences);
			System.out.println("lSignificantConservationScore=" + lSignificantConservationScore);
			System.out.println("lSignificanceForRandomizedSequences=" + lSignificanceForRandomizedSequences);
			System.out.println("lSignificantConservationScoreForRandomizedSequences=" + lSignificantConservationScoreForRandomizedSequences);

			assertTrue(lSignificanceForOriginalSequences > 10);
			assertTrue(lSignificanceForRandomizedSequences <= 6);

		}
		catch (Throwable e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

}
