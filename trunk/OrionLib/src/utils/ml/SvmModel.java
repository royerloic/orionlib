package utils.ml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import utils.io.LineReader;
import utils.utils.Timer;

public class SvmModel
{

	private static class UniProtPair
	{
		public String mUniProt1;
		public String mUniProt2;

		public UniProtPair(final String pUniProt1, final String pUniProt2)
		{
			super();
			mUniProt1 = pUniProt1;
			mUniProt2 = pUniProt2;
		}

		@Override
		public String toString()
		{
			return "[" + mUniProt1 + " - " + mUniProt2 + "]";
		}
	}

	List<UniProtPair> mUniProtInteractionPairsList = new ArrayList<UniProtPair>();

	Map<String, List<Integer>> mUniprot2GoListMap = new HashMap<String, List<Integer>>();

	private static class GoIdAndDistance
	{
		Integer mGoId;
		Integer mDistance;

		public GoIdAndDistance(final Integer pGoId, final Integer pDistance)
		{
			mGoId = pGoId;
			mDistance = pDistance;
		}

		@Override
		public boolean equals(final Object pObj)
		{
			if (this == pObj)
				return true;
			else if (this.hashCode() != ((GoIdAndDistance) pObj).hashCode())
				return false;
			else
			{
				final GoIdAndDistance lGoIdAndDistance = (GoIdAndDistance) pObj;
				return (mGoId.equals(lGoIdAndDistance.mGoId));
			}
		}

		@Override
		public int hashCode()
		{
			return mGoId.hashCode();
		}

		@Override
		public String toString()
		{
			return "(GO:" + mGoId + " d=" + mDistance + ")";
		}
	}

	Map<Integer, Set<GoIdAndDistance>> mGoIdToGoIdAndDistanceSetMap = new HashMap<Integer, Set<GoIdAndDistance>>();

	private static class GoIdPair
	{
		public int mGoId1;
		public int mGoId2;

		public GoIdPair(final Integer pGoId1, final Integer pGoId2)
		{
			super();
			mGoId1 = pGoId1;
			mGoId2 = pGoId2;
		}

		@Override
		public boolean equals(final Object pObj)
		{
			final GoIdPair lGoIdPair = (GoIdPair) pObj;
			return ((mGoId1 == lGoIdPair.mGoId1) && (mGoId2 == lGoIdPair.mGoId2)) || ((mGoId1 == lGoIdPair.mGoId2) && (mGoId2 == lGoIdPair.mGoId1));

		}

		@Override
		public int hashCode()
		{
			return mGoId1 ^ mGoId2;
		}

		@Override
		public String toString()
		{
			return "[" + mGoId1 + " - " + mGoId2 + "]";
		}
	}

	Map<GoIdPair, Integer> mGoIdPairToIndexMap = new HashMap<GoIdPair, Integer>();
	Integer mNextFreeIndex = 0;

	private static class SparseVectorEntry
	{
		Integer mIndex;
		Double mValue;

		public SparseVectorEntry(final Integer pIndex, final Double pValue)
		{
			mIndex = pIndex;
			mValue = pValue;
		}

		@Override
		public String toString()
		{
			return mIndex + ":" + mValue;
		}

	}

