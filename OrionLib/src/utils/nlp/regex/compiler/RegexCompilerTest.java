package utils.nlp.regex.compiler;

import java.io.IOException;

import junit.framework.TestCase;

public class RegexCompilerTest extends TestCase
{
	public void testRealNumber() throws IOException
	{
		final RegexCompiler lRegexCompiler = new RegexCompiler("test.regex.txt");

		assertTrue(lRegexCompiler.getRegex("regex1").equals("(?:[0-9]+)"));
		assertTrue(lRegexCompiler.getRegex("regex2").equals("(?:A(?:[0-9]+)B)"));
		assertTrue(lRegexCompiler.getRegex("regex3").equals("(?:(?:A(?:[0-9]+)B)|(?:[0-9]+))"));
		assertTrue(lRegexCompiler.getRegex("regex4").contains("a"));
		assertTrue(lRegexCompiler.getRegex("regex4").contains("b"));
		assertTrue(lRegexCompiler.getRegex("regex4").contains("c"));
		assertTrue(lRegexCompiler.getRegex("regex4").contains("d"));
		System.out.println(lRegexCompiler.getRegex("regex5"));

		assertTrue(lRegexCompiler.getRegex("regeximported").equals("(?:this is imported)"));
		System.out.println(lRegexCompiler.getRegex("regeximported"));

		assertTrue(lRegexCompiler.getRegex("regex6").contains("d"));

	}

}
