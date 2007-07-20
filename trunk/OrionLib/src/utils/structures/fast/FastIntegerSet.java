package utils.structures.fast;

import java.util.Arrays;
import java.util.Collection;
import java.util.Random;


public final class FastIntegerSet
{

	public static final int[] add(final int[] pArray, final int pInteger)
	{
		if(pArray.length==0)
		{
			int[] lNewArray = new int[1];
			lNewArray[0] = pInteger;
			return lNewArray;
		}
		
		final int lInsertPosition = locate(pArray, pInteger);
    final int lInsertValue = pArray[lInsertPosition];  
		if (lInsertValue==pInteger)
		{
			return pArray;
		}
		else if (lInsertValue<=pInteger)
		{
			int[] lNewArray = insertAt(pArray, lInsertPosition+1, pInteger);
			return lNewArray;
		}
		else if (lInsertValue>=pInteger)
		{
			int[] lNewArray = insertAt(pArray, lInsertPosition, pInteger);
			return lNewArray;
		}
		
		return null;
	}

	public static final int locate(final int[] pArray, final int pInt)
	{
		if(pArray.length==0)
			return 0;
		return locateRecursive(pArray, pInt, 0, pArray.length);
	}

	private static final int locateRecursive(final int[] pArray,
																					final int pInt,
																					final int pStartIndex,
																					final int pEndIndex)
	{
		final int lLength = pEndIndex - pStartIndex;
		if (lLength == 1)
		{
			return pStartIndex;
		}

		final int lMedianIndex = (pEndIndex + pStartIndex) / 2;
		final int lMedianValue = pArray[lMedianIndex];
		
		 if(lMedianValue==pInt)
		{
			return lMedianIndex;
		}
		else if (lMedianValue <= pInt)
		{
			return locateRecursive(pArray, pInt, lMedianIndex, pEndIndex);
		}
		else 
		{
			return locateRecursive(pArray, pInt, pStartIndex, lMedianIndex);
		}
		
	}

	public static final int[] insertAt(final int[] pArray, final int pInsertPosition, final int pI)
	{
		final int oldArrayLength = pArray.length;
		final int[] lNewArray = getNewArrayWithCapacity(pArray.length + 1);

		if (pInsertPosition == oldArrayLength)
		{
			System.arraycopy(pArray, 0, lNewArray, 0, oldArrayLength);
			lNewArray[pInsertPosition] = pI;
		}
		else if (pInsertPosition == 0)
		{
			System.arraycopy(pArray, 0, lNewArray, 1, oldArrayLength);
			lNewArray[pInsertPosition] = pI;
		}
		else
		{
			final int lPreLength = pInsertPosition;
			final int lPostLength = oldArrayLength - lPreLength;
			System.arraycopy(pArray, 0, lNewArray, 0, lPreLength);
			lNewArray[pInsertPosition] = pI;
			System.arraycopy(pArray, pInsertPosition, lNewArray, pInsertPosition + 1, lPostLength);
		}

		return lNewArray;
	}

	

