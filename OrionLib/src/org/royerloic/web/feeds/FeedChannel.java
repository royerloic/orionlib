package org.royerloic.web.feeds;

import de.nava.informa.core.ChannelIF;

public class FeedChannel implements FeedChannelInterface
{

	private int	mFrequence;

	public FeedChannel(ChannelIF pChannelIF)
	{
		super();
		// TODO Auto-generated constructor stub
	}

	public void setFrequence(int pFrequence)
	{
		mFrequence = pFrequence;
	}

}
