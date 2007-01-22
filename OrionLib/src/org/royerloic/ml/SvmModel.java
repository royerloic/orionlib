package org.royerloic.ml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.royerloic.io.MatrixFile;
import org.royerloic.utils.Timer;

public class SvmModel
{

	private static class UniProtPair
	{
		public String	mUniProt1;
		public String	mUniProt2;

		public UniProtPair(String pUniProt1, String pUniProt2)
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

	private static final Double	cCutOff												= 0.1;

	List<UniProtPair>						mUniProtInteractionPairsList	= new ArrayList<UniProtPair>();

	Map<String, List<Integer>>	mUniprot2GoListMap						= new HashMap<String, List<Integer>>();

	private static class GoIdAndDistance
	{
		Integer	mGoId;
		Integer	mDistance;

		public GoIdAndDistance(Integer pGoId, Integer pDistance)
		{
			mGoId = pGoId;
			mDistance = pDistance;
		}

		@Override
		public boolean equals(Object pObj)
		{
			if (this == pObj)
				return true;
			else if (this.hashCode() != ((GoIdAndDistance) pObj).hashCode())
				return false;
			else
			{
				GoIdAndDistance lGoIdAndDistance = (GoIdAndDistance) pObj;
				return (this.mGoId.equals(lGoIdAndDistance.mGoId));
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

	Map<Integer, Set<GoIdAndDistance>>	mGoIdToGoIdAndDistanceSetMap	= new HashMap<Integer, Set<GoIdAndDistance>>();

	private static class GoIdPair
	{
		public int	mGoId1;
		public int	mGoId2;

		public GoIdPair(Integer pGoId1, Integer pGoId2)
		{
			super();
			mGoId1 = pGoId1;
			mGoId2 = pGoId2;
		}

		@Override
		public boolean equals(Object pObj)
		{
			GoIdPair lGoIdPair = (GoIdPair) pObj;
			return (this.mGoId1 == lGoIdPair.mGoId1 && this.mGoId2 == lGoIdPair.mGoId2)
					|| (this.mGoId1 == lGoIdPair.mGoId2 && this.mGoId2 == lGoIdPair.mGoId1);

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

	Map<GoIdPair, Integer>	mGoIdPairToIndexMap	= new HashMap<GoIdPair, Integer>();
	Integer									mNextFreeIndex			= 0;

	private static class SparseVectorEntry
	{
		Integer	mIndex;
		Double	mValue;

		public SparseVectorEntry(Integer pIndex, Double pValue)
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

	public void compute(Map<String, String> pParameters) throws IOException
	{
		{
			String lUniprot2GoFileName = pParameters.get("uniprot2go");
			File lUniprot2GoFile = new File(lUniprot2GoFileName);

			List<List<String>> lUniprot2GoMatrix = MatrixFile.readMatrixFromFile(lUniprot2GoFile, false);

			for (List<String> lList : lUniprot2GoMatrix)
			{
				String lUniProtId = lList.get(0);
				List<Integer> lGoIdList = mUniprot2GoListMap.get(lUniProtId);
				if (lGoIdList == null)
				{
					lGoIdList = new ArrayList<Integer>();
					mUniprot2GoListMap.put(lUniProtId, lGoIdList);
				}
				String lGoIdString = lList.get(1);
				Integer lGoIdInteger = Integer.parseInt(lGoIdString);
				lGoIdList.add(lGoIdInteger);
			}
		}

		{
			String lPairsFileName = pParameters.get("pairs");
			File lPairsFile = new File(lPairsFileName);

			List<List<String>> lPairsMatrix = MatrixFile.readMatrixFromFile(lPairsFile, false);

			for (List<String> lList : lPairsMatrix)
			{
				List<String> lUniProtIdsList = lList.subList(1, lList.size());

				List<String> lValidatedUniProtIdList = new ArrayList<String>();
				for (String lUniProtId : lUniProtIdsList)
				{
					lUniProtId = lUniProtId.replaceAll("-[0-9]+", "");
					boolean isValidUniProtId = mUniprot2GoListMap.containsKey(lUniProtId);
					if (isValidUniProtId)
						lValidatedUniProtIdList.add(lUniProtId);
					else
						System.out.println("Not validated: " + lUniProtId);
				}

				Set<UniProtPair> lPairSet = new HashSet<UniProtPair>();
				if (lValidatedUniProtIdList.size() > 1)
				{
					for (String lUniProtId1 : lValidatedUniProtIdList)
						for (String lUniProtId2 : lValidatedUniProtIdList)
							if (!lUniProtId1.equals(lUniProtId2))
							{
								UniProtPair lPair = new UniProtPair(lUniProtId1, lUniProtId2);
								lPairSet.add(lPair);
							}
				}
				else if (lValidatedUniProtIdList.size() == 1)
				{
					String lUniProtId = lValidatedUniProtIdList.get(0);
					UniProtPair lPair = new UniProtPair(lUniProtId, lUniProtId);
					lPairSet.add(lPair);
				}

				mUniProtInteractionPairsList.addAll(lPairSet);
			}
		}

		{
			String lGo2GoFileName = pParameters.get("go2go");
			File lGo2GoFile = new File(lGo2GoFileName);

			List<List<String>> lGo2GoMatrix = MatrixFile.readMatrixFromFile(lGo2GoFile, false);

			for (List<String> lList : lGo2GoMatrix)
			{
				String lGoParentIdString = lList.get(0);
				Integer lGoParentId = Integer.parseInt(lGoParentIdString);
				String lGoChildIdString = lList.get(1);
				Integer lGoChildId = Integer.parseInt(lGoChildIdString);
				String lDistanceString = lList.get(2);
				Integer lDistance = Integer.parseInt(lDistanceString);

				// first direction up the tree:
				{
					Set<GoIdAndDistance> lGoIdAndDistanceSet = mGoIdToGoIdAndDistanceSetMap.get(lGoChildId);
					if (lGoIdAndDistanceSet == null)
					{
						lGoIdAndDistanceSet = new HashSet<GoIdAndDistance>();
						mGoIdToGoIdAndDistanceSetMap.put(lGoChildId, lGoIdAndDistanceSet);
					}
					GoIdAndDistance lGoIdAndDistance = new GoIdAndDistance(lGoParentId, -lDistance);
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
					GoIdAndDistance lGoIdAndDistance = new GoIdAndDistance(lGoChildId, lDistance);
					lGoIdAndDistanceSet.add(lGoIdAndDistance);
				}

			}
		}

		Timer lTimer = new Timer();
		lTimer.start();

		List<List<SparseVectorEntry>> lSparseVectorEntryListList = new ArrayList<List<SparseVectorEntry>>();
		double lPercentFinished = 0;
		for (UniProtPair lUniProtPair : mUniProtInteractionPairsList)
		{

			Map<Integer, Double> lExtendedGoIdList1;
			{
				String lUniProt1 = lUniProtPair.mUniProt1;
				Map<Integer, Double> lGoIdMap1 = new HashMap<Integer, Double>();
				List<Integer> lGoIdList1 = mUniprot2GoListMap.get(lUniProt1);
				for (Integer lGoId : lGoIdList1)
				{
					lGoIdMap1.put(lGoId, 1.0);
				}
				lExtendedGoIdList1 = lGoIdMap1; // addRelatedGoIds(lGoIdMap1,0.3);
				// lExtendedGoIdList1 = addRelatedGoIds(lExtendedGoIdList1,0.3);
			}

			Map<Integer, Double> lExtendedGoIdList2;
			{
				String lUniProt2 = lUniProtPair.mUniProt2;
				Map<Integer, Double> lGoIdMap2 = new HashMap<Integer, Double>();
				List<Integer> lGoIdList2 = mUniprot2GoListMap.get(lUniProt2);
				for (Integer lGoId : lGoIdList2)
				{
					lGoIdMap2.put(lGoId, 1.0);
				}
				lExtendedGoIdList2 = lGoIdMap2; // addRelatedGoIds(lGoIdMap2,0.3);
				// lExtendedGoIdList2 = addRelatedGoIds(lExtendedGoIdList2,0.3);
			}

			Map<GoIdPair, Double> lGoIdPairToValueMap = new HashMap<GoIdPair, Double>();
			{
				for (Map.Entry<Integer, Double> lEntry1 : lExtendedGoIdList1.entrySet())
					for (Map.Entry<Integer, Double> lEntry2 : lExtendedGoIdList2.entrySet())
					{
						Integer lGoId1 = lEntry1.getKey();
						Integer lGoId2 = lEntry2.getKey();
						Double lValue1 = lEntry1.getValue();
						Double lValue2 = lEntry2.getValue();

						GoIdPair lGoIdPair = new GoIdPair(lGoId1, lGoId2);
						Double lValue = lValue1 * lValue2;
						lGoIdPairToValueMap.put(lGoIdPair, lValue);
					}
			}

			List<SparseVectorEntry> lSparseVectorEntryList = new ArrayList<SparseVectorEntry>();

			for (Map.Entry<GoIdPair, Double> lEntry : lGoIdPairToValueMap.entrySet())
			{
				GoIdPair lGoIdPair = lEntry.getKey();
				Integer lIndex = getIndexForGoIdPair(lGoIdPair);
				Double lValue = lEntry.getValue();
				SparseVectorEntry lSparseVectorEntry = new SparseVectorEntry(lIndex, lValue);
				lSparseVectorEntryList.add(lSparseVectorEntry);
			}

			lSparseVectorEntryListList.add(lSparseVectorEntryList);

			// System.out.println(lSparseVectorEntryList.size());
			lPercentFinished += 1 / (double) mUniProtInteractionPairsList.size();
			String lESTString = lTimer.getESTString(lPercentFinished);
			System.out.println(lESTString);
		}
		lTimer.stop();

		String lTrainingName = pParameters.get("training");
		File lTrainingFile = new File(lTrainingName);
		lTrainingFile.delete();

		List<List<String>> lOutputMatrix = new ArrayList<List<String>>();

		for (List<SparseVectorEntry> lSparseVectorEntryList : lSparseVectorEntryListList)
		{
			List<String> lLine = new ArrayList<String>();
			lLine.add("0");
			for (SparseVectorEntry lSparseVectorEntry : lSparseVectorEntryList)
			{
				lLine.add(lSparseVectorEntry.toString());
			}
			lOutputMatrix.add(lLine);
		}

		MatrixFile.writeMatrixToFile(lOutputMatrix, lTrainingFile);
	}

	private Map<Integer, Double> addRelatedGoIds(Map<Integer, Double> pGoIdMap, double pCutOff)
	{
		Map<Integer, Double> lNewGoIdMap = new HashMap<Integer, Double>(pGoIdMap);

		for (Map.Entry<Integer, Double> lEntry : pGoIdMap.entrySet())
		{
			Integer lGoId = lEntry.getKey();
			Set<GoIdAndDistance> lGoIdAndDistanceSet = mGoIdToGoIdAndDistanceSetMap.get(lGoId);

			if (lGoIdAndDistanceSet != null)
				for (GoIdAndDistance lGoIdAndDistance : lGoIdAndDistanceSet)
				{
					Integer lNewGoId1 = lGoIdAndDistance.mGoId;
					Double lValue = computeValueFromDistance(lGoIdAndDistance.mDistance);
					if (lValue > pCutOff)
					{
						lNewGoIdMap.put(lNewGoId1, lValue);
					}
				}
		}
		return lNewGoIdMap;
	}

	// private Map<GoIdPair, Double> addRelatedGoIdPairs(Map<GoIdPair, Double>
	// pGoIdPairToValueMap)
	// {
	// Map<GoIdPair, Double> lNewGoIdPairToValueMap = new HashMap<GoIdPair,
	// Double>(pGoIdPairToValueMap);
	//
	// for (Map.Entry<GoIdPair, Double> lEntry : pGoIdPairToValueMap.entrySet())
	// {
	// GoIdPair lGoIdPair = lEntry.getKey();
	// Integer lGoId1 = lGoIdPair.mGoId1;
	// Integer lGoId2 = lGoIdPair.mGoId2;
	//
	// Set<GoIdAndDistance> lGoIdAndDistanceSet1 =
	// mGoIdToGoIdAndDistanceSetMap.get(lGoId1);
	// Set<GoIdAndDistance> lGoIdAndDistanceSet2 =
	// mGoIdToGoIdAndDistanceSetMap.get(lGoId2);
	//
	// if (lGoIdAndDistanceSet1 != null)
	// for (GoIdAndDistance lGoIdAndDistance1 : lGoIdAndDistanceSet1)
	// {
	// Integer lNewGoId1 = lGoIdAndDistance1.mGoId;
	// Double lValue1 = computeValueFromDistance(lGoIdAndDistance1.mDistance);
	// if (lValue1 > cCutOff)
	// if (lGoIdAndDistanceSet2 != null)
	// for (GoIdAndDistance lGoIdAndDistance2 : lGoIdAndDistanceSet2)
	// {
	// Integer lNewGoId2 = lGoIdAndDistance2.mGoId;
	// Double lValue2 = computeValueFromDistance(lGoIdAndDistance2.mDistance);
	// if (lValue2 > cCutOff)
	// {
	// Double lValue = lValue1 * lValue2;
	// GoIdPair lNewGoIdPair = new GoIdPair(lNewGoId1, lNewGoId2);
	// lNewGoIdPairToValueMap.put(lNewGoIdPair, lValue);
	// }
	// }
	// }
	// }
	//
	// return lNewGoIdPairToValueMap;
	// }

	private double computeValueFromDistance(int pDistance)
	{
		if (pDistance <= 0)
		{
			return 1.0;
		}
		else if (pDistance > 0)
		{
			double lValue = Math.pow(2, -pDistance);
			return lValue;
		}
		else
			return 0;
	}

	private Integer getIndexForGoIdPair(GoIdPair pGoIdPair)
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
	public static void main(String[] args)
	{
		try
		{
			Map<String, String> lArgumentsMap = getMapFromCommandLine(args);
			new SvmModel().compute(lArgumentsMap);
		}
		catch (Throwable e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static Map<String, String> getMapFromCommandLine(String[] pArguments)
	{
		Map<String, String> lParameterMap = new HashMap<String, String>();
		for (String lArgument : pArguments)
		{
			lArgument = lArgument.trim();
			if (lArgument.contains("="))
			{
				String[] lKeyValueArray = lArgument.split("=");
				String lKey = lKeyValueArray[0];
				String lValue = lKeyValueArray[1];
				lParameterMap.put(lKey, lValue);
			}
			else
			{
				lParameterMap.put(lArgument, "yes");
			}
		}
		return lParameterMap;
	}

}
