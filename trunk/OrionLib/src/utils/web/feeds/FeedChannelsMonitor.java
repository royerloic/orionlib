/**
 * Created on 26 avr. 2005 By Dipl.-Inf. MSC. Ing. Loic Royer
 * 
 */
package utils.web.feeds;

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

import utils.utils.ProgressListenerInterface;
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
			public boolean canAddItem(final ItemIF pArg0, final ChannelIF pArg1)
			{
				return true;
			}

		};

		mPoller.addApprover(mApprover);

		// Create and register observer
		mObserver = new PollerObserverIF()
		{

			public void itemFound(final ItemIF pItemIF, final ChannelIF pChannelIF)
			{
				try
				{
					Thread.currentThread().setPriority(mThreadPriority);
					pChannelIF.addItem(pItemIF);

					final FeedAnalyserInterface lFeedAnalyser = mFeedAnalysers.get(pChannelIF);

					final FeedItemInterface lFeedItem = lFeedAnalyser.newFeedItem();
					lFeedItem.setTitle(pItemIF.getTitle());

					final String lDescription = pItemIF.getDescription();
					// lDescription = lDescription.replaceAll("<[\\p{Alpha}]+>", " ");
					// lDescription = lDescription.substring(0, mDescriptionSizeLimit);

					lFeedItem.setDescription(lDescription);
					lFeedItem.setLink(pItemIF.getLink());

					Date lDate = pItemIF.getDate();
					if (lDate != null)
						lFeedItem.setDate(pItemIF.getDate());
					else
					{
						try
						{
							lDate = new Date(Date.parse(pItemIF.getDescription()));
						}
						catch (final Throwable e)
						{
							e.printStackTrace();
						}
						if (lDate != null)
							lFeedItem.setDate(lDate);
						else
							lFeedItem.setDate(new Date());
					}

					final FeedChannelInterface lFeedChannel = mChannelMap.get(pChannelIF);
					lFeedItem.setChannel(lFeedChannel);

					if (pChannelIF.getCopyright() != null)
						lFeedItem.setSource(pChannelIF.getCopyright());
					else if (pChannelIF.getCreator() != null)
						lFeedItem.setSource(pChannelIF.getCreator());
					else if (pChannelIF.getPublisher() != null)
						lFeedItem.setSource(pChannelIF.getPublisher());
					else if (pChannelIF.getSite() != null)
						lFeedItem.setSource(pChannelIF.getSite().getHost());

					lFeedAnalyser.analyseItem(lFeedItem);
					mFeedListener.onItemFound(lFeedItem);
				}
				catch (final Throwable e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

			public void channelErrored(final ChannelIF pChannelIF, final Exception pException)
			{
				mFeedListener.onError(pChannelIF.getLocation(), pException);
			}

			public void channelChanged(final ChannelIF pChannelIF)
			{
			}

			public void pollStarted(final ChannelIF pChannelIF)
			{
			}

			public void pollFinished(final ChannelIF pChannelIF)
			{

			}
		};
		mPoller.addObserver(mObserver);

	}

	public void addFeedChannel(	final URL pFeedURL,
															final FeedAnalyserInterface pFeedAnalyser,
															final int pFrequence) throws IOException, ParseException
	{
		final ChannelIF lChannelIF = FeedParser.parse(new ChannelBuilder(), pFeedURL);
		mFeedAnalysers.put(lChannelIF, pFeedAnalyser);
		final FeedChannel lFeedChannel = new FeedChannel(lChannelIF);
		lFeedChannel.setFrequence(pFrequence);
		mChannelMap.put(lChannelIF, lFeedChannel);
		lChannelIF.getItems().clear();
		mPoller.registerChannel(lChannelIF, getMonitoringInterval() * 1000);
	}

	public void addFeedChannelFromListInFile(	final URL pURL,
																						final FeedAnalyserInterface pFeedAnalyser,
																						final ProgressListenerInterface pProgressListener) throws IOException
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
			catch (final FileNotFoundException e)
			{
				System.out.println("File: " + pURL + " not found.");
				throw e;
			}

			try
			{
				int lCurrentFrequence = 2; // default frequence 2 per hour.
				String lLineString;
				final List<URL> lUrlList = new ArrayList<URL>();
				while ((lLineString = lBufferedReader.readLine()) != null)
				{
					lLineString.trim();
					if (lLineString.matches("http://.*") || lLineString.matches("file:/.*"))
					{
						final URL lFeedURL = new URL(lLineString);
						lUrlList.add(lFeedURL);
					}
					else if (lLineString.matches("CurrentMaximalFrequence=\\d*"))
						lCurrentFrequence = Integer.parseInt(lLineString.split("=")[1]);
				}

				final double lNumberOfFeeds = lUrlList.size();
				double lCounter = 0;
				for (final URL lFeedUrl : lUrlList)
				{
					try
					{
						addFeedChannel(lFeedUrl, pFeedAnalyser, lCurrentFrequence);
						System.out.println("added feed: (" + lFeedUrl);
					}
					catch (final Throwable e)
					{
						e.printStackTrace();
					}
					lCounter++;
					pProgressListener.setProgress(lCounter / lNumberOfFeeds);
				}

			}
			catch (final IOException e2)
			{
				System.out.println("Error while reading: " + e2.getCause());
			}
			finally
			{
				lBufferedReader.close();
				lInputStreamReader.close();
			}
		}
		catch (final Exception any)
		{
			any.printStackTrace(System.out);

		}

	}

	public void removeFeedChannel(final URL pFeedURL)
	{
		for (final Iterator lIterator = mChannelMap.keySet().iterator(); lIterator.hasNext();)
		{
			final ChannelIF lChannelIF = (ChannelIF) lIterator.next();
			final URL lUrl = lChannelIF.getLocation();
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
		for (final ChannelIF lChannel : mChannelMap.keySet())
			mPoller.unregisterChannel(lChannel);
		mPoller.removeObserver(mObserver);
		mPoller.removeApprover(mApprover);
	}

	public int getMonitoringInterval()
	{
		return mMonitoringInterval;
	}

	public void setMonitoringInterval(final int pMonitoringInterval)
	{
		mMonitoringInterval = pMonitoringInterval;
	}

	public FeedListenerInterface getFeedListener()
	{
		return mFeedListener;
	}

	public void setFeedListener(final FeedListenerInterface<FeedItemInterface> pFeedListener)
	{
		mFeedListener = pFeedListener;
	}

	public int getThreadPriority()
	{
		return mThreadPriority;
	}

	public void setThreadPriority(final int pThreadPriority)
	{
		mThreadPriority = pThreadPriority;
	}

}
