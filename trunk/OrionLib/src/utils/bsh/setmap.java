package utils.bsh;

import java.util.List;

import utils.structures.HashSetMap;
import utils.structures.Matrix;
import utils.structures.SetMap;
import bsh.CallStack;
import bsh.Interpreter;

public class setmap
{

	public static SetMap<String,String> invoke(final Interpreter env,
																					final CallStack callstack,
																					final Matrix<String> pMatrix,
																					final int pKeyColumnIndex,
																					final int pValueColumnIndex)
	{
		final SetMap<String,String> lSetMap = new HashSetMap<String,String>();

		for (final List<String> lList : pMatrix)
		{
			final String lKey = lList.get(pKeyColumnIndex);
			final String lValue = lList.get(pValueColumnIndex);
			lSetMap.put(lKey,lValue);
		}

		return lSetMap;
	}

}
