package utils.random.sequence;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import utils.io.LineReader;
import utils.utils.Arrays;

public class ContextPreservingSequenceRandomizer implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	static Random mRandom = new Random(System.currentTimeMillis());

	HashMap<Context, CharDistribution> mContext2DistributionMap = new HashMap<Context, CharDistribution>(100000);

	private final int mRadius;

	public static class Context implements Serializable
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		public char[] left;
		public char[] right;

		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			for (final char element : left)
			{
				result = prime * result + element;
			}
			for (final char element : right)
			{
				result = prime * result + element;
			}
			return result;
		}

		@Override
		public boolean equals(final Object obj)
		{
			if (this == obj)
			{
				return true;
			}
			if (obj == null)
			{
				return false;
			}
			final Context other = (Context) obj;
			if (!Arrays.equals(left, other.left))
			{
				return false;
			}
			if (!Arrays.equals(right, other.right))
			{
				return false;
			}
			return true;
		}

		@Override
		public String toString()
		{
			return Arrays.toString(left) + "_" + Arrays.toString(right);
		}
	}

	public static class CharDistribution implements Serializable
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		public HashMap<Character, Double> mDistribution = new HashMap<Character, Double>(128);
		public char[] mCumulativeDistributionX;
		public double[] mCumulativeDistributionY;

		public void update(final char pChar)
		{
			Double count = mDistribution.get(pChar);
			if (count == null)
			{
				count = 0d;
			}
			count++;
			mDistribution.put(pChar, count);
		}

		public void normalize()
		{
			double sum = 0;
			for (final Entry<Character, Double> lEntry : mDistribution.entrySet())
			{
				sum += lEntry.getValue();
			}
			for (final Entry<Character, Double> lEntry : mDistribution.entrySet())
			{
				lEntry.setValue(lEntry.getValue() / sum);
			}
		}

		public void cumulate()
		{
			double sum = 0;
			final ArrayList<Character> lListX = new ArrayList<Character>(mDistribution.size() + 2);
			final ArrayList<Double> lListY = new ArrayList<Double>(mDistribution.size() + 2);
			for (final Entry<Character, Double> lEntry : mDistribution.entrySet())
			{
				sum += lEntry.getValue();

				lListX.add(lEntry.getKey());
				lListY.add(sum);
			}
			mCumulativeDistributionX = new char[lListX.size()];
			mCumulativeDistributionY = new double[lListY.size()];
			for (int i = 0; i < lListX.size(); i++)
			{
				mCumulativeDistributionX[i] = lListX.get(i);
				mCumulativeDistributionY[i] = lListY.get(i);
			}
		}

		public char getRandomChar()
		{
			final int index = Arrays.binarySearch(mCumulativeDistributionY,
																						mRandom.nextDouble());

			if (index >= 0)
			{
				return mCumulativeDistributionX[index];
			}
			else
			{
				return mCumulativeDistributionX[-(index + 1)];
			}

		}

		@Override
		public String toString()
		{
			return mDistribution.toString();
		}
	}

	public ContextPreservingSequenceRandomizer(final int pRadius)
	{
		super();
		mRadius = pRadius;
	}

	public void addSequences(final File pFilein, final String format) throws IOException
	{
		if (format.equalsIgnoreCase("txt"))
		{
			for (final String line : LineReader.getLines(pFilein))
			{
				final char[] array = line.toCharArray();
				addSequenceToStatistics(array);
			}
		}
		else if (format.equalsIgnoreCase("fasta"))
		{

			final StringBuilder builder = new StringBuilder();
			for (final String line : LineReader.getLines(pFilein))
			{
				if (line.startsWith(">") || line.length() == 0)
				{
					if (builder.length() > 0)
					{
						final char[] array = builder.toString().toCharArray();
						addSequenceToStatistics(array);
						builder.setLength(0);
					}

				}
				else
				{
					builder.append(line);
				}
			}
		}

	}

	public void addSequenceToStatistics(final char[] pSequence)
	{
		for (int i = mRadius; i < pSequence.length - mRadius; i++)
		{
			final Context lContext = new Context();
			lContext.left = Arrays.copyOfRange(pSequence, i - mRadius, i);
			lContext.right = Arrays.copyOfRange(pSequence, i + 1, i + 1 + mRadius);

			CharDistribution dist = mContext2DistributionMap.get(lContext);
			if (dist == null)
			{
				dist = new CharDistribution();
				mContext2DistributionMap.put(lContext, dist);
			}

			dist.update(pSequence[i]);
		}
	}

	public void finalizeStatistics()
	{
		for (final Entry<Context, CharDistribution> lEntry : mContext2DistributionMap.entrySet())
		{
			lEntry.getValue().normalize();
			lEntry.getValue().cumulate();
		}
	}

	public char[] randomize(final char[] pSequence)
	{
		final int rotation1 = 2 + mRandom.nextInt(pSequence.length) * 2 / 3;
		final char[] newarray = SequenceRandomization.rotate(pSequence, rotation1);

		final ArrayList<Integer> indexlist = new ArrayList<Integer>(pSequence.length);
		for (int i = mRadius; i < pSequence.length - mRadius; i++)
		{
			indexlist.add(i);
		}
		Collections.shuffle(indexlist);
		for (final int index : indexlist)
		{
			final Context lContext = new Context();
			lContext.left = Arrays.copyOfRange(newarray, index - mRadius, index);
			lContext.right = Arrays.copyOfRange(newarray, index + 1, index + 1
																																+ mRadius);

			final CharDistribution lCharDistribution = mContext2DistributionMap.get(lContext);

			if (lCharDistribution != null)
			{
				newarray[index] = lCharDistribution.getRandomChar();
			}
		}
		return newarray;
	}

	public static ContextPreservingSequenceRandomizer load(final File pCache) throws IOException
	{
		try
		{
			final FileInputStream lFileInputStream = new FileInputStream(pCache);
			final GZIPInputStream lGZIPInputStream = new GZIPInputStream(lFileInputStream);
			final ObjectInputStream lObjectInputStream = new ObjectInputStream(lGZIPInputStream);
			final ContextPreservingSequenceRandomizer obj = (ContextPreservingSequenceRandomizer) lObjectInputStream.readObject();
			return obj;
		}
		catch (final ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public void save(final File pCache) throws IOException
	{
		final FileOutputStream lFileOutputStream = new FileOutputStream(pCache);
		final GZIPOutputStream lGZIPOutputStream = new GZIPOutputStream(lFileOutputStream);
		final ObjectOutputStream lObjectOutputStream = new ObjectOutputStream(lGZIPOutputStream);
		lObjectOutputStream.writeObject(this);
		lObjectOutputStream.flush();
		lObjectOutputStream.close();
	}

}
