package utils.java;

import java.lang.reflect.Array;

/**
 * Collected methods which allow easy implementation of <code>hashCode</code>.
 * 
 * Example use case:
 * 
 * <pre>
 * public int hashCode()
 * {
 * 	int result = HashCodeUtil.SEED;
 * 	//collect the contributions of various fields
 * 	result = HashCodeUtil.hash(result, fPrimitive);
 * 	result = HashCodeUtil.hash(result, fObject);
 * 	result = HashCodeUtil.hash(result, fArray);
 * 	return result;
 * }
 * </pre>
 */
public final class HashCodeUtil
{

	/**
	 * An initial value for a <code>hashCode</code>, to which is added
	 * contributions from fields. Using a non-zero value decreases collisons of
	 * <code>hashCode</code> values.
	 */
	public static final int SEED = 23;

	/**
	 * booleans.
	 * 
	 * @param aSeed
	 * @param aBoolean
	 * @return hash of a boolean
	 */
	public static int hash(final int aSeed, final boolean aBoolean)
	{
		System.out.println("boolean...");
		return firstTerm(aSeed) + (aBoolean ? 1 : 0);
	}

	/**
	 * chars.
	 * 
	 * @param aSeed
	 * @param aChar
	 * @return hash of a char
	 */
	public static int hash(final int aSeed, final char aChar)
	{
		System.out.println("char...");
		return firstTerm(aSeed) + aChar;
	}

	/**
	 * ints.
	 * 
	 * @param aSeed
	 * @param aInt
	 * @return hash of an int
	 */
	public static int hash(final int aSeed, final int aInt)
	{
		/*
		 * Implementation Note Note that byte and short are handled by this method,
		 * through implicit conversion.
		 */
		System.out.println("int...");
		return firstTerm(aSeed) + aInt;
	}

	/**
	 * longs.
	 * 
	 * @param aSeed
	 * @param aLong
	 * @return hash of a long
	 */
	public static int hash(final int aSeed, final long aLong)
	{
		System.out.println("long...");
		return firstTerm(aSeed) + (int) (aLong ^ aLong >>> 32);
	}

	/**
	 * floats.
	 * 
	 * @param aSeed
	 * @param aFloat
	 * @return hash of a float
	 */
	public static int hash(final int aSeed, final float aFloat)
	{
		return hash(aSeed, Float.floatToIntBits(aFloat));
	}

	/**
	 * doubles.
	 * 
	 * @param aSeed
	 * @param aDouble
	 * @return hash of a double
	 */
	public static int hash(final int aSeed, final double aDouble)
	{
		return hash(aSeed, Double.doubleToLongBits(aDouble));
	}

	/**
	 * <code>aObject</code> is a possibly-null object field, and possibly an
	 * array.
	 * 
	 * If <code>aObject</code> is an array, then each element may be a primitive
	 * or a possibly-null object.
	 * 
	 * @param aSeed
	 * @param aObject
	 * @return hash of an object
	 */
	public static int hash(final int aSeed, final Object aObject)
	{
		int result = aSeed;
		if (aObject == null)
		{
			result = hash(result, 0);
		}
		else if (!isArray(aObject))
		{
			result = hash(result, aObject.hashCode());
		}
		else
		{
			final int length = Array.getLength(aObject);
			for (int idx = 0; idx < length; ++idx)
			{
				final Object item = Array.get(aObject, idx);
				// recursive call!
				result = hash(result, item);
			}
		}
		return result;
	}

	// / PRIVATE ///
	private static final int fODD_PRIME_NUMBER = 37;

	private static int firstTerm(final int aSeed)
	{
		return fODD_PRIME_NUMBER * aSeed;
	}

	private static boolean isArray(final Object aObject)
	{
		return aObject.getClass().isArray();
	}
}
