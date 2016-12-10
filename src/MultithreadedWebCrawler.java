import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;

public class MultithreadedWebCrawler extends WebCrawler{

	private final ThreadSafeInvertedIndex multiIndex;
	private final HashSet<String> urlset;
	private final WorkQueue workers;
	private int limit = 50;
	
	public MultithreadedWebCrawler(ThreadSafeInvertedIndex index,  WorkQueue workers) {
		super(index);
		multiIndex = index;
		this.urlset = new HashSet<String>();
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
			try {
				URL base = new URL(url);
				html = HTMLCleaner.fetchHTML(url);
				ArrayList<String> urls = LinkParser.listLinks(html);
				
				for(String temp : urls){
					URL absolute = new URL(base, temp);
					URL cleaned = new URL(absolute.getProtocol(), absolute.getHost(), absolute.getFile());
					String absoluteURL = cleaned.toString();
					
					if (urlset.size() > limit) {
						break;
					}else if (!urlset.contains(absoluteURL) && urlset.size() < limit) {
						urlset.add(absoluteURL);
						workers.execute(new CrawlRun(absoluteURL));
					}
				}

				html = HTMLCleaner.cleanHTML(html);
				String[] results = HTMLCleaner.parseWords(html);
				
				int count = 1;
				
				for(String result: results){
					localindex.add(result, url, count);
					count++;
				}
				multiIndex.addIndex(localindex);
			} catch (IOException e) {
				System.out.println("Cannot get url: " + url);
			}
		}
	}
	
	public void crawlHelper(String url){
		if(!urlset.contains(url) && urlset.size() < limit){
			urlset.add(url);
			workers.execute(new CrawlRun(url));
		}
		workers.finish();
	}
}
