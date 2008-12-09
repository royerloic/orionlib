package utils.bioinformatics.mafft;

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
import utils.io.StreamToFile;
import utils.process.ProcessUtils;

public class LocalMafft 
{
	public boolean mDeleteOnExit = true;
	
	private File mTempFolder;
	private Process mMafftProcess;
	private File mTempResultFile;



	public LocalMafft() throws IOException
	{
		super();
	  mTempFolder = File.createTempFile("NcbiLocalBlast","mTempFolder");
		mTempFolder.delete();
		mTempFolder.mkdir();
		assert(mTempFolder.exists() && mTempFolder.isDirectory());
	
	}

	public FastaSet run(FastaSet pInput)	throws InterruptedException,
																												IOException
	{
		File lInputFastaFile = File.createTempFile("input.", ".fasta", mTempFolder);
		pInput.toFile(lInputFastaFile);

		mTempResultFile = new File(mTempFolder, "result.txt");
		if(mDeleteOnExit) 
			mTempResultFile.deleteOnExit();

		ProcessBuilder lProcessBuilder = new ProcessBuilder("mafft",
		                                                    //"--auto",
		                                                    //"--retree2",
		                                                    //" --maxiterate1000",
		                                                    "--reorder",
		                                                    lInputFastaFile.getAbsolutePath());
		lProcessBuilder.directory(mTempFolder);

		
		
		mMafftProcess = lProcessBuilder.start();
		
		StreamToFile.streamToFile(mMafftProcess.getInputStream(), mTempResultFile);
		
		return new FastaSet(mTempResultFile);		
	}


}
