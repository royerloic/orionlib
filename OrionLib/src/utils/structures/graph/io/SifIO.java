package utils.structures.graph.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import utils.io.MatrixFile;
import utils.structures.Matrix;
import utils.structures.graph.Edge;
import utils.structures.graph.Graph;
import utils.structures.graph.HashGraph;
import utils.structures.graph.Node;
import utils.structures.graph.UndirectedEdge;

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
	public static Graph<Node, Edge<Node>> load(final File pFile) throws FileNotFoundException,
																															IOException
	{
		final HashGraph<Node, Edge<Node>> lGraph = new HashGraph<Node, Edge<Node>>();

		final Map<String, Node> lStringIdToNodeMap = new HashMap<String, Node>();

		final Matrix<String> lMatrix = MatrixFile.readMatrixFromFile(	pFile,
																																	false,
																																	"\\t+");

		for (final List<String> lStringList : lMatrix)
		{
			final String lLineType = lStringList.get(0);
			if (lStringList.size() == 3)
			{
				final String lNodeName1 = lStringList.get(0);
				final String lNodeName2 = lStringList.get(2);

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

				final Edge<Node> lEdge = new UndirectedEdge<Node>(lFirstNode,
																													lSecondNode);
				lGraph.addEdge(lEdge);
			}
		}

		return lGraph;
	}

}
