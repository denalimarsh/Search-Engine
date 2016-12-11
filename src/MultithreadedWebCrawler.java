import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;

public class MultithreadedWebCrawler implements WebCrawlerInterface{
	
	private final ThreadSafeInvertedIndex multiIndex;
	private final HashSet<String> duplicateSet;
	private final WorkQueue workers;
	private int MAX_LINKS = 50;
	
	public MultithreadedWebCrawler(ThreadSafeInvertedIndex index,  WorkQueue workers) {
		multiIndex = index;
		this.duplicateSet = new HashSet<String>();
		this.workers = workers;
	}
	
	public void crawl(String url){
		synchronized(duplicateSet){
			if(!duplicateSet.contains(url) && duplicateSet.size() < MAX_LINKS){
				duplicateSet.add(url);
				workers.execute(new CrawlRunner(url));
			}
		}
		workers.finish();
	}
	
	private class CrawlRunner implements Runnable {
		private String link;
		private InvertedIndex localindex;

		public CrawlRunner(String url) {
			this.link = url;
			this.localindex = new InvertedIndex();
		}
		
		@Override
		public void run() {
			String html;
			try {
				URL base = new URL(link);
				html = HTMLCleaner.fetchHTML(link);
				ArrayList<String> urls = LinkParser.listLinks(html);
				
				for(String current : urls){
					URL absolute = new URL(base, current);
					URL cleaned = new URL(absolute.getProtocol(), absolute.getHost(), absolute.getFile());
					String finishedURL = cleaned.toString();
					
					if (duplicateSet.size() >= MAX_LINKS) {
						break;
					}else if (!duplicateSet.contains(finishedURL)) {
						duplicateSet.add(finishedURL);
						workers.execute(new CrawlRunner(finishedURL));
					}
				}

				WebCrawler.polishHTML(link, html, localindex);
				multiIndex.addIndex(localindex);
			} catch (IOException e) {
				System.out.println("Cannot get url: " + link);
			}
		}
	}

}
