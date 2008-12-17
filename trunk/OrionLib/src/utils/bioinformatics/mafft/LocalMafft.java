package utils.bioinformatics.mafft;

import java.io.File;
import java.io.IOException;

import utils.bioinformatics.fasta.FastaSet;
import utils.io.StreamToFile;

public class LocalMafft implements MultipleSequenceAlignment
{
	public boolean mDeleteOnExit = true;

	private File mTempFolder;
	private Process mMafftProcess;
	private File mTempResultFile;

	public boolean mFast = true;

	public LocalMafft() throws IOException
	{
		super();
		mTempFolder = File.createTempFile("LocalMafft", "mTempFolder");
		if (mDeleteOnExit)
			mTempFolder.deleteOnExit();
		mTempFolder.delete();
		mTempFolder.mkdir();
		assert (mTempFolder.exists() && mTempFolder.isDirectory());

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * utils.bioinformatics.mafft.MultipleSequenceAlignment#run(utils.bioinformatics
	 * .fasta.FastaSet)
	 */
	public FastaSet run(FastaSet pInput) throws InterruptedException, IOException
	{
		File lInputFastaFile = File.createTempFile("input.", ".fasta", mTempFolder);
		if (mDeleteOnExit)
			lInputFastaFile.deleteOnExit();
		pInput.toFile(lInputFastaFile);

		mTempResultFile = File.createTempFile("result.", ".txt", mTempFolder);
		if (mDeleteOnExit)
			mTempResultFile.deleteOnExit();

		ProcessBuilder lProcessBuilder;
		if (mFast)
		{
			lProcessBuilder = new ProcessBuilder(	"mafft",
																						"--retree",
																						"2",
																						"--maxiterate",
																						"0",
																						"--reorder",
																						lInputFastaFile.getAbsolutePath());
		}
		else
		{
			lProcessBuilder = new ProcessBuilder(	"mafft",
																						"--localpair",
																						"--maxiterate",
																						"1000",
																						"--reorder",
																						lInputFastaFile.getAbsolutePath());
		}
		lProcessBuilder.directory(mTempFolder);


		System.out.println("Mafft started");
		mMafftProcess = lProcessBuilder.start();

		System.out.println("Reading from Mafft OutputStream...");
		StreamToFile.streamToFile(mMafftProcess.getInputStream(), mTempResultFile);

		//mMafftProcess.waitFor();
		System.out.println("Mafft finished");

		return new FastaSet(mTempResultFile);
	}
}
