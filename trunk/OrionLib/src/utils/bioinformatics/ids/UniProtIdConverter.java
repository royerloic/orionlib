package utils.bioinformatics.ids;

/*
 * The AC (ACcession number) line lists the accession number(s) associated with an entry. The format of the AC line is:

 AC   AC_number_1;[ AC_number_2;]...[ AC_number_N;]

 An example of an accession number line is shown below:

 AC   P00321;

 Semicolons separate the accession numbers and a semicolon terminates the list. If necessary, more than one AC line can be used. Example:

 AC   Q16653; O00713; O00714; O00715; Q13054; Q13055; Q14855; Q92891;
 AC   Q92892; Q92893; Q92894; Q92895; Q93053; Q96KU9; Q96KV0; Q96KV1;
 AC   Q99605;

 The purpose of accession numbers is to provide a stable way of identifying entries from release to release. 
 It is sometimes necessary for reasons of consistency to change the names of the entries, 
 for example, to ensure that related entries have similar names. 

 However, an accession number is always conserved, and therefore allows unambiguous citation of entries.
 Researchers who wish to cite entries in their publications should always cite the first accession number. 
 This is commonly referred to as the 'primary accession number'. 'Secondary accession numbers' 
 are sorted alphanumerically.

 We strongly advise those users who have programs performing mappings of Swiss-Prot to another data resource 
 to use Swiss-Prot accession numbers to identify an entry.

 Entries will have more than one accession number if they have been merged or split. 
 For example, when two entries are merged into one, the accession numbers from both entries are stored 
 in the AC line(s).

 If an existing entry is split into two or more entries (a rare occurrence), 
 the original accession numbers are retained in all the derived entries and a new primary accession number 
 is added to all the entries.

 An accession number is dropped only when the data to which it was assigned have been completely removed 
 from the database. Accession numbers deleted from Swiss-Prot are listed in the document file delac_sp.txt 
 and those deleted from TrEMBL are listed in delac_tr.txt.

 Accession numbers consist of 6 alphanumerical characters in the following format:

 1 	2 	3 	4 	5 	6
 [O,P,Q]	[0-9]	[A-Z, 0-9]	[A-Z, 0-9]	[A-Z, 0-9]	[0-9]

 Here are some examples of valid accession numbers: P12345, Q1AAA9, O456A1 and P4A123.
 * 
 * 
 * */

public class UniProtIdConverter
{
	/**
	 * Converts a UniProtId
	 * 
	 * @param pUniProtId
	 * @return
	 */
	public static int convertUniProtIdToInteger(final String pUniProtId)
	{
		int lUniProtIntegerId = 0;
		for (int i = 0; i < 6; i++)
		{
			lUniProtIntegerId = lUniProtIntegerId * 36
													+ decodeLetter(pUniProtId.charAt(i));
		}
		return lUniProtIntegerId;
	}

	/**
	 * @param pUniProtIntegerId
	 * @return
	 */
	public static String convertIntegerToUniProtId(final int pUniProtIntegerId)
	{
		final char[] lUniProtIdCharArray = new char[6];
		int lValue = pUniProtIntegerId;
		for (int i = 0; i < 6; i++)
		{
			final int lTranslatedCharCode = lValue % 36;
			lValue = lValue / 36;
			lUniProtIdCharArray[5 - i] = encodeLetter(lTranslatedCharCode);
		}
		return new String(lUniProtIdCharArray);
	}

	private static int decodeLetter(final char pChar)
	{
		int lCode = pChar;
		if (lCode >= 65 && lCode <= 90)
		{
			lCode = lCode - 65 + 10;
		}
		else if (lCode >= 48 && lCode <= 57)
		{
			lCode = lCode - 48;
		}
		return lCode;
	}

	private static char encodeLetter(final int pTranslatedCharCode)
	{
		char lChar = '!';
		if (pTranslatedCharCode <= 9)
		{
			lChar = (char) (48 + pTranslatedCharCode);
		}
		else if (pTranslatedCharCode >= 10 && pTranslatedCharCode <= 36)
		{
			lChar = (char) (65 + pTranslatedCharCode - 10);
		}
		return lChar;
	}

	/**
	 * @param pArguments
	 * @throws InterruptedException
	 */
	public static void main(final String[] pArguments) throws InterruptedException
	{
		final String lA = "A18JX0";

		final int lAi = convertUniProtIdToInteger(lA);
		final String lAis = convertIntegerToUniProtId(lAi);

		if (!lA.equals(lAis))
		{
			System.out.println(lA + " is not equal to " + lAis);
		}

		final int lB = 1586917556;

		final String lBs = convertIntegerToUniProtId(lB);
		final int lBsi = convertUniProtIdToInteger(lBs);

		if (lB != lBsi)
		{
			System.out.println(lB + " is not equal to " + lBsi);
		}

	}
}
