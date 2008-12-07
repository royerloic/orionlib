package utils.bioinformatics.blast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Pattern;

import utils.bioinformatics.fasta.FastaSet;
import utils.bioinformatics.genome.ParseException;
import utils.io.LineReader;

public class NcbiLocalBlast implements Blast
{

	private File mTempDatabaseFile;
	private Process mDatabaseFormattingProcess;
	private Process mBlastProcess;

	public NcbiLocalBlast(FastaSet pBlastDatabase) throws IOException
	{
		super();
		/*
		 * formatdb -p F -i all_seqs.fasta -n customBLASTdb -i name of the input
		 * file -p type of BLAST database T protein (default) F nucleotide -n name
		 * of the BLAST index that you are about to create
		 */
		File lTempFastaFile = File.createTempFile("NcbiLocalBlast",
																							"lTempFastaFile");
		pBlastDatabase.toFile(lTempFastaFile);
		mTempDatabaseFile = File.createTempFile("NcbiLocalBlast",
																						"lTempDatabaseFile");
		ProcessBuilder lProcessBuilder = new ProcessBuilder("formatdb",
																												"-P F",
																												"-i \"" + lTempFastaFile.getAbsolutePath()
																														+ "\" ",
																												"-n " + mTempDatabaseFile.getAbsolutePath());
		
		mDatabaseFormattingProcess = lProcessBuilder.start();
		

	}

	public void searchFor(FastaSet pBlastQuery, double e) throws InterruptedException, IOException
	{
		mDatabaseFormattingProcess.waitFor();

		File lTempQueryFastaFile = File.createTempFile(	"NcbiLocalBlast",
																										"lTempQueryFastaFile");
		pBlastQuery.toFile(lTempQueryFastaFile);

		File lTempResultFile = File.createTempFile(	"NcbiLocalBlast",
																								"lTempQueryFastaFile");

		ProcessBuilder lProcessBuilder = new ProcessBuilder("blastall",
																												"-p blastp",
																												"-d " + mTempDatabaseFile.getAbsolutePath(),
																												"-i " + lTempQueryFastaFile.getAbsolutePath(),
																												"-o " + lTempResultFile.getAbsolutePath());

		mBlastProcess = lProcessBuilder.start();		
	}

	public BlastResult getResult() throws InterruptedException
	{
		mBlastProcess.waitFor();

		return null;
	}

}
