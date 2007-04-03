package org.royerloic.structures.graph.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.royerloic.io.MatrixFile;
import org.royerloic.structures.Matrix;
import org.royerloic.structures.graph.Edge;
import org.royerloic.structures.graph.Graph;
import org.royerloic.structures.graph.HashGraph;
import org.royerloic.structures.graph.Node;
import org.royerloic.structures.graph.UndirectedEdge;
import org.royerloic.structures.graph.io.psimi.PsiMiGraph;
import org.royerloic.structures.graph.io.psimi.PsiMiNode;

/**
 * Loic Royer, Copyright (c) 2005, Some Rights Reserved.
 * 
 */
public class SifIO
{

	/**
	 * @param pFile
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static Graph<Node, Edge<Node>> load(File pFile) throws FileNotFoundException, IOException
	{
		HashGraph<Node, Edge<Node>> lGraph = new HashGraph<Node, Edge<Node>>();

		Map<String, Node> lStringIdToNodeMap = new HashMap<String, Node>();

		Matrix<String> lMatrix = MatrixFile.readMatrixFromFile(pFile, false, "\\t+");


		for (List<String> lStringList : lMatrix)
		{
			String lLineType = lStringList.get(0);
			if (lStringList.size()==3)
			{
				String lNodeName1 = lStringList.get(0);
				String lNodeName2 = lStringList.get(2);

				Node lFirstNode = lStringIdToNodeMap.get(lNodeName1);
				Node lSecondNode = lStringIdToNodeMap.get(lNodeName2);

				if (lFirstNode == null)
				{
					lFirstNode = new Node(lNodeName1);
					lStringIdToNodeMap.put(lNodeName1, lFirstNode);
				}

				if (lSecondNode == null)
				{
					lSecondNode = new Node(lNodeName2);
					lStringIdToNodeMap.put(lNodeName2, lSecondNode);
				}

				
				Edge<Node> lEdge = new UndirectedEdge<Node>(lFirstNode, lSecondNode);
						lGraph.addEdge(lEdge);
			}
		}
	
		return lGraph;
	}

}
