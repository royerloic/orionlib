package org.royerloic.string;

import java.util.List;

public class XmlStringUtils
{
	public static final List<String> getTagsContentList(final String pXML, final String pTag)
	{
		final String lStartTag = "<" + pTag + ">";
		final String lEndTag = "</" + pTag + ">";
		final String lNoTagRegex = "[^<>]+";
		final String lTagRegex = lStartTag + "(" + lNoTagRegex + ")" + lEndTag;

		final List<String> lTagsContentList = StringUtils.findAllmatches(pXML, lTagRegex, 1);

		return lTagsContentList;
	}
}
