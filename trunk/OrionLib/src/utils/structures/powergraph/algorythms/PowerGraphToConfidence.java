package utils.structures.powergraph.algorythms;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import utils.io.LineReader;
import utils.io.MatrixFile;
import utils.structures.ArrayMatrix;
import utils.structures.Matrix;
import utils.structures.graph.Edge;
import utils.structures.graph.UndirectedEdge;
import utils.structures.powergraph.PowerGraph;

public class PowerGraphToConfidence<N>
{

	final PowerGraph<N> mPowerGraph;
	private int mTotalNumberOfEdges;
	private int mMaximalPowerEdgeSize;

	public PowerGraphToConfidence(final PowerGraph<N> pPowerGraph)
	{
		super();
		this.mPowerGraph = pPowerGraph;
		this.mTotalNumberOfEdges = pPowerGraph.getNumberOfEdges();
		this.mMaximalPowerEdgeSize = pPowerGraph.getMaximalPowerEdgeSize();
	}

	public final double getConfidenceOld(final N pFirstNode, final N pSecondNode)
	{
		final List<Set<N>> lSetList1 = this.mPowerGraph.getAllPowerNodeContaining(pFirstNode);
		final List<Set<N>> lSetList2 = this.mPowerGraph.getAllPowerNodeContaining(pSecondNode);

		double lSize = 0;
		for (final Set<N> lSet1 : lSetList1)
			for (final Set<N> lSet2 : lSetList2)
			{
				final boolean isPowerEdge = this.mPowerGraph.isPowerEdge(lSet1, lSet2);
				if (isPowerEdge)
					if (lSet1.equals(lSet2))
						lSize += lSet1.size() * (lSet1.size() - 1) / 2;
					else
						lSize += lSet1.size() * lSet2.size();
			}

		final double lConfidence = lSize / this.mMaximalPowerEdgeSize;

		return lConfidence;
	}

	public final double getConfidence(final N pFirstNode, final N pSecondNode)
	{
		final List<Set<N>> lSetList1 = this.mPowerGraph.getAllPowerNodeContaining(pFirstNode);
		final List<Set<N>> lSetList2 = this.mPowerGraph.getAllPowerNodeContaining(pSecondNode);

		final double lSize = 0;
		for (final Set<N> lSet1 : lSetList1)
		{

		}

		final double lConfidence = lSize / this.mMaximalPowerEdgeSize;

		return lConfidence;
	}

	public final void dumpEdgeConfidenceToFile(final File pFile) throws FileNotFoundException,
																															IOException
	{
		final Matrix<String> lMatrix = new ArrayMatrix<String>();

		for (final Edge<Set<N>> lPowerEdge : this.mPowerGraph.getPowerEdgeSet())
			for (final N lNode1 : lPowerEdge.getFirstNode())
				for (final N lNode2 : lPowerEdge.getSecondNode())
					if (!lNode1.equals(lNode2))
					{
						final Double lConfidence = getConfidence(lNode1, lNode2);
						final List<String> lList = new ArrayList<String>();
						lList.add("EDGE");
						lList.add(lNode1.toString());
						lList.add(lNode2.toString());
						lList.add(lConfidence.toString());
						lMatrix.add(lList);
					}

		MatrixFile.writeMatrixToFile(lMatrix, pFile);
	}

	public final void joinConfidenceFiles(final File pFile1,
																				final int[] pIndices1,
																				final File pFile2,
																				final int[] pIndices2,
																				final File pResultFile)	throws FileNotFoundException,
																																IOException
	{
		final Matrix<String> lMatrix1 = MatrixFile.readMatrixFromFile(pFile1,
																																	"\\s+");
		final Matrix<String> lMatrix2 = MatrixFile.readMatrixFromFile(pFile2,
																																	"\\s+");

		final Map<Edge<String>, Double> lMap1 = new HashMap<Edge<String>, Double>();
		final Map<Edge<String>, Double> lMap2 = new HashMap<Edge<String>, Double>();

		for (final List<String> lList : lMatrix1)
			try
			{
				final String lNode1 = lList.get(pIndices1[0]);
				final String lNode2 = lList.get(pIndices1[1]);
				final Double lConfidence = Double.parseDouble(lList.get(pIndices1[2]));
				final Edge<String> lEdge = new UndirectedEdge<String>(lNode1, lNode2);
				lMap1.put(lEdge, lConfidence);
			}
			catch (final Throwable lThrowable)
			{
				lThrowable.printStackTrace();
			}

		for (final List<String> lList : lMatrix2)
			try
			{
				final String lNode1 = lList.get(pIndices2[0]);
				final String lNode2 = lList.get(pIndices2[1]);
				final Double lConfidence = Double.parseDouble(lList.get(pIndices2[2]));
				final Edge<String> lEdge = new UndirectedEdge<String>(lNode1, lNode2);
				lMap2.put(lEdge, lConfidence);
			}
			catch (final Throwable lThrowable)
			{
				lThrowable.printStackTrace();
			}

		final Matrix<String> lResultMatrix = new ArrayMatrix<String>();

		for (final Entry<Edge<String>, Double> lEntry : lMap1.entrySet())
		{
			final Edge<String> lEdge = lEntry.getKey();
			final Double lConfidence1 = lEntry.getValue();
			final Double lConfidence2 = lMap2.get(lEdge);
			if (lConfidence2 != null)
			{
				final List<String> lList = new ArrayList<String>();
				lList.add("EDGE");
				lList.add(lEdge.getFirstNode());
				lList.add(lEdge.getSecondNode());
				lList.add(lConfidence1.toString());
				lList.add(lConfidence2.toString());
				lResultMatrix.add(lList);
			}

		}

		MatrixFile.writeMatrixToFile(lResultMatrix, pResultFile);
	}

}
