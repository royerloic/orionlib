//package org.royerloic.ml.bayes.goint;
//
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
//import org.royerloic.io.MatrixFile;
//import org.royerloic.structures.Attach;
//import org.royerloic.structures.Pair;
//import org.royerloic.utils.Timer;
//
//public class TrainNaiveBayesModel
//{
//
//	private static final Double									cCutOff												= 0.1;
//	Map<String, List<Integer>>									mUniprot2GoListMap						= new HashMap<String, List<Integer>>();
//
//	Map<Integer, Set<Attach<Integer, Integer>>>	mGoIdToGoIdAndDistanceSetMap	= new HashMap<Integer, Set<Attach<Integer, Integer>>>();
//
//	Map<Pair<Integer>, Integer>									mGoIdPairToIndexMap						= new HashMap<Pair<Integer>, Integer>();
//	Integer																			mNextFreeIndex								= 0;
//
//	public TrainNaiveBayesModel(Map<String, String> pParameters) throws FileNotFoundException, IOException
//	{
//		super();
//		{
//			String lUniprot2GoFileName = pParameters.get("uniprot2go");
//			File lUniprot2GoFile = new File(lUniprot2GoFileName);
//
//			List<List<String>> lUniprot2GoMatrix = MatrixFile.readMatrixFromFile(lUniprot2GoFile, false);
//
//			for (List<String> lList : lUniprot2GoMatrix)
//			{
//				String lUniProtId = lList.get(0);
//				List<Integer> lGoIdList = mUniprot2GoListMap.get(lUniProtId);
//				if (lGoIdList == null)
//				{
//					lGoIdList = new ArrayList<Integer>();
//					mUniprot2GoListMap.put(lUniProtId, lGoIdList);
//				}
//				String lGoIdString = lList.get(1);
//				Integer lGoIdInteger = Integer.parseInt(lGoIdString);
//				lGoIdList.add(lGoIdInteger);
//			}
//		}
//
//		{
//			String lGo2GoFileName = pParameters.get("go2go");
//			File lGo2GoFile = new File(lGo2GoFileName);
//
//			List<List<String>> lGo2GoMatrix = MatrixFile.readMatrixFromFile(lGo2GoFile, false);
//
//			for (List<String> lList : lGo2GoMatrix)
//			{
//				String lGoParentIdString = lList.get(0);
//				Integer lGoParentId = Integer.parseInt(lGoParentIdString);
//				String lGoChildIdString = lList.get(1);
//				Integer lGoChildId = Integer.parseInt(lGoChildIdString);
//				String lDistanceString = lList.get(2);
//				Integer lDistance = Integer.parseInt(lDistanceString);
//
//				// first direction up the tree:
//				{
//					Set<Attach<Integer, Integer>> lGoIdAndDistanceSet = mGoIdToGoIdAndDistanceSetMap.get(lGoChildId);
//					if (lGoIdAndDistanceSet == null)
//					{
//						lGoIdAndDistanceSet = new HashSet<Attach<Integer, Integer>>();
//						mGoIdToGoIdAndDistanceSetMap.put(lGoChildId, lGoIdAndDistanceSet);
//					}
//					Attach<Integer, Integer> lAttachedValue = new Attach<Integer, Integer>(lGoParentId, -lDistance);
//					lGoIdAndDistanceSet.add(lAttachedValue);
//				}
//
//				// second direction down the tree:
//				{
//					Set<Attach<Integer, Integer>> lGoIdAndDistanceSet = mGoIdToGoIdAndDistanceSetMap.get(lGoParentId);
//					if (lGoIdAndDistanceSet == null)
//					{
//						lGoIdAndDistanceSet = new HashSet<Attach<Integer, Integer>>();
//						mGoIdToGoIdAndDistanceSetMap.put(lGoParentId, lGoIdAndDistanceSet);
//					}
//					Attach<Integer, Integer> lGoIdAndDistance = new Attach<Integer, Integer>(lGoChildId, lDistance);
//					lGoIdAndDistanceSet.add(lGoIdAndDistance);
//				}
//
//			}
//		}
//	}
//
//	public void loadIPSInteractions(File pIPSInteractionFile) throws IOException
//	{
//		Set<Pair<String>> lUniProtInteractingPairsSet = new HashSet<Pair<String>>();
//		Set<Pair<String>> lUniProtNonInteractingPairsSet = new HashSet<Pair<String>>();
//		;
//
//		{
//			List<List<String>> lPairsMatrix = MatrixFile.readMatrixFromFile(pIPSInteractionFile, false);
//
//			Map<Integer, Set<String>> lPubMedIdToUniProtIdSet = new HashMap<Integer, Set<String>>();
//			for (List<String> lList : lPairsMatrix)
//			{
//				List<String> lUniProtIdsList = lList.subList(1, lList.size());
//
//				List<String> lValidatedUniProtIdList = new ArrayList<String>();
//				for (String lUniProtId : lUniProtIdsList)
//				{
//					lUniProtId = lUniProtId.replaceAll("-[0-9]+", "");
//					boolean isValidUniProtId = mUniprot2GoListMap.containsKey(lUniProtId);
//					if (isValidUniProtId)
//						lValidatedUniProtIdList.add(lUniProtId);
//					else
//						System.out.println("Not validated: " + lUniProtId);
//				}
//
//				Set<Pair<String>> lPairSet = new HashSet<Pair<String>>();
//				if (lValidatedUniProtIdList.size() > 1)
//				{
//					for (String lUniProtId1 : lValidatedUniProtIdList)
//						for (String lUniProtId2 : lValidatedUniProtIdList)
//							if (!lUniProtId1.equals(lUniProtId2))
//							{
//								Pair<String> lPair = new Pair<String>(lUniProtId1, lUniProtId2);
//								lPairSet.add(lPair);
//							}
//				}
//				else if (lValidatedUniProtIdList.size() == 1)
//				{
//					String lUniProtId = lValidatedUniProtIdList.get(0);
//					Pair<String> lPair = new Pair<String>(lUniProtId, lUniProtId);
//					lPairSet.add(lPair);
//				}
//
//				lUniProtInteractingPairsSet.addAll(lPairSet);
//
//				String PubMedIdString = lList.get(0);
//
//				Integer lPubMedId = Integer.parseInt(PubMedIdString);
//				Set<String> lUniProtIDSet = lPubMedIdToUniProtIdSet.get(lPubMedId);
//				if (lUniProtIDSet == null)
//				{
//					lUniProtIDSet = new HashSet<String>();
//					lPubMedIdToUniProtIdSet.put(lPubMedId, lUniProtIDSet);
//				}
//				lUniProtIDSet.addAll(lValidatedUniProtIdList);
//			}
//
//			for (Set<String> lSet : lPubMedIdToUniProtIdSet.values())
//			{
//				if (lSet.size() > 1)
//				{
//					for (String lUniProtId1 : lSet)
//						for (String lUniProtId2 : lSet)
//							if (!lUniProtId1.equals(lUniProtId2))
//							{
//								Pair<String> lPair = new Pair<String>(lUniProtId1, lUniProtId2);
//								lUniProtNonInteractingPairsSet.add(lPair);
//							}
//				}
//			}
//		}
//
//	}
//
//	public void computeModel(Set<Pair<String>> pInteractingPairSet, Set<Pair<String>> pNonInteractingPairSet)
//			throws IOException
//	{
//
//		// We make sure that no interacting pair is among the non-interacting pair
//		pNonInteractingPairSet.removeAll(pInteractingPairSet);
//
//		double lNumberOfInteractingPairs = pInteractingPairSet.size();
//		double lNumberOfNonInteractingPairs = pNonInteractingPairSet.size();
//		double lTotalOfInteractingPairs = lNumberOfInteractingPairs + lNumberOfNonInteractingPairs;
//
//		double lInteractionProbability = lNumberOfInteractingPairs / lTotalOfInteractingPairs;
//		double lNonInteractionProbability = lNumberOfNonInteractingPairs / lTotalOfInteractingPairs;
//
//		Map<Pair<Integer>, Double> lInteractingGoIdPairToProbability = getGoIdPairProbability(pInteractingPairSet);
//		Map<Pair<Integer>, Double> lNonInteractingGoIdPairToProbability = getGoIdPairProbability(pNonInteractingPairSet);
//
//		String lTrainingName = pParameters.get("model");
//		File lTrainingFile = new File(lTrainingName);
//		lTrainingFile.delete();
//
//		List<List<String>> lOutputMatrix = new ArrayList<List<String>>();
//		{
//			List<String> lPIP = Collections.singletonList(Double.toString(lInteractionProbability));
//			List<String> lPNIP = Collections.singletonList(Double.toString(lNonInteractionProbability));
//			lOutputMatrix.add(lPIP);
//			lOutputMatrix.add(lPNIP);
//
//			writeToMatrix(lOutputMatrix, lInteractingGoIdPairToProbability, lNonInteractingGoIdPairToProbability);
//
//		}
//		MatrixFile.writeMatrixToFile(lOutputMatrix, lTrainingFile);
//
//	}
//
//	private void writeToMatrix(	List<List<String>> pMatrix,
//															Map<Pair<Integer>, Double> pInteractingProbabilities,
//															Map<Pair<Integer>, Double> pNonInteractingProbabilities)
//	{
//		for (Map.Entry<Pair<Integer>, Double> lEntry : pInteractingProbabilities.entrySet())
//		{
//			List<String> lLine = new ArrayList<String>();
//			Pair<Integer> lGoIdPair = lEntry.getKey();
//			lLine.add(Integer.toString(lGoIdPair.mA));
//			lLine.add(Integer.toString(lGoIdPair.mB));
//			Double lInteractingProbability = lEntry.getValue();
//			Double lNonInteractingProbability = pNonInteractingProbabilities.get(lEntry.getKey());
//			if (lNonInteractingProbability != null)
//			{
//				lLine.add(lInteractingProbability.toString());
//				lLine.add(lNonInteractingProbability.toString());
//				Double lInteractionLikelyHood = lInteractingProbability / lNonInteractingProbability;
//				lLine.add(lInteractionLikelyHood.toString());
//				pMatrix.add(lLine);
//			}
//		}
//	}
//
//	private Map<Pair<Integer>, Double> getGoIdPairProbability(Collection<Pair<String>> pUniProtPairsList)
//	{
//		Timer lTimer = new Timer();
//		double lPercent = 0;
//		lTimer.start();
//		Map<Pair<Integer>, Double> lGoIdPairToProbability = new HashMap<Pair<Integer>, Double>();
//		{
//			double lTotalNumberOfGoIdPairs = 0;
//			for (Pair<String> lUniProtPair : pUniProtPairsList)
//			{
//				String lUniProtId1 = lUniProtPair.mA;
//				String lUniProtId2 = lUniProtPair.mB;
//
//				List<Integer> lGoIdList1 = mUniprot2GoListMap.get(lUniProtId1);
//				List<Integer> lGoIdList2 = mUniprot2GoListMap.get(lUniProtId2);
//
//				Set<Integer> lGoIdListEnriched1 = addRelatedGoIds(lGoIdList1);
//				Set<Integer> lGoIdListEnriched2 = addRelatedGoIds(lGoIdList2);
//
//				for (Integer lGoId1 : lGoIdListEnriched1)
//					for (Integer lGoId2 : lGoIdListEnriched2)
//					{
//						Pair<Integer> lGoIdPair = new Pair<Integer>(lGoId1, lGoId2);
//						Double lCount = lGoIdPairToProbability.get(lGoIdPair);
//						if (lCount == null)
//						{
//							lCount = 0.0;
//						}
//						lCount = lCount + 1;
//						lGoIdPairToProbability.put(lGoIdPair, lCount);
//						lTotalNumberOfGoIdPairs++;
//					}
//
//				lPercent += 1 / ((double) pUniProtPairsList.size());
//				String lESTString = lTimer.getESTString(lPercent);
//				System.out.println(lESTString);
//			}
//			for (Map.Entry<Pair<Integer>, Double> lEntry : lGoIdPairToProbability.entrySet())
//			{
//				double lProbability = lEntry.getValue() / lTotalNumberOfGoIdPairs;
//				lEntry.setValue(lProbability);
//			}
//
//		}
//		lTimer.stop();
//
//		return lGoIdPairToProbability;
//	}
//
//	private Set<Integer> addRelatedGoIds(Collection<Integer> pGoIdSet)
//	{
//		Set<Integer> lNewGoIdSet = new HashSet<Integer>(pGoIdSet);
//
//		for (Integer lGoId : pGoIdSet)
//		{
//			Set<Attach<Integer, Integer>> lGoIdAndDistanceSet = mGoIdToGoIdAndDistanceSetMap.get(lGoId);
//
//			if (lGoIdAndDistanceSet != null)
//				for (Attach<Integer, Integer> lGoIdAndDistance : lGoIdAndDistanceSet)
//				{
//					Integer lNewGoId = lGoIdAndDistance.getO();
//					Integer lDistance = lGoIdAndDistance.getV();
//					if (lDistance < 2)
//					{
//						lNewGoIdSet.add(lNewGoId);
//					}
//				}
//		}
//		return lNewGoIdSet;
//	}
//
//	/**
//	 * @param args
//	 * @throws IOException
//	 * @throws FileNotFoundException
//	 */
//	public static void main(String[] args)
//	{
//		try
//		{
//			Map<String, String> lArgumentsMap = getMapFromCommandLine(args);
//			new TrainNaiveBayesModel(lArgumentsMap).compute();
//		}
//		catch (Throwable e)
//		{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//	}
//
//	private static Map<String, String> getMapFromCommandLine(String[] pArguments)
//	{
//		Map<String, String> lParameterMap = new HashMap<String, String>();
//		for (String lArgument : pArguments)
//		{
//			lArgument = lArgument.trim();
//			if (lArgument.contains("="))
//			{
//				String[] lKeyValueArray = lArgument.split("=");
//				String lKey = lKeyValueArray[0];
//				String lValue = lKeyValueArray[1];
//				lParameterMap.put(lKey, lValue);
//			}
//			else
//			{
//				lParameterMap.put(lArgument, "yes");
//			}
//		}
//		return lParameterMap;
//	}
//
//}
