import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Driver {

	/**
	 * The main driver method which reads in the input arguments, instantiates
	 * the main InvertedIndex data structure, and if appropriate, calls traverse
	 * 
	 * @param args
	 *            - the command line arguments which designate where the input
	 *            and output paths are
	 * @throws IOException
	 */
	public static void main(String[] args) {

		ArgumentParser parser = new ArgumentParser(args);

		InvertedIndex index = new InvertedIndex();

		if (parser.hasFlag("-dir")) {
			if (parser.hasValue("-dir")) {
				Path input = Paths.get(parser.getValue("-dir"));
				System.out.println(input.toString());
				InvertedIndexBuilder.traverse(input, index);
			}
		}

		if (parser.hasFlag("-index")) {
			Path output = Paths.get(parser.getValue("-index", "index.json"));
			index.print(output);
		}
	}
}