package utils.structures.fast.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import utils.structures.fast.set.FastBoundedIntegerSet;

public class FastIntegerGraphNoises
{

	public static boolean rewireOnce(	final Random pRandom,
																		final FastIntegerGraph pIntegerGraph,
																		final int pTries)
	{
		final int[] lNodeSet = pIntegerGraph.getNodeSet().toIntArray();

		if (lNodeSet.length < 4)
		{
			return false;
		}

		int fefn = -1;
		int fesn = -1;
		int sefn = -1;
		int sesn = -1;

		boolean valid = false;
		int tries = 0;
		do
		{

			fefn = lNodeSet[pRandom.nextInt(lNodeSet.length)];
			final FastBoundedIntegerSet fefnNei = pIntegerGraph.getNodeNeighbours(fefn);
			if (fefnNei.size() < 1)
			{
				continue;
			}
			fesn = fefnNei.toIntArray()[pRandom.nextInt(fefnNei.size())];

			sefn = lNodeSet[pRandom.nextInt(lNodeSet.length)];
			final FastBoundedIntegerSet sefnNei = pIntegerGraph.getNodeNeighbours(sefn);
			if (sefnNei.size() < 1)
			{
				continue;
			}
			sesn = sefnNei.toIntArray()[pRandom.nextInt(sefnNei.size())];

			valid = fefn != fesn && sefn != sesn
							&& fefn != sefn
							&& fesn != sesn
							&& fefn != sesn
							&& fesn != sefn;

			valid &= pIntegerGraph.isEdge(fefn, fesn) && pIntegerGraph.isEdge(sefn,
																																				sesn)
								&& !pIntegerGraph.isEdge(fefn, sesn)
								&& !pIntegerGraph.isEdge(sefn, fesn);
			tries++;
		}
		while (!valid && tries < pTries);

		if (valid)
		{
			/*************************************************************************
			 * System.out.println("fefn: " + fefn + ", fesn: " + fesn + ", sefn: " +
			 * sefn + ", sesn: " + sesn);/
			 ************************************************************************/

			pIntegerGraph.removeEdge(fefn, fesn);
			pIntegerGraph.removeEdge(sefn, sesn);

			pIntegerGraph.addEdge(fefn, sesn);
			pIntegerGraph.addEdge(sefn, fesn);
		}

		return valid;
	}

	public static FastIntegerGraph rewireOnce2(	final Random pRandom,
																							final FastIntegerGraph pIntegerGraph)
	{
		final FastIntegerGraph lFastIntegerGraph = new FastIntegerGraph();

		final ArrayList<int[]> lEdgeList = pIntegerGraph.getIntPairList();
		Collections.shuffle(lEdgeList, pRandom);
		final ArrayList<Integer> lFirstNodeList = new ArrayList<Integer>(pIntegerGraph.getNumberOfEdges());
		final ArrayList<Integer> lSecondNodeList = new ArrayList<Integer>(pIntegerGraph.getNumberOfEdges());
		for (int node1 = 0; node1 < pIntegerGraph.mSparseMatrix.size(); node1++)
		{
			for (final int node2 : pIntegerGraph.mSparseMatrix.get(node1)
																												.toIntArray())
			{
				lFirstNodeList.add(node1);
				lSecondNodeList.add(node2);
			}
		}

		Collections.rotate(lSecondNodeList, -1);

		for (int i = 0; i < lFirstNodeList.size(); i++)
		{
			final int node1 = lFirstNodeList.get(i);
			final int node2 = lSecondNodeList.get(i);
			lFastIntegerGraph.addEdge(node1, node2);
		}

		return lFastIntegerGraph;
	}

	public static int rewire(	final Random pRandom,
														final FastIntegerGraph pIntegerGraph,
														final int pRewireSteps)
	{
		double lAverageDegreeBefore = pIntegerGraph.getAverageDegree();
		int lNumberOfSuccesses = 0;
		for (int i = 1; i <= pRewireSteps; i++)
		{
			lNumberOfSuccesses += FastIntegerGraphNoises.rewireOnce(pRandom,
																															pIntegerGraph,
																															10) ? 1 : 0;
		}
		double lAverageDegreeAfter = pIntegerGraph.getAverageDegree();
		assert (lAverageDegreeBefore == lAverageDegreeAfter);
		return lNumberOfSuccesses;
	}

}
