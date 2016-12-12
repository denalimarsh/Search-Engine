import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

public class QueryHelper implements QueryHelperInterface {

	private final TreeMap<String, List<SearchResult>> buildResult;
	private final InvertedIndex index;

	/**
	 * Initializes a new QueryHelper object
	 */
	public QueryHelper(InvertedIndex input) {
		buildResult = new TreeMap<String, List<SearchResult>>();
		this.index = input;
	}

	/**
	 * Reads a text file, cleans the words, adds them to a list
	 * 
	 * @param path
	 *            - the path to be read and parsed into queries
	 * @return uniqueList - a sorted, unique list of Strings
	 */
	public void parseQuery(Path file, boolean searchFlag) {

		try (BufferedReader reader = Files.newBufferedReader(file, Charset.forName("UTF-8"));) {

			String line;
			while ((line = reader.readLine()) != null) {
				String[] words = line.trim().replaceAll("\\p{Punct}+", "").toLowerCase().split("\\s+");
				Arrays.sort(words);
				String searchname = String.join(" ", words);
				if (searchFlag) {
					List<SearchResult> results = index.exactSearch(words);
					buildResult.put(searchname, results);
				} else if (!searchFlag) {
					List<SearchResult> results = index.partialSearch(words);
					buildResult.put(searchname, results);
				}
			}

			reader.close();

		} catch (IOException e) {
			System.out.println("Unable to read query file");
		}

	}

	/**
	 * Prints the fully populated buildQuery to the out path
	 * 
	 * @param path
	 *            - the file location to print the results to
	 * @param finishedBuildQuery
	 *            - the data structure containing the results to be printed
	 */
	public static void printQuery(Path path, TreeMap<String, List<SearchResult>> finishedBuildQuery) {
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
	 * Wrapper method to allow the driver to access the print Query method
	 * 
	 * @param path
	 *            - the file location to print the results to
	 */
	public void printHelper(Path path) {
		QueryHelper.printQuery(path, buildResult);
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