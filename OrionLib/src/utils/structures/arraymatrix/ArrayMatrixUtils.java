package utils.structures.arraymatrix;

public class ArrayMatrixUtils
{
	public static final byte[][] downSample(final byte[][] pMatrix, final int pFactor)
	{
		final int lLength = pMatrix.length;
		final int lHeight = pMatrix[0].length;

		final int lDownSampledLength = lLength / pFactor;
		final int lDownSampledHeight = lHeight / pFactor;

		final byte[][] lDownSampledMatrix = new byte[lDownSampledLength][lDownSampledHeight];

		for (int i = 0; i < lDownSampledLength; i++)
		{
			for (int j = 0; j < lDownSampledLength; j++)
			{
				lDownSampledMatrix[i][j] = average(pMatrix, pFactor * i, pFactor * j, pFactor, pFactor);
			}
		}

		return lDownSampledMatrix;
	}

	private static final byte average(final byte[][] pMatrix,
																		final int pX,
																		final int pY,
																		final int pL,
																		final int pH)
	{
		final int lLength = pMatrix.length;
		final int lHeight = pMatrix[0].length;

		final int lEndX = Math.min(lLength, pX + pL);
		final int lEndY = Math.min(lHeight, pY + pH);

		int lAverage = 0;
		for (int i = pX; i < lEndX; i++)
		{
			for (int j = pY; j < lEndY; j++)
			{
				lAverage += pMatrix[i][j];
			}
		}

		lAverage = lAverage / (pL * pH);
		return (byte) lAverage;
	}

	public static final byte[][] downSampleNonZero(final byte[][] pMatrix, final int pFactor)
	{
		final int lLength = pMatrix.length;
		final int lHeight = pMatrix[0].length;

		final int lDownSampledLength = lLength / pFactor;
		final int lDownSampledHeight = lHeight / pFactor;

		final byte[][] lDownSampledMatrix = new byte[lDownSampledLength][lDownSampledHeight];

		for (int i = 0; i < lDownSampledLength; i++)
		{
			for (int j = 0; j < lDownSampledLength; j++)
			{
				lDownSampledMatrix[i][j] = averageNonZero(pMatrix, pFactor * i, pFactor * j, pFactor, pFactor);
			}
		}

		return lDownSampledMatrix;
	}

	private static final byte averageNonZero(	final byte[][] pMatrix,
																						final int pX,
																						final int pY,
																						final int pL,
																						final int pH)
	{
		final int lLength = pMatrix.length;
		final int lHeight = pMatrix[0].length;

		final int lEndX = Math.min(lLength, pX + pL);
		final int lEndY = Math.min(lHeight, pY + pH);

		int lAverage = 0;
		int lCount = 0;
		for (int i = pX; i < lEndX; i++)
		{
			for (int j = pY; j < lEndY; j++)
			{
				final byte lValue = pMatrix[i][j];
				lAverage += lValue;
				if (lValue != 0)
				{
					lCount++;
				}
			}
		}

		if (lCount == 0)
		{
			lAverage = 0;
		}
		else
		{
			lAverage = lAverage / lCount;
		}

		return (byte) lAverage;
	}
	
	public static final byte[][] downSampleMax(final byte[][] pMatrix, final int pFactor)
	{
		final int lLength = pMatrix.length;
		final int lHeight = pMatrix[0].length;

		final int lDownSampledLength = lLength / pFactor;
		final int lDownSampledHeight = lHeight / pFactor;

		final byte[][] lDownSampledMatrix = new byte[lDownSampledLength][lDownSampledHeight];

		for (int i = 0; i < lDownSampledLength; i++)
		{
			for (int j = 0; j < lDownSampledLength; j++)
			{
				lDownSampledMatrix[i][j] = max(pMatrix, pFactor * i, pFactor * j, pFactor, pFactor);
			}
		}

		return lDownSampledMatrix;
	}

	private static final byte max(	final byte[][] pMatrix,
																						final int pX,
																						final int pY,
																						final int pL,
																						final int pH)
	{
		final int lLength = pMatrix.length;
		final int lHeight = pMatrix[0].length;

		final int lEndX = Math.min(lLength, pX + pL);
		final int lEndY = Math.min(lHeight, pY + pH);

		int lMax = 0;
		for (int i = pX; i < lEndX; i++)
		{
			for (int j = pY; j < lEndY; j++)
			{
				final byte lValue = pMatrix[i][j];
				lMax = Math.max(lMax, lValue);
			}
		}

		return (byte) lMax;
	}
}
