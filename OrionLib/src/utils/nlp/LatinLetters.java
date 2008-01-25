package utils.nlp;

import java.util.HashMap;
import java.util.Map;

public class LatinLetters
{

	public static Map<String, String> sLatinRegexMap = getLatinRegexMap();

	private static Map<String, String> getLatinRegexMap()
	{
		final Map<String, String> lLatinRegexMap = new HashMap<String, String>();
		lLatinRegexMap.put("(?:A|a)l(?:ph|f)a", "alpha");
		lLatinRegexMap.put("(?:B|b)eta", "beta");
		lLatinRegexMap.put("(?:G|g)amma", "gamma");
		lLatinRegexMap.put("(?:D|d)elta", "delta");
		lLatinRegexMap.put("(?:E|e)psilon", "epsilon");
		lLatinRegexMap.put("(?:Z|z)eta", "zeta");

		lLatinRegexMap.put("(?:E|e)ta", "eta");
		lLatinRegexMap.put("(?:T|t)heta", "theta");
		lLatinRegexMap.put("(?:I|i)ota", "iota");
		lLatinRegexMap.put("(?:K|k)appa", "kappa");
		lLatinRegexMap.put("(?:L|l)ambda", "lambda");
		lLatinRegexMap.put("(?:M|m)u", "mu");

		lLatinRegexMap.put("(?:N|n)u", "nu");
		lLatinRegexMap.put("(?:X|x)i", "xi");
		lLatinRegexMap.put("(?:O|o)micron", "omicron");
		lLatinRegexMap.put("(?:P|p)i", "pi");
		lLatinRegexMap.put("(?:R|r)ho", "rho");
		lLatinRegexMap.put("(?:S|s)igma", "sigma");

		lLatinRegexMap.put("(?:T|t)au", "tau");
		lLatinRegexMap.put("(?:U|u)psilon", "upsilon");
		lLatinRegexMap.put("(?:P|p)hi", "phi");
		lLatinRegexMap.put("(?:C|c)hi", "chi");
		lLatinRegexMap.put("(?:P|p)si", "psi");
		lLatinRegexMap.put("(?:O|o)mega", "omega");

		return lLatinRegexMap;
	}

	public static String getAllLatinRegex()
	{
		final StringBuffer lStringBuffer = new StringBuffer();
		for (final String lRegex : sLatinRegexMap.keySet())
		{
			lStringBuffer.append("(?:");
			lStringBuffer.append(lRegex);
			lStringBuffer.append(")");
			lStringBuffer.append("|");
		}
		lStringBuffer.delete(lStringBuffer.length() - 2, lStringBuffer.length());

		return lStringBuffer.toString();
	}

}
