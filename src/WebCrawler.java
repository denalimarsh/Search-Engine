import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class WebCrawler {

	private final LinkedList<String> crawlQueue;
	private final HashSet<String> duplicateSet;
	private final InvertedIndex index;
	private final int MAX_LINKS = 50;
	private int max;

	/**
	 * Initializes the link queue and duplicate set, sets inverted index to the
	 * index initialized in driver
	 * 
	 * @param invertedIndex
	 *            - index instantiated
	 */
	public WebCrawler(InvertedIndex invertedIndex) {
		this.max = 0;
		index = invertedIndex;
		crawlQueue = new LinkedList<String>();
		duplicateSet = new HashSet<String>();
	}

	/**
	 * Crawls up to 50 urls found on the original link
	 * 
	 * @param seed
	 *            - the starting link from which to begin the search
	 */
	public void crawl(String seed) {

		max += MAX_LINKS;
		duplicateSet.add(seed);
		crawlQueue.add(seed);

		while (!crawlQueue.isEmpty()) {
			String currLink = crawlQueue.removeFirst();
			crawlOneLink(currLink);
		}
	}

	/**
	 * Processes one link, searching it for both words to add to the inverted
	 * index and other links on the page to crawl
	 * 
	 * @param url
	 *            - the link to be processed, saved as a String
	 */
	public void crawlOneLink(String url) {

		String html = HTMLCleaner.fetchHTML(url);
		processLinks(url, html);
		String[] words = HTMLCleaner.parseWords(HTMLCleaner.cleanHTML(html));

		for (int i = 0; i < words.length; i++) {
			index.add(words[i], url, i + 1);
		}

	}

	/**
	 * Searches the cleaned HTML for links located on the page, adding them to
	 * the crawl queue if they are not duplicate links
	 * 
	 * @param url
	 *            - the url from which the HTML originated
	 * @param html
	 *            - the HTML to be processed for links
	 */
	public void processLinks(String url, String html) {

		List<String> links = LinkParser.listLinks(html);
		String cleanedLink = new String();

		for (int i = 0; i < links.size(); i++) {
			if (duplicateSet.size() == max) {
				break;
			}
			cleanedLink = cleanLink(links.get(i), url);
			if (!duplicateSet.contains(cleanedLink)) {
				duplicateSet.add(cleanedLink);
				crawlQueue.add(cleanedLink);
			}
		}
	}

	/**
	 * Cleans each link by using the URL data type to convert it to its absolute
	 * form before returning it back as a String
	 * 
	 * @param link
	 *            - the link it its original form
	 * @param url
	 *            - the url where the link was found
	 * @return - the cleaned link returned as a String
	 * @throws MalformedURLException
	 */
	public static String cleanLink(String link, String url) {

		URL base;
		String finished = new String();

		try {
			base = new URL(url);
			URL absolute = new URL(base, link);
			finished = absolute.getProtocol() + "://" + absolute.getHost() + absolute.getFile();
		} catch (MalformedURLException e) {
			System.out.println("Unable to clean link: " + link);
		}

		return finished;

	}
}