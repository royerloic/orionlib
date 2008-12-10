package utils.bioinformatics.mafft;

import java.io.IOException;

import utils.bioinformatics.fasta.FastaSet;

public interface MultipleSequenceAlignment
{

	public abstract FastaSet run(FastaSet pInput)	throws InterruptedException,
																								IOException;

}