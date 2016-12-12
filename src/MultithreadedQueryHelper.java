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

	/**
	 * Builds a map that holds words parsed from the query file
	 * 
	 * @param index
	 *            - the inverted index passed to the queryHelper
	 * @param workers
	 *            - the work queue
	 */
	public MultithreadedQueryHelper(ThreadSafeInvertedIndex index, WorkQueue workers) {
		this.workers = workers;
		buildResult = new TreeMap<String, List<SearchResult>>();
		multipleIndex = index;
	}

	/**
	 * Parses the query file, passing each line of the file to a new worker in
	 * the work queue which then processes and sorts the words before performing
	 * an exact or partial search.
	 * 
	 * @param file
	 *            - the location path of the query
	 * 
	 * @param searchFlag
	 *            - the flag that determines whether the search is partial or
	 *            exact
	 */
	public void parseQuery(Path file, boolean searchFlag) {
		try (BufferedReader reader = Files.newBufferedReader(file, Charset.forName("UTF-8"));) {
			String line;
			while ((line = reader.readLine()) != null) {
				workers.execute(new QueryRunner(line, searchFlag));
			}
			workers.finish();
			reader.close();

		} catch (IOException e) {
			System.out.println("An error occuring while trying to parse the query");
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
			if (flag) {
				List<SearchResult> results = multipleIndex.exactSearch(words);
				buildResult.put(word, results);
			} else if (!flag) {
				List<SearchResult> results = multipleIndex.partialSearch(words);
				buildResult.put(word, results);
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
		QueryHelper.printQuery(path, buildResult);
	}
}
