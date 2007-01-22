/**
 * Created on 26 avr. 2005 By Dipl.-Inf. MSC. Ing. Loic Royer
 * 
 */
package org.royerloic.web.feeds;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.royerloic.utils.ProgressListenerInterface;

import de.nava.informa.core.ChannelIF;
import de.nava.informa.core.ItemIF;
import de.nava.informa.core.ParseException;
import de.nava.informa.impl.basic.ChannelBuilder;
import de.nava.informa.parsers.FeedParser;
import de.nava.informa.utils.poller.Poller;
import de.nava.informa.utils.poller.PollerApproverIF;
import de.nava.informa.utils.poller.PollerObserverIF;

public class FeedChannelsMonitor
{
	int																								mMonitoringInterval;					// in
	// seconds

	private Poller																		mPoller;

	Map<ChannelIF, FeedChannelInterface>							mChannelMap;

	Map<ChannelIF, FeedAnalyserInterface>							mFeedAnalysers;

	private PollerApproverIF													mApprover;

	private PollerObserverIF													mObserver;

	private FeedListenerInterface<FeedItemInterface>	mFeedListener;

	private int																				mNumberOfThreads;

	private int																				mThreadPriority;

	protected static final int												mDescriptionSizeLimit	= 200;

	public FeedChannelsMonitor(final FeedListenerInterface<FeedItemInterface> pFeedListener)
	{
		super();
		mFeedListener = pFeedListener;
		mNumberOfThreads = 5;
		mMonitoringInterval = 60; // 1 minute.
		mChannelMap = new HashMap<ChannelIF, FeedChannelInterface>();
		mFeedAnalysers = new HashMap<ChannelIF, FeedAnalyserInterface>();
		mThreadPriority = Thread.MIN_PRIORITY;
	}

	public void setNumberOfThreads(final int lNumberOfThreads)
	{
		mNumberOfThreads = lNumberOfThreads;
	}

