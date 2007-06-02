package utils.ml.bayes.goint;

/*
 * @(#)Echo02.java 1.5 99/02/09
 *
 * Copyright (c) 1998 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Sun grants you ("Licensee") a non-exclusive, royalty free, license to use,
 * modify and redistribute this software in source and binary code form,
 * provided that i) this copyright notice and license appear on all copies of
 * the software; and ii) Licensee does not utilize the software in a manner
 * which is disparaging to Sun.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING
 * OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS
 * LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT,
 * INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF
 * OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 *
 * This software is not designed or intended for use in on-line control of
 * aircraft, air traffic, aircraft navigation or aircraft communications; or in
 * the design, construction, operation or maintenance of any nuclear
 * facility. Licensee represents and warrants that it will not use or
 * redistribute the Software for such purposes.
 */

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Writer;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.imageio.ImageIO;

import org.xml.sax.helpers.DefaultHandler;

import utils.bioinformatics.ontology.IndexMaker;
import utils.bioinformatics.ontology.OboTerm;
import utils.bioinformatics.ontology.go.GoOntology;
import utils.io.MatrixFile;
import utils.structures.DoubleMap;
import utils.structures.HashDoubleMap;
import utils.structures.IntegerMap;
import utils.structures.arraymatrix.ArrayMatrixUtils;
import utils.structures.graph.Edge;
import utils.structures.graph.UndirectedEdge;
import utils.structures.graph.io.psimi.PsiMiGraph;
import utils.structures.graph.io.psimi.PsiMiIO;
import utils.structures.graph.io.psimi.PsiMiNode;
import utils.utils.CmdLine;
import utils.utils.FileUtils;
import utils.utils.Timer;

/**
 * Loic Royer, Copyright (c) 2005, Some Rights Reserved.
 * 
 */
public class IntActModelBuilder extends DefaultHandler
{
	Random										mRandom									= new Random(System.currentTimeMillis());
	GoOntology								mGoOntology;

	File											mIntActFolder;

	Set<Edge<PsiMiNode>>			mInteractingPairSet			= new HashSet<Edge<PsiMiNode>>();
	Set<Edge<PsiMiNode>>			mNonInteractingPairSet	= new HashSet<Edge<PsiMiNode>>();

	DoubleMap<Edge<OboTerm>>	mInteractingGoEdgeDoubleMap;
	DoubleMap<Edge<OboTerm>>	mNonInteractingGoEdgeDoubleMap;
	private boolean						mNonInteracting;

