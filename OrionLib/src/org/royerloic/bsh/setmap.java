package org.royerloic.bsh;

import java.util.List;

import org.royerloic.structures.HashSetMap;
import org.royerloic.structures.IntegerHashMap;
import org.royerloic.structures.IntegerMap;
import org.royerloic.structures.Matrix;
import org.royerloic.structures.SetMap;

import bsh.CallStack;
import bsh.Interpreter;

public class setmap
{

	public static SetMap<String,String> invoke(Interpreter env,
																					CallStack callstack,
																					Matrix<String> pMatrix,
																					int pKeyColumnIndex,
																					int pValueColumnIndex)
	{
		SetMap<String,String> lSetMap = new HashSetMap<String,String>();

		for (List<String> lList : pMatrix)
		{
			final String lKey = lList.get(pKeyColumnIndex);
			final String lValue = lList.get(pValueColumnIndex);
			lSetMap.put(lKey,lValue);
		}

		return lSetMap;
	}

}
