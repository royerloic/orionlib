package org.royerloic.string;

import java.util.List;

public class XmlStringUtils
{
	public static final List<String> getTagsContentList(String pXML, String pTag)
	{
		String lStartTag = "<" + pTag + ">";
		String lEndTag = "</" + pTag + ">";
		String lNoTagRegex = "[^<>]+";
		String lTagRegex = lStartTag + "(" + lNoTagRegex + ")" + lEndTag;

		List<String> lTagsContentList = StringUtils.findAllmatches(pXML, lTagRegex, 1);

		return lTagsContentList;
	}
}
