import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class WebCrawler {

	private static LinkedList<String> crawlQueue;
	private static HashSet<String> duplicateSet;
	private InvertedIndex index;

	/**
	 * Initializes the link queue and duplicate set, sets inverted
	 * index to the index initialized in driver
	 * 
	 * @param invertedIndex
	 * 			- index instantiated 
	 */
	public WebCrawler(InvertedIndex invertedIndex) {
		index = invertedIndex;
		crawlQueue = new LinkedList<String>();
		duplicateSet = new HashSet<String>();
	}
	
	/**
	 * Crawls up to 50 urls found on the original link
	 * 
	 * @param seed
	 * 			- the starting link from which to begin the search
	 */
	public void crawl(String seed) {

		duplicateSet.add(seed);
		crawlQueue.add(seed);

		int i = 0;
		while (crawlQueue.size() > 0 && i != 50) {
			String currLink = crawlQueue.removeFirst();
			crawlOneLink(currLink);
			i++;
		}
	}
	
	/**
	 * Processes one link, searching it for both words to add 
	 * to the inverted index and other links on the page to crawl
	 * 
	 * @param url
	 * 		- the link to be processed, saved as a String
	 */
	public void crawlOneLink(String url) {
		
		String html = HTMLCleaner.fetchHTML(url);
		processLinks(url, html);
		String[] words = processWords(html);

		for (int i = 0; i < words.length; i++) {
			index.add(words[i], url, i + 1);
		}

	}

	/**
	 * Searches the cleaned HTML for links located on the page,
	 * adding them to the crawl queue if they are not duplicate links
	 * 
	 * @param url
	 * 			- the url from which the HTML originated
	 * @param html
	 * 			- the HTML to be processed for links
	 */
	public static void processLinks(String url, String html) {

		List<String> links = LinkParser.listLinks(html);

		String holder = new String();

		for (int i = 0; i < links.size(); i++) {
			
				holder = cleanLink(links.get(i), url);
				if (!duplicateSet.contains(holder)) {
					duplicateSet.add(holder);
					crawlQueue.add(holder);
				}
			
		}
	}

	/**
	 * Processes the individual words contained within the cleaned HTML 
	 * 
	 * @param html
	 * 			- HTML to be processed into words
	 * @return 
	 * 			- the processed words returned as an array of Strings
	 */
	public static String[] processWords(String html) {
		String text = HTMLCleaner.cleanHTML(html);
		return HTMLCleaner.parseWords(text);
	}

	/**
	 * Cleans each link by using the URL data type to convert it to its 
	 * absolute form before returning it back as a String
	 * 
	 * @param link
	 * 			- the link it its original form
	 * @param url
	 * 			- the url where the link was found
	 * @return 
	 * 			- the cleaned link returned as a String
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