	public void compute(final Map<String, String> pParameters) throws IOException
	{
		{
			final String lUniprot2GoFileName = pParameters.get("uniprot2go");
			final File lUniprot2GoFile = new File(lUniprot2GoFileName);

			final List<List<String>> lUniprot2GoMatrix = LineReader.readMatrixFromFile(	lUniprot2GoFile,
																																									false);

			for (final List<String> lList : lUniprot2GoMatrix)
			{
				final String lUniProtId = lList.get(0);
				List<Integer> lGoIdList = mUniprot2GoListMap.get(lUniProtId);
				if (lGoIdList == null)
				{
					lGoIdList = new ArrayList<Integer>();
					mUniprot2GoListMap.put(lUniProtId, lGoIdList);
				}
				final String lGoIdString = lList.get(1);
				final Integer lGoIdInteger = Integer.parseInt(lGoIdString);
				lGoIdList.add(lGoIdInteger);
			}
		}

		{
			final String lPairsFileName = pParameters.get("pairs");
			final File lPairsFile = new File(lPairsFileName);

			final List<List<String>> lPairsMatrix = LineReader.readMatrixFromFile(lPairsFile,
																																						false);

			for (final List<String> lList : lPairsMatrix)
			{
				final List<String> lUniProtIdsList = lList.subList(1, lList.size());

				final List<String> lValidatedUniProtIdList = new ArrayList<String>();
				for (String lUniProtId : lUniProtIdsList)
				{
					lUniProtId = lUniProtId.replaceAll("-[0-9]+", "");
					final boolean isValidUniProtId = mUniprot2GoListMap.containsKey(lUniProtId);
					if (isValidUniProtId)
						lValidatedUniProtIdList.add(lUniProtId);
					else
						System.out.println("Not validated: " + lUniProtId);
				}

				final Set<UniProtPair> lPairSet = new HashSet<UniProtPair>();
				if (lValidatedUniProtIdList.size() > 1)
				{
					for (final String lUniProtId1 : lValidatedUniProtIdList)
						for (final String lUniProtId2 : lValidatedUniProtIdList)
							if (!lUniProtId1.equals(lUniProtId2))
							{
								final UniProtPair lPair = new UniProtPair(lUniProtId1,
																													lUniProtId2);
								lPairSet.add(lPair);
							}
				}
				else if (lValidatedUniProtIdList.size() == 1)
				{
					final String lUniProtId = lValidatedUniProtIdList.get(0);
					final UniProtPair lPair = new UniProtPair(lUniProtId, lUniProtId);
					lPairSet.add(lPair);
				}

				mUniProtInteractionPairsList.addAll(lPairSet);
			}
		}

		{
			final String lGo2GoFileName = pParameters.get("go2go");
			final File lGo2GoFile = new File(lGo2GoFileName);

			final List<List<String>> lGo2GoMatrix = LineReader.readMatrixFromFile(lGo2GoFile,
																																						false);

			for (final List<String> lList : lGo2GoMatrix)
			{
				final String lGoParentIdString = lList.get(0);
				final Integer lGoParentId = Integer.parseInt(lGoParentIdString);
				final String lGoChildIdString = lList.get(1);
				final Integer lGoChildId = Integer.parseInt(lGoChildIdString);
				final String lDistanceString = lList.get(2);
				Integer lDistance = Integer.parseInt(lDistanceString);

				// first direction up the tree:
				{
					Set<GoIdAndDistance> lGoIdAndDistanceSet = mGoIdToGoIdAndDistanceSetMap.get(lGoChildId);
					if (lGoIdAndDistanceSet == null)
					{
						lGoIdAndDistanceSet = new HashSet<GoIdAndDistance>();
						mGoIdToGoIdAndDistanceSetMap.put(lGoChildId, lGoIdAndDistanceSet);
					}
					final GoIdAndDistance lGoIdAndDistance = new GoIdAndDistance(	lGoParentId,
																																				-lDistance);
					lGoIdAndDistanceSet.add(lGoIdAndDistance);
				}

				// second direction down the tree:
				{
					Set<GoIdAndDistance> lGoIdAndDistanceSet = mGoIdToGoIdAndDistanceSetMap.get(lGoParentId);
					if (lGoIdAndDistanceSet == null)
					{
						lGoIdAndDistanceSet = new HashSet<GoIdAndDistance>();
						mGoIdToGoIdAndDistanceSetMap.put(lGoParentId, lGoIdAndDistanceSet);
					}
					final GoIdAndDistance lGoIdAndDistance = new GoIdAndDistance(	lGoChildId,
																																				lDistance);
					lGoIdAndDistanceSet.add(lGoIdAndDistance);
				}

			}
		}

		final Timer lTimer = new Timer();
		lTimer.start();

		final List<List<SparseVectorEntry>> lSparseVectorEntryListList = new ArrayList<List<SparseVectorEntry>>();
		double lPercentFinished = 0;
		for (final UniProtPair lUniProtPair : mUniProtInteractionPairsList)
		{

			Map<Integer, Double> lExtendedGoIdList1;
			{
				final String lUniProt1 = lUniProtPair.mUniProt1;
				final Map<Integer, Double> lGoIdMap1 = new HashMap<Integer, Double>();
				final List<Integer> lGoIdList1 = mUniprot2GoListMap.get(lUniProt1);
				for (final Integer lGoId : lGoIdList1)
					lGoIdMap1.put(lGoId, 1.0);
				lExtendedGoIdList1 = lGoIdMap1; // addRelatedGoIds(lGoIdMap1,0.3);
				// lExtendedGoIdList1 = addRelatedGoIds(lExtendedGoIdList1,0.3);
			}

			Map<Integer, Double> lExtendedGoIdList2;
			{
				final String lUniProt2 = lUniProtPair.mUniProt2;
				final Map<Integer, Double> lGoIdMap2 = new HashMap<Integer, Double>();
				final List<Integer> lGoIdList2 = mUniprot2GoListMap.get(lUniProt2);
				for (final Integer lGoId : lGoIdList2)
					lGoIdMap2.put(lGoId, 1.0);
				lExtendedGoIdList2 = lGoIdMap2; // addRelatedGoIds(lGoIdMap2,0.3);
				// lExtendedGoIdList2 = addRelatedGoIds(lExtendedGoIdList2,0.3);
			}

			final Map<GoIdPair, Double> lGoIdPairToValueMap = new HashMap<GoIdPair, Double>();
			{
				for (final Map.Entry<Integer, Double> lEntry1 : lExtendedGoIdList1.entrySet())
					for (final Map.Entry<Integer, Double> lEntry2 : lExtendedGoIdList2.entrySet())
					{
						final Integer lGoId1 = lEntry1.getKey();
						final Integer lGoId2 = lEntry2.getKey();
						final Double lValue1 = lEntry1.getValue();
						final Double lValue2 = lEntry2.getValue();

						final GoIdPair lGoIdPair = new GoIdPair(lGoId1, lGoId2);
						final Double lValue = lValue1 * lValue2;
						lGoIdPairToValueMap.put(lGoIdPair, lValue);
					}
			}

			final List<SparseVectorEntry> lSparseVectorEntryList = new ArrayList<SparseVectorEntry>();

			for (final Map.Entry<GoIdPair, Double> lEntry : lGoIdPairToValueMap.entrySet())
			{
				final GoIdPair lGoIdPair = lEntry.getKey();
				final Integer lIndex = getIndexForGoIdPair(lGoIdPair);
				final Double lValue = lEntry.getValue();
				final SparseVectorEntry lSparseVectorEntry = new SparseVectorEntry(	lIndex,
																																						lValue);
				lSparseVectorEntryList.add(lSparseVectorEntry);
			}

			lSparseVectorEntryListList.add(lSparseVectorEntryList);

			// System.out.println(lSparseVectorEntryList.size());
			lPercentFinished += 1 / (double) mUniProtInteractionPairsList.size();
			final String lESTString = lTimer.getESTString(lPercentFinished);
			System.out.println(lESTString);
		}
		lTimer.stop();

		final String lTrainingName = pParameters.get("training");
		final File lTrainingFile = new File(lTrainingName);
		lTrainingFile.delete();

		final List<List<String>> lOutputMatrix = new ArrayList<List<String>>();

		for (final List<SparseVectorEntry> lSparseVectorEntryList : lSparseVectorEntryListList)
		{
			final List<String> lLine = new ArrayList<String>();
			lLine.add("0");
			for (final SparseVectorEntry lSparseVectorEntry : lSparseVectorEntryList)
				lLine.add(lSparseVectorEntry.toString());
			lOutputMatrix.add(lLine);
		}

		LineReader.writeMatrixToFile(lOutputMatrix, lTrainingFile);
	}

	private Integer getIndexForGoIdPair(final GoIdPair pGoIdPair)
	{
		Integer lIndex = mGoIdPairToIndexMap.get(pGoIdPair);
		if (lIndex == null)
		{
			lIndex = mNextFreeIndex;
			mNextFreeIndex++;
			mGoIdPairToIndexMap.put(pGoIdPair, lIndex);
		}
		return lIndex;
	}

	/**
	 * @param args
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public static void main(final String[] args)
	{
		try
		{
			final Map<String, String> lArgumentsMap = getMapFromCommandLine(args);
			new SvmModel().compute(lArgumentsMap);
		}
		catch (final Throwable e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static Map<String, String> getMapFromCommandLine(final String[] pArguments)
	{
		final Map<String, String> lParameterMap = new HashMap<String, String>();
		for (String lArgument : pArguments)
		{
			lArgument = lArgument.trim();
			if (lArgument.contains("="))
			{
				final String[] lKeyValueArray = lArgument.split("=");
				final String lKey = lKeyValueArray[0];
				final String lValue = lKeyValueArray[1];
				lParameterMap.put(lKey, lValue);
			}
			else
				lParameterMap.put(lArgument, "yes");
		}
		return lParameterMap;
	}

}
