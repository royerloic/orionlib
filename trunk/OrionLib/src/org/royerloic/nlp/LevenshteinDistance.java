package org.royerloic.nlp;

public class LevenshteinDistance
{

	private static final double minimum(final double a, final double b, final double c)
	{
		if (a <= b && a <= c)
			return a;
		if (b <= a && b <= c)
			return b;
		return c;
	}

	public static final double max(final double a, final double b)
	{
		return (a >= b) ? a : b;
	}

	public static final double similarity(final String str1, final String str2)
	{
		double lLevenshteinDistance = distance(str1.toCharArray(), str2.toCharArray());
		double lMaxLength = max(str1.length(), str2.length());
		double lLevenshteinSimilarity = (lMaxLength - lLevenshteinDistance) / lMaxLength;

		return lLevenshteinSimilarity;
	}

	public static final double distance(final String str1, final String str2)
	{
		return distance(str1.toCharArray(), str2.toCharArray());
	}

	private static final double distance(final char[] str1, final char[] str2)
	{
		final double[][] distance = new double[str1.length + 1][];

		for (int i = 0; i <= str1.length; i++)
		{
			distance[i] = new double[str2.length + 1];
			distance[i][0] = i;
		}
		for (int j = 0; j < str2.length + 1; j++)
			distance[0][j] = j;

		for (int i = 1; i <= str1.length; i++)
			for (int j = 1; j <= str2.length; j++)
				distance[i][j] = minimum(distance[i - 1][j] + deletion(str1[i - 1]), distance[i][j - 1]
						+ insertion(str2[j - 1]), distance[i - 1][j - 1] + (substitution(str1[i - 1], str2[j - 1])));

		return distance[str1.length][str2.length];
	}

	private static final double deletion(final char c)
	{
		final boolean cd = isDigit(c);
		final boolean cu = isUpper(c);
		final boolean cl = isLower(c);
		final boolean cp = isPunctuation(c);

		if (cd || cu)
			return 1.0;

		else if (cl)
			return 0.75;

		else if (cp)
			return 0.5;

		else
			return 1.0;

	}

	private static final double insertion(final char c)
	{
		return deletion(c);
	}

	private static final double substitution(final char c1, final char c2)
	{
		final boolean c1d = isDigit(c1);
		final boolean c1u = isUpper(c1);
		final boolean c1l = isLower(c1);

		final boolean c2d = isDigit(c2);
		final boolean c2u = isUpper(c2);
		final boolean c2l = isLower(c2);

		if ((c1d && (c2u || c2l)) || (c2d && (c1u || c1l)))
			return 1;

		else if ((c1l && c2u) || (c1u && c2l))
			return 0.5;

		else if ((c1d) && (c2d))
			return c1 == c2 ? 0 : 0.75;

		else if (c1l && c2l)
			return c1 == c2 ? 0 : 0.75;

		else if (c1u && c2u)
			return c1 == c2 ? 0 : 0.75;

		else
			return (c1 == c2) ? 0 : 1;
	}

	private static final boolean isDigit(final char c)
	{
		return c >= 48 && c <= 57;
	}

	private static final boolean isUpper(final char c)
	{
		return c >= 65 && c <= 90;
	}

	private static final boolean isLower(final char c)
	{
		return c >= 97 && c <= 122;
	}

	private static final boolean isPunctuation(final char c)
	{
		return c >= 32 && c <= 47;
	}

}