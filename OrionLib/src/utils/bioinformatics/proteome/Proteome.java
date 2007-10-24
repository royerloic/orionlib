package utils.bioinformatics.proteome;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.regex.Pattern;

import utils.bioinformatics.genome.FastaSequence;
import utils.bioinformatics.genome.FastaSet;
import utils.bioinformatics.genome.Gene;
import utils.bioinformatics.genome.Genome;
import utils.io.MatrixFile;
import utils.io.SerializationUtils;
import utils.structures.HashSetMap;
import utils.structures.graph.Edge;
import utils.structures.graph.Graph;
import utils.structures.graph.HashGraph;
import utils.structures.graph.Node;
import utils.structures.graph.io.EdgIO;

public class Proteome implements Serializable
{

	private static final Pattern lSplitTabPattern = Pattern.compile("\t");
	private static final Pattern lSplitSemicolonPattern = Pattern.compile("\\;");
	private static final Pattern lSplitCommaPattern = Pattern.compile("\\,");

	private Genome mGenome = null;

	private ProteinSet mProteinSet = new ProteinSet();
	private FastaSet mFastaSet;
	
		private Graph<Node, Edge<Node>> mInteractionGraph = new HashGraph<Node, Edge<Node>>();

	public Proteome(Genome pGenome, FastaSet pFastaSet) throws IOException
	{
		mGenome = pGenome;
		mFastaSet = pFastaSet;

		for (Gene lGene : mGenome.getGeneSet().getSet())
		{
			final String lId = lGene.getId();

			FastaSequence lFastaSequence = mFastaSet.getSequenceByName(lId);
			if (lFastaSequence != null)
			{
				Protein lProtein = new Protein(lGene);
				lProtein.setCorrespondingFastaSequence(lFastaSequence);
				mProteinSet.add(lProtein);
			}
		}
	}
	
	public Proteome(FastaSet pFastaSet) throws IOException
	{
		mFastaSet = pFastaSet;

		for (FastaSequence lFastaSequence : mFastaSet.getFastaSequences())
		{
			if (lFastaSequence != null)
			{
				Protein lProtein = new Protein(lFastaSequence.getFastaName());
				lProtein.setCorrespondingFastaSequence(lFastaSequence);
				mProteinSet.add(lProtein);
			}
		}
	}

	public void addInteractions(File pFile) throws IOException
	{
		addInteractions(new FileInputStream(pFile));
	}

	public void addInteractions(InputStream pInputStream) throws IOException
	{
		mInteractionGraph.addGraph(EdgIO.load(pInputStream));
	}

	public ProteinSet getProteinSet()
	{
		return mProteinSet;
	}

	public Graph<Node, Edge<Node>> getInteractionGraph()
	{
		return mInteractionGraph;
	}

	@Override
	public String toString()
	{
		final StringBuilder lStringBuilder = new StringBuilder();
		lStringBuilder.append(mProteinSet);
		lStringBuilder.append("\n");
		lStringBuilder.append(mFastaSet);
		return lStringBuilder.toString();
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((mFastaSet == null) ? 0 : mFastaSet.hashCode());
		result = prime * result + ((mGenome == null) ? 0 : mGenome.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Proteome other = (Proteome) obj;
		if (mFastaSet == null)
		{
			if (other.mFastaSet != null)
				return false;
		}
		else if (!mFastaSet.equals(other.mFastaSet))
			return false;
		if (mGenome == null)
		{
			if (other.mGenome != null)
				return false;
		}
		else if (!mGenome.equals(other.mGenome))
			return false;
		return true;
	}

	public final void write(File pProteomeCache) throws IOException
	{
		SerializationUtils.write(this, pProteomeCache);
	}

	public static final Proteome read(File pProteomeCache) throws IOException,
																												ClassNotFoundException
	{
		if (!pProteomeCache.exists())
		{
			return null;
		}
		else
		{
			final Object lObject = SerializationUtils.read(pProteomeCache);
			return (Proteome) lObject;
		}
	}

}
