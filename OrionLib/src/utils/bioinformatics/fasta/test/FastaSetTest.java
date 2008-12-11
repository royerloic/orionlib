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

import utils.bioinformatics.fasta.FastaSequence;
import utils.bioinformatics.fasta.FastaSet;
import utils.io.StreamToFile;
import utils.io.filedb.FileDB;

public class FastaSetTest
{

	@Test
	public void testReadFromFile() throws IOException
	{
		InputStream lInputStream = FastaSetTest.class.getResourceAsStream("test.fasta");

		FastaSet lFastaSet = new FastaSet(lInputStream);

		assertSame(8, lFastaSet.size());

		assertTrue(lFastaSet.getSequenceByName("sp|P31947|1433S_HUMAN 14-3-3 protein sigma OS=Homo sapiens GN=SFN PE=1 SV=1")
												.getSequenceString()
												.equals("MERASLIQKAKLAEQAERYEDMAAFMKGAVEKGEELSCEERNLLSVAYKNVVGGQRAAWRVLSSIEQKSNEEGSEEKGPEVREYREKVETELQGVCDTVLGLLDSHLIKEAGDAESRVFYEAPQEPQS"));

	}

	@Test
	public void testDownloadFromUniprot() throws IOException
	{
		FastaSet lFastaSet = new FastaSet(new String[]
		{ "P31947",
			"Q61151",
			"P03949",
			"uniprotkb:Q9PJ25|intact:EBI-1194099",
			">sp|B2GUW6|AEN_RAT Apoptosis-enhancing nuclease OS=Rattus norvegicus GN=Aen PE=2 SV=1" });

		assertSame(5, lFastaSet.size());

		String lString = lFastaSet.toString();
		assertTrue(lString.contains("|P31947|"));
		assertTrue(lString.contains("|Q61151|"));
		assertTrue(lString.contains("|P03949|"));
		assertTrue(lString.contains("|B2GUW6|"));
		assertTrue(lString.contains("|Q9PJ25|"));
	}

}
