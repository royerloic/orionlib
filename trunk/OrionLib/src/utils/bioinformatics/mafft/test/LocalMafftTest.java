package utils.bioinformatics.mafft.test;

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
import utils.bioinformatics.fasta.Randomize;
import utils.bioinformatics.mafft.LocalMafft;
import utils.io.StreamToFile;
import utils.io.filedb.FileDB;

public class LocalMafftTest
{

	@Test
	public void testLocalMafft() throws IOException
	{
		String result = "IPRTI-------------PPKPAVSSGKPLVAPKPAANR";
		
		try
		{
			InputStream lInputStream = LocalMafftTest.class.getResourceAsStream("test.many.fasta");

			FastaSet lInput = new FastaSet(lInputStream);

			LocalMafft lLocalMafft = new LocalMafft();

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
			
			FastaSet lRandomizedInput = Randomize.randomize("1gram", lInput);

			LocalMafft lLocalMafft = new LocalMafft();

			FastaSet lOutput = lLocalMafft.run(lRandomizedInput);
			
			System.out.println(lOutput.toAlignmentString());
			
			//assertTrue(lOutput.toAlignmentString().contains(result));
			
		}
		catch (Throwable e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

}
