import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.TreeMap;
import java.util.TreeSet;

public class InvertedIndex {

	private final TreeMap<String, TreeMap<String, TreeSet<Integer>>> invertedIndex;

	/**
	 * TODO
	 */
	public InvertedIndex() {
		invertedIndex = new TreeMap<String, TreeMap<String, TreeSet<Integer>>>();
	}

	/**
	 * 
	 * Adds a node containing the word, file the word is located in, and the
	 * position of the word within the file to an inverted index
	 * 
	 * @param wordUpper
	 *            - the cleaned word from the parseFile function
	 * @param file
	 *            - the file which the specified word is located within
	 * @param position
	 *            - the location of the word within the designated file
	 */
	public void add(String wordUpper, String path, int position) {

		String word = wordUpper.toLowerCase();

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

	/**
	 * Determines if a specified word is contained within the inverted index
	 * 
	 * @param word
	 *            - the word to be searched for
	 * @return boolean true or false
	 */
	public boolean containsWord(String word) {
		if (invertedIndex.containsKey(word)) {
			return true;
		}
		return false;
	}
}
