// ©2006 Transinsight GmbH - www.transinsight.com - All rights reserved.
package utils.regex.compiler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

/**
 */
public class RegexCompilerTests
{

	@Test
	public void testExample() throws IOException
	{
		RegexCompiler lRegexCompiler = new RegexCompiler(RegexCompilerTests.class
				.getResourceAsStream("/utils/regex/compiler/test.regex.txt"));

		assertEquals("(?:[0-9]+)",lRegexCompiler.getRegex("regex1"));
		assertEquals("(?:A(?:[0-9]+)B)",lRegexCompiler.getRegex("regex2"));
		assertEquals("(?:(?:A(?:[0-9]+)B)|(?:[0-9]+))",lRegexCompiler.getRegex("regex3"));
		
		assertTrue(lRegexCompiler.getRegex("regex4").contains("a"));
		assertTrue(lRegexCompiler.getRegex("regex4").contains("b"));
		assertTrue(lRegexCompiler.getRegex("regex4").contains("c"));
		assertTrue(lRegexCompiler.getRegex("regex4").contains("d"));		
		
		assertTrue(lRegexCompiler.getRegex("regex5").contains("blue"));
		assertTrue(lRegexCompiler.getRegex("regex5").contains("elefant"));
		assertTrue(lRegexCompiler.getRegex("regex5").contains("Tree"));
		assertTrue(lRegexCompiler.getRegex("regex5").contains("sun"));
		
		assertTrue(lRegexCompiler.getRegex("regex6").contains("a"));
		assertTrue(lRegexCompiler.getRegex("regex6").contains("b"));
		assertTrue(lRegexCompiler.getRegex("regex6").contains("c"));
		assertTrue(lRegexCompiler.getRegex("regex6").contains("d"));
		assertTrue(lRegexCompiler.getRegex("regex6").contains("e"));
		
		assertEquals("(?:this is imported)", lRegexCompiler.getRegex("regeximported"));
				
	}

}
