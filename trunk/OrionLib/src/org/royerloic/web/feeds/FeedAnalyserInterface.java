/*
 * Created on 28.4.2005
 * by Dipl.-Inf. MSc. Ing Loic Royer
 */
package org.royerloic.web.feeds;

public interface FeedAnalyserInterface
{

	FeedItemInterface newFeedItem();

	void analyseItem(FeedItemInterface pFeedItem);

}
