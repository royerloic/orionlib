package utils.structures.fast.powergraph.algorythms;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Pattern;

import utils.structures.fast.graph.Edge;
import utils.structures.fast.powergraph.FastPowerGraph;
import utils.structures.map.IntegerHashMap;

public class PowerGraphSpectrum
{

	private PowerGraphSpectrum()
	{
		super();
	}

	public static double[][] getSpectrumFromBblFile(final File pFile)	throws FileNotFoundException,
																																		IOException
	{
		return getSpectrumFromBblStream(new FileInputStream(pFile));
	}

	public static double[][] getSpectrumFromBblStream(final InputStream pInputStream)	throws FileNotFoundException,
																																										IOException
	{
		FastPowerGraph<String> lPowerGraph = FastPowerGraph.readBblStream(pInputStream);

		ArrayList<Edge<String>> lPowerEdgeList = lPowerGraph.getPowerEdgeList();

		System.out.println(lPowerEdgeList);

		ArrayList<int[]> lEdgeSizeList = new ArrayList<int[]>();

		for (Edge<String> lEdge : lPowerEdgeList)
		{
			String lFirstNode = lEdge.getFirstNode();
			String lSecondNode = lEdge.getSecondNode();
			Integer lFirstPowerNodeId = lPowerGraph.getPowerNodeId(lFirstNode);
			Integer lSecondPowerNodeId = lPowerGraph.getPowerNodeId(lSecondNode);
			int lFirstPowerNodeSize = lPowerGraph.getPowerNodeSize(lFirstPowerNodeId);
			int lSecondPowerNodeSize = lPowerGraph.getPowerNodeSize(lSecondPowerNodeId);
			lEdgeSizeList.add(new int[]
			{ lFirstPowerNodeSize, lSecondPowerNodeSize });

		}

		int lMax = 0;
		for (final int[] lEdgeSize : lEdgeSizeList)
		{
			lMax = Math.max(lMax, Math.max(lEdgeSize[0], lEdgeSize[1]));
		}

		final double[][] lMatrix = new double[lMax + 1][lMax + 1];

		for (final int[] lEdgeSize : lEdgeSizeList)
			if (lEdgeSize[0] == lEdgeSize[1])
			{
				lMatrix[lEdgeSize[0]][lEdgeSize[0]] += 1;
			}
			else
			{
				lMatrix[lEdgeSize[0]][lEdgeSize[1]] += 1;
				lMatrix[lEdgeSize[1]][lEdgeSize[0]] += 1;
			}

		return lMatrix;
	}

	public static void writeSpectrumToFile(double[][] pMatrix, File pFile)
	{
		for (int i = 1; i < pMatrix.length; i++)
		{
			for (int j = 1; j < pMatrix.length; j++)
			{
				System.out.print(i + "\t" + j + "\t" + pMatrix[i][j] + "\n");
			}
		}
	}

	public static StringBuffer writeSpectrumToString(double[][] pMatrix)
	{
		StringBuffer lStringBuffer = new StringBuffer();
		for (int i = 1; i < pMatrix.length; i++)
		{
			for (int j = 1; j < pMatrix.length; j++)
			{
				lStringBuffer.append(i + "\t" + j + "\t" + pMatrix[i][j] + "\n");
			}
		}
		return lStringBuffer;
	}

	public static StringBuffer writeFilteredSpectrumToString(	double[][] pMatrix,
																														int pMinSize,
																														int pMinCount)
	{
		StringBuffer lStringBuffer = new StringBuffer();
		for (int i = pMinSize; i < pMatrix.length; i++)
			for (int j = pMinSize; j < pMatrix.length; j++)
				if (pMatrix[i][j] >= pMinCount)
				{
					lStringBuffer.append(i + "\t" + j + "\t" + pMatrix[i][j] + "\n");
				}

		return lStringBuffer;
	}

