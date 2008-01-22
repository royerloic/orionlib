package utils.bioinformatics.proteome.simcorr;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import utils.bioinformatics.genome.FastaSequence;
import utils.bioinformatics.genome.FastaSet;
import utils.bioinformatics.genome.Genome;
import utils.bioinformatics.jaligner.Alignment;
import utils.bioinformatics.jaligner.Sequence;
import utils.bioinformatics.jaligner.SmithWatermanGotoh;
import utils.bioinformatics.jaligner.matrix.MatrixLoader;
import utils.bioinformatics.jaligner.util.SequenceParser;
import utils.bioinformatics.proteome.Domain;
import utils.bioinformatics.proteome.InterProScanReaderYeast;
import utils.bioinformatics.proteome.Protein;
import utils.bioinformatics.proteome.Proteome;
import utils.bioinformatics.proteome.test.DomainIndex;
import utils.structures.HashMapMap;
import utils.structures.HashSetMap;
import utils.structures.graph.Node;

public class MainYeast
{

	@Test
	public void main() throws IOException, ClassNotFoundException
	{

		File lProteomeCache = new File("YeastProteome.bin");

		Proteome lProteome = null;
		try
		{
			lProteome = Proteome.read(lProteomeCache);
			if (lProteome == null)
			{

				Genome lGenome = null;
				final File lGenomeFile = new File("G:/Projects/PowerGraphs/Data/Yeast/saccharomyces_cerevisiae.gff");
				lGenome = new Genome(lGenomeFile);

				final File lYeastProteinSequencesFile = new File("G:/Projects/PowerGraphs/Data/Yeast/orf_trans_all.fasta");
				FastaSet lFastaSet = new FastaSet(lYeastProteinSequencesFile);
				lProteome = new Proteome(lGenome, lFastaSet);

				final File lBioGridInteractionsFile = new File("G:/Projects/PowerGraphs/Data/Yeast/yeast_interologome--HPRD--seqid60_hitcov60_1e-5.tab");
				lProteome.addInteractions(lBioGridInteractionsFile);

				final File lInterProScanFile = new File("G:/Projects/PowerGraphs/Data/Yeast/domains.tab");
				InterProScanReaderYeast.addDomainsTo(lProteome, lInterProScanFile);

				lProteome.write(lProteomeCache);

				// System.out.println(lProteome);

			}

			assertTrue(lProteome.getProteinSet().getNumberOfProteins() == 6719);
			assertNotNull(lProteome	.getProteinSet()
															.getProteinById("YAL065C")
															.getCorrespondingFastaSequence());

			System.out.println(lProteome.getInteractionGraph().getNumberOfNodes());
			System.out.println(lProteome.getInteractionGraph().getNumberOfEdges());

			DomainIndex lDomainIndex = new DomainIndex(lProteome);
			lDomainIndex.index();

			final FileWriter lFileWriter = new FileWriter("test.txt");
			final PrintWriter lPrintWriter = new PrintWriter(lFileWriter);

			/*************************************************************************
			 * final String lSH2 = "IPR000980"; final String lSH3 = "IPR001452"; final
			 * String lPDZ = "IPR001478"; collectFor(lProteome, lDomainIndex, lPDZ,
			 * lPrintWriter);/
			 ************************************************************************/

			Set<String> lDomainInterProIdSet = lDomainIndex.getDomainInterProIdSet();
			for (String lInterProId : lDomainInterProIdSet)
			{
				collectFor(lProteome, lDomainIndex, lInterProId, lPrintWriter);
			}/**/

		}
		catch (Throwable e)
		{
			e.printStackTrace();
			fail("Exception: " + e);
		}
	}

