package org.royerloic.bsh;

import java.util.List;

import org.royerloic.structures.IntegerHashMap;
import org.royerloic.structures.IntegerMap;
import org.royerloic.structures.Matrix;

import bsh.CallStack;
import bsh.Interpreter;

public class integermap
{
	
	public static IntegerMap<String> invoke(final Interpreter env, final CallStack callstack, final Matrix<String> pMatrix, final int pColumnIndex)
	{
		final IntegerMap<String> lIntegerMap = new IntegerHashMap<String>();
		
		for (final List<String> lList : pMatrix)
		{
			final String pValue  = lList.get(pColumnIndex);
			lIntegerMap.add(pValue, 1);
		}
		
		return lIntegerMap;		
	}
	
	

}
