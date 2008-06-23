package utils.random.sequence;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.Random;

import utils.io.LineReader;
import utils.io.LineWriter;
import utils.utils.Arrays;
import utils.utils.CmdLine;

public class SequenceRandomizer
{

	static Random mRandom = new Random(System.currentTimeMillis());

	static ContextPreservingSequenceRandomizer mContextPreservingSequenceRandomizer;

	public static void main(final String argstring)
	{
		final String[] args = argstring.split("\\s+");
		main(args);
	}

	/**
	 * @param args
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public static void main(final String[] args)
	{
		try
		{
			System.out.println("Sequence randomizer (Loic Royer)");
			System.out.println("--------------------------------");
			System.out.println("");
			System.out.println("Syntax:");
			System.out.println("java -jar seqrand.jar filein=<inputfile> fileout=<outputfile> type=<1gram/2gramsym>");
			System.out.println("number=<number> iterations=<iterations> burnout=<burnout> format=<txt/fasta> ");
			System.out.println("");
			System.out.println(" <inputfile>: one sequence per line file input file (format=txt) or Fasta file");
			System.out.println(" <outputfile>: one sequence per line file output file (format=txt)");
			System.out.println("               or prefix for ouput files: <outputfile>.i.fasta i in [0..number]");
			System.out.println(" <1gram/2gramsym>: type of randomization");
			System.out.println(" <number>: number of random sequences perinput sequence to output");
			System.out.println(" <iterations>: number of randomizations between two outputs");
			System.out.println(" <burnout>: initial number of randomizations");
			System.out.println(" IMPORTANT: Fasta file must have a last empty line");
			System.out.println(" ");

			final Map<String, String> map = CmdLine.getMap(args);
			final File filein = new File(map.get("filein"));

			System.out.println("Filein = " + filein);

			final String format = map.get("format") == null	? "txt"
																											: map.get("format");

			final int number = map.get("number") == null ? 100
																									: Integer.parseInt(map.get("number"));
			final int iterations = map.get("iterations") == null ? 10
																													: 1 + Math.abs(Integer.parseInt(map.get("iterations")));
			final int burnout = map.get("burnout") == null ? 1000
																										: 1 + Math.abs(Integer.parseInt(map.get("burnout")));

			final String type = map.get("type") == null	? "2gramsym"
																									: map.get("type");

			if (type.equalsIgnoreCase("context"))
			{
				final File cache = new File(map.get("filein") + ".cache");
				if (cache.exists())
				{
					mContextPreservingSequenceRandomizer = ContextPreservingSequenceRandomizer.load(cache);
				}
				else
				{
					final int radius = map.get("radius") == null ? 2
																											: Integer.parseInt(map.get("radius"));
					mContextPreservingSequenceRandomizer = new ContextPreservingSequenceRandomizer(radius);
					mContextPreservingSequenceRandomizer.addSequences(filein, format);
					mContextPreservingSequenceRandomizer.finalizeStatistics();
					mContextPreservingSequenceRandomizer.save(cache);
				}
			}

			System.out.println("type = " + type);
			System.out.println("number = " + number);
			System.out.println("iterations = " + iterations);
			System.out.println("burnout = " + burnout);

			if (format.equalsIgnoreCase("txt"))
			{
				final File fileout = new File(map.get("fileout"));
				System.out.println("Fileout = " + fileout);
				final Writer lWriter = LineWriter.getWriter(fileout);

				for (final String line : LineReader.getLines(filein))
				{
					final char[] array = line.toCharArray();
					char[] rndarray = array;
					for (int i = 0; i < burnout; i++)
					{
						rndarray = randomize(type, rndarray);
					}
					for (int i = 0; i < number; i++)
					{
						for (int j = 0; j < iterations; j++)
						{
							rndarray = randomize(type, rndarray);
						}
						lWriter.append(new String(rndarray));
						lWriter.append("\n");
					}
					lWriter.append("\n");
				}

				lWriter.close();
			}
			else if (format.equalsIgnoreCase("fasta"))
			{
				final String fileoutname = map.get("fileout");

				final File[] filearray = new File[number];
				for (int i = 0; i < number; i++)
				{
					filearray[i] = new File(fileoutname + "." + i + ".fasta");
				}

				final Writer[] writerarray = new Writer[number];
				for (int i = 0; i < number; i++)
				{
					writerarray[i] = LineWriter.getWriter(filearray[i]);
				}

				System.out.println("Output files:");
				for (final File file : filearray)
				{
					System.out.println("\t" + file);
				}

				final StringBuilder builder = new StringBuilder();
				for (final String line : LineReader.getLines(filein))
				{
					if (line.startsWith(">") || line.length() == 0)
					{
						if (builder.length() > 0)
						{
							final char[] array = builder.toString().toCharArray();
							char[] rndarray = array;
							for (int i = 0; i < burnout; i++)
							{
								rndarray = randomize(type, rndarray);
							}
							for (int i = 0; i < number; i++)
							{
								for (int j = 0; j < iterations; j++)
								{
									rndarray = randomize(type, rndarray);
								}
								writerarray[i].append(new String(rndarray));
								writerarray[i].append("\n");
							}
							builder.setLength(0);
						}

						for (int i = 0; i < number; i++)
						{
							writerarray[i].append(line);
							writerarray[i].append("\n");
						}
					}
					else
					{
						builder.append(line);
					}
				}

				for (final Writer writer : writerarray)
				{
					writer.close();
				}
			}

		}
		catch (final IOException e)
		{
			e.printStackTrace();
		}
	}

	private static final char[] randomize(final String type,
																				final char[] pSequence)
	{
		if (type.equalsIgnoreCase("1gram"))
		{
			return oneGramInvariantRandomization(pSequence);
		}
		else if (type.equalsIgnoreCase("2gramsym"))
		{
			return pairInvariantRandomization(pSequence);
		}
		else if (type.equalsIgnoreCase("context"))
		{
			return mContextPreservingSequenceRandomizer.randomize(pSequence);
		}
		throw new RuntimeException("Invalid type: " + type);
	}

	public static final char[] oneGramInvariantRandomization(final char[] pSequence)
	{
		final char[] newarray = Arrays.copyOf(pSequence, pSequence.length);

		final int size = pSequence.length;
		// Shuffle array
		for (int i = size; i > 1; i--)
		{
			swap(newarray, i - 1, mRandom.nextInt(i));
		}

		return newarray;
	}

	public static final char[] pairInvariantRandomization(final char[] pSequence)
	{
		final int rotation1 = 2 + mRandom.nextInt(pSequence.length) * 2 / 3;
		char[] newarray = SequenceRandomizer.rotate(pSequence, rotation1);

		final int size = pSequence.length;
		// Shuffle array
		for (int i = 0; i < size - 1; i++)
		{
			final char a = newarray[i];
			final char b = newarray[i + 1];

			final int otherabindex = indexOf(newarray, b, a, i + 2);
			if (otherabindex >= 0)
			{
				reverse(newarray, i + 1, otherabindex + 1);
			}
		}
		newarray = SequenceRandomizer.rotate(newarray, rotation1);

		return newarray;
	}

	// Utility static methods:

	/**
	 * Swaps the two specified elements in the specified array.
	 */
	private static final void swap(final char[] array, final int i, final int j)
	{
		final char tmp = array[i];
		array[i] = array[j];
		array[j] = tmp;
	}

