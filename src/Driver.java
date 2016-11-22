import java.nio.file.Path;
import java.nio.file.Paths;

public class Driver {

	/**
	 * The main driver method which reads in the input arguments, instantiates
	 * the main InvertedIndex data structure, and if appropriate, calls
	 * traverse. If appropriate, calls parseQuery, exactSearch, and
	 * partialSearch, and printHelper.
	 * 
	 * @param args
	 *            - the command line arguments which designate where the input
	 *            and output paths are
	 */
	public static void main(String[] args) {

		ArgumentParser parser = new ArgumentParser(args);
		InvertedIndex index = new InvertedIndex();
		QueryHelper queryHelper = new QueryHelper();

		if (parser.hasFlag("-dir")) {
			if (parser.hasValue("-dir")) {
				Path input = Paths.get(parser.getValue("-dir"));
				InvertedIndexBuilder.traverse(input, index);
			}
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