	public void startMonitoring()
	{
		mPoller = new Poller();
		mPoller.setWorkerThreads(mNumberOfThreads);

		mApprover = new PollerApproverIF()
		{
			public boolean canAddItem(ItemIF pArg0, ChannelIF pArg1)
			{
				return true;
			}

		};

		mPoller.addApprover(mApprover);

		// Create and register observer
		mObserver = new PollerObserverIF()
		{

			public void itemFound(ItemIF pItemIF, ChannelIF pChannelIF)
			{
				try
				{
					Thread.currentThread().setPriority(mThreadPriority);
					pChannelIF.addItem(pItemIF);

					FeedAnalyserInterface lFeedAnalyser = (FeedAnalyserInterface) mFeedAnalysers.get(pChannelIF);

					FeedItemInterface lFeedItem = lFeedAnalyser.newFeedItem();
					lFeedItem.setTitle(pItemIF.getTitle());

					String lDescription = pItemIF.getDescription();
					// lDescription = lDescription.replaceAll("<[\\p{Alpha}]+>", " ");
					// lDescription = lDescription.substring(0, mDescriptionSizeLimit);

					lFeedItem.setDescription(lDescription);
					lFeedItem.setLink(pItemIF.getLink());

					Date lDate = pItemIF.getDate();
					if (lDate != null)
					{
						lFeedItem.setDate(pItemIF.getDate());
					}
					else
					{
						try
						{
							lDate = new Date(Date.parse(pItemIF.getDescription()));
						}
						catch (Throwable e)
						{
							e.printStackTrace();
						}
						if (lDate != null)
						{
							lFeedItem.setDate(lDate);
						}
						else
						{
							lFeedItem.setDate(new Date());
						}
					}

					FeedChannelInterface lFeedChannel = mChannelMap.get(pChannelIF);
					lFeedItem.setChannel(lFeedChannel);

					if (pChannelIF.getCopyright() != null)
					{
						lFeedItem.setSource(pChannelIF.getCopyright());
					}
					else if (pChannelIF.getCreator() != null)
					{
						lFeedItem.setSource(pChannelIF.getCreator());
					}
					else if (pChannelIF.getPublisher() != null)
					{
						lFeedItem.setSource(pChannelIF.getPublisher());
					}
					else if (pChannelIF.getSite() != null)
					{
						lFeedItem.setSource(pChannelIF.getSite().getHost());
					}

					lFeedAnalyser.analyseItem(lFeedItem);
					mFeedListener.onItemFound(lFeedItem);
				}
				catch (Throwable e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

			public void channelErrored(ChannelIF pChannelIF, Exception pException)
			{
				mFeedListener.onError(pChannelIF.getLocation(), pException);
			}

			public void channelChanged(ChannelIF pChannelIF)
			{
			}

			public void pollStarted(ChannelIF pChannelIF)
			{
			}

			public void pollFinished(ChannelIF pChannelIF)
			{

			}
		};
		mPoller.addObserver(mObserver);

	}

	public void addFeedChannel(	final URL pFeedURL,
															final FeedAnalyserInterface pFeedAnalyser,
															final int pFrequence) throws IOException, ParseException
	{
		ChannelIF lChannelIF = FeedParser.parse(new ChannelBuilder(), pFeedURL);
		mFeedAnalysers.put(lChannelIF, pFeedAnalyser);
		FeedChannel lFeedChannel = new FeedChannel(lChannelIF);
		lFeedChannel.setFrequence(pFrequence);
		mChannelMap.put(lChannelIF, lFeedChannel);
		lChannelIF.getItems().clear();
		mPoller.registerChannel(lChannelIF, getMonitoringInterval() * 1000);
	}

	public void addFeedChannelFromListInFile(	final URL pURL,
																						final FeedAnalyserInterface pFeedAnalyser,
																						ProgressListenerInterface pProgressListener) throws IOException
	{
		try
		{

			InputStreamReader lInputStreamReader;
			BufferedReader lBufferedReader;
			try
			{
				lInputStreamReader = new InputStreamReader(pURL.openStream());
				lBufferedReader = new BufferedReader(lInputStreamReader);
			}
			catch (FileNotFoundException e)
			{
				System.out.println("File: " + pURL + " not found.");
				throw e;
			}

			try
			{
				int lCurrentFrequence = 2; // default frequence 2 per hour.
				String lLineString;
				List<URL> lUrlList = new ArrayList<URL>();
				while ((lLineString = lBufferedReader.readLine()) != null)
				{
					lLineString.trim();
					if (lLineString.matches("http://.*") || lLineString.matches("file:/.*"))
					{
						URL lFeedURL = new URL(lLineString);
						lUrlList.add(lFeedURL);
					}
					else if (lLineString.matches("CurrentMaximalFrequence=\\d*"))
					{
						lCurrentFrequence = Integer.parseInt(lLineString.split("=")[1]);
					}
				}

				double lNumberOfFeeds = lUrlList.size();
				double lCounter = 0;
				for (URL lFeedUrl : lUrlList)
				{
					try
					{
						addFeedChannel(lFeedUrl, pFeedAnalyser, lCurrentFrequence);
						System.out.println("added feed: (" + lFeedUrl);
					}
					catch (Throwable e)
					{
						e.printStackTrace();
					}
					lCounter++;
					pProgressListener.setProgress(lCounter / lNumberOfFeeds);
				}

			}
			catch (IOException e2)
			{
				System.out.println("Error while reading: " + e2.getCause());
			}
			finally
			{
				lBufferedReader.close();
				lInputStreamReader.close();
			}
		}
		catch (Exception any)
		{
			any.printStackTrace(System.out);

		}

	}

	public void removeFeedChannel(final URL pFeedURL)
	{
		for (Iterator lIterator = mChannelMap.keySet().iterator(); lIterator.hasNext();)
		{
			ChannelIF lChannelIF = (ChannelIF) lIterator.next();
			URL lUrl = lChannelIF.getLocation();
			if (lUrl.equals(pFeedURL))
			{
				lIterator.remove();
				mPoller.unregisterChannel(lChannelIF);
				mFeedAnalysers.remove(lChannelIF);
			}
		}
	}

	public void stopMonitoring()
	{
		for (ChannelIF lChannel : mChannelMap.keySet())
		{
			mPoller.unregisterChannel(lChannel);
		}
		mPoller.removeObserver(mObserver);
		mPoller.removeApprover(mApprover);
	}

	public int getMonitoringInterval()
	{
		return mMonitoringInterval;
	}

	public void setMonitoringInterval(int pMonitoringInterval)
	{
		mMonitoringInterval = pMonitoringInterval;
	}

	public FeedListenerInterface getFeedListener()
	{
		return mFeedListener;
	}

	public void setFeedListener(FeedListenerInterface<FeedItemInterface> pFeedListener)
	{
		mFeedListener = pFeedListener;
	}

	public int getThreadPriority()
	{
		return mThreadPriority;
	}

	public void setThreadPriority(int pThreadPriority)
	{
		mThreadPriority = pThreadPriority;
	}

}
