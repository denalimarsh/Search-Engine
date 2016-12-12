
public interface WebCrawlerInterface {

	/**
	 * Adds the seed to the queue and removes existing url's from the set and
	 * queue.
	 * 
	 * @param url
	 */
	public void crawl(String url);

}