	public static final int[] intersection(final int[] pArray1, final int[] pArray2)
	{
		final int lMaximalSize = max(pArray1.length, pArray2.length);
		final int[] lNewArray = new int[lMaximalSize];

		int i = 0;
		int j = 0;
		int k = 0;

		while (i < pArray1.length && j < pArray2.length)
		{
			final int lA = pArray1[i];
			final int lB = pArray2[j];

			if (lA == lB)
			{
				lNewArray[k] = lA;
				k++;
				i++;
				j++;
			}
			else if (lA > lB)
			{
				j++;
			}
			else if (lA < lB)
			{
				i++;
			}
		}

		int[] lResizedArray = setCapacity(lNewArray,k);

		return lResizedArray;
	}
	
	
	
	
	public static final int[] union(final int[] pArray1, final int[] pArray2)
	{
		if (pArray1.length==0 && pArray2.length!=0)
		{
			final int[] lNewArray = new int[pArray2.length];
			System.arraycopy(pArray2, 0, lNewArray, 0, pArray2.length);
			return lNewArray;
		}
		if (pArray2.length==0 && pArray1.length!=0)
		{
			final int[] lNewArray = new int[pArray1.length];
			System.arraycopy(pArray1, 0, lNewArray, 0, pArray1.length);
			return lNewArray;
		}
		if(pArray2.length==0 && pArray1.length==0)
		{
			return new int[0];
		}
			
		final int lMaximalSize = pArray1.length + pArray2.length;
		final int[] lNewArray = new int[lMaximalSize];

		int i = 0;
		int j = 0;
		int k = 0;

		int lA = pArray1[i];
		int lB = pArray2[j];
		while (true)
		{
			
			if(i<pArray1.length)
			{
			 lA = pArray1[i];
				
				if(j<pArray2.length)
				{
					lB=pArray2[j];
				}
				else
				{
					lB=Integer.MAX_VALUE;
				}
			}
			else
			{
			 lA=Integer.MAX_VALUE;
			 				
				if(j<pArray2.length)
				{
					lB=pArray2[j];
				}
				else
				{
					break;
				}
			}			

			if (lA == lB)
			{
				lNewArray[k] = lA;
				k++;
				i++;
				j++;
			}
			else if (lA > lB)
			{
				lNewArray[k] = lB;
				k++;
				j++;
			}
			else if (lA < lB)
			{
				lNewArray[k] = lA;
				k++;
				i++;
			}						
		}

		int[] lResizedArray = setCapacity(lNewArray,k);

		return lResizedArray;
	}
	

	
	/**
	 * Computes the difference Set1 - Set2 (not the symmetric one!!!)
	 * @param pArray1
	 * @param pArray2
	 * @return
	 */
	public static final int[] difference(final int[] pArray1, final int[] pArray2)
	{
		if(pArray1.length==0 && pArray2.length!=0)
		{
			return new int[0];
		}
		if(pArray1.length!=0 && pArray2.length==0)
		{
			final int[] lNewArray = new int[pArray1.length];
			System.arraycopy(pArray1, 0, lNewArray, 0, pArray1.length);
			return lNewArray;
		}
		if(pArray2.length==0 && pArray1.length==0)
		{
			return new int[0];
		}
		
		
		final int lMaximalSize = pArray1.length;
		final int[] lNewArray = new int[lMaximalSize];

		int i = 0;
		int j = 0;
		int k = 0;
		int lA = pArray1[i];
		int lB = pArray2[j];
		while (true)
		{
			
			if(i<pArray1.length)
			{
			 lA = pArray1[i];
				
				if(j<pArray2.length)
				{
					lB=pArray2[j];
				}
				else
				{
					lB=Integer.MAX_VALUE;
				}
			}
			else
			{
			 lA=Integer.MAX_VALUE;
			 				
				if(j<pArray2.length)
				{
					lB=pArray2[j];
				}
				else
				{
					break;
				}
			}			

			if (lA == lB)
			{
				i++;
				j++;
			}
			else if (lA > lB)
			{
				j++;
			}
			else if (lA < lB)
			{
				lNewArray[k] = lA;
				k++;
				i++;
			}						
		}

		int[] lResizedArray = setCapacity(lNewArray,k);

		return lResizedArray;
	}
	
	public static final int[] from(final Collection<Integer> pCollection)
	{
		final int[] lArray = getNewArrayWithCapacity(pCollection.size());
		
		int i=0;
		for (int lI : pCollection)
		{
			lArray[i]=lI;
			i++;
		}
		return lArray;
	}
	
	public static final int[] from(final Integer[] pIntegerArray)
	{
		final int[] lArray = getNewArrayWithCapacity(pIntegerArray.length);
		
		int i=0;
		for (int lI : pIntegerArray)
		{
			lArray[i]=lI;
			i++;
		}
		return lArray;
	}
	
	
	
	private static final int[] getNewArrayWithCapacity(final int pNewCapacity)
	{
		final int[] newArray = new int[pNewCapacity];
		return newArray;
		/*
		final int lOldCapacity = pArray.length;
		if (lOldCapacity < pNewCapacity)
		{
			final int[] oldArray = pArray;
			int newCapacity = (lOldCapacity * 4) / 3 + 1;
			if (newCapacity < pNewCapacity)
				newCapacity = pNewCapacity;
			final int[] newArray = new int[newCapacity];
			return newArray;
		}
		return pArray;/***/
	}

	private static final int[] setCapacity(final int[] pArray, final int pNewCapacity)
	{
		final int[] lNewArray = new int[pNewCapacity];
		System.arraycopy(pArray, 0, lNewArray, 0, pNewCapacity);
		return lNewArray;
	}

	
	public static final boolean equals(final int[] pArray1, final int[] pArray2)
	{
		if(pArray1.length!=pArray2.length)
		{
			return false;
		}
		else
		{
			for(int i=0; i<pArray1.length; i++)
			{
				if(pArray1[i]!=pArray2[i])
					return false;
			}
			return true;
		}		
	}
	
	
	public static final int[] random(final Random pRandom, final int pDomainSize, final double pDensity)
	{
		final int[] lArray = getNewArrayWithCapacity(pDomainSize);
		
		int lCurrentIndex=0;
		int i=0;
		while (i<pDomainSize)
		{
			if(pRandom.nextDouble()<pDensity)
			{
				lArray[lCurrentIndex] = i;
				lCurrentIndex++;
			}
			i++;
		}
		
		int[] lResizedArray = setCapacity(lArray,lCurrentIndex);
		return lResizedArray;
	}
	
	private static final int max(int a, int b)
	{
		return (a >= b) ? a : b;
	}

	public static void validate(int[] pReferenceSet)
	{
		Arrays.sort(pReferenceSet);
	}
}
