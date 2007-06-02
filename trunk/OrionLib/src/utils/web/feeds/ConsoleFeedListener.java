/**
 * Created on 26 avr. 2005
 * By Dipl.-Inf. MSC. Ing. Loic Royer
 * 
 */
package utils.web.feeds;

import java.net.URL;

public class ConsoleFeedListener implements FeedListenerInterface<FeedItemInterface>
{

	public void onItemFound(final FeedItemInterface pFeedItem)
	{
		System.out.println("Item found: \n" + pFeedItem);
	}

	public void onError(final URL pUrl, final Exception pException)
	{
		System.out.println("Error while querying feed: " + pUrl);
		System.out.println("The exception is: " + pException);
	}

	public void onItemRemoved(final FeedItemInterface pFeedItem)
	{
		System.out.println("Item removed: \n" + pFeedItem);
	}

}
