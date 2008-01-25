package utils.nlp;

import java.util.HashMap;
import java.util.Map;

public class ArabicRomanConversion
{

	public static Map<Integer, String> sArabicToRomanMap = getArabicToRomanMap();
	public static Map<String, Integer> sRomanToArabicMap = getRomanToArabicMap();

	private static Map<Integer, String> getArabicToRomanMap()
	{
		final Map<Integer, String> lArabicToRomanMap = new HashMap<Integer, String>();
		lArabicToRomanMap.put(1, "I");
		lArabicToRomanMap.put(2, "II");
		lArabicToRomanMap.put(3, "III");
		lArabicToRomanMap.put(4, "IV");
		lArabicToRomanMap.put(5, "V");
		lArabicToRomanMap.put(6, "VI");
		lArabicToRomanMap.put(7, "VII");
		lArabicToRomanMap.put(8, "VIII");
		return lArabicToRomanMap;
	}

	private static Map<String, Integer> getRomanToArabicMap()
	{
		final Map<String, Integer> lRomanToArabicMap = new HashMap<String, Integer>();
		lRomanToArabicMap.put("I", 1);
		lRomanToArabicMap.put("II", 2);
		lRomanToArabicMap.put("III", 3);
		lRomanToArabicMap.put("IV", 4);
		lRomanToArabicMap.put("V", 5);
		lRomanToArabicMap.put("VI", 6);
		lRomanToArabicMap.put("VII", 7);
		lRomanToArabicMap.put("VIII", 8);
		return lRomanToArabicMap;
	}

}
