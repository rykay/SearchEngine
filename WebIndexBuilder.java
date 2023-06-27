package edu.usfca.cs272;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Java Class that builds an inverted index from the web!
 * 
 * @author ryank
 *
 */
public class WebIndexBuilder {

	/**
	 * thread safe inverted index object
	 */
	private final ThreadSafeInvertedIndex threadSafeInvertedIndex;

	/**
	 * workQueue object
	 */
	private final WorkQueue workQueue;

	/**
	 * starting url to build inverted index from
	 */
	private final String seedUrl;

	/**
	 * the number of max crawls
	 */
	private final int maxCrawls;

	/**
	 * Set data structure to store checked links
	 */
	private final Set<URL> checkedLinks;

	/**
	 * el constructor
	 * 
	 * @param threadSafeInvertedIndex thread safe inverted index object
	 * @param workQueue               work queue object
	 * @param seedUrl                 the base url to start crawling from
	 * @param maxCrawls               the number of crawls to do
	 */
	public WebIndexBuilder(ThreadSafeInvertedIndex threadSafeInvertedIndex, WorkQueue workQueue, String seedUrl,
			int maxCrawls) {
		this.threadSafeInvertedIndex = threadSafeInvertedIndex;
		this.workQueue = workQueue;
		this.seedUrl = seedUrl;
		this.maxCrawls = maxCrawls;
		checkedLinks = new HashSet<>();
	}

	/**
	 * The initial crawl called from driver. This method executes a task from the
	 * seed URL and finishes the work queue.
	 * 
	 * @throws MalformedURLException throws MalformedURLException
	 */
	public void crawlWeb() throws MalformedURLException {
		checkedLinks.add(new URL(seedUrl));
		workQueue.execute(new Task(new URL(seedUrl)));
		workQueue.finish();

	}

	/**
	 * Recursive method that builds the invertedIndex from the web.
	 * 
	 * @param base the url to build from!
	 */
	public void crawlWeb(URL base) {
		// 3 redirects
		String html = HtmlFetcher.fetch(base, 3);
		if (html == null) {
			return;
		}
		html = HtmlCleaner.stripBlockElements(html);
		ArrayList<URL> urlsFound = LinkFinder.listUrls(base, html);
		for (URL link : urlsFound) {
			// check if URL has not already been crawled or queued to be crawled && maxCrawl
			if (maxCrawls > checkedLinks.size() && !checkedLinks.contains(link)) {
				checkedLinks.add(link);
				workQueue.execute(new Task(link));
			}

		}
		html = HtmlCleaner.stripHtml(html);
		int position = 1;
		ThreadSafeInvertedIndex local = new ThreadSafeInvertedIndex();
		for (String word : WordCleaner.listStems(html)) {
			local.add(word, base.toString(), position);
			position++;
		}
		threadSafeInvertedIndex.addAll(local);
	}

	/**
	 * task class
	 * 
	 * @author ryank
	 *
	 */
	private class Task implements Runnable {

		/**
		* the URL to build the inverted index off of
		*/
		private final URL link;

		/*
		* The constructor
		* @param link the link to build the inverted index off of
		*
		*/
		public Task(URL link) {
			this.link = link;
		}

		@Override
		public void run() {
			crawlWeb(link);
		}

	}
}