	/**
	 * @param pArgumentsMap
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public IntActModelBuilder(final Map<String, String> pArgumentsMap) throws FileNotFoundException, IOException
	{
		final String lFolderPath = pArgumentsMap.get("folder");
		mIntActFolder = new File(lFolderPath);
		mGoOntology = GoOntology.getUniqueInstance();
	}

	private void build(final boolean pNonInteracting)
	{
		mNonInteracting = pNonInteracting;
		final List<File> lPsiMiFileList = FileUtils.listFiles(mIntActFolder, false);
		System.out.println("Number Of Files = " + lPsiMiFileList.size());
		for (final File lFile : lPsiMiFileList)
			if (lFile.getName().endsWith(".xml"))
				if (!lFile.getName().contains("negative"))
				{
					final PsiMiGraph lPsiMiGraph = PsiMiIO.load(lFile, false);
					if (lPsiMiGraph.getNodeSet().size() < 1300)
					{
						lPsiMiGraph.addGoIdsFromDomains();
						final int lNumberOfInteractingPairsExtracted = extractInteractingPairs(lPsiMiGraph);
						if (mNonInteracting)
							extractNonInteractingPairs(lPsiMiGraph, 2 * lNumberOfInteractingPairsExtracted);
						System.out.println("I=" + mInteractingPairSet.size() + " NI=" + mNonInteractingPairSet.size());
					}
				}
		mInteractingGoEdgeDoubleMap = getGoIdPairProbability(mInteractingPairSet);
		if (mNonInteracting)
			mNonInteractingGoEdgeDoubleMap = getGoIdPairProbability(mNonInteractingPairSet);

		System.out.println("Number of interacting     GO pairs: " + mInteractingGoEdgeDoubleMap.size());
		if (mNonInteracting)
			System.out.println("Number of non-interacting GO pairs: " + mNonInteractingGoEdgeDoubleMap.size());
		System.out.println("Total number of           GO pairs: " + mGoOntology.getNumberOfNodes()
				* mGoOntology.getNumberOfNodes() / 2);

	}

	public void saveModel(final File pFile) throws IOException
	{
		final DecimalFormat lFormat = new DecimalFormat("##.################");
		final Writer lWriter = MatrixFile.getWriterFromFile(pFile);

		for (final Map.Entry<Edge<OboTerm>, Double> lEntry : mInteractingGoEdgeDoubleMap.entrySet())
		{
			final Edge<OboTerm> lEdge = lEntry.getKey();
			final Double lGoPairInInteractionProbability = lEntry.getValue();
			Double lGoPairInNonInteractionProbability = 0.0;
			if (mNonInteracting)
				lGoPairInNonInteractionProbability = mNonInteractingGoEdgeDoubleMap.get(lEdge);
			if (lGoPairInNonInteractionProbability != null)
			{
				Double lLogLikelyHood = 0.0;
				if (mNonInteracting)
					lLogLikelyHood = Math.log(lGoPairInInteractionProbability / lGoPairInNonInteractionProbability);

				final List<String> lLine = new ArrayList<String>();
				if (mNonInteracting)
					lLine.add(lFormat.format(lLogLikelyHood));

				final Double lDepth1 = mGoOntology.getDepth(lEdge.getFirstNode());
				final Double lDepth2 = mGoOntology.getDepth(lEdge.getSecondNode());

				final Double lScore = lGoPairInInteractionProbability * Math.min(lDepth1, lDepth2);

				lLine.add(lFormat.format(lScore));

				if (mNonInteracting)
					lLine.add(lGoPairInNonInteractionProbability.toString());
				lLine.add(lEdge.getFirstNode().toString());
				lLine.add(lEdge.getSecondNode().toString());
				MatrixFile.writeListToStream(lWriter, lLine, "\t");
				lWriter.append("\r\n");
			}
		}
		lWriter.close();

	}

	public void buildHeatMap(final File pModelFile, final File pImageFile) throws NumberFormatException, IOException
	{

		System.out.println("Finding min and max values...");
		double lMinValue = Double.POSITIVE_INFINITY;
		double lMaxValue = Double.NEGATIVE_INFINITY;
		{
			final BufferedReader lBufferedReader = MatrixFile.getBufferedReaderFromFile(pModelFile);
			List<String> lList;
			while ((lList = MatrixFile.readListFromStream(lBufferedReader, "\\t")) != null)
			{
				final double lValue = Double.parseDouble(lList.get(0));

				lMinValue = Math.min(lMinValue, lValue);
				lMaxValue = Math.max(lMaxValue, lValue);
			}
			lBufferedReader.close();
		}

		System.out.println("Reading values...");
		final IndexMaker<OboTerm> lIndexMaker = new IndexMaker<OboTerm>(mGoOntology);
		final List<OboTerm> lRootList = new ArrayList<OboTerm>();
		lRootList.add(new OboTerm(8150)); // BP
		lRootList.add(new OboTerm(5575)); // CC
		lRootList.add(new OboTerm(3674)); // MF
		final Map<OboTerm, Integer> lMap = lIndexMaker.computeMapBreathFirst(lRootList);
		final int lNumberOfNodes = mGoOntology.getNumberOfNodes();
		final byte[][] lMatrix = new byte[lNumberOfNodes][lNumberOfNodes];
		{
			final BufferedReader lBufferedReader = MatrixFile.getBufferedReaderFromFile(pModelFile);

			List<String> lList;
			while ((lList = MatrixFile.readListFromStream(lBufferedReader, "\\t")) != null)
			{
				final double lValue = Double.parseDouble(lList.get(0));
				final double lNormalizedValue = (lValue - lMinValue) / lMaxValue;
				final byte lByteValue = (byte) (lNormalizedValue * 127);

				final String lGoTermString1 = lList.get(1);
				final String lGoTermString2 = lList.get(2);
				final String lIdString1 = lGoTermString1.substring(0, lGoTermString1.indexOf(':'));
				final String lIdString2 = lGoTermString2.substring(0, lGoTermString2.indexOf(':'));
				final Integer lId1 = Integer.parseInt(lIdString1);
				final Integer lId2 = Integer.parseInt(lIdString2);
				final OboTerm lOboTerm1 = new OboTerm(lId1);
				final OboTerm lOboTerm2 = new OboTerm(lId2);

				final Integer lIndex1 = lMap.get(lOboTerm1);
				final Integer lIndex2 = lMap.get(lOboTerm2);

				if ((lIndex1 != null) && (lIndex2 != null))
				{
					lMatrix[lIndex1][lIndex2] = lByteValue;
					lMatrix[lIndex2][lIndex1] = lByteValue;
				}

			}
			lBufferedReader.close();
		}

		// System.out.println(Arrays.toString(lMatrix));

		final int lFactor = 20;
		System.out.println("Downsampling from: " + lNumberOfNodes + "*" + lNumberOfNodes + " by factor:"
				+ lFactor + " ...");
		final byte[][] DownSampledMatrix = ArrayMatrixUtils.downSampleMax(lMatrix, lFactor);

		final int lSize = DownSampledMatrix.length;

		// System.out.println(Arrays.toString(DownSampledMatrix));

		System.out.println("Creating image...");
		final BufferedImage lBufferedImage = new BufferedImage(lSize, lSize, BufferedImage.TYPE_INT_RGB);
		for (int i = 0; i < lSize; i++)
			for (int j = 0; j < lSize; j++)
			{
				final int lValue = DownSampledMatrix[i][j] * 10;
				/***********************************************************************
				 * if (lValue != 0) System.out.println(lValue);/
				 **********************************************************************/
				final int lPixelvalue = lValue + 256 * lValue + 256 * 256 * lValue;
				lBufferedImage.setRGB(i, j, lPixelvalue);

			}

		System.out.println("Writing image...");
		ImageIO.write(lBufferedImage, "png", pImageFile);
	}

	private int extractInteractingPairs(final PsiMiGraph pPsiMiGraph)
	{
		int lCount = 0;
		for (final Edge<PsiMiNode> lEdge : pPsiMiGraph.getEdgeSet())
		{
			mInteractingPairSet.add(lEdge);
			mNonInteractingPairSet.remove(lEdge);
			lCount++;
		}

		return lCount;
	}

	private void extractNonInteractingPairs(final PsiMiGraph pPsiMiGraph, final int pNumberOfInteractingPairsExtracted)
	{
		final List<PsiMiNode> lNodeList = new ArrayList<PsiMiNode>(pPsiMiGraph.getNodeSet());

		final double lDensity = ((double) pPsiMiGraph.getNumberOfEdges())
				/ ((pPsiMiGraph.getNumberOfNodes() * (pPsiMiGraph.getNumberOfNodes() - 1)) / 2);

		if (lDensity < 1)
		{
			int lTimeOut = (int) ((1 / (1 - lDensity)) * pNumberOfInteractingPairsExtracted);
			int lCount = 0;
			while ((lCount < pNumberOfInteractingPairsExtracted) && (lTimeOut > 0))
			{
				final int lIndex1 = mRandom.nextInt(lNodeList.size());
				final int lIndex2 = mRandom.nextInt(lNodeList.size());

				final PsiMiNode lNode1 = lNodeList.get(lIndex1);
				final PsiMiNode lNode2 = lNodeList.get(lIndex2);

				final boolean isInteraction = pPsiMiGraph.isEdge(lNode1, lNode2);

				if (!isInteraction)
				{
					final Edge<PsiMiNode> lEdge = new UndirectedEdge<PsiMiNode>(lNode1, lNode2);
					final boolean isKnownInteraction = mInteractingPairSet.contains(lEdge);
					if (!isKnownInteraction)
					{
						mNonInteractingPairSet.add(lEdge);
						lCount++;
					}
					else
						System.out.println("Interaction allready known:" + lEdge);
				}
				else
					lTimeOut--;
			}
		}

	}

	DoubleMap<Edge<OboTerm>> getGoIdPairProbability(final Set<Edge<PsiMiNode>> pEdgeSet)
	{
		final Timer lTimer = new Timer();
		double lPercent = 0;
		lTimer.start();
		final DoubleMap<Edge<OboTerm>> lGoIdPairToProbability = new HashDoubleMap<Edge<OboTerm>>();
		{
			double lTotalNumberOfGoIdPairs = 0;
			for (final Edge<PsiMiNode> lEdge : pEdgeSet)
			{
				final PsiMiNode lPsiMiNode1 = lEdge.getFirstNode();
				final PsiMiNode lPsiMiNode2 = lEdge.getSecondNode();

				final List<Integer> lGoIdList1 = lPsiMiNode1.getGoIdList();
				final List<Integer> lGoIdList2 = lPsiMiNode2.getGoIdList();

				final List<OboTerm> lGoTermList1 = mGoOntology.getOboTermFromId(lGoIdList1);
				final List<OboTerm> lGoTermList2 = mGoOntology.getOboTermFromId(lGoIdList2);

				final Set<OboTerm> lGoIdListEnriched1 = addRelatedGoIds(lGoTermList1);
				final Set<OboTerm> lGoIdListEnriched2 = addRelatedGoIds(lGoTermList2);

				if (!lGoIdListEnriched1.isEmpty() && !lGoIdListEnriched2.isEmpty())
					for (final OboTerm lGoId1 : lGoIdListEnriched1)
						for (final OboTerm lGoId2 : lGoIdListEnriched2)
						// Double lDepth1 = mGoOntology.getDepth(lGoId1);
						// Double lDepth2 = mGoOntology.getDepth(lGoId2);
						//
						// if (lDepth2 < lDepth1 + 2)
						{
							final Edge<OboTerm> lGoIdEdge = new UndirectedEdge<OboTerm>(lGoId1, lGoId2);
							// System.out.println(lGoIdPair);
							lGoIdPairToProbability.add(lGoIdEdge, 1.0);

							lTotalNumberOfGoIdPairs++;
						}

				lPercent += 1 / ((double) pEdgeSet.size());
				final String lESTString = lTimer.getESTString(lPercent);
				System.out.println(lESTString);
			}
			for (final Map.Entry<Edge<OboTerm>, Double> lEntry : lGoIdPairToProbability.entrySet())
			{
				final double lProbability = lEntry.getValue() / lTotalNumberOfGoIdPairs;
				lEntry.setValue(lProbability);
			}

		}
		lTimer.stop();

		return lGoIdPairToProbability;
	}

	private Set<OboTerm> addRelatedGoIds(final List<OboTerm> pGoTermList)
	{
		final Set<OboTerm> lNewGoTermSet = new HashSet<OboTerm>(pGoTermList);

		for (final OboTerm lGoTerm : pGoTermList)
		{
			final IntegerMap<OboTerm> lGoTermToDepthMap = mGoOntology.getAncestors(lGoTerm);

			if (lGoTermToDepthMap != null)
				for (final Map.Entry<OboTerm, Integer> lEntry : lGoTermToDepthMap.entrySet())
				{
					final OboTerm lNewGoTerm = lEntry.getKey();
					final Integer lDistance = lEntry.getValue();

					lNewGoTermSet.add(lNewGoTerm);

				}
		}
		return lNewGoTermSet;
	}

	/**
	 * @param args
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public static void main(final String[] args)
	{
		try
		{
			final Map<String, String> lArgumentsMap = CmdLine.getMap(args);
			final IntActModelBuilder lIntActModelBuilder = new IntActModelBuilder(lArgumentsMap);
			lIntActModelBuilder.build(false);
			lIntActModelBuilder.saveModel(new File("model.tab.txt"));
			lIntActModelBuilder.buildHeatMap(new File("model.tab.txt"), new File("heatmap.png"));
		}
		catch (final Throwable e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
