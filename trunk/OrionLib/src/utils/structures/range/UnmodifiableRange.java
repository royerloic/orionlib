package utils.structures.range;

public class UnmodifiableRange extends Range
{

	public UnmodifiableRange(Range pRange)
	{
		super(pRange);		
	}
		
	public UnmodifiableRange(int pStart, int pLength)
	{
		super(pStart, pLength);
	}

	/**
	 * Create a range given the start and end indices. NOTE: the end index is
	 * _exclusive_ following the Java convention!!!
	 * 
	 * @param start
	 * @param end
	 */
	public static UnmodifiableRange constructRangeWithStartEnd(final int start, final int end)
	{
		if (end < start)
		{
			throw new RuntimeException("Range must have min length 0. (start<=end)");
		}
		return new UnmodifiableRange(start, end - start);
	}
	
	/**
	 * Create a range given the start and end indices. NOTE: the end index is
	 * _exclusive_ following the Java convention!!!
	 * 
	 * @param start
	 * @param end
	 */
	public static UnmodifiableRange constructRangeWithStartLength(final int start, final int length)
	{
		if (length < 0)
		{
			throw new RuntimeException("Range must have min length 0. ");
		}
		return new UnmodifiableRange(start, length);
	}
	

	@Override
	public void convexUnion(Range pOtherRange)
	{
		throw new UnsupportedOperationException("This Range is unmodifiable");
	}

	@Override
	public Range translateRange(int pBy)
	{
		throw new UnsupportedOperationException("This Range is unmodifiable");
	}

	@Override
	public void setEnd(int pRangeEnd)
	{
		throw new UnsupportedOperationException("This Range is unmodifiable");
	}

	@Override
	public void setRange(Range pRange)
	{
		throw new UnsupportedOperationException("This Range is unmodifiable");
	}

	@Override
	public void setStart(int pRangeStart)
	{
		throw new UnsupportedOperationException("This Range is unmodifiable");
	}
	
	
	

}
