package utils.bioinformatics.fasta;

import utils.random.sequence.SequenceRandomizer;

public class Randomize
{
	public static final FastaSet randomize(String pType, FastaSet pFastaSet)
	{
		FastaSet lFastaSet = new FastaSet();

		for (FastaSequence lFastaSequence : pFastaSet)
		{
			FastaSequence lRandomizedFastaSequence = randomize(pType, lFastaSequence);
			lFastaSet.addFastaSequence(lRandomizedFastaSequence);
		}

		return lFastaSet;
	}

	public static final FastaSequence randomize(String pType,
																							FastaSequence pFastaSequence)
	{
		FastaSequence lFastaSequence = new FastaSequence(pFastaSequence);
		char[] lRandomizedSequenceArray = SequenceRandomizer.randomize(	pType,
																																		lFastaSequence.getSequenceArray());
		lFastaSequence.setSequenceArray(lRandomizedSequenceArray);
		return lFastaSequence;
	}

}
