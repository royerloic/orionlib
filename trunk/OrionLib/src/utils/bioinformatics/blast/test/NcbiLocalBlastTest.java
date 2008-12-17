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

import utils.bioinformatics.blast.BlastResult;
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

			File lResultFile = lNcbiLocalBlast.searchFor(lFastaSet, 10);
			
			System.out.println(lNcbiLocalBlast.getLog());
			
			BlastResult lResult = lNcbiLocalBlast.getResult(lResultFile);
			
			assertSame(31,lResult.size());			
			
		}
		catch (Throwable e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}

	}
	
	@Test
	public void testBlastAllAgainstAll() throws IOException
	{
		try
		{
			FastaSet lFastaSet = FastaSet.buildFromArray("Q06410","Q12421","Q12092");

			NcbiLocalBlast lNcbiLocalBlast = new NcbiLocalBlast(lFastaSet);

			File lResultFile = lNcbiLocalBlast.searchFor(lFastaSet, 100);
			
			System.out.println(lNcbiLocalBlast.getLog());
			
			BlastResult lResult = lNcbiLocalBlast.getResult(lResultFile);
			
			System.out.println(lResult);
			
		}
		catch (Throwable e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}

	}

}
