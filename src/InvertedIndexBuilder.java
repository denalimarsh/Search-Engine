import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

public class InvertedIndexBuilder {

	/**
	 * Traverses the input directory recursively, locating all folders and files
	 * inside. If the file is a text file, call the "parseFile" function
	 * 
	 * @param originalPath
	 *            - the original input directory to be searched
	 * @param index
	 *            - the inverted index to be added to
	 * @param outPath
	 *            - the final destination for the inverted index to be written
	 *            to
	 */

	public static void traverse(Path originalPath, InvertedIndex index) {
		try (DirectoryStream<Path> listing = Files.newDirectoryStream(originalPath)) {
			for (Path path : listing) {
				if (Files.isDirectory(path)) {
					traverse(path, index);
				} else if (path.toString().toLowerCase().endsWith(".txt")) {
					parseFile(path, index);
				}
			}
		} catch (IOException e) {
			System.out.println("Unable to access " + originalPath.toString() + " to parse.");
		}
	}

	/**
	 * 
	 * Reads a text file, cleans the words, adds them to the inverted index
	 * 
	 * @param path
	 *            - the text file to be read in
	 * @param index
	 *            - the inverted index to be added to
	 * @throws IOException
	 *             - thrown if the bufferedReader is unable to write to the
	 *             designated path
	 */

	public static void parseFile(Path path, InvertedIndex index) {

		String pathName = path.normalize().toString();
		int positionHolder = 0;

		try (BufferedReader br = Files.newBufferedReader(path, Charset.forName("UTF-8"))) {
			String line = br.readLine();
			while ((line) != null) {
				String[] words = line.split("\\s+");
				String cleanedWord = null;
				for (int i = 0; i < words.length; i++) {
					String holder = words[i];
					cleanedWord = holder.replaceAll("\\p{Punct}+", "");
					String trimmedWord = cleanedWord.trim();
					String lowerCaseWord = trimmedWord.toLowerCase();
					if (!lowerCaseWord.isEmpty()) {
						positionHolder++;
						index.add(lowerCaseWord, pathName, positionHolder);
					}
				}
				line = br.readLine();
			}
		} catch (IOException ex) {
			System.out.println("Unable to parse " + pathName + " into an inverted index.");
		}
	}

	/**
	 * 
	 * Reads a text file, cleans the words, adds them to a list
	 * 
	 * @param path
	 *            - the path to be read and parsed into queries
	 * @return uniqueList - a sorted, unique list of Strings
	 */
	public static ArrayList<String> parseQuery(Path path) {

		Charset charset = java.nio.charset.StandardCharsets.UTF_8;
		ArrayList<String> list = new ArrayList<>();

		try (BufferedReader br = Files.newBufferedReader(path, charset)) {
			String line = br.readLine();
			while ((line) != null) {
				String[] words = line.split("\n");
				String cleaned = null;
				for (int i = 0; i < words.length; i++) {

					// clean each word, removing all non-alphanumerics
					String holder = words[i];
					cleaned = holder.replaceAll("\\p{Punct}+", "");
					String trimmed = cleaned.trim();
					String lowerCase = trimmed.toLowerCase();

					if (lowerCase.compareTo("") != 0) {

						// sort and add multiple word query
						if (lowerCase.matches(".*\\s++.*")) {
							String multiWordQuery = new String();
							lowerCase = lowerCase.replaceAll("\\s+", " ");
							String[] multiWordArray = lowerCase.split("\\s+");
							Arrays.sort(multiWordArray);

							for (int p = 0; p < multiWordArray.length; p++) {
								if (p == (multiWordArray.length - 1)) {
									multiWordQuery += multiWordArray[p];
								} else {
									multiWordQuery += multiWordArray[p] + " ";
								}
							}
							list.add(multiWordQuery);

							// add single word query
						} else {
							list.add(lowerCase);
						}
					}
				}
				line = br.readLine();
			}
		} catch (IOException ex) {
			System.out.println(ex.toString());
			System.out.println("Could not parse queries from file " + path.toString());
		}

		// remove duplicates from the list and sort
		ArrayList<String> uniqueList = removeDuplicates(list);
		Collections.sort(uniqueList);

		return uniqueList;
	}

	/**
	 * Removes all duplicates from a list
	 * 
	 * @param list
	 *            - list of Strings
	 * @return result - unique list of Strings with duplicates removed
	 */
	public static ArrayList<String> removeDuplicates(ArrayList<String> list) {

		ArrayList<String> result = new ArrayList<>();
		HashSet<String> set = new HashSet<>();

		for (String item : list) {
			if (!set.contains(item)) {
				result.add(item);
				set.add(item);
			}
		}
		return result;
	}
}
