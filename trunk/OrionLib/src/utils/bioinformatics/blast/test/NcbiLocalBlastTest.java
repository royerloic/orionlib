package utils.bioinformatics.blast.test;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.junit.Test;

import utils.bioinformatics.blast.NcbiLocalBlast;
import utils.bioinformatics.fasta.FastaSequence;
import utils.bioinformatics.fasta.FastaSet;
import utils.io.StreamToFile;
import utils.io.filedb.FileDB;

public class NcbiLocalBlastTest
{

	@Test
	public void testNcbiLocalBlast() throws IOException
	{
		try
		{
			InputStream lInputStream = NcbiLocalBlastTest.class.getResourceAsStream("test.fasta");

			FastaSet lFastaSet = new FastaSet(lInputStream);

			NcbiLocalBlast lNcbiLocalBlast = new NcbiLocalBlast(lFastaSet);

			lNcbiLocalBlast.searchFor(lFastaSet, 10);
		}
		catch (Throwable e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}

	}

}
