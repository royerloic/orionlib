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
		final HashSet<String> lNodeSet = new HashSet<String>();
		final HashSet<String> lPowerNodeSet = new HashSet<String>();
		final HashSet<String[]> lInSet = new HashSet<String[]>();
		final HashSet<String[]> lEdgeSet = new HashSet<String[]>();

		final IntegerHashMap<String> lPowerNodeToSizeMap = new IntegerHashMap<String>();

		BufferedReader lBufferedReader = new BufferedReader(new InputStreamReader(pInputStream));
		Pattern lPattern = Pattern.compile("\t");

		String lLine = null;
		while ((lLine = lBufferedReader.readLine()) != null)
			if (!lLine.isEmpty() && !lLine.startsWith("#") && !lLine.startsWith("//"))
			{
				String[] lArray = lPattern.split(lLine);

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

		for (String[] lStrings : new ArrayList<String[]>(lInSet))
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
			for (String[] lStrings : new ArrayList<String[]>(lInSet))
			{
				if (lPowerNodeSet.contains(lStrings[0]) && !lNodeSet.contains(lStrings[0]))
				{
					Integer lPowerNodeSize = lPowerNodeToSizeMap.get(lStrings[0]);
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
		for (int val : lPowerNodeToSizeMap.values())
		{
			lMax = Math.max(lMax, val);
		}

		double[][] lMatrix = new double[lMax + 1][lMax + 1];

		for (String[] lStrings : lEdgeSet)
		{
			final int lSize1 = lPowerNodeToSizeMap.get(lStrings[0]);
			final int lSize2 = lPowerNodeToSizeMap.get(lStrings[1]);

			lMatrix[lSize1][lSize2] += 1;
			lMatrix[lSize2][lSize1] += 1;
		}

		for (int i = 1; i <= 16; i++)
		{
			for (int j = 1; j <= 16; j++)
			{
				System.out.print(i + "\t" + j + "\t" + lMatrix[i][j] + "\n");
			}
		}

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

		BufferedReader lBufferedReader = new BufferedReader(new InputStreamReader(pInputStream));
		Pattern lPattern = Pattern.compile("\t");

		String lLine = null;
		while ((lLine = lBufferedReader.readLine()) != null)
			if (!lLine.isEmpty() && !lLine.startsWith("#") && !lLine.startsWith("//"))
			{
				String[] lArray = lPattern.split(lLine);

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

		for (String[] lStrings : new ArrayList<String[]>(lInSet))
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
			for (String[] lStrings : new ArrayList<String[]>(lInSet))
			{
				if (lPowerNodeSet.contains(lStrings[0]) && !lNodeSet.contains(lStrings[0]))
				{
					Integer lPowerNodeSize = lPowerNodeToSizeMap.get(lStrings[0]);
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
		for (int val : lPowerNodeToSizeMap.values())
		{
			lMax = Math.max(lMax, val);
		}

		double[] lX = new double[2 * lMax];
		double[] lY = new double[2 * lMax];

		// for()

		for (String[] lStrings : lEdgeSet)
		{
			final int lSize1 = lPowerNodeToSizeMap.get(lStrings[0]);
			final int lSize2 = lPowerNodeToSizeMap.get(lStrings[1]);

			double lSize = 0;
			if (lStrings[0].equals(lStrings[1]))
			{
				lSize = (lSize1 * (lSize1 - 1)) / 2;
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

		int columns = 0;
		for (double lD : lY)
		{
			System.out.print(lD + "\t");
			if (columns > lMax)
				break;
		}
		System.out.println("");

		// System.out.println(Arrays.toString(lMatrix));

		return lY;

	}

}
