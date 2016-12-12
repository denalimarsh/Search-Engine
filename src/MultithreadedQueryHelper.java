import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

public class MultithreadedQueryHelper implements QueryHelperInterface {

	private final TreeMap<String, List<SearchResult>> buildResult;
	private final ThreadSafeInvertedIndex multipleIndex;
	private final WorkQueue workers;
	private final ReadWriteLock lock;

	public MultithreadedQueryHelper(ThreadSafeInvertedIndex index, WorkQueue workers) {
		this.workers = workers;
		buildResult = new TreeMap<String, List<SearchResult>>();
		multipleIndex = index;
		lock = new ReadWriteLock();
	}
	
	public void parseQuery(Path file, boolean searchFlag) throws IOException {
		try (BufferedReader reader = Files.newBufferedReader(file, Charset.forName("UTF-8"));) {

			String line;

			while ((line = reader.readLine()) != null) {
				workers.execute(new QueryRunner(line, searchFlag));
			}

			workers.finish();
			reader.close();

		} catch (IOException e) {
			System.out.println("Unable to read query file.");
		}

	}

	private class QueryRunner implements Runnable {

		private String line;
		private boolean flag;

		public QueryRunner(String line, boolean flag) {
			this.line = line;
			this.flag = flag;
		}

		@Override
		public void run() {

			String[] words = line.trim().replaceAll("\\p{Punct}+", "").toLowerCase().split("\\s+");
			Arrays.sort(words);
			String word = String.join(" ", words);
			List<SearchResult> results = (flag) ? multipleIndex.exactSearch(words) : multipleIndex.partialSearch(words);

			lock.lockReadWrite();
			try {
				buildResult.put(word, results);
			} finally {
				lock.unlockReadWrite();
			}

		}
	}

	/**
	 * Wrapper method to allow the driver to access the print Query method
	 * 
	 * @param path
	 *            - the file location to print the results to
	 */

	@Override
	public void printHelper(Path path) {
		lock.lockReadWrite();
		try {
			QueryHelper.printQuery(path, buildResult);
		} finally {
			lock.unlockReadWrite();
		}
	}
}
