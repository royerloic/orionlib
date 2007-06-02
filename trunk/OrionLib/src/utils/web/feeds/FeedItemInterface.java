/*
 * Created on 28.4.2005
 * by Dipl.-Inf. MSc. Ing Loic Royer
 */
package utils.web.feeds;

import java.net.URL;
import java.util.Date;

public interface FeedItemInterface
{

	public abstract FeedChannelInterface getChannel();

	public abstract void setChannel(FeedChannelInterface pChannel);

	public abstract String getDescription();

	public abstract void setDescription(String pDescription);

	public abstract String getSource();

	public abstract void setSource(String pDescription);

	public abstract URL getLink();

	public abstract void setLink(URL pLink);

	public abstract String getTitle();

	public abstract void setTitle(String pTitle);

	public abstract Date getDate();

	public abstract void setDate(Date pDate);

}