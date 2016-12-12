import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Driver {

	/**
	 * Search engine. Builds an inverted index from input files and returns
	 * search matches specified by the user.
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) {

		QueryHelperInterface query;
		WorkQueue workers = null;
		InvertedIndex index;
		ArgumentParser parser = new ArgumentParser();
		WebCrawlerInterface crawler;

		parser.parseArguments(args);

		if (parser.hasFlag("-multi") && parser.getValue("-multi") != null) {

			int threads = 5;
			try {
				if (Integer.parseInt(parser.getValue("-multi")) > 0) {
					threads = Integer.parseInt(parser.getValue("-multi"));
				}
			} catch (Exception e) {
				System.out.println("Multi flag exception ");
			}
			workers = new WorkQueue(threads);
			ThreadSafeInvertedIndex threadSafeIndex = new ThreadSafeInvertedIndex();
			index = threadSafeIndex;
			query = new MultithreadedQueryHelper(threadSafeIndex, workers);
			crawler = new MultithreadedWebCrawler(threadSafeIndex, workers);

			if (parser.hasFlag("-dir")) {
				try {
					Path input = Paths.get(parser.getValue("-dir"));
					MultithreadedInvertedIndexBuilder.traverse(input, threadSafeIndex, workers);
				} catch (Exception e) {
					System.out.println("Bad directories");
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
					System.out.println("Unable to parse directories.");
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
			try {
				query.parseQuery(inputQueryPath, true);
			} catch (IOException e) {
				System.out.println("Unable to parse file.");
			}
		}

		if (parser.hasFlag("-query") && parser.hasValue("-query")) {
			String inputQuery = parser.getValue("-query");
			Path inputQueryPath = Paths.get(inputQuery);
			try {
				query.parseQuery(inputQueryPath, false);
			} catch (IOException e) {
				System.out.println("Unable to parse file.");
			}
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
