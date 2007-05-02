package org.royerloic.nlp;

/*
 * Created on May 5, 2005
 */

/**
 * Splits a text into sentences.
 * 
 * @author Joerg Hakenberg
 * 
 */

public class SentenceSplitter
{

	/**
	 * Splits a text around each punctuation mark (.!?;:) followed by space,
	 * newline, or return.
	 * 
	 * @param text -
	 *          input text
	 * @return list of sentences
	 */
	public static String[] naivSplit(String text)
	{
		final String[] list = text.split("[\r\n]+");
		String content = list[0];
		for (int i = 1; i < list.length; i++)
			content += " " + list[i];
		text = content;
		return text.split("[\\.\\!\\?\\:\\;][\\s\r\n]+");
	}

	/**
	 * Splits a text into sentences.
	 * 
	 * @param text
	 * @return String[] - the list of sentences
	 */
	public static String[] split(String text)
	{
		// transform multi-line texts into single line texts
		final String[] list = text.split("[\r\n]+");
		String content = list[0];
		for (int i = 1; i < list.length; i++)
			content += " " + list[i];
		text = content;

		// replace all duplicate white spaces - might occur when a multi-line text
		// was transformed into a one-line text
		text = text.replaceAll("\\t", " ");
		text = text.replaceAll("\\s\\s", " ");

		// check for and remove foreign language title markup (Medline specific!):
		// enclosed in square brackets "[text text text]"
		boolean foreignLanguageTitle = false;
		if ((text == null) || (text.length() == 0))
			return new String[]
			{ "" };
		if ((text.charAt(0) == '[') && (text.charAt(text.length() - 1) == ']'))
		{
			foreignLanguageTitle = true;
			text = text.substring(1, text.length() - 1);
		}

		// naively, mark all possible ends-of-sentences (. ; ! ?), followed by a
		// white space and an
		// upper case letter, number, or enclosing marks (" ' ( [ { ); keep the
		// puntucation
		text = text.replaceAll("([\\.\\!\\?\\;\\:])\\s([A-Z0-9\"\''\\[\\(\\{])", "$1###SPLIT###$2");
		text = text
				.replaceAll(
						"(\\.)(Conclusion|CONCLUSION|Result|RESULT|Motivation|MOTIVATION|Background|BACKGROUND|Method|METHOD|Setting|SETTING|Objective|OBJECTIVE)([A-Za-z0-9]*\\:*)\\s*",
						"$1###SPLIT###$2$3###SPLIT###");

		// now some false positive filtering
		// all false marks <punct><mark> should be replaced by <punct><space>
		// ".###SPLIT###" -> ". "
		//
		// single upper case letters (most time) belong to the following sentence
		// part: .. D. mel, Trevor P. Jackson, Dr. P. Peng, ..
		// same for Dr., Mr., Prof.
		text = text.replaceAll("(\\s[A-Z]|Dr|Drs|Prof|Profs|Mr|Mrs|Ms)\\.###SPLIT###", "$1. ");

		// no sentence boundary within open brackets ( )
		text = text.replaceAll("(\\([^\\)]*?[\\.\\!\\?\\;])###SPLIT###", "$1 ");
		// for some reason, we need to do this multiple times... why??? TODO put
		// into one expression
		text = text.replaceAll("(\\([^\\)]*[\\.\\!\\?\\;])###SPLIT###", "$1 ");
		text = text.replaceAll("(\\([^\\)]*[\\.\\!\\?\\;])###SPLIT###", "$1 ");
		text = text.replaceAll("(\\([^\\)]*[\\.\\!\\?\\;])###SPLIT###", "$1 ");
		text = text.replaceAll("(\\([^\\)]*[\\.\\!\\?\\;])###SPLIT###", "$1 ");
		text = text.replaceAll("(\\([^\\)]*[\\.\\!\\?\\;])###SPLIT###", "$1 ");
		text = text.replaceAll("(\\([^\\)]*[\\.\\!\\?\\;])###SPLIT###", "$1 ");
		text = text.replaceAll("(\\([^\\)]*[\\.\\!\\?\\;])###SPLIT###", "$1 ");
		// same for "xxx.xxx)"
		text = text.replaceAll("([\\.\\;\\!\\?])###SPLIT###([^\\(]*\\))", "$1 $2");
		text = text.replaceAll("([\\.\\;\\!\\?])###SPLIT###([^\\(]*\\))", "$1 $2");
		text = text.replaceAll("([\\.\\;\\!\\?])###SPLIT###([^\\(]*\\))", "$1 $2");

		// same for [ ]
		text = text.replaceAll("(\\[[^\\]]*?[\\.\\!\\?\\;])###SPLIT###", "$1 ");
		text = text.replaceAll("(\\[[^\\]]*[\\.\\!\\?\\;])###SPLIT###", "$1 ");
		text = text.replaceAll("(\\[[^\\]]*[\\.\\!\\?\\;])###SPLIT###", "$1 ");
		text = text.replaceAll("(\\[[^\\]]*[\\.\\!\\?\\;])###SPLIT###", "$1 ");
		text = text.replaceAll("(\\[[^\\]]*[\\.\\!\\?\\;])###SPLIT###", "$1 ");
		text = text.replaceAll("(\\[[^\\]]*[\\.\\!\\?\\;])###SPLIT###", "$1 ");
		text = text.replaceAll("(\\[[^\\]]*[\\.\\!\\?\\;])###SPLIT###", "$1 ");
		text = text.replaceAll("(\\[[^\\]]*[\\.\\!\\?\\;])###SPLIT###", "$1 ");
		// same for "xxx.xxx]"
		text = text.replaceAll("([\\.\\;\\!\\?])###SPLIT###([^\\[]*\\])", "$1 $2");
		text = text.replaceAll("([\\.\\;\\!\\?])###SPLIT###([^\\[]*\\])", "$1 $2");
		text = text.replaceAll("([\\.\\;\\!\\?])###SPLIT###([^\\[]*\\])", "$1 $2");

		// split when sentence ends within quotation: ...referred to here as the
		// "hairpin model." Here is another...
		text = text.replaceAll("([\\.\\!\\?\\;][\"])\\s*", "$1###SPLIT###");

		// text = text.replaceAll("(\\s[A-Z]\\.)###SPLIT###", "$1 ");

		// remove splits appearing before lower case letters, but not after ;
		// text = text.replaceAll("[^\\;]###SPLIT###([a-z])", " $1");
		// remove splits appearing before lower case letters, even not after ;
		// text = text.replaceAll("###SPLIT###([a-z])", " $1");

		// remove punctuation and white spaces at end of each sentence and at end of
		// string
		// TODO should remain intact, but CISTagger adds a new "." to each sentence
		// end,
		// thus we would have duplicate end marks! Solve this in CISTagger/RunTagger
		text = text.replaceAll("([\\!\\?\\;\\.\\s\\:]*)###SPLIT###", "###SPLIT###");
		text = text.replaceAll("([\\!\\?\\;\\.\\s\\:]*)$", "");

		// re-insert enclosing markup for foreign language title
		if (foreignLanguageTitle)
			text = "[ " + text + " ]";

		return text.split("###SPLIT###");
	}

}
