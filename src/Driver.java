import java.nio.file.Path;
import java.nio.file.Paths;

public class Driver {

	/**
	 * The main driver method which reads in the input arguments, instantiates
	 * the main InvertedIndex data structure, and if appropriate, calls
	 * traverse. If appropriate, calls parseQuery, exactSearch, and
	 * partialSearch, and printHelper. If the multi flag is enabled, 
	 * the InvertedIndex and its methods will execute as multithreaded.
	 * 
	 * @param args
	 *            - the command line arguments which designate where the input
	 *            and output paths are
	 */
	public static void main(String[] args) {

		WorkQueue workers = null;
		InvertedIndex index;
		ArgumentParser parser = new ArgumentParser();
		QueryHelperInterface query;
		WebCrawlerInterface crawler;
		parser.parseArguments(args);

		if (parser.hasFlag("-multi") && parser.getValue("-multi") != null) {
			int threadCount = 5;
			try {
				if (Integer.parseInt(parser.getValue("-multi")) != 0) {
					threadCount = Integer.parseInt(parser.getValue("-multi"));
				}
			} catch (Exception e) {
				System.out.println("Error with multithread argument");
			}
			workers = new WorkQueue(threadCount);
			ThreadSafeInvertedIndex threadSafeIndex = new ThreadSafeInvertedIndex();
			index = threadSafeIndex;
			query = new MultithreadedQueryHelper(threadSafeIndex, workers);
			crawler = new MultithreadedWebCrawler(threadSafeIndex, workers);

			if (parser.hasFlag("-dir")) {
				try {
					Path input = Paths.get(parser.getValue("-dir"));
					MultithreadedInvertedIndexBuilder.traverse(input, threadSafeIndex, workers);
				} catch (Exception e) {
					System.out.println("Error with directory argument");
				}
			}
		} else {
			index = new InvertedIndex();
			crawler = new WebCrawler(index);
			query = new QueryHelper(index);

			if (parser.hasFlag("-dir")) {
				try {
					Path input = Paths.get(parser.getValue("-dir"));
					InvertedIndexBuilder.traverse(input, index);
				} catch (Exception e) {
					System.out.println("Error with directory argument");
				}
			}
		}

		if (parser.hasFlag("-url") && parser.hasValue("-url")) {
			String link = parser.getValue("-url");
			crawler.crawl(link);
		}

		if (parser.hasFlag("-index")) {
			String output = parser.getValue("-index", "index.json");
			Path outputPath = Paths.get(output);
			index.print(outputPath);
		}

		if (parser.hasFlag("-exact") && parser.hasValue("-exact")) {
			String inputQuery = parser.getValue("-exact");
			Path inputQueryPath = Paths.get(inputQuery);
			query.parseQuery(inputQueryPath, true);
		}

		if (parser.hasFlag("-query") && parser.hasValue("-query")) {
			String inputQuery = parser.getValue("-query");
			Path inputQueryPath = Paths.get(inputQuery);
			query.parseQuery(inputQueryPath, false);
		}

		if (parser.hasFlag("-results")) {
			String outputQuery = parser.getValue("-results", "results.json");
			Path outputQueryPath = Paths.get(outputQuery);
			query.printHelper(outputQueryPath);
		}

		if (workers != null) {
			workers.shutdown();
		}
	}
}
