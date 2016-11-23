import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

public class QueryHelper {

	private final TreeMap<String, List<SearchResult>> buildQuery;

	/**
	 * Initializes a new QueryHelper object
	 */
	public QueryHelper() {
		buildQuery = new TreeMap<String, List<SearchResult>>();
	}

	/**
	 * Reads a text file, cleans the words, adds them to a list
	 * 
	 * @param path
	 *            - the path to be read and parsed into queries
	 * @return uniqueList - a sorted, unique list of Strings
	 */
	public void parseQuery(Path path, int searchFlag, InvertedIndex index) {

		Charset charset = java.nio.charset.StandardCharsets.UTF_8;

		try (BufferedReader br = Files.newBufferedReader(path, charset)) {
			String line = br.readLine();
			while ((line) != null) {
				line = line.trim();
				line = line.replaceAll("\\p{Punct}+", "");
				line = line.replaceAll("\\s+", " ");
				line = line.toLowerCase();

				String[] arrayWord = line.split(" ");
				Arrays.sort(arrayWord);

				List<SearchResult> list = new ArrayList<>();

				if (searchFlag == 0) {
					list = index.exactSearch(arrayWord);
				} else {
					list = index.partialSearch(arrayWord);
				}

				String text = String.join(" ", arrayWord);

				buildQuery.put(text, list);
				line = br.readLine();
			}
		} catch (IOException ex) {
			System.out.println("Could not parse queries from file " + path.toString());
		}

	}

	/**
	 * Wrapper method to allow the driver to access the print Query method
	 * 
	 * @param path
	 *            - the file location to print the results to
	 */
	public void printHelper(Path path) {
		QueryHelper.printQuery(path, buildQuery);
	}

	/**
	 * Prints the fully populated buildQuery to the out path
	 * 
	 * @param path
	 *            - the file location to print the results to
	 * @param finishedBuildQuery
	 *            - the data structure containing the results to be printed
	 */
	private static void printQuery(Path path, TreeMap<String, List<SearchResult>> finishedBuildQuery) {
		try (BufferedWriter writer = Files.newBufferedWriter(path);) {
			writer.write("{\n");
			int wordCount = 0;

			if (!finishedBuildQuery.isEmpty()) {
				for (String key : finishedBuildQuery.keySet()) {
					int SearchResultCount = 0;
					if (wordCount == 0) {
						writer.write("\t" + quote(key) + ": [");
						wordCount++;
					} else {
						writer.write(",\n\t" + quote(key) + ": [");
					}
					for (SearchResult qq : finishedBuildQuery.get(key)) {
						int size = finishedBuildQuery.get(key).size();
						if (SearchResultCount == size - 1) {
							writer.write("\n\t\t{\n\t\t\t" + quote("where") + ": " + quote(qq.getFile()) + ",\n\t\t\t"
									+ quote("count") + ": " + qq.getFrequency() + ",\n\t\t\t" + quote("index") + ": "
									+ qq.getPosition() + "\n\t\t}");
						} else {
							writer.write("\n\t\t{\n\t\t\t" + quote("where") + ": " + quote(qq.getFile()) + ",\n\t\t\t"
									+ quote("count") + ": " + qq.getFrequency() + ",\n\t\t\t" + quote("index") + ": "
									+ qq.getPosition() + "\n\t\t},");
						}
						SearchResultCount++;
					}
					writer.write("\n\t]");
				}
				writer.write("\n}");
			}
			wordCount = 0;
			writer.write("\n");
		} catch (IOException e) {
			System.err.println("Could not print to: " + path.toString());
		}
	}

	/**
	 * Adds quotes to any string, useful in pretty printing JSON
	 * 
	 * @param text
	 *            - the string to be quoted
	 * @return the string with quotations on it
	 */
	public static String quote(String text) {
		return String.format("\"%s\"", text);
	}

}