	/**************************************************************************************/
	public static double[][] getSpectrumFromBblStreamOld(final InputStream pInputStream) throws FileNotFoundException,
																																											IOException
	{
		final HashSet<String> lNodeSet = new HashSet<String>();
		final HashSet<String> lPowerNodeSet = new HashSet<String>();
		final HashSet<String[]> lInSet = new HashSet<String[]>();
		final HashSet<String[]> lEdgeSet = new HashSet<String[]>();

		final IntegerHashMap<String> lPowerNodeToSizeMap = new IntegerHashMap<String>();

		final BufferedReader lBufferedReader = new BufferedReader(new InputStreamReader(pInputStream));
		final Pattern lPattern = Pattern.compile("\t");

		String lLine = null;
		while ((lLine = lBufferedReader.readLine()) != null)
		{
			if (!(lLine.length() == 0) && !lLine.startsWith("#")
					&& !lLine.startsWith("//"))
			{
				final String[] lArray = lPattern.split(lLine);

				if (lArray[0].equals("NODE"))
				{
					lNodeSet.add(lArray[1]);
					lPowerNodeToSizeMap.put(lArray[1], 1);
				}
				else if (lArray[0].equals("SET"))
				{
					lPowerNodeSet.add(lArray[1]);
				}
				else if (lArray[0].equals("IN"))
				{
					lInSet.add(new String[]
					{ lArray[1], lArray[2] });
				}
				else if (lArray[0].equals("EDGE"))
				{
					lEdgeSet.add(new String[]
					{ lArray[1], lArray[2] });
				}
			}
		}

		for (final String[] lStrings : new ArrayList<String[]>(lInSet))
		{
			if (lNodeSet.contains(lStrings[0]))
			{
				lInSet.remove(lStrings);
				lPowerNodeToSizeMap.add(lStrings[1], 1);
			}
		}

		boolean unresolved;
		do
		{
			unresolved = false;
			for (final String[] lStrings : new ArrayList<String[]>(lInSet))
			{
				if (lPowerNodeSet.contains(lStrings[0]) && !lNodeSet.contains(lStrings[0]))
				{
					final Integer lPowerNodeSize = lPowerNodeToSizeMap.get(lStrings[0]);
					if (lPowerNodeSize != null)
					{
						lInSet.remove(lStrings);
						lPowerNodeToSizeMap.add(lStrings[1], lPowerNodeSize);
					}
					else
					{
						unresolved = true;
					}
				}
			}
		}
		while (unresolved);

		int lMax = 0;
		for (final int val : lPowerNodeToSizeMap.values())
		{
			lMax = Math.max(lMax, val);
		}

		final double[][] lMatrix = new double[lMax + 1][lMax + 1];

		for (final String[] lStrings : lEdgeSet)
		{
			final int lSize1 = lPowerNodeToSizeMap.get(lStrings[0]);
			final int lSize2 = lPowerNodeToSizeMap.get(lStrings[1]);

			lMatrix[lSize1][lSize2] += 1;
			lMatrix[lSize2][lSize1] += 1;
		}

		/*
		 * for (int i = 1; i <= 16; i++) { for (int j = 1; j <= 16; j++) {
		 * System.out.print(i + "\t" + j + "\t" + lMatrix[i][j] + "\n"); } }/
		 */

		return lMatrix;

	}

	/** ********************* */

	public static double[] getSpectrumFromBblStreamOLD(final InputStream pInputStream) throws FileNotFoundException,
																																										IOException
	{
		final HashSet<String> lNodeSet = new HashSet<String>();
		final HashSet<String> lPowerNodeSet = new HashSet<String>();
		final HashSet<String[]> lInSet = new HashSet<String[]>();
		final HashSet<String[]> lEdgeSet = new HashSet<String[]>();

		final IntegerHashMap<String> lPowerNodeToSizeMap = new IntegerHashMap<String>();

		final BufferedReader lBufferedReader = new BufferedReader(new InputStreamReader(pInputStream));
		final Pattern lPattern = Pattern.compile("\t");

		String lLine = null;
		while ((lLine = lBufferedReader.readLine()) != null)
		{
			if (!(lLine.length() == 0) && !lLine.startsWith("#")
					&& !lLine.startsWith("//"))
			{
				final String[] lArray = lPattern.split(lLine);

				if (lArray[0].equals("NODE"))
				{
					lNodeSet.add(lArray[1]);
					lPowerNodeToSizeMap.put(lArray[1], 1);
				}
				else if (lArray[0].equals("SET"))
				{
					lPowerNodeSet.add(lArray[1]);
				}
				else if (lArray[0].equals("IN"))
				{
					lInSet.add(new String[]
					{ lArray[1], lArray[2] });
				}
				else if (lArray[0].equals("EDGE"))
				{
					lEdgeSet.add(new String[]
					{ lArray[1], lArray[2] });
				}
			}
		}

		for (final String[] lStrings : new ArrayList<String[]>(lInSet))
		{
			if (lNodeSet.contains(lStrings[0]))
			{
				lInSet.remove(lStrings);
				lPowerNodeToSizeMap.add(lStrings[1], 1);
			}
		}

		boolean unresolved;
		do
		{
			unresolved = false;
			for (final String[] lStrings : new ArrayList<String[]>(lInSet))
			{
				if (lPowerNodeSet.contains(lStrings[0]) && !lNodeSet.contains(lStrings[0]))
				{
					final Integer lPowerNodeSize = lPowerNodeToSizeMap.get(lStrings[0]);
					if (lPowerNodeSize != null)
					{
						lInSet.remove(lStrings);
						lPowerNodeToSizeMap.add(lStrings[1], lPowerNodeSize);
					}
					else
					{
						unresolved = true;
					}
				}
			}
		}
		while (unresolved);

		int lMax = 0;
		for (final int val : lPowerNodeToSizeMap.values())
		{
			lMax = Math.max(lMax, val);
		}

		final double[] lY = new double[2 * lMax];

		// for()

		for (final String[] lStrings : lEdgeSet)
		{
			final int lSize1 = lPowerNodeToSizeMap.get(lStrings[0]);
			final int lSize2 = lPowerNodeToSizeMap.get(lStrings[1]);

			double lSize = 0;
			if (lStrings[0].equals(lStrings[1]))
			{
				lSize = lSize1 * (lSize1 - 1) / 2;
			}
			else
			{
				lSize = lSize1 * lSize2;
			}

			lY[(int) (2 * Math.sqrt(lSize))] += 1;
		}

		/***************************************************************************
		 * for (int i = 0; i < lMatrix.length; i++) for (int j = 0; j <
		 * lMatrix.length; j++) { lMatrix[i][j] /= count; }/
		 **************************************************************************/

		final int columns = 0;
		for (final double lD : lY)
		{
			System.out.print(lD + "\t");
			if (columns > lMax)
			{
				break;
			}
		}
		System.out.println("");

		// System.out.println(Arrays.toString(lMatrix));

		return lY;

	}

}
