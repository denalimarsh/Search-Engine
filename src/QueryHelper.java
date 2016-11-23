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

// TODO Class name should start with capital letter.
public class QueryHelper {

	private final TreeMap<String, List<PrintResult>> buildQuery;

	/**
	 * Initializes a new QueryHelper object
	 */
	public QueryHelper() {
		buildQuery = new TreeMap<String, List<PrintResult>>();
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
				// TODO Since you are using readLine so it will definitely have one "\n" at the end.
				// So using split("\n") is not making sense since it will always return you ["searhLine", "\n"]
				// Try this template.
				line = line.trim();
				line = line.replaceAll("\\p{Punct}+", "");
				line = line.replaceAll("\\s+", " ");
				line = line.toLowerCase();
				String[] arrayWord = line.split(" ");
				// This mean "multi-word query"
				List<PrintResult> list = new ArrayList<>();
				if (searchFlag == 0) {
					list = index.exactSearch(arrayWord);
				} else {
					list = index.partialSearch(arrayWord);
				}
				// TODO Change getbuildQuery() to buildQuery. It does have access.
				getbuildQuery().put(arrayWord, list);
				line = br.readLine();
				// ----- //
				
				
				String[] words = line.split("\n");
				String cleaned = null;
				for (int i = 0; i < words.length; i++) {

					// clean each word, removing all non-alphanumerics
					String holder = words[i];
					cleaned = holder.replaceAll("\\p{Punct}+", "");
					String trimmed = cleaned.trim();
					String lowerCase = trimmed.toLowerCase();

					if (lowerCase.compareTo("") != 0) {

						// if multiple word query, sort the words
						if (lowerCase.matches(".*\\s++.*")) {
							String multiWordQuery = new String();
							lowerCase = lowerCase.replaceAll("\\s+", " ");
							String[] multiWordArray = lowerCase.split("\\s+");
							Arrays.sort(multiWordArray);

							// recombine multiple word query
							for (int p = 0; p < multiWordArray.length; p++) {
								if (p == (multiWordArray.length - 1)) {
									multiWordQuery += multiWordArray[p];
								} else {
									multiWordQuery += multiWordArray[p] + " ";
								}
							}

							lowerCase = multiWordQuery;
						}

						// instantiate list to hold results of search
						List<PrintResult> list = new ArrayList<>();
						if (searchFlag == 0) {
							list = index.exactSearch(lowerCase);
						} else {
							list = index.partialSearch(lowerCase);
						}
						getbuildQuery().put(lowerCase, list);
					}
				}
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
		QueryHelper.printQuery(path, getbuildQuery());
	}

	/**
	 * Prints the fully populated buildQuery to the out path
	 * 
	 * @param path
	 *            - the file location to print the results to
	 * @param finishedBuildQuery
	 *            - the data structure containing the results to be printed
	 */
	private static void printQuery(Path path, TreeMap<String, List<PrintResult>> finishedBuildQuery) {
		try (BufferedWriter writer = Files.newBufferedWriter(path);) {
			writer.write("{\n");
			int wordCount = 0;

			if (!finishedBuildQuery.isEmpty()) {
				for (String key : finishedBuildQuery.keySet()) {
					int printResultCount = 0;
					if (wordCount == 0) {
						writer.write("\t" + quote(key) + ": [");
						wordCount++;
					} else {
						writer.write(",\n\t" + quote(key) + ": [");
					}
					for (PrintResult qq : finishedBuildQuery.get(key)) {
						int size = finishedBuildQuery.get(key).size();
						if (printResultCount == size - 1) {
							writer.write("\n\t\t{\n\t\t\t" + quote("where") + ": " + quote(qq.getFile()) + ",\n\t\t\t"
									+ quote("count") + ": " + qq.getFrequency() + ",\n\t\t\t" + quote("index") + ": "
									+ qq.getPosition() + "\n\t\t}");
						} else {
							writer.write("\n\t\t{\n\t\t\t" + quote("where") + ": " + quote(qq.getFile()) + ",\n\t\t\t"
									+ quote("count") + ": " + qq.getFrequency() + ",\n\t\t\t" + quote("index") + ": "
									+ qq.getPosition() + "\n\t\t},");
						}
						printResultCount++;
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

	/**
	 * Get method to return the buildQuery
	 * 
	 * @return buildQuery
	 */
	// TODO No need this method.
	public TreeMap<String, List<PrintResult>> getbuildQuery() {
		return buildQuery;
	}
}