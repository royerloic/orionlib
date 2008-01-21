package utils.structures.fast;

import java.util.Random;

public class FastIntegerGraphUtils
{

	public static boolean rewireOnce(	Random pRandom,
																		FastIntegerGraph pIntegerGraph,
																		int pTries)
	{
		int[] lNodeSet = pIntegerGraph.getNodeSet();

		if (lNodeSet.length < 4)
			return false;

		int fefn = -1;
		int fesn = -1;
		int sefn = -1;
		int sesn = -1;

		boolean valid = false;
		int tries = 0;
		do
		{

			fefn = lNodeSet[pRandom.nextInt(lNodeSet.length)];
			int[] fefnNei = pIntegerGraph.getNodeNeighbours(fefn);
			if (fefnNei.length < 1)
				continue;
			fesn = fefnNei[pRandom.nextInt(fefnNei.length)];

			sefn = lNodeSet[pRandom.nextInt(lNodeSet.length)];
			int[] sefnNei = pIntegerGraph.getNodeNeighbours(sefn);
			if (sefnNei.length < 1)
				continue;
			sesn = sefnNei[pRandom.nextInt(sefnNei.length)];

			valid = fefn != fesn && sefn != sesn
							&& fefn != sefn
							&& fesn != sesn
							&& fefn != sesn
							&& fesn != sefn;

			valid &= pIntegerGraph.isEdge(fefn, fesn) && pIntegerGraph.isEdge(sefn,
																																				sesn);
			tries++;
		}
		while (!valid && tries < pTries);

		if (valid)
		{
			System.out.println("fefn: " + fefn
													+ ", fesn: "
													+ fesn
													+ ", sefn: "
													+ sefn
													+ ", sesn: "
													+ sesn);

			pIntegerGraph.removeEdge(fefn, fesn);
			pIntegerGraph.removeEdge(sefn, sesn);

			pIntegerGraph.addEdge(fefn, sesn);
			pIntegerGraph.addEdge(sefn, fesn);
		}

		return valid;
	}

	public static int rewire(	Random pRandom,
														FastIntegerGraph pIntegerGraph,
														int pTries,
														int pRewireSteps)
	{
		int lNumberofRewirings = 0;
		for (int i = 1; i <= pRewireSteps; i++)
			lNumberofRewirings += rewireOnce(pRandom, pIntegerGraph, pTries) ? 1 : 0;
		return lNumberofRewirings;
	}

	

}
