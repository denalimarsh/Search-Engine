import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

public class WebCrawler implements WebCrawlerInterface {

	private final InvertedIndex index;
	private final HashSet<String> duplicateSet;
	private final Queue<String> linkQueue;
	private int MAX_LINKS;

	/**
	 * Instantiates a new WebCrawler
	 * 
	 * @param index
	 */
	public WebCrawler(InvertedIndex index) {
		this.duplicateSet = new HashSet<String>();
		this.linkQueue = new LinkedList<String>();
		this.index = index;
		this.MAX_LINKS = 50;
	}

	/**
	 * Adds the seed to the queue and removes existing url's from the set and
	 * queue.
	 * 
	 * Adds the original link to the linkQueue and while the queue isn't empty
	 * calls callOneLink on the last object in the queue
	 * 
	 * @param link
	 *            - the original url to be crawled
	 */
	public void crawl(String link) {
		if (!duplicateSet.contains(link) && duplicateSet.size() < MAX_LINKS) {
			duplicateSet.add(link);
			linkQueue.add(link);
		}
		while (!linkQueue.isEmpty()) {
			crawlOneLink(linkQueue.remove());
		}
	}

	/**
	 * Gets and cleans HTML from a link and it's associated html, adding it into
	 * the inverted index
	 * 
	 * @param link
	 *            - the url associated with the html block
	 * @param html
	 *            - the html from the link
	 * @param index
	 *            - inverted index to be added to
	 */
	public static void polishHTML(String link, String html, InvertedIndex index) {
		html = HTMLCleaner.cleanHTML(html);
		String[] parsedHTML = HTMLCleaner.parseWords(html);

		for (int i = 0; i < parsedHTML.length; i++) {
			index.add(parsedHTML[i], link, i + 1);
		}

	}

	/**
	 * Crawls a url for other links on the page, adding them to the queue and
	 * stops after the 50 links have been processed
	 * 
	 * @param link
	 *            - the link to be crawled through
	 */
	private void crawlOneLink(String link) {
		String html;
		try {
			URL baseLink = new URL(link);
			html = HTMLCleaner.fetchHTML(link);
			ArrayList<String> urlList = LinkParser.listLinks(html);

			for (String current : urlList) {
				URL absolute = new URL(baseLink, current);
				URL cleaned = new URL(absolute.getProtocol(), absolute.getHost(), absolute.getFile());
				String finishedURL = cleaned.toString();

				if (duplicateSet.size() >= MAX_LINKS) {
					break;
				} else if (!duplicateSet.contains(finishedURL)) {
					duplicateSet.add(finishedURL);
					linkQueue.add(finishedURL);
				}
			}

			polishHTML(link, html, index);

		} catch (IOException e) {
			System.out.println("Error occured while crawling url");
		}
	}

}