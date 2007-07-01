package utils.regex;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Ignore;
import org.junit.Test;

import utils.regex.QuantityRegexes;


public class QuantityRegexesTest
{
	@Test
	@Ignore
	public void testRealNumber()
	{
		assertTrue(QuantityRegexes.isRealNumber("0"));
		assertFalse(QuantityRegexes.isRealNumber(" 0"));
		assertFalse(QuantityRegexes.isRealNumber("0 "));
		assertTrue(QuantityRegexes.isRealNumber("1"));
		assertFalse(QuantityRegexes.isRealNumber("1."));
		assertTrue(QuantityRegexes.isRealNumber("1.0"));
		assertTrue(QuantityRegexes.isRealNumber("1.00"));
		assertTrue(QuantityRegexes.isRealNumber("01.00"));
		assertTrue(QuantityRegexes.isRealNumber("123"));
		assertTrue(QuantityRegexes.isRealNumber("123.456"));
		assertFalse(QuantityRegexes.isRealNumber("123. 456"));
		assertTrue(QuantityRegexes.isRealNumber("123.3"));
		assertTrue(QuantityRegexes.isRealNumber("+123.3"));
		assertTrue(QuantityRegexes.isRealNumber("+ 123.3"));
		assertTrue(QuantityRegexes.isRealNumber("-123.3"));
		assertTrue(QuantityRegexes.isRealNumber("- 123.3"));
	}

	@Test
	@Ignore
	public void testUnits()
	{
		assertFalse(QuantityRegexes.isUnit("0"));
		assertTrue(QuantityRegexes.isUnit("%"));
		assertFalse(QuantityRegexes.isUnit("10"));
		assertFalse(QuantityRegexes.isUnit(""));
		assertFalse(QuantityRegexes.isUnit("10.192"));
		assertTrue(QuantityRegexes.isUnit("nm"));
		assertTrue(QuantityRegexes.isUnit("mm"));

	}

	@Test
	@Ignore
	public void testQuantity()
	{
		// assertFalse(QuantityRegexes.isQuantity("0"));
		assertTrue(QuantityRegexes.isQuantity("10 %"));
		// assertFalse(QuantityRegexes.isQuantity("10"));
		assertFalse(QuantityRegexes.isQuantity(""));
		assertFalse(QuantityRegexes.isQuantity("10.192"));
		assertTrue(QuantityRegexes.isQuantity("0 nm"));
		assertTrue(QuantityRegexes.isQuantity("0.1 mm"));
		assertTrue(QuantityRegexes.isQuantity("0.1cm"));
		assertTrue(QuantityRegexes.isQuantity("+10.1 angstrom"));
		assertTrue(QuantityRegexes.isQuantity("-10.1 angstrom"));
		assertTrue(QuantityRegexes.isQuantity("-130.1 mg/L"));
		assertTrue(QuantityRegexes.isQuantity("-130.1 L/kg/mol"));
		assertTrue(QuantityRegexes.isQuantity("-130.1 L-1kg-1"));
		assertTrue(QuantityRegexes.isQuantity("-130.1 L/mol2"));
		assertTrue(QuantityRegexes.isQuantity("130.1 cycles/mol2"));
		assertTrue(QuantityRegexes.isQuantity("153-residue"));
		assertTrue(QuantityRegexes.isQuantity("359 amino acids"));
		assertTrue(QuantityRegexes.isQuantity("2.1 kbp"));
		
		
	}

}

// private static final String cPostFixedUnitsPatternString =
// "(nm|mm|cm|(�|a|A|�)ngstr(o|�)m(s?)|�|mM|(F|f)ermi|(-?)kD(a?)|mg/L|BCE/nM|muM|"+
// "(H|h)z|(K|k)g|mg|g|eV|mL|ppm|(y|Y)ears|(d|D)ays|s|min|fold|%)";
// private static final String cPreFixedUnitsPatternString = "(pH)";

