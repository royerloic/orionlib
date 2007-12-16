package utils.bsh;

import java.util.Iterator;
import java.util.List;

import utils.structures.HashSetMap;
import utils.structures.Matrix;
import utils.structures.SetMap;
import bsh.CallStack;
import bsh.Interpreter;

public class matrixfilter
{

	public static void invoke(final Interpreter env,
																					final CallStack callstack,
																					final Matrix<String> pMatrix,
																					final int pColumnIndex,
																					final double pMin,
																					final double pMax)
	{
		final SetMap<String,String> lSetMap = new HashSetMap<String,String>();

		final Iterator<List<String>> lIterator = pMatrix.iterator();
		for (;lIterator.hasNext();)
			try
			{
				final List<String> lList = lIterator.next();
				final String lValueString = lList.get(pColumnIndex);
				final Double lValue = Double.parseDouble(lValueString);
				if(!((lValue>pMin) && (lValue<pMax)))
					lIterator.remove();
			}
			catch (final Throwable e)
			{
				e.printStackTrace();
			}
	}

}