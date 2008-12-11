package utils.bioinformatics.fasta.test;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.junit.Test;

import utils.bioinformatics.fasta.Conservation;
import utils.bioinformatics.fasta.FastaSequence;
import utils.bioinformatics.fasta.FastaSet;
import utils.io.StreamToFile;
import utils.io.filedb.FileDB;
import utils.utils.Arrays;

public class ConservationTest
{

	@Test
	public void testEntropyConservation() throws IOException
	{
		InputStream lInputStream = ConservationTest.class.getResourceAsStream("test.aligned.fasta");

		FastaSet lFastaSet = new FastaSet(lInputStream);

		double[] lComputeEntropy = Conservation.computeRelativeNegentropy(lFastaSet);
		String lCodedEntropy = Conservation.encodeAsString(lComputeEntropy);

		System.out.println(lFastaSet.toAlignmentString());
		System.out.println(lCodedEntropy);

	}

}
