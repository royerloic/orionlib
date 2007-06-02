//package org.royerloic.ml.bayes.goint;
//
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
//import org.royerloic.io.MatrixFile;
//import org.royerloic.nlp.PrecisionRecall;
//
//public class NaiveBayesClassifier
//{
//
//	private static final Double	cCutOff	= 0.1;
//
//	private static class GoIdAndDistance
//	{
//		Integer	mGoId;
//		Integer	mDistance;
//
//		public GoIdAndDistance(Integer pGoId, Integer pDistance)
//		{
//			mGoId = pGoId;
//			mDistance = pDistance;
//		}
//
//		@Override
//		public boolean equals(Object pObj)
//		{
//			if (this == pObj)
//				return true;
//			else if (this.hashCode() != ((GoIdAndDistance) pObj).hashCode())
//				return false;
//			else
//			{
//				GoIdAndDistance lGoIdAndDistance = (GoIdAndDistance) pObj;
//				return (this.mGoId.equals(lGoIdAndDistance.mGoId));
//			}
//		}
//
//		@Override
//		public int hashCode()
//		{
//			return mGoId.hashCode();
//		}
//
//		@Override
//		public String toString()
//		{
//			return "(GO:" + mGoId + " d=" + mDistance + ")";
//		}
//	}
//
//	private Map<Integer, Set<GoIdAndDistance>>	mGoIdToGoIdAndDistanceSetMap	= new HashMap<Integer, Set<GoIdAndDistance>>();
//
//	private static class GoIdPair
//	{
//		public int	mGoId1;
//		public int	mGoId2;
//
//		public GoIdPair(Integer pGoId1, Integer pGoId2)
//		{
//			super();
//			mGoId1 = pGoId1;
//			mGoId2 = pGoId2;
//		}
//
//		@Override
//		public boolean equals(Object pObj)
//		{
//			GoIdPair lGoIdPair = (GoIdPair) pObj;
//			return (this.mGoId1 == lGoIdPair.mGoId1 && this.mGoId2 == lGoIdPair.mGoId2)
//					|| (this.mGoId1 == lGoIdPair.mGoId2 && this.mGoId2 == lGoIdPair.mGoId1);
//
//		}
//
//		@Override
//		public int hashCode()
//		{
//			return mGoId1 ^ mGoId2;
//		}
//
//		@Override
//		public String toString()
//		{
//			return "[" + mGoId1 + " - " + mGoId2 + "]";
//		}
//	}
//
//	private Map<GoIdPair, Integer>	mGoIdPairToIndexMap				= new HashMap<GoIdPair, Integer>();
//	private Integer									mNextFreeIndex						= 0;
//
//	private double									mInteractionProbability;
//	private double									mNonInteractionProbability;
//	private Map<GoIdPair, Double>		mGoIdPairToLikelyhoodMap	= new HashMap<GoIdPair, Double>();
//
//	public NaiveBayesClassifier(Map<String, String> pParameters) throws IOException
//	{
//
//		String lModelFileName = pParameters.get("model");
//		File lModelFile = new File(lModelFileName);
//
//		List<List<String>> lModelMatrix = MatrixFile.readMatrixFromFile(lModelFile, false);
//
//		readModelFromMatrix(lModelMatrix);
//
//	}
//
//	private void readModelFromMatrix(List<List<String>> pMatrix)
//	{
//		List<String> lFirstLine = pMatrix.get(0);
//		mInteractionProbability = Double.parseDouble(lFirstLine.get(0));
//		List<String> lSecondLine = pMatrix.get(1);
//		mNonInteractionProbability = Double.parseDouble(lSecondLine.get(0));
//
//		for (List<String> lLine : pMatrix.subList(2, pMatrix.size()))
//		{
//			Integer lGoId1 = Integer.parseInt(lLine.get(0));
//			Integer lGoId2 = Integer.parseInt(lLine.get(1));
//			Double lLikelyhood = Double.parseDouble(lLine.get(4));
//			GoIdPair lGoIdPair = new GoIdPair(lGoId1, lGoId2);
//			mGoIdPairToLikelyhoodMap.put(lGoIdPair, lLikelyhood);
//		}
//	}
//
//	private Set<Integer> addRelatedGoIds(Collection<Integer> pGoIdSet)
//	{
//		Set<Integer> lNewGoIdSet = new HashSet<Integer>(pGoIdSet);
//
//		for (Integer lGoId : pGoIdSet)
//		{
//			Set<GoIdAndDistance> lGoIdAndDistanceSet = mGoIdToGoIdAndDistanceSetMap.get(lGoId);
//
//			if (lGoIdAndDistanceSet != null)
//				for (GoIdAndDistance lGoIdAndDistance : lGoIdAndDistanceSet)
//				{
//					Integer lNewGoId = lGoIdAndDistance.mGoId;
//					Integer lDistance = lGoIdAndDistance.mDistance;
//					if (lDistance <= 0)
//					{
//						lNewGoIdSet.add(lNewGoId);
//					}
//				}
//		}
//		return lNewGoIdSet;
//	}
//
//	public Double logLikelyhood(String pUniProtId1, String pUniProtId2)
//	{
//		List<Integer> lGoIdList1 = mUniprot2GoListMap.get(pUniProtId1);
//		List<Integer> lGoIdList2 = mUniprot2GoListMap.get(pUniProtId2);
//
//		if (lGoIdList1 != null && lGoIdList2 != null)
//		{
//
//			Set<Integer> lGoIdSet1 = addRelatedGoIds(lGoIdList1);
//			Set<Integer> lGoIdSet2 = addRelatedGoIds(lGoIdList2);
//
//			Double lLikelyHood = Math.log(mInteractionProbability / mNonInteractionProbability);
//
//			for (Integer lGoId1 : lGoIdSet1)
//				for (Integer lGoId2 : lGoIdSet2)
//				{
//					GoIdPair lGoIdPair = new GoIdPair(lGoId1, lGoId2);
//					final Double lGoIdPairLikelyHood = mGoIdPairToLikelyhoodMap.get(lGoIdPair);
//					if (lGoIdPairLikelyHood != null)
//					{
//						// System.out.println(lLikelyHood);
//						lLikelyHood += Math.log(lGoIdPairLikelyHood);
//					}
//				}
//
//			return lLikelyHood;
//		}
//		else
//			return null;
//	}
//
//	private void evaluateOnTraining(Map<String, String> pParameters) throws FileNotFoundException, IOException
//	{
//		Set<UniProtPair> lUniProtInteractingPairsSet = new HashSet<UniProtPair>();
//		Set<UniProtPair> lUniProtNonInteractingPairsSet = new HashSet<UniProtPair>();
//
//		{
//			String lPairsFileName = pParameters.get("pairs");
//			File lPairsFile = new File(lPairsFileName);
//
//			List<List<String>> lPairsMatrix = MatrixFile.readMatrixFromFile(lPairsFile, false);
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
//				Set<UniProtPair> lPairSet = new HashSet<UniProtPair>();
//				if (lValidatedUniProtIdList.size() > 1)
//				{
//					for (String lUniProtId1 : lValidatedUniProtIdList)
//						for (String lUniProtId2 : lValidatedUniProtIdList)
//							if (!lUniProtId1.equals(lUniProtId2))
//							{
//								UniProtPair lPair = new UniProtPair(lUniProtId1, lUniProtId2);
//								lPairSet.add(lPair);
//							}
//				}
//				else if (lValidatedUniProtIdList.size() == 1)
//				{
//					String lUniProtId = lValidatedUniProtIdList.get(0);
//					UniProtPair lPair = new UniProtPair(lUniProtId, lUniProtId);
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
//								UniProtPair lPair = new UniProtPair(lUniProtId1, lUniProtId2);
//								lUniProtNonInteractingPairsSet.add(lPair);
//							}
//				}
//			}
//		}
//		lUniProtNonInteractingPairsSet.removeAll(lUniProtInteractingPairsSet);
//
//		for (double lFactor = 0; lFactor < 0.1; lFactor += 0.01)
//		{
//			double FMeasure = evaluateTraining(lUniProtInteractingPairsSet, lUniProtNonInteractingPairsSet,
//					lFactor, lFactor);
//			System.out.println("lFactor=" + lFactor + " \t\tFMeasure=" + FMeasure);
//		}
//
//	}
//
//	private double evaluateTraining(Set<UniProtPair> pUniProtInteractingPairsSet,
//																	Set<UniProtPair> pUniProtNonInteractingPairsSet,
//																	double pFactor1,
//																	double pFactor2)
//	{
//		double lMaxI = 15988 * pFactor1;
//		double lMinI = 10089 * pFactor2;
//
//		double lTruePositives = 0;
//		double lTrueNegatives = 0;
//		double lFalsePositives = 0;
//		double lFalseNegatives = 0;
//		double lDontKnow = 0;
//
//		{
//			double lMaxLikelyHood = Double.NEGATIVE_INFINITY;
//			double lMinLikelyHood = Double.POSITIVE_INFINITY;
//			for (UniProtPair lPair : pUniProtInteractingPairsSet)
//			{
//				String lUniProtId1 = lPair.mUniProt1;
//				String lUniProtId2 = lPair.mUniProt2;
//				double lLikelyhood = logLikelyhood(lUniProtId1, lUniProtId2);
//				lMinLikelyHood = Math.min(lMinLikelyHood, lLikelyhood);
//				lMaxLikelyHood = Math.max(lMaxLikelyHood, lLikelyhood);
//				if (lLikelyhood > lMaxI)
//					lTruePositives++;
//				else if (lLikelyhood < -lMinI)
//					lFalsePositives++;
//				else
//					lDontKnow++;
//			}
//			// System.out.println("lMaxLikelyHood=" + lMaxLikelyHood);
//			// System.out.println("lMinLikelyHood=" + lMinLikelyHood);
//		}
//
//		{
//			double lMaxLikelyHood = Double.NEGATIVE_INFINITY;
//			double lMinLikelyHood = Double.POSITIVE_INFINITY;
//			for (UniProtPair lPair : pUniProtNonInteractingPairsSet)
//			{
//				String lUniProtId1 = lPair.mUniProt1;
//				String lUniProtId2 = lPair.mUniProt2;
//				double lLikelyhood = logLikelyhood(lUniProtId1, lUniProtId2);
//				lMinLikelyHood = Math.min(lMinLikelyHood, lLikelyhood);
//				lMaxLikelyHood = Math.max(lMaxLikelyHood, lLikelyhood);
//				if (lLikelyhood < -lMinI)
//					lTrueNegatives++;
//				else if (lLikelyhood > lMaxI)
//					lFalseNegatives++;
//				else
//					lDontKnow++;
//			}
//			// System.out.println("lMaxLikelyHood=" + lMaxLikelyHood);
//			// System.out.println("lMinLikelyHood=" + lMinLikelyHood);
//		}
//
//		// System.out.println("lTruePositives=" + lTruePositives);
//		// System.out.println("lTrueNegatives=" + lTrueNegatives);
//		// System.out.println("lFalseNegatives=" + lFalseNegatives);
//		// System.out.println("lFalsePositives=" + lFalsePositives);
//
//		double lRight = lTruePositives + lTrueNegatives;
//		double lWrong = lFalseNegatives + lFalsePositives;
//
//		// System.out.println("Right=" + lRight);
//		// System.out.println("Wrong=" + lWrong);
//		// System.out.println("DontKnow=" + lDontKnow);
//
//		double lTotal = lRight + lWrong;
//		double lProbability = lRight / lTotal;
//		double lRecall = lTotal / (lTotal + lDontKnow);
//
//		double lFHalfMeasure = PrecisionRecall.fmeasurePrecision(lProbability, lRecall);
//		//
//		System.out.println("Probability of correct prediction =" + lProbability);
//		System.out.println("Probability of providing a prediction =" + lRecall);
//		// System.out.println("F0.5 Measure =" + lFHalfMeasure);
//
//		return lFHalfMeasure;
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
//			NaiveBayesClassifier lNaiveBayesClassifier = new NaiveBayesClassifier(lArgumentsMap);
//			System.out.println(lNaiveBayesClassifier.logLikelyhood("P12294", "P12398"));
//			System.out.println(lNaiveBayesClassifier.logLikelyhood("P12294", "P12962"));
//			System.out.println(lNaiveBayesClassifier.logLikelyhood("P12294", "P07260"));
//
//			lNaiveBayesClassifier.evaluateOnTraining(lArgumentsMap);
//
//			/*************************************************************************
//			 * List<String> lList1 = new ArrayList<String>(); List<String> lList2 =
//			 * new ArrayList<String>();
//			 * 
//			 * lNaiveBayesClassifier.rank(lList1,lList2);/
//			 ************************************************************************/
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
