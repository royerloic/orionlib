package utils.bioinformatics;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class RegexPatternsTest
{

	@Test
	public void testIsId()
	{
		assertTrue(RegexPatterns.isId("OTTHUMG00000130507"));

		assertFalse(RegexPatterns.isAbbreviationType1("AN"));
		assertTrue(RegexPatterns.isAbbreviationType1("ANI"));

		assertTrue(RegexPatterns.isAbbreviationType2("RAB5"));
		assertTrue(RegexPatterns.isAbbreviationType2("RAB-5"));
		assertTrue(RegexPatterns.isAbbreviationType2("RAB/5"));
		assertTrue(RegexPatterns.isAbbreviationType2("Uae1"));
		assertTrue(RegexPatterns.isAbbreviationType2("ARP2/3"));
		assertTrue(RegexPatterns.isAbbreviationType2("UNQ155/PRO181"));
		assertTrue(RegexPatterns.isAbbreviationType2("ETBR-LP-2"));
		assertTrue(RegexPatterns.isAbbreviationType2("TLiSA1"));
		assertTrue(RegexPatterns.isAbbreviationType2("p15"));

		assertTrue(RegexPatterns.isAbbreviationType3("MAGE-C1"));
		// assertTrue(RegexPatterns.isAbbreviationType3("BCL2-like 10"));
		assertTrue(RegexPatterns.isAbbreviationType3("1-AGPAT 2"));
		assertTrue(RegexPatterns.isAbbreviationType3("p21-Arc"));

	}

	@Test
	public void testIsEnglishWord()
	{
		// fail("Not yet implemented");
	}

	@Test
	public void testIsMultiWord()
	{
		// fail("Not yet implemented");
	}

	@Test
	public void testIsGarbage()
	{
		// fail("Not yet implemented");
	}

}
