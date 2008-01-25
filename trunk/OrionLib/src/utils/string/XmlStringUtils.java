package utils.string;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

public class XmlStringUtils
{
	public static final List<String> getTagsContentList(final String pXML,
																											final String pTag)
	{
		final String lStartTag = "<" + pTag + ">";
		final String lEndTag = "</" + pTag + ">";
		final String lNoTagRegex = "[^<>]+";
		final String lTagRegex = lStartTag + "(" + lNoTagRegex + ")" + lEndTag;

		final List<String> lTagsContentList = StringUtils.findAllmatches(	pXML,
																																			lTagRegex,
																																			1);

		return lTagsContentList;
	}

	public static final List<String> getTagsContentListSlow(final String pXML,
																													final String pTag)
	{
		XMLReader parser;
		final StringBuilder lStringBuilder = new StringBuilder();
		final ArrayList<String> lStringList = new ArrayList<String>();

		try
		{
			parser = XMLReaderFactory.createXMLReader();
			ContentHandler lContentHandler = new DefaultHandler()
			{

				private boolean mInTag;

				@Override
				public void characters(char[] pCh, int pStart, int pLength) throws SAXException
				{
					if (mInTag)
						lStringBuilder.append(pCh, pStart, pLength);
				}

				@Override
				public void startElement(	String pUri,
																	String pLocalName,
																	String pName,
																	Attributes pAtts) throws SAXException
				{
					if (pLocalName.equals(pTag))
						mInTag = true;

				}

				@Override
				public void endElement(String pUri, String pLocalName, String pName) throws SAXException
				{
					if (pLocalName.equals(pTag))
					{
						mInTag = false;
						lStringList.add(lStringBuilder.toString());
						lStringBuilder.setLength(0);
					}

				}

			};

			parser.setContentHandler(lContentHandler);
			ByteArrayInputStream lByteArrayInputStream = new java.io.ByteArrayInputStream(pXML.getBytes());
			InputSource lInputSource = new InputSource(lByteArrayInputStream);
			parser.parse(lInputSource);

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return lStringList;
	}

}
