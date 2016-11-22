import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

public class InvertedIndex {

	private final TreeMap<String, TreeMap<String, TreeSet<Integer>>> invertedIndex;

	/**
	 * Initializes the inverted index
	 */
	public InvertedIndex() {
		invertedIndex = new TreeMap<String, TreeMap<String, TreeSet<Integer>>>();
	}

	/**
	 * Adds a node containing the word, file the word is located in, and the
	 * position of the word within the file to an inverted index
	 * 
	 * @param word
	 *            - the cleaned word from the parseFile function
	 * @param file
	 *            - the file which the specified word is located within
	 * @param position
	 *            - the location of the word within the designated file
	 */
	public void add(String word, String path, int position) {

		if (invertedIndex.get(word) == null) {
			invertedIndex.put(word, new TreeMap<String, TreeSet<Integer>>());
			invertedIndex.get(word).put(path, new TreeSet<Integer>());
			invertedIndex.get(word).get(path).add(position);
		} else {
			if (invertedIndex.get(word).get(path) == null) {
				invertedIndex.get(word).put(path, new TreeSet<Integer>());
				invertedIndex.get(word).get(path).add(position);
			} else {
				invertedIndex.get(word).get(path).add(position);
			}
		}
	}

	/**
	 * Prints the inverted index by iterating through the TreeMap of words, the
	 * TreeMap of files, and the TreeSet of integer positions, writing them to
	 * the designated output location in a pretty print JSON format
	 * 
	 * @param index
	 *            - the complete inverted index, populated with data
	 * @param path
	 *            - the designated location which the function will print to
	 * @throws IOException
	 *             - the exception through if the bufferedReader is unable to
	 *             print to the path
	 */
	public void print(Path path) {

		try (BufferedWriter bufferedWriter = Files.newBufferedWriter(path, Charset.forName("UTF-8"));) {
			bufferedWriter.write("{\n");

			int keyCount = 0;
			for (String key : invertedIndex.keySet()) {
				bufferedWriter.write("\t" + quote(key) + ": {\n");

				TreeMap<String, TreeSet<Integer>> map = invertedIndex.get(key);

				int fileCount = 0;
				for (String fileName : map.navigableKeySet()) {
					bufferedWriter.write("\t\t" + quote(fileName) + ": [\n");

					TreeSet<Integer> set = map.get(fileName);
					int positionCount = 0;

					for (int a : set) {
						if (positionCount == set.size() - 1) {
							bufferedWriter.write("\t\t\t" + a + "\n");
						} else {
							bufferedWriter.write("\t\t\t" + a + ",\n");
						}
						positionCount++;
					}

					if (fileCount == map.navigableKeySet().size() - 1) {
						bufferedWriter.write("\t\t]\n");
					} else {
						bufferedWriter.write("\t\t],\n");
					}
					fileCount++;
				}

				if (keyCount == invertedIndex.keySet().size() - 1) {
					bufferedWriter.write("\t}\n");
				} else {
					bufferedWriter.write("\t},\n");
				}
				keyCount++;
			}
			bufferedWriter.write("}");
		} catch (IOException e) {
			System.out.println("Unable to print the inverted index to location " + quote(path.toString()));
		}
	}

	/**
	 * Performs an exact search of the inverted index for the specified string
	 * 
	 * @param stringQuery
	 *            - the string to be searched for
	 * @return list - list of Query objects found as the result of the search
	 */
	public List<PrintResult> exactSearch(String stringQuery) {

		Map<String, PrintResult> resultMap = new HashMap<>();
		List<PrintResult> list = new ArrayList<>();

		String[] arrayWords = stringQuery.split(" ");

		for (int i = 0; i < arrayWords.length; i++) {
			if (invertedIndex.containsKey(arrayWords[i])) {
				TreeMap<String, TreeSet<Integer>> map = invertedIndex.get(arrayWords[i]);
				for (Map.Entry<String, TreeSet<Integer>> entry : map.entrySet()) {
					TreeSet<Integer> set = entry.getValue();
					int count = set.size();
					int initalIndex = Collections.min(set);

					if (!resultMap.containsKey(entry.getKey())) {
						PrintResult result = new PrintResult(entry.getKey(), count, initalIndex);
						resultMap.put(entry.getKey(), result);
						list.add(result);
					} else {
						PrintResult oldQuery = resultMap.get(entry.getKey());
						oldQuery.updatePrintResult(count, initalIndex);
					}
				}
			}
			Collections.sort(list);
		}
		return list;
	}

	/**
	 * Performs a partial search of the inverted index for the specified string
	 * 
	 * @param stringQuery
	 *            - the string to be searched for
	 * @return list - list of Query objects found as the result of the search
	 */
	public List<PrintResult> partialSearch(String stringQuery) {

		Map<String, PrintResult> resultMap = new HashMap<>();
		List<PrintResult> list = new ArrayList<>();

		String[] arrayWords = stringQuery.split(" ");

		for (int i = 0; i < arrayWords.length; i++) {
			String word = arrayWords[i];
			for (String key : invertedIndex.tailMap(word).keySet()) {
				if (key.startsWith(word)) {
					TreeMap<String, TreeSet<Integer>> map = invertedIndex.get(key);
					for (Map.Entry<String, TreeSet<Integer>> entry : map.entrySet()) {
						TreeSet<Integer> set = entry.getValue();
						int count = set.size();
						int initalIndex = Collections.min(set);

						if (!resultMap.containsKey(entry.getKey())) {
							PrintResult result = new PrintResult(entry.getKey(), count, initalIndex);
							resultMap.put(entry.getKey(), result);
							list.add(result);
						} else {
							PrintResult oldQuery = resultMap.get(entry.getKey());
							oldQuery.updatePrintResult(count, initalIndex);
						}
					}
				} else {
					break;
				}
			}
			Collections.sort(list);
		}
		return list;
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
	 * @return the Inverted Index as a string
	 */
	@Override
	public String toString() {
		return invertedIndex.toString();
	}
}
