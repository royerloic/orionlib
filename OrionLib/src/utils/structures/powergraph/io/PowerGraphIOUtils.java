package utils.structures.powergraph.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import utils.io.MatrixFile;
import utils.structures.graph.Node;
import utils.structures.map.HashSetMap;
import utils.structures.map.SetMap;

public class PowerGraphIOUtils
{

	public PowerGraphIOUtils()
	{
		super();
	}

	public static SetMap<String, Node> getPowerNodes(final File pFile) throws FileNotFoundException,
																																		IOException
	{
		final List<List<String>> lMatrix = MatrixFile.readMatrixFromFile(	pFile,
																																			false);

		final SetMap<String, Node> lPowerNodeNameToSetMap = new HashSetMap<String, Node>();

		for (final List<String> lLine : lMatrix)
			if (lLine.get(0).equalsIgnoreCase("SET"))
			{
				final String lSetName = lLine.get(1);
				lPowerNodeNameToSetMap.put(lSetName);
			}

		for (final Map.Entry<String, Set<Node>> lEntry : lPowerNodeNameToSetMap.entrySet())
		{
			final String lSetName = lEntry.getKey();
			computeSet(lMatrix, lPowerNodeNameToSetMap, lSetName);
		}

		return lPowerNodeNameToSetMap;
	}

	private static void computeSet(	final List<List<String>> pMatrix,
																	final SetMap<String, Node> pPowerNodeNameToSetMap,
																	final String pSetName)
	{
		if (!pPowerNodeNameToSetMap.get(pSetName).isEmpty())
		{
			return;
		}

		HashSet<Node> lHashSet = new HashSet<Node>();

		for (final List<String> lLine : pMatrix)
			if (lLine.get(0).equalsIgnoreCase("IN"))
			{
				final String lNodeOrSetName1 = lLine.get(1);
				final String lNodeOrSetName2 = lLine.get(2);

				if (lNodeOrSetName2.equals(pSetName))
				{
					// First we check if the first name refers to a set or node
					if (pPowerNodeNameToSetMap.get(lNodeOrSetName1) == null)
					{
						final Node lNode = new Node(lNodeOrSetName1);
						lHashSet.add(lNode);
					}
					else
					{
						computeSet(pMatrix, pPowerNodeNameToSetMap, lNodeOrSetName1);
						lHashSet.addAll(pPowerNodeNameToSetMap.get(lNodeOrSetName1));
					}
				}
			}

		pPowerNodeNameToSetMap.put(pSetName, lHashSet);
	}
}
