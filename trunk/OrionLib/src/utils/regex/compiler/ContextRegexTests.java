package utils.regex.compiler;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

/**
 */
public class ContextRegexTests
{

	private ContextRegex mContextRegexFilter;

	@Test
	public void testExample() throws IOException
	{
		InputStream lInputStream = ContextRegexTests.class.getResourceAsStream("/utils/regex/compiler/Test.3rules.txt");
		mContextRegexFilter = new ContextRegex(lInputStream);

		// raw match without left or right trailing characters
		assertMatch("A", "a", "0");
		assertMatch("b", "1", "A0");
		assertNoMatch("3", "a", "0");

		// with trailing caracters and delimiting whitespaces
		assertMatch("zxczxc A", "a", "0 zxczxc");
		assertMatch("zxczxc b", "1", "A0  zxczxc");
		assertNoMatch("zxczxc 3", "a", "0 zxcxc");

		// without whitespaces
		assertMatch("zxczxcA", "a", "0zxczxc");
		assertMatch("zxczxcb", "1", "A0xczxc");
		assertNoMatch("zxczxc3", "a", "0zxcxc");

		// testing set imports:
		assertMatch("a", "Tree", "A");
		assertMatch("a", "elefant", "AB");
		assertMatch("a", "blue", "0");
		assertMatch("a", "sun", "01");

		// testing regex imports:
		assertMatch("a", "regeximports", "A0B");
		assertMatch("b", "regeximports", "blue");
		assertMatch("c", "regeximports", "a");
		assertMatch("c", "regeximports", "b");
		assertMatch("c", "regeximports", "c");
		assertMatch("c", "regeximports", "d");
		assertMatch("c", "regeximports", "e");

		// testing builtins:
		assertMatch(" x", "x", "x\t");
		assertNoMatch("x", "x", "x");

		assertMatch("-y", "y", "y=");
		assertNoMatch("y", "y", "y");

		assertMatch(",z", "z", "z.");
		assertNoMatch("z", "z", "z");

		assertMatch("Elefant t", "t", "t more");
		assertNoMatch("t", "t", "t");

		assertMatch("\nElefant t", "t", "t more");
	}

	private void assertMatch(	final CharSequence pPreFix,
														final CharSequence pMatch,
														final CharSequence pPostFix)
	{
		assertTrue(mContextRegexFilter.match(pPreFix, pMatch, pPostFix));
	}

	private void assertNoMatch(	final CharSequence pPreFix,
															final CharSequence pMatch,
															final CharSequence pPostFix)
	{
		assertTrue(!mContextRegexFilter.match(pPreFix, pMatch, pPostFix));
	}

}
