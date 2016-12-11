import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

public class WebCrawler implements WebCrawlerInterface{
	
	private final InvertedIndex index;
	private final HashSet<String> duplicateSet;
	private final Queue<String> linkQueue;
	private int MAX_LINKS;
	
	public WebCrawler(InvertedIndex index){

		this.index = index;
		this.MAX_LINKS = 50;
		this.duplicateSet = new HashSet<String>();
		this.linkQueue = new LinkedList<String>();
	}
		
	/**
	 * Adds the seed to the queue and removes existing url's from the set
	 * and queue.
	 * @param url
	 */
	public void crawl(String url){
		if(!duplicateSet.contains(url) && duplicateSet.size() < MAX_LINKS){
			duplicateSet.add(url);
			linkQueue.add(url);
		}
		while(!linkQueue.isEmpty()){
			crawlHelper(linkQueue.remove());
		}
	}
	
	private void crawlHelper(String link){
		String html;
		try{
			URL base = new URL(link);
			html = HTMLCleaner.fetchHTML(link);
			ArrayList<String> urlList = LinkParser.listLinks(html);
			
			for(String current : urlList){
				URL absolute = new URL(base, current);
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

		}catch(IOException e){
			System.out.println("URL error.");
		}
	}
	
	public static void polishHTML(String link, String html, InvertedIndex index){
		html = HTMLCleaner.cleanHTML(html);
		String[] parsedHTML = HTMLCleaner.parseWords(html);
		
		for(int i = 0; i < parsedHTML.length; i++){
			index.add(parsedHTML[i], link, i + 1);
		}
		
	}
}