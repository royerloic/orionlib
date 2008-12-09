package utils.bioinformatics.blast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Pattern;

import utils.bioinformatics.fasta.FastaSet;
import utils.bioinformatics.genome.ParseException;
import utils.io.LineReader;
import utils.process.ProcessUtils;

public class NcbiLocalBlast implements Blast
{
	public boolean mDeleteOnExit = true;

	private File mTempFolder;
	private File mDatabaseFastaFile;
	private Process mDatabaseFormattingProcess;
	private Process mBlastProcess;


	private StringBuilder mLog = new StringBuilder();

	public NcbiLocalBlast(FastaSet pBlastDatabase) throws IOException
	{
		super();

		mTempFolder = File.createTempFile("NcbiLocalBlast", "mTempFolder");
		mTempFolder.delete();
		mTempFolder.mkdir();
		if (mDeleteOnExit)
			mTempFolder.deleteOnExit();
		assert (mTempFolder.exists() && mTempFolder.isDirectory());

		/*
		 * formatdb -p F -i all_seqs.fasta -n customBLASTdb -i name of the input
		 * file -p type of BLAST database T protein (default) F nucleotide -n name
		 * of the BLAST index that you are about to create
		 */
		mDatabaseFastaFile = new File(mTempFolder, "database.fasta");
		if (mDeleteOnExit)
			mDatabaseFastaFile.deleteOnExit();
		pBlastDatabase.toFile(mDatabaseFastaFile);

		ProcessBuilder lProcessBuilder = new ProcessBuilder("formatdb",
																												"-i" + mDatabaseFastaFile.getName(),
																												"-oF",
																												"-pT");
		lProcessBuilder.directory(mTempFolder);
		lProcessBuilder.redirectErrorStream(true);
		mLog.append("Starting: " + lProcessBuilder.command()+ "\n");
		mDatabaseFormattingProcess = lProcessBuilder.start();

		StringBuilder lReadProcessOutput = ProcessUtils.readProcessOutput(mDatabaseFormattingProcess);
		mLog.append(lReadProcessOutput);

		if (mDeleteOnExit)
			for (File lFile : mTempFolder.listFiles())
			{
				lFile.deleteOnExit();
			}
	}

	public File searchFor(FastaSet pBlastQuery, double e)	throws InterruptedException,
																												IOException
	{
		mDatabaseFormattingProcess.waitFor();

		File lTempQueryFastaFile = File.createTempFile(	"query.",
																										".fasta",
																										mTempFolder);
		lTempQueryFastaFile.delete();
		if (mDeleteOnExit)
			lTempQueryFastaFile.deleteOnExit();

		pBlastQuery.toFile(lTempQueryFastaFile);

		File lTempResultFile = File.createTempFile("result.", ".txt", mTempFolder);
		lTempResultFile.delete();
		if (mDeleteOnExit)
			lTempResultFile.deleteOnExit();

		ProcessBuilder lProcessBuilder = new ProcessBuilder("blastall",
																												"-i" + lTempQueryFastaFile.getAbsolutePath(),
																												"-d" + mDatabaseFastaFile.getAbsolutePath(),
																												"-o" + lTempResultFile.getName(),
																												"-pblastp",
																												"-m9");
		lProcessBuilder.directory(mTempFolder);
		lProcessBuilder.redirectErrorStream(true);
		mLog.append("Starting: " + lProcessBuilder.command()+ "\n");
		mBlastProcess = lProcessBuilder.start();

		StringBuilder lReadProcessOutput = ProcessUtils.readProcessOutput(mBlastProcess);
		mLog.append(lReadProcessOutput);
		
		return lTempResultFile;
	}

	public BlastResult getResult(File pResultFile) throws InterruptedException, IOException
	{
		mBlastProcess.waitFor();

		BlastResult lBlastResult = new BlastResult(pResultFile);

		return lBlastResult;
	}

	public StringBuilder getLog()
	{
		return mLog;
	}

}
