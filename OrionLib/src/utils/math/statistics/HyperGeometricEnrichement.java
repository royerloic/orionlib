package utils.math.statistics;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Collections;
import java.util.Set;
import java.util.Stack;
import java.util.Map.Entry;

import utils.io.LineReader;
import utils.io.LineWriter;
import utils.math.distlib.hypergeometric;
import utils.structures.map.HashSetMap;

public class HyperGeometricEnrichement
{

	private static class Test
	{
		String mLine;
		double set1;
		double set2;
		double inter;
		double total;
		double mThreshold;
		double mCorrection = 1;

		volatile double pvalue = -1;

		public Test(final String pLine,
								final double pSet1,
								final double pSet2,
								final double pInter,
								final double pTotal,
								final double pThreshold)
		{
			super();
			mLine = pLine;
			set1 = pSet1;
			set2 = pSet2;
			inter = pInter;
			total = pTotal;
			mThreshold = pThreshold;
		}

		public void applyCorrection(final double pCorrection)
		{
			mCorrection = pCorrection;
		}

		public double getPValue()
		{
			if (pvalue != -1)
			{
				return pvalue;
			}

			pvalue = hyperG(total, set1, set2, inter, mThreshold) * mCorrection;
			return pvalue;
		}

		public double getBitScore()
		{
			if (pvalue == -1)
			{
				getPValue();
			}

			final double bitscore = -Math.log(pvalue);

			return bitscore;
		}

		public String toTabDel()
		{
			final StringBuilder lStringBuilder = new StringBuilder();

			lStringBuilder.append(mLine);
			lStringBuilder.append("\t" + getPValue());
			lStringBuilder.append("\t" + getBitScore());

			return lStringBuilder.toString();
		}
	}

	public static void testFile(final File pInputFile,
															final int pStartLine,
															final int pTestNameIndex,
															final int pIndex,
															final double pPValueThreshold,
															final File pOutputFile) throws IOException
	{
		testStream(	new FileInputStream(pInputFile),
								pStartLine,
								pTestNameIndex,
								pIndex,
								pPValueThreshold,
								new FileOutputStream(pOutputFile));
	}

	static boolean echo = true;

	public static void testStream(final InputStream pInputStream,
																final int pStartLine,
																final int pTestNameIndex,
																final int pIndex,
																final double pPValueThreshold,
																final OutputStream pOutputStream) throws IOException
	{

		final int set1index = pIndex;
		final int set2index = set1index + 1;
		final int interindex = set1index + 2;
		final int totalindex = set1index + 3;
		final double threshold = pPValueThreshold;

		final HashSetMap<String, Test> lNameToTestSetsMap = new HashSetMap<String, Test>();

		final Writer lWriter = LineWriter.getWriter(pOutputStream);
		String lCurrentTestName = null;

		int lLineIndex = 0;
		for (final String lLine : LineReader.getLines(pInputStream))
		{
			if (lLineIndex >= pStartLine)
			{
				if (!lLine.startsWith("//") && !(lLine.length() == 0))
				{
					final String[] lTokenArray = lLine.split("\t", -1);
					final String lTestName = lTokenArray[pTestNameIndex];

					if (lCurrentTestName == null)
					{
						lCurrentTestName = lTestName;
					}
					else if (!lCurrentTestName.equals(lTestName))
					{
						for (final Entry<String, Set<Test>> lEntry : lNameToTestSetsMap.entrySet())
						{
							final String lTestNameInMap = lEntry.getKey();
							final Set<Test> lTestSet = lEntry.getValue();
							final double lCorrection = lTestSet.size();

							lWriter.append("//\n");
							lWriter.append("//Test results for: " + lTestNameInMap + "\n");
							lWriter.append("//correction used: " + lCorrection
															+ "*pvalue"
															+ "\n");
							for (final Test lTest : lTestSet)
							{
								lTest.applyCorrection(lCorrection);
								if (lTest.getPValue() > pPValueThreshold)
								{
									if (echo)
									{
										lWriter.append("// p-value too high, skipped: '" + lTest.toTabDel()
																		+ "'\n");
									}
								}
								else
								{
									lWriter.append(lTest.toTabDel() + "\n");
								}
							}
						}

						lNameToTestSetsMap.clear();
					}

					lCurrentTestName = lTestName;

					final double set1 = Double.parseDouble(lTokenArray[set1index]);
					final double set2 = Double.parseDouble(lTokenArray[set2index]);
					final double inter = Double.parseDouble(lTokenArray[interindex]);
					final double total = Double.parseDouble(lTokenArray[totalindex]);
					final Test lTest = new Test(lLine,
																			set1,
																			set2,
																			inter,
																			total,
																			threshold);
					lNameToTestSetsMap.put(lTestName, lTest);
				}
			}
			lLineIndex++;
		}

		for (final Entry<String, Set<Test>> lEntry : lNameToTestSetsMap.entrySet())
		{
			final String lTestNameInMap = lEntry.getKey();
			final Set<Test> lTestSet = lEntry.getValue();
			final double lCorrection = lTestSet.size();

			lWriter.append("//\n");
			lWriter.append("//Test results for: " + lTestNameInMap + "\n");
			lWriter.append("//correction used: " + lCorrection + "*pvalue" + "\n");
			for (final Test lTest : lTestSet)
			{
				lTest.applyCorrection(lCorrection);
				if (lTest.getPValue() > pPValueThreshold)
				{
					if (echo)
					{
						lWriter.append("// p-value too high, skipped: '" + lTest.toTabDel()
														+ "'\n");
					}
				}
				else
				{
					lWriter.append(lTest.toTabDel() + "\n");
				}
			}
		}
		/***/

		lWriter.close();

	}
	
	
	public final static double generalizedHyperG(	final double total,
																								double[] setsAsArray,
																								final double inter,
																								final double threshold)
	{
		double pvalue = 1;
		
		for(int i=0; i<setsAsArray.length; i++)
			for(int j=0; j<i; j++)
			{
				double pvaluelocal = hyperG(total, setsAsArray[i], setsAsArray[j], inter, threshold);
				pvalue *= pvaluelocal;
			}

		return pvalue;
	}
	

	public final static double oldgeneralizedHyperG(	final double total,
																								double[] setsAsArray,
																								final double inter,
																								final double threshold)
	{

		Stack<Double> sets = new Stack<Double>();
		for (double lSetSize : setsAsArray)
		{
			sets.add(lSetSize);
		}
		Collections.sort(sets);

		return generalizedHyperGIntern(total, sets, inter, threshold);
	}

	private final static double generalizedHyperGIntern(final double total,
																											Stack<Double> sets,
																											final double inter,
																											final double threshold)
	{

		if (sets.size() == 1)
		{
			return inter <= sets.firstElement() ? 1 : 0;
		}
		else if (sets.size() == 2)
		{
			return hyperG(total,
										sets.firstElement().doubleValue(),
										sets.lastElement().doubleValue(),
										inter,
										threshold);
		}
		else
		{
			sets = (Stack<Double>) sets.clone();

			final int lSmallestSetSize = sets.get(0).intValue();
			final int lLargestSetSize = sets.pop().intValue();
			double pvalue = 0;
			double oldpvalue = 0;
			for (int j = (int) inter; j <= lSmallestSetSize; j++)
			{
				double pvaluelocal = hyperG(total, j, lLargestSetSize, inter, threshold);
				double recursivepvalue = generalizedHyperGIntern(	total,
																													sets,
																													j,
																													threshold / pvaluelocal);

				pvalue += recursivepvalue * pvaluelocal;

				if (pvalue > threshold || oldpvalue == pvalue)
					break;
				oldpvalue = pvalue;
			}

			return pvalue;
		}
	}

	public final static double hyperG(final double total,
																		final double set1,
																		final double set2,
																		final double inter,
																		final double threshold)
	{
		final double pvalue = hypergeometricpvalue(	inter,
																								set1,
																								total - set1,
																								set2,
																								threshold);
		return pvalue;
	}

	private final static double hypergeometricpvalueold(double k,
																											final double R,
																											final double B,
																											final double n,
																											final double threshold)
	{
		return 1 - hypergeometric.cumulative(k, R, B, n);
	}

	private final static double hypergeometricpvalue(	double k,
																										final double R,
																										final double B,
																										final double n,
																										final double threshold)
	{
		if (k > R)
		{
			k = R;
		}
		if (k > n)
		{
			k = n;
		}

		final double min = Math.min(R, n);
		double sum = 0;
		double oldsum = 0;

		double kk = k;
		while (kk <= min)
		{
			sum += hypergeometric.density(kk, R, B, n);

			if (sum > threshold || oldsum == sum)
			{
				break;
			}
			oldsum = sum;
			kk++;
		}
		return sum;
	}
}
