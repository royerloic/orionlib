package utils.math.stats;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Set;
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

		public Test(String pLine,
								double pSet1,
								double pSet2,
								double pInter,
								double pTotal,
								double pThreshold)
		{
			super();
			mLine = pLine;
			set1 = pSet1;
			set2 = pSet2;
			inter = pInter;
			total = pTotal;
			mThreshold = pThreshold;
		}

		public void applyCorrection(double pCorrection)
		{
			mCorrection = pCorrection;
		}

		public double getPValue()
		{
			if (pvalue != -1)
				return pvalue;

			pvalue = hyperg(total, set1, set2, inter, mThreshold) * mCorrection;
			return pvalue;
		}

		public double getBitScore()
		{
			if (pvalue == -1)
				getPValue();

			final double bitscore = -Math.log(pvalue);

			return bitscore;
		}

		public String toTabDel()
		{
			StringBuilder lStringBuilder = new StringBuilder();

			lStringBuilder.append(mLine);
			lStringBuilder.append("\t" + getPValue());
			lStringBuilder.append("\t" + getBitScore());

			return lStringBuilder.toString();
		}
	}

	public static void testFile(File pInputFile,
															int pStartLine,
															int pTestNameIndex,
															int pIndex,
															double pPValueThreshold,
															File pOutputFile) throws IOException
	{
		testStream(	new FileInputStream(pInputFile),
								pStartLine,
								pTestNameIndex,
								pIndex,
								pPValueThreshold,
								new FileOutputStream(pInputFile));
	}

	public static void testStream(InputStream pInputStream,
																int pStartLine,
																int pTestNameIndex,
																int pIndex,
																double pPValueThreshold,
																OutputStream pOutputStream) throws IOException
	{

		final int set1index = pIndex;
		final int set2index = set1index + 1;
		final int interindex = set1index + 2;
		final int totalindex = set1index + 3;
		final double threshold = pPValueThreshold;

		HashSetMap<String, Test> lNameToTestSetsMap = new HashSetMap<String, Test>();

		int lLineIndex = 0;
		for (String lLine : LineReader.getLines(pInputStream))
		{
			if (lLineIndex >= pStartLine)
				if (!lLine.startsWith("//") && !lLine.isEmpty())
				{
					final String[] lTokenArray = lLine.split("\t", -1);
					final String lTestName = lTokenArray[pTestNameIndex];
					final double set1 = Double.parseDouble(lTokenArray[set1index]);
					final double set2 = Double.parseDouble(lTokenArray[set2index]);
					final double inter = Double.parseDouble(lTokenArray[interindex]);
					final double total = Double.parseDouble(lTokenArray[totalindex]);
					Test lTest = new Test(lLine, set1, set2, inter, total, threshold);

					lNameToTestSetsMap.put(lTestName, lTest);
				}
			lLineIndex++;
		}

		Writer lWriter = LineWriter.getWriter(pOutputStream);

		for (Entry<String, Set<Test>> lEntry : lNameToTestSetsMap.entrySet())
		{
			final String lTestName = lEntry.getKey();
			final Set<Test> lTestSet = lEntry.getValue();
			final double lCorrection = lTestSet.size();

			lWriter.append("//\n");
			lWriter.append("//test results for: " + lTestName + "\n");
			lWriter.append("//correction used: " + lCorrection + "*pvalue" + "\n");
			for (Test lTest : lTestSet)
			{
				lTest.applyCorrection(lCorrection);
				if (lTest.getPValue() > pPValueThreshold)
				{
					lWriter.append("// p-value too high, skipped: '" + lTest.toTabDel()
													+ "'\n");
				}
				else
				{
					lWriter.append(lTest.toTabDel() + "\n");
				}
			}
		}

		lWriter.close();

	}

	public final static double hyperg(double total,
																		double set1,
																		double set2,
																		double inter,
																		double threshold)
	{
		final double pvalue = hypergeometricpvalue(	inter,
																								set1,
																								total - set1,
																								set2,
																								threshold);
		/*System.out.println("total=" + total
												+ " set1="
												+ set1
												+ " set2="
												+ set2
												+ " inter="
												+ inter
												+ " ->pvalue="
												+ pvalue);/**/
		return pvalue;
	}

	private final static double hypergeometricpvalue(	double k,
																										double R,
																										double B,
																										double n,
																										double threshold)
	{
		if (k > R)
			k = R;
		if (k > n)
			k = n;

		final double min = Math.min(R, n);
		double sum = 0;

		double kk = k;
		while (kk <= min)
		{
			sum += hypergeometric.density(kk, R, B, n);
			if (sum > threshold)
				break;
			kk++;
		}
		return sum;
	}
}
