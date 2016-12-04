import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;

public class MultithreadedWebCrawler extends WebCrawler {

	private final ThreadSafeInvertedIndex multipleIndex;
	private final HashSet<String> linkSet;
	private final WorkQueue workers;
	private int MAX_LINKS = 50;

	public MultithreadedWebCrawler(ThreadSafeInvertedIndex index, WorkQueue workers) {
		super(index);
		multipleIndex = index;
		this.linkSet = new HashSet<String>();
		this.workers = workers;
	}

	private class CrawlRun implements Runnable {
		private String url;
		private InvertedIndex localindex;

		public CrawlRun(String url) {
			this.url = url;
			this.localindex = new InvertedIndex();
		}

		@Override
		public void run() {

			String html;
			URL base;

			try {
				html = HTMLCleaner.fetchHTML(url);
				base = new URL(url);
				ArrayList<String> links = LinkParser.listLinks(html);

				for (int i = 0; i < links.size(); i++) {
					URL absolute = new URL(base, links.get(i));
					URL finished = new URL(absolute.getProtocol(), absolute.getHost(), absolute.getFile());
					String absoluteURL = finished.toString();

					if (linkSet.size() > MAX_LINKS) {
						break;
					}

					if (!linkSet.contains(absoluteURL)) {
						linkSet.add(absoluteURL);
						workers.execute(new CrawlRun(absoluteURL));
					}
				}

				html = HTMLCleaner.cleanHTML(html);
				String[] results = HTMLCleaner.parseWords(html);

				for (int i = 0; i < results.length; i++) {
					localindex.add(results[i], url, i + 1);
				}

				multipleIndex.addIndex(localindex);
			} catch (IOException e) {
				System.out.println("Error accessing link: " + url);
			}
		}
	}

	public void crawlHelper(String url) {
		if (linkSet.size() < MAX_LINKS && !linkSet.contains(url)) {
			linkSet.add(url);
			workers.execute(new CrawlRun(url));
		}
		workers.finish();
	}
}
