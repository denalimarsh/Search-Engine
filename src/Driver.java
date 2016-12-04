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

		ArgumentParser parser = new ArgumentParser(args);
		InvertedIndex index;
		InvertedIndexBuilder indexBuilder;
		QueryHelper queryHelper;
		WebCrawler crawler;
		WorkQueue workers = null;

		if (parser.hasFlag("-multi") && parser.getValue("-multi") != null) {
			int threadCount = 5;
			try {
				if (Integer.parseInt(parser.getValue("-multi")) != 0) {
					threadCount = Integer.parseInt(parser.getValue("-multi"));
				}
			} catch (Exception e) {
				System.out.println("Error occured while obtaining the number of threads");
			}
			ThreadSafeInvertedIndex threadSafeIndex = new ThreadSafeInvertedIndex();
			index = threadSafeIndex;
			workers = new WorkQueue(threadCount);
			crawler = new MultithreadedWebCrawler(threadSafeIndex, workers);
			indexBuilder = new MultithreadedInvertedIndexBuilder(threadSafeIndex, workers);
			queryHelper = new MultithreadedQueryHelper(threadSafeIndex, workers);
			
		} else {
			indexBuilder = new InvertedIndexBuilder();
			index = new InvertedIndex();
			crawler = new WebCrawler(index);
			queryHelper = new QueryHelper(index);
		}

		if (parser.hasFlag("-dir")) {
			if (parser.hasValue("-dir")) {
				Path inputPath = Paths.get(parser.getValue("-dir"));
				indexBuilder.traverse(inputPath, index);
				if (workers != null) {
					workers.finish();
				}
			}
		}

		if (parser.hasFlag("-url")) {
			String url = parser.getValue("-url");
			crawler.crawl(url);
		}

		if (parser.hasFlag("-index")) {
			Path output = Paths.get(parser.getValue("-index", "index.json"));
			index.print(output);
		}

		if (parser.hasFlag("-query")) {
			if (parser.hasValue("-query")) {
				Path queryPath = Paths.get(parser.getValue("-query"));
				queryHelper.parseQuery(queryPath, 1, index);
			}
		}

		if (parser.hasFlag("-exact")) {
			if (parser.hasValue("-exact")) {
				Path exactPath = Paths.get(parser.getValue("-exact"));
				queryHelper.parseQuery(exactPath, 0, index);
			}
		}

		if (parser.hasFlag("-results")) {
			Path results = Paths.get(parser.getValue("-results", "results.json"));
			queryHelper.printHelper(results);
		}

	}
}