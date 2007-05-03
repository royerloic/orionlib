/*
 * Created on 13.5.2005 by Dipl.-Inf. MSc. Ing Loic Royer
 */
package org.royerloic.web.google;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.royerloic.web.WebPageFetcher;

public class Google
{
	private String							mSearchString;

	private String							mGoogleServer;

	private String							mFileType;

	private String							mLocalCode;

	private static final String	cGOOGLE_MAIN_SERVER	= "www.google.com";

	private WebPageFetcher			mWebPageFetcher;

	private String							mHeader;

	private String							mContent;

	private Pattern							mPattern;

	private Matcher							mMatcher;

	private Google()
	{
		super();
		mGoogleServer = cGOOGLE_MAIN_SERVER;
		mLocalCode = "en";
		mFileType = "";
	}

	private Google(final String pSearchString)
	{
		this();
		mSearchString = pSearchString;
	}

	private Google(final String pSearchString, final String pFileType)
	{
		this();
		mSearchString = pSearchString;
		mFileType = pFileType;
	}

	public void doQuery() throws IOException
	{
		final String lQuery = buildGoogleQueryURL(mGoogleServer, mLocalCode, mSearchString, mFileType);
		System.out.println("Query:" + lQuery);
		mWebPageFetcher = new WebPageFetcher(lQuery);
		mWebPageFetcher.setReferer(mGoogleServer);
		mHeader = mWebPageFetcher.getPageHeader();
		System.out.println(mHeader);
		mContent = mWebPageFetcher.getPageContent();
		mMatcher = mPattern.matcher(mContent);
	}

	private String buildGoogleQueryURL(	final String pGoogleServer,
																			final String pLocalCode,
																			final String pSearchString,
																			final String pFileType)
	{
		String lTypeString = "";

		if (!pFileType.equals(""))
			lTypeString = "+filetype%3A" + pFileType;

		final String lSearchString = pSearchString.trim().replaceAll("\\s+", "+");
		final String lQueryUrl = "http://" + mGoogleServer + "/search?hl=" + pLocalCode + "&q=" + lSearchString
				+ lTypeString + "&btnG=Search";

		return lQueryUrl;
	}

	public void grabUrlFromFileType(final String pFileType)
	{
		mPattern = Pattern.compile("http://[^<>]+\\." + pFileType);
	}

	public String nextMatch()
	{
		if (mMatcher.find())
			return mMatcher.group();
		else
			return null;
	}

	public String getFileType()
	{
		return mFileType;
	}

	public void setFileType(final String pFileType)
	{
		mFileType = pFileType;
	}

	public String getGoogleServer()
	{
		return mGoogleServer;
	}

	public void setGoogleServer(final String pGoogleServer)
	{
		mGoogleServer = pGoogleServer;
	}

	public String getLocalCode()
	{
		return mLocalCode;
	}

	public void setLocalCode(final String pLocalCode)
	{
		mLocalCode = pLocalCode;
	}

	public String getContent()
	{
		return mContent;
	}

	public String getHeader()
	{
		return mHeader;
	}

	/**
	 * Test: tries to get a list of 10 xml files from google
	 * 
	 * @param aArguments
	 * 
	 * @throws IOException
	 */
	public static void main(final String[] aArguments) throws IOException
	{
		final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		final String lQuery = in.readLine();
		final Google mGoogle = new Google(lQuery, "rss");
		mGoogle.grabUrlFromFileType("rss");
		mGoogle.doQuery();

		String lLink = null;
		System.out.println(mGoogle.getContent());
		while ((lLink = mGoogle.nextMatch()) != null)
			System.out.println(lLink);
	}

}
