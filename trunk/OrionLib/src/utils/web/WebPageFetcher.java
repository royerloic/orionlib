/**
 * Created on 11 mai 2005 By Dipl.-Inf. MSC. Ing. Loic Royer
 * 
 */
package utils.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Fetches the HTML content of a web page as a String.
 */
public final class WebPageFetcher
{

	private URL									mURL;

	private static final String	cHTTP								= "http";

	private static final String	cNEWLINE						= System.getProperty("line.separator");

	public static final String	cUSERAGENT_FIREFOX	= "Mozilla/5.0 (Windows; U; Windows NT 5.0; de-DE; rv:1.7.6) Gecko/20050321 Firefox/1.0.2";

	public static final String	cUSERAGENT_IE				= "Mozilla/4.0 (compatible; MSIE 5.5; Windows NT 5.0)";

	public static final String	cUSERAGENT_NETSCAPE	= "Mozilla/5.0 (Windows; U; Win98; en-US; Localization; rv1.4) Gecko20030624 Netscape7.1 (ax)";

	private static final int		sBufferSize					= 1000000;

	private String							mUserAgent;

	private String							mReferer;

	private String							mAccept;

	private String							mAcceptLanguage;

	private String							mAcceptEncoding;

	private String							mAcceptCharset;

	private String							mKeepAlive;

	private String							mConnection;

	private URLConnection				mURLConnection;

	public WebPageFetcher(final URL pURL)
	{
		if (!pURL.getProtocol().equals(cHTTP))
			throw new IllegalArgumentException("URL is not for HTTP Protocol: " + pURL);
		mURL = pURL;
		mUserAgent = cUSERAGENT_FIREFOX;
		mReferer = "";

		mAccept = "text/xml,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5";
		mAcceptLanguage = "en-us,en;q=0.5";
		mAcceptEncoding = "identity";
		mAcceptCharset = "ISO-8859-1,utf-8;q=0.7,*;q=0.7";
		mKeepAlive = "300";
		mConnection = "keep-alive";
	}

	public WebPageFetcher(final String aUrlName) throws MalformedURLException
	{
		this(new URL(aUrlName));
	}

	/**
	 * Fetch the HTML headers as simple text.
	 * 
	 * @return header of the webpage.
	 * 
	 * @throws IOException
	 */
	public String getPageHeader() throws IOException
	{
		final StringBuffer result = new StringBuffer();

		mURLConnection = null;

		try
		{
			mURLConnection = mURL.openConnection();

			mURLConnection.setRequestProperty("Host", mURL.getHost());
			mURLConnection.setRequestProperty("User-Agent", mUserAgent);
			mURLConnection.setRequestProperty("Accept", mAccept);
			mURLConnection.setRequestProperty("Accept-Language", mAcceptLanguage);
			mURLConnection.setRequestProperty("Accept-Encoding", mAcceptEncoding);
			mURLConnection.setRequestProperty("Accept-Charset", mAcceptCharset);
			mURLConnection.setRequestProperty("Keep-Alive", mKeepAlive);
			mURLConnection.setRequestProperty("Connection", mConnection);
			/*************************************************************************
			 * if (!mReferer.equals("")) { connection.setRequestProperty("Referer",
			 * mReferer); }/
			 ************************************************************************/

			// not all headers come in key-value pairs - sometimes the key is
			// null or an empty String
			int headerIdx = 0;
			String headerKey = null;
			String headerValue = null;
			while ((headerValue = mURLConnection.getHeaderField(headerIdx)) != null)
			{
				headerKey = mURLConnection.getHeaderFieldKey(headerIdx);
				if ((headerKey != null) && (headerKey.length() > 0))
				{
					result.append(headerKey);
					result.append(" : ");
				}
				result.append(headerValue);
				result.append(cNEWLINE);
				headerIdx++;
			}
			return result.toString();

		}
		catch (final IOException ex)
		{
			throw new IOException("Cannot open connection to URL: " + mURL);
		}

	}

	/**
	 * Fetch the HTML content of the page as simple text.
	 * 
	 * @return HTML content of the page as a String.
	 * 
	 * @throws IOException
	 */
	public StringBuilder getPageContent() throws IOException
	{
		final StringBuilder result = new StringBuilder();
		BufferedReader reader = null;
		try
		{
			reader = new BufferedReader(new InputStreamReader(mURLConnection.getInputStream()), sBufferSize);
			String line = null;
			while ((line = reader.readLine()) != null)
			{
				result.append(line);
				result.append(cNEWLINE);
			}
		}
		catch (final IOException ex)
		{
			throw new IOException("Cannot retrieve contents of: " + mURL + "\n" + ex.getMessage());
		}
		finally
		{
			shutdown(reader);
		}
		return result;
	}

	private void shutdown(final Reader aReader)
	{
		try
		{
			if (aReader != null)
				aReader.close();
		}
		catch (final IOException ex)
		{
			System.err.println("Cannot close reader: " + aReader);
		}
	}

	/**
	 * Demo harness.
	 * <ul>
	 * <li>argument[0] : an http URL
	 * <li>argument[1] : (header | content)
	 * </ul>
	 * 
	 * @param aArguments
	 * 
	 * @throws IOException
	 */
	public static void main(final String[] aArguments) throws IOException
	{
		final WebPageFetcher fetcher = new WebPageFetcher(aArguments[0]);
		if (aArguments[1].equalsIgnoreCase("header"))
			System.out.println(fetcher.getPageHeader());
		else if (aArguments[1].equalsIgnoreCase("content"))
			System.out.println(fetcher.getPageContent());
		else
			System.err.println("Unknown option.");
	}

	public String getReferer()
	{
		return mReferer;
	}

	public void setReferer(final String pReferer)
	{
		mReferer = pReferer;
	}

}