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

}
