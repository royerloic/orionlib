package utils.random.sequence;

import java.util.Random;

import utils.utils.Arrays;

public class SequenceRandomization
{


	
	public static final char[] oneGramInvariantRandomization(final Random pRandom, final char[] pSequence)
	{
		final char[] newarray = Arrays.copyOf(pSequence, pSequence.length);

		final int size = pSequence.length;
		// Shuffle array
		for (int i = size; i > 1; i--)
		{
			swap(newarray, i - 1, pRandom.nextInt(i));
		}

		return newarray;
	}

	public static final char[] pairInvariantRandomization(final Random pRandom, final char[] pSequence)
	{
		final int rotation1 = 2 + pRandom.nextInt(pSequence.length) * 2 / 3;
		char[] newarray = rotate(pSequence, rotation1);

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
		newarray = rotate(newarray, rotation1);

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
