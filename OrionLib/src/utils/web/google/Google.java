/*
 * Created on 13.5.2005 by Dipl.-Inf. MSc. Ing Loic Royer
 */
package utils.web.google;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import utils.string.StringUtils;
import utils.web.WebPageFetcher;

/**
 * 
 * http://www.google.com/search? as_q=Test (query string) &hl=en (language)
 * &num=10 (number of results [10,20,30,50,100]) &btnG=Google+Search &as_epq=
 * (complete phrase) &as_oq= (at least one) &as_eq= (excluding) &lr= (language
 * results. [lang_countrycode]) &as_ft=i (filetype include or exclude. [i,e])
 * &as_filetype= (filetype extension) &as_qdr=all (date [all,M3,m6,y]) &as_nlo=
 * (number range, low) &as_nhi= (number range, high) &as_occt=any (terms occur
 * [any,title,body,url,links]) &as_dt=i (restrict by domain [i,e])
 * &as_sitesearch= (restrict by [site]) &as_rights= (usage rights
 * [cc_publicdomain, cc_attribute, cc_sharealike, cc_noncommercial,
 * cc_nonderived] &safe=images (safesearch [safe=on,images=off]) &as_rq=
 * (similar pages) &as_lq= (pages that link) &gl=us (country)
 * 
 * 
 */

public class Google implements Iterable<Google.GoogleResult>
{
	private String mSearchString;

	private String mGoogleServer;

	private String mFileType;

	private String mLocalCode;

	private static final String cGOOGLE_MAIN_SERVER = "www.google.com";

	private WebPageFetcher mWebPageFetcher;

	private String mHeader;

	private StringBuilder mContent;

	private Pattern mPattern;

	private Matcher mMatcher;

	public class GoogleResult
	{
		String mSnippet;
		String mUrl;
	}

	public Google()
	{
		super();
		mGoogleServer = cGOOGLE_MAIN_SERVER;
		mLocalCode = "en";
		mFileType = "";
	}

	public Google(final String pSearchString)
	{
		this();
		mSearchString = pSearchString;
		mFileType = "";
	}

	public Google(final String pSearchString, final String pFileType)
	{
		this();
		mSearchString = pSearchString;
		mFileType = pFileType;
	}

	static final Random cRandom = new Random(System.currentTimeMillis());

	public void doQuery() throws IOException
	{
		final String lQuery = buildGoogleQueryURL(mGoogleServer,
																							mLocalCode,
																							mSearchString,
																							mFileType);
		// System.out.println("Query:" + lQuery);

		try
		{
			Thread.sleep(cRandom.nextInt(30) + 1);
		}
		catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		mWebPageFetcher = new WebPageFetcher(lQuery);
		mWebPageFetcher.setReferer(mGoogleServer);
		mHeader = mWebPageFetcher.getPageHeader();
		// System.out.println(mHeader);
		mContent = mWebPageFetcher.getPageContent();
		if (mPattern != null)
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
		final String lQueryUrl = "http://" + mGoogleServer
															+ "/search?hl="
															+ pLocalCode
															+ "&q="
															+ lSearchString
															+ lTypeString
															+ "&btnG=Search";

		return lQueryUrl;
	}

	public void grabUrlFromFileType(final String pFileType)
	{
		mPattern = Pattern.compile("http://[^<>]+\\." + pFileType);
	}

	public String[] tokenize()
	{
		String[] lStringArray = StringUtils.split(mContent,
																							"(\\<\\!\\-\\-[a-z]\\-\\-\\>)|(\\<br\\>)",
																							0);
		return lStringArray;
	}

	static Pattern cScorePattern = Pattern.compile(	"Results .*? of (?:about )?\\<b\\>([0-9\\,]+)\\<\\/b\\> for \\<b\\>",
																									Pattern.MULTILINE);
	static Pattern cNoMatchPattern = Pattern.compile(	"did not match any documents",
																										Pattern.MULTILINE);

	public int getScore()
	{
		Matcher lMatcher = cScorePattern.matcher(mContent);
		if (lMatcher.find())
		{
			final String lScoreString = lMatcher.group(1);
			final String lScoreStringWithoutComma = lScoreString.replace(",", "");
			final int lScore = Integer.parseInt(lScoreStringWithoutComma);
			return lScore;
		}
		// System.out.println(mSearchString);
		// System.out.println(mContent);
		// throw new RuntimeException("cannot find score");
		Matcher lMatcherNoMatch = cNoMatchPattern.matcher(mContent);
		if (lMatcherNoMatch.find())
		{
			return 0;
		}
		throw new RuntimeException("cannot find score");
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

	public StringBuilder getContent()
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

	public Iterator<GoogleResult> iterator()
	{
		// TODO Auto-generated method stub
		return null;
	}

}
