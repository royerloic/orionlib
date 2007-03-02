package org.royerloic.bsh;

import java.util.Iterator;
import java.util.List;

import org.royerloic.structures.HashSetMap;
import org.royerloic.structures.IntegerHashMap;
import org.royerloic.structures.IntegerMap;
import org.royerloic.structures.Matrix;
import org.royerloic.structures.SetMap;

import bsh.CallStack;
import bsh.Interpreter;

public class matrixfilter
{

	public static void invoke(Interpreter env,
																					CallStack callstack,
																					Matrix<String> pMatrix,
																					int pColumnIndex,
																					double pMin,
																					double pMax)
	{
		SetMap<String,String> lSetMap = new HashSetMap<String,String>();

		Iterator<List<String>> lIterator = pMatrix.iterator();
		for (;lIterator.hasNext();)
		{
			List<String> lList = lIterator.next();
			final String lValueString = lList.get(pColumnIndex);
			final Double lValue = Double.parseDouble(lValueString);
			if(!(lValue>pMin && lValue<pMax))
			{
				lIterator.remove();
			}
		}
	}

}
