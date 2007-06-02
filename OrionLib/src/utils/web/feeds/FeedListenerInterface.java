/*
 * Created on 27.4.2005 by Dipl.-Inf. MSc. Ing Loic Royer
 */
package utils.web.feeds;

import java.net.URL;

public interface FeedListenerInterface<FeedItemClass>
{
	public abstract void onItemFound(FeedItemClass pFeedItem);

	public abstract void onItemRemoved(FeedItemClass pFeedItem);

	public abstract void onError(URL pUrl, Exception pException);

}