	/**
	 * Reverses the specified array.
	 */
	static final void reverse(final char[] array, final int start, final int end)
	{
		for (int i = start; i < start + (end - start) / 2; i++)
		{
			final int j = start + end - (i + 1);
			final char tmp = array[i];
			array[i] = array[j];
			array[j] = tmp;
		}
	}

	/**
	 * Rotates the specified array.
	 */
	static final char[] rotate(final char[] array, final int amount)
	{
		final int size = array.length;
		if (amount >= size)
		{
			return array;
		}
		final char[] newarray = new char[array.length];
		System.arraycopy(array, 0, newarray, amount, array.length - amount);
		System.arraycopy(array, array.length - amount, newarray, 0, amount);
		return newarray;
	}

	/**
	 * The source is the character array being searched, and the target is the
	 * string being searched for.
	 * 
	 * @param source
	 *          the characters being searched.
	 * @param target
	 *          the characters being searched for.
	 * @param fromIndex
	 *          the index to begin searching from.
	 */
	static int indexOf(	final char[] source,
											final char a,
											final char b,
											final int fromIndex)
	{

		final int max = source.length - 2;

		for (int i = fromIndex; i <= max; i++)
		{
			/* Look for first character: a */
			if (source[i] != a)
			{
				while (++i <= max && source[i] != a)
				{
					;
				}
			}

			/* Found first character, now look for b */
			if (i + 1 < source.length)
			{
				if (source[i + 1] == b)
				{
					return i;
				}
			}
		}
		return -1;
	}

	/**
	 * The source is the character array being searched, and the target is the
	 * string being searched for.
	 * 
	 * @param source
	 *          the characters being searched.
	 * @param target
	 *          the characters being searched for.
	 * @param fromIndex
	 *          the index to begin searching from.
	 */
	static int indexOf(	final char[] source,
											final char[] target,
											final int fromIndex)
	{

		final char first = target[0];
		final int max = source.length - target.length;

		for (int i = fromIndex; i <= max; i++)
		{
			/* Look for first character. */
			if (source[i] != first)
			{
				while (++i <= max && source[i] != first)
				{
					;
				}
			}

			/* Found first character, now look at the rest of v2 */
			if (i <= max)
			{
				int j = i + 1;
				final int end = j + target.length - 1;
				for (int k = 1; j < end && source[j] == target[k]; j++, k++)
				{
					;
				}

				if (j == end)
				{
					/* Found whole string. */
					return i;
				}
			}
		}
		return -1;
	}

}
