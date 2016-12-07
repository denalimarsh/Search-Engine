import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

public class MultithreadedQueryHelper extends QueryHelper {

	private final TreeMap<String, List<SearchResult>> searchResult;
	private final ThreadSafeInvertedIndex multipleIndex;
	private ReadWriteLock lock;
	private final WorkQueue workers;

	public MultithreadedQueryHelper(ThreadSafeInvertedIndex index, WorkQueue workers) {
		super(index);
		searchResult = new TreeMap<String, List<SearchResult>>();
		multipleIndex = index;
		lock = new ReadWriteLock();
		this.workers = workers;
	}

	public void parseQuery(Path file, boolean searchFlag) throws IOException {
		try (BufferedReader reader = Files.newBufferedReader(file, Charset.forName("UTF-8"));) {
			String line;
			while ((line = reader.readLine()) != null) {
				String[] words = line.trim().replaceAll("\\p{Punct}+", "").toLowerCase().split("\\s+");
				Arrays.sort(words);
				String searchString = String.join(" ", words);
				workers.execute(new QueryMinions(words, searchString, searchFlag));
			}
			workers.finish();
			reader.close();

		} catch (IOException e) {
			System.out.println("Unable to read query file.");
		}

	}

	private class QueryMinions implements Runnable {

		private String key;
		private boolean searchFlag;
		private String[] queries;

		public QueryMinions(String[] line, String key, boolean searchFlag) {
			this.queries = line;
			this.key = key;
			this.searchFlag = searchFlag;
		}

		@Override
		public void run() {
			if (searchFlag == true) {
				List<SearchResult> results = multipleIndex.partialSearch(queries);
				lock.lockReadWrite();
				try {
					searchResult.put(key, results);
				} finally {
					lock.unlockReadWrite();
				}
			}
			if (searchFlag == false) {
				List<SearchResult> results = multipleIndex.exactSearch(queries);
				lock.lockReadWrite();
				try {
					searchResult.put(key, results);
				} finally {
					lock.unlockReadWrite();
				}
			}
		}
	}

	public void print(Path path) {
		QueryHelper.printQueryHelper(path, searchResult);
	}

}
