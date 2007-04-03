package org.royerloic.structures.graph.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.royerloic.structures.graph.Edge;
import org.royerloic.structures.graph.Graph;
import org.royerloic.structures.graph.HashGraph;
import org.royerloic.structures.graph.Node;
import org.royerloic.structures.graph.io.psimi.PsiMiIO;
import org.royerloic.structures.powergraph.PowerGraph;
import org.royerloic.structures.powergraph.algorythms.PowerGraphExtractor;
import org.royerloic.structures.powergraph.io.PsiMiPowerGraphIO;

/**
 * Loic Royer, Copyright (c) 2005, Some Rights Reserved.
 * 
 */
public class GraphLoader
{
	/**
	 * @param pFile
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static Graph<Node, Edge<Node>> loadGraph(File pFile) throws FileNotFoundException, IOException
	{
		return loadGraph(pFile, true,null);
	}

	public static Graph<Node, Edge<Node>> loadGraph(File pFile, boolean pSpokeModel, String pConfidenceFilter)
			throws FileNotFoundException, IOException
	{
		Graph lGraph = (Graph<Node, Edge<Node>>) new HashGraph<Node, Edge<Node>>();
		if (isPsiMi(pFile))
		{
			lGraph = PsiMiIO.load(pFile, pSpokeModel,pConfidenceFilter);
		}
		else if (isEdg(pFile))
		{
			lGraph = EdgIO.load(pFile);
		}
		else if (isSif(pFile))
		{
			lGraph = SifIO.load(pFile);
		}
			
		EdgIO.save(lGraph, new File("dump.edg"));
		return lGraph;
	}

//	public static Graph<Node, Edge<Node>> loadGraph(File pFile, boolean pSpokeModel)
//			throws FileNotFoundException, IOException
//	{
//		return loadGraph(pFile, pSpokeModel, null);
//	}

	public static boolean isPsiMi(File pFile)
	{
		return pFile.getName().endsWith(".xml");
	}

	public static boolean isEdg(File pFile)
	{
		return pFile.getName().endsWith(".edg");
	}

	private static boolean isSif(File pFile)
	{
		return pFile.getName().endsWith(".sif");
	}
	
	public static PowerGraph<Node> loadPowerGraph(File pFile,
																								boolean pSpokeModel,
																								double pDensityThreshold,
																								boolean pKeepAsInPsiMi,
																								String pConfidenceFilter) throws FileNotFoundException,
			IOException
	{
		if (isPsiMi(pFile) && pKeepAsInPsiMi)
		{
			System.out
					.println("File is PsiMi, using special PsiMi loader (does not reduce representation to individual interactions)");
			PsiMiPowerGraphIO lPsiMiPowerGraphIO = new PsiMiPowerGraphIO();
			PowerGraph<Node> lPowerGraph = lPsiMiPowerGraphIO.load(pFile, pSpokeModel);
			return lPowerGraph;
		}
		else if (isEdg(pFile) || (isPsiMi(pFile) && !pKeepAsInPsiMi))
		{
			System.out.println("File is Edg, using standard Edg file loader...");
			Graph<Node, Edge<Node>> lGraph = loadGraph(pFile, pSpokeModel,pConfidenceFilter);
			PowerGraphExtractor<Node> lPowerGraphExtractor = new PowerGraphExtractor<Node>();
			PowerGraph<Node> lPowerGraph = lPowerGraphExtractor.extractPowerGraph(lGraph, pDensityThreshold);
			return lPowerGraph;
		}
		return null;
	}

}
