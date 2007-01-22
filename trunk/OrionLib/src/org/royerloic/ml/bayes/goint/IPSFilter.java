//package org.royerloic.ml.bayes.goint;
//
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import org.royerloic.io.MatrixFile;
//import org.royerloic.string.StringUtils;
//import org.royerloic.utils.Timer;
//
//public class IPSFilter
//{
//
//	private static void filter(NaiveBayesClassifier pNaiveBayesClassifier, File pInputFile, File pOutPutFile)
//			throws FileNotFoundException, IOException
//	{
//
//		List<List<String>> lInputMatrix = MatrixFile.readMatrixFromFile(pInputFile, false);
//
//		Timer lTimer = new Timer();
//		double lPercent = 0;
//		lTimer.start();
//		for (List<String> lLine : lInputMatrix)
//		{
//			String PubMedIdString = lLine.get(0);
//			Integer PubMedId = Integer.parseInt(PubMedIdString);
//
//			String lUniProtIdArrayString1 = lLine.get(1);
//			String[] lUniProtIdArray1 = StringUtils.split(lUniProtIdArrayString1, ";", 0);
//			List<String> lUniProtIdList1 = Arrays.asList(lUniProtIdArray1);
//
//			String lUniProtIdArrayString2 = lLine.get(2);
//			String[] lUniProtIdArray2 = StringUtils.split(lUniProtIdArrayString2, ";", 0);
//			List<String> lUniProtIdList2 = Arrays.asList(lUniProtIdArray2);
//
//			boolean isAtLeastOnePrediction = false;
//			String lBestUniProtId1 = null;
//			String lBestUniProtId2 = null;
//			Double lBestLogLikelyhood = Double.NEGATIVE_INFINITY;
//			for (String lUniProtId1 : lUniProtIdList1)
//				for (String lUniProtId2 : lUniProtIdList2)
//				{
//					Double lLogLikelyhood = pNaiveBayesClassifier.logLikelyhood(lUniProtId1, lUniProtId2);
//					if (lLogLikelyhood != null)
//						if (lLogLikelyhood > lBestLogLikelyhood)
//						{
//							isAtLeastOnePrediction |= true;
//							lBestLogLikelyhood = lLogLikelyhood;
//							lBestUniProtId1 = lUniProtId1;
//							lBestUniProtId2 = lUniProtId2;
//						}
//				}
//
//			if (isAtLeastOnePrediction)
//			{
//				lLine.set(1, lBestUniProtId1);
//				lLine.set(2, lBestUniProtId2);
//				lLine.set(3, lBestLogLikelyhood.toString());
//			}
//			else
//			{
//				lLine.set(3, "0");
//			}
//			System.out.println(lLine);
//
//			lPercent += 1 / ((double) lInputMatrix.size());
//			System.out.println(lTimer.getESTString(lPercent));
//		}
//
//		MatrixFile.writeMatrixToFile(lInputMatrix, pOutPutFile);
//
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
//
//			String lInputFileName = lArgumentsMap.get("in");
//			File lInputFile = new File(lInputFileName);
//			String lOutputFileName = lArgumentsMap.get("out");
//			File lOutputFile = new File(lOutputFileName);
//
//			NaiveBayesClassifier lNaiveBayesClassifier = new NaiveBayesClassifier(lArgumentsMap);
//
//			IPSFilter.filter(lNaiveBayesClassifier, lInputFile, lOutputFile);
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