	public void collectFor(	Proteome pProteome,
													DomainIndex pDomainIndex,
													String pInterProDomainId,
													PrintWriter pPrintWriter)	throws IOException,
																										ClassNotFoundException
	{
		final Set<Protein> lProteinSet = pDomainIndex.getProteinByDomainInterproId(pInterProDomainId);
		assertNotNull(lProteinSet);
		// System.out.println(lProteinSet);

		HashMap<FastaSequence, Protein> lSequenceToProteinMap = new HashMap<FastaSequence, Protein>();
		ArrayList<FastaSequence> lSequenceList = new ArrayList<FastaSequence>();
		for (Protein lProtein : lProteinSet)
		// if (lProtein.getNumberOfDistinctDomains() == 1)
		{
			Set<Domain> lDomainSet = lProtein.getDomainsByInterProId(pInterProDomainId);
			System.out.println("protein has: " + lProtein	.getDomainMap()
																										.keySet()
																										.size()
													+ " distinct domains");

			for (Domain lDomain : lDomainSet)
				if (lDomain.getSource().equals("HMMPfam"))
					if (lDomain.getEValue() < 1E-5)

					{
						final FastaSequence lFastaSequence = lDomain.getCorrespondingFastaSequence();
						lSequenceToProteinMap.put(lFastaSequence, lProtein);
						lSequenceList.add(lFastaSequence);

						System.out.println("Domain:");
						System.out.println(lDomain);
						System.out.println(lFastaSequence);
						System.out.println("");

					}

		}

		File lSequenceSimilarityCache = new File("SequenceSimilarity.bin");
		HashMapMap<Protein, Protein, Double> lSequenceSimilarityMap = new HashMapMap<Protein, Protein, Double>();
		try
		{
			for (int i = 0; i < lSequenceList.size(); i++)
				for (int j = 0; j < i; j++)
				{
					final FastaSequence lFastaSequence1 = lSequenceList.get(i);
					final FastaSequence lFastaSequence2 = lSequenceList.get(j);

					Sequence lSequence1 = SequenceParser.parse(lFastaSequence1.getSequenceString());
					Sequence lSequence2 = SequenceParser.parse(lFastaSequence2.getSequenceString());

					Alignment lAlignment = SmithWatermanGotoh.align(lSequence1,
																													lSequence2,
																													MatrixLoader.load("BLOSUM70"),
																													10f,
																													0.5f);

					final double lLength = Math.min(lSequence1.length(),
																					lSequence2.length());
					double lSimilarity = lAlignment.getSimilarity() / lLength;

					final Protein lProtein1 = lSequenceToProteinMap.get(lFastaSequence1);
					final Protein lProtein2 = lSequenceToProteinMap.get(lFastaSequence2);

					if (!lProtein1.equals(lProtein2))
					{
						final Double lExistingSimilarity = lSequenceSimilarityMap.get(lProtein1,
																																					lProtein2);
						if (lExistingSimilarity != null)
						{
							lSimilarity = Math.max(lSimilarity, lExistingSimilarity);
						}
						lSequenceSimilarityMap.put(lProtein1, lProtein2, lSimilarity);
						lSequenceSimilarityMap.put(lProtein2, lProtein1, lSimilarity);
					}
				}

		}
		catch (Throwable e)
		{
			e.printStackTrace();
		}

		System.out.println(lSequenceSimilarityMap);

		HashSetMap<Protein, Protein> lProteinNeighboorsSetMap = new HashSetMap<Protein, Protein>();
		for (Protein lProtein : lProteinSet)
		{
			Node lNode = new Node(lProtein.getId());
			Set<Node> lNodeSet = pProteome.getInteractionGraph()
																		.getNodeNeighbours(lNode, 1);

			for (Node lNodeNeighboor : lNodeSet)
			{
				final Protein lProteinNeighboor = pProteome	.getProteinSet()
																										.getProteinById(lNodeNeighboor.getName());
				lProteinNeighboorsSetMap.put(lProtein, lProteinNeighboor);
			}
		}

		HashMapMap<Protein, Protein, Double> lInteractorsSimilarityMap = new HashMapMap<Protein, Protein, Double>();
		ArrayList<Protein> lProteinList = new ArrayList<Protein>(lProteinNeighboorsSetMap.keySet());

		double lMaxIntSim = 0;
		for (int i = 0; i < lProteinList.size(); i++)
			for (int j = 0; j < i; j++)
			{
				final Protein lProtein1 = lProteinList.get(i);
				final Protein lProtein2 = lProteinList.get(j);

				final Set<Protein> lProteinSet1 = lProteinNeighboorsSetMap.get(lProtein1);
				final Set<Protein> lProteinSet2 = lProteinNeighboorsSetMap.get(lProtein2);

				final HashSet<Protein> lIntersection = new HashSet<Protein>();
				lIntersection.addAll(lProteinSet1);
				lIntersection.retainAll(lProteinSet2);

				final double lIntersectionSize = lIntersection.size();
				final double lUnionSize = lProteinSet1.size() + lProteinSet2.size()
																	- lIntersectionSize;

				final double lJacquard = lIntersectionSize / lUnionSize;

				lMaxIntSim = Math.max(lMaxIntSim, lJacquard);

				lInteractorsSimilarityMap.put(lProtein1, lProtein2, lJacquard);
				lInteractorsSimilarityMap.put(lProtein2, lProtein1, lJacquard);
			}

		System.out.println(lInteractorsSimilarityMap);

		for (int i = 0; i < lProteinList.size(); i++)
			for (int j = 0; j < i; j++)
			{
				final Protein lProtein1 = lProteinList.get(i);
				final Protein lProtein2 = lProteinList.get(j);

				final Double lSeqSim = lSequenceSimilarityMap.get(lProtein1, lProtein2);
				final Double lIntSim = lInteractorsSimilarityMap.get(	lProtein1,
																															lProtein2);

				if (lSeqSim != null)
				{
					pPrintWriter.println(lProtein1.getId() + "\t"
																+ lProtein2.getId()
																+ "\t"
																+ lSeqSim
																+ "\t"
																+ lIntSim
																+ "\t"
																+ lProtein1.getNumberOfDistinctDomains()
																+ "\t"
																+ lProtein2.getNumberOfDistinctDomains()
																+ "\t"
																+ lMaxIntSim);
				}

			}

	}
}
