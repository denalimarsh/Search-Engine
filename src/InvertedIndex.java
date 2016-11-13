import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeMap;
import java.util.TreeSet;

public class InvertedIndex {

	private final TreeMap<String, TreeMap<String, TreeSet<Integer>>> invertedIndex;
	private String stringQuery;

	/**
	 * Initializes the inverted index
	 */
	public InvertedIndex() {
		invertedIndex = new TreeMap<String, TreeMap<String, TreeSet<Integer>>>();
	}

	/**
	 * 
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
	 * Create print results from the information contained in each Query's
	 * inverted index. Prints each print result.
	 * 
	 * @param bufferedWriter
	 *            - the result path to print to.
	 */
	
	//TODO This could be in query helper class as well
	//TODO: You can create and update during search, then store results in Query Helper class, then write separately
	public void printQueryIndex(BufferedWriter bufferedWriter) {
		try {
			TreeSet<PrintResult> resultSet = new TreeSet<>();
			TreeMap<String, PrintResult> resultMap = new TreeMap<>();

			// iterate through each word in the inverted index
			for (String key : invertedIndex.keySet()) {
				TreeMap<String, TreeSet<Integer>> files = invertedIndex.get(key);
				for (String fileName : files.navigableKeySet()) {

					// add map to new query
					if (!resultMap.containsKey(fileName)) {
						TreeSet<Integer> set = files.get(fileName);

						// create new print result
						PrintResult printResult = new PrintResult();

						// populate each print result with file name, count, and
						// initial index
						int count = set.size();
						int initalIndex = Collections.min(set);
						printResult.setFile(fileName);
						printResult.setCount(count);
						printResult.setPosition(initalIndex);

						// add the print result to a temporary set
						resultSet.add(printResult);

						// add each print result to a map, used to determine
						// if a query's map should be mapped to a new query
						// or added to an existing query
						resultMap.put(fileName, printResult);

						// add map to existing query, used for multiple word
						// queries
					} else {

						// get the current print result
						PrintResult printResult = resultMap.get(fileName);
						TreeSet<Integer> set = files.get(fileName);

						// update the print result
						int count = set.size();
						int position = -1;
						position = Collections.min(set);
						int currentCount = printResult.getCount();
						printResult.setCount(currentCount + count);
						int currentPosition = printResult.getPosition();
						int minValue = Math.min(currentPosition, position);
						printResult.setPosition(minValue);
					}
				}
			}

			// remove duplicate print results
			TreeSet<PrintResult> finalSet = new TreeSet<>();
			for (PrintResult curr : resultSet) {
				finalSet.add(curr);
			}

			bufferedWriter.write("\t" + quote(stringQuery) + ": [");

			int i = 0;
			for (PrintResult printResult : finalSet) {
				i++;
				if (i == finalSet.size()) {
					bufferedWriter.write(printResult.toString());
				} else {
					bufferedWriter.write(printResult.toString() + ",");
				}
			}
		} catch (Exception e) {
			System.out.println("Unable to print the inverted index to location");
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
	 * Searches the inverted index for exact matches of strings contained in the
	 * parsed queries
	 * 
	 * @param fullQueries
	 *            - list of queries, stored as strings.
	 * @return queryList - list of queries, stored as queries.
	 */
	public ArrayList<Query> exactSearch(ArrayList<String> fullQueries) {
		
		ArrayList<Query> queryList = new ArrayList<>();
		for (String stringQuery : fullQueries) {

			// create a new query for each string contained in the list
			Query query = new Query(stringQuery);

			// break each query by space into array of strings
			String[] arrayWords = stringQuery.split(" ");

			// for each word, see if it is in the main inverted index
			// if it is, add that word's map to query's unique inverted index
			//TODO: If index contains query, create new query object, then add query object to list, return list at the end
			//TODO: IF multiword, check if query is already there, if not create new query, otherwise update
			for (int i = 0; i < arrayWords.length; i++) {
				if (invertedIndex.containsKey(arrayWords[i])) {
					TreeMap<String, TreeSet<Integer>> map = invertedIndex.get(arrayWords[i]);
					query.addWordMap(arrayWords[i], map);
				}
			}

			// add the query ADT to the list
			queryList.add(query);
		}
		return queryList;
	}

	/**
	 * Maps a word to a map containing its file name and position, used by Query
	 * class to populate each unique inverted index
	 * 
	 * @param word
	 * @param map
	 */
	public void addWordMapIndex(String word, TreeMap<String, TreeSet<Integer>> map) {
		if (invertedIndex.get(word) == null) {
			invertedIndex.put(word, map);
		}
	}

	/**
	 * Searches the inverted index for exact matches of strings contained in the
	 * parsed queries
	 * 
	 * @param fullQueries
	 *            - list of queries, stored as strings.
	 * @return queryList - list of queries, stored as queries.
	 */
	public ArrayList<Query> partialSearch(ArrayList<String> fullQueries) {

		int numbWords;

		ArrayList<Query> queryList = new ArrayList<>();
		for (String stringQuery : fullQueries) {

			Query query = new Query(stringQuery);

			String[] arrayWords = stringQuery.split(" ");
			numbWords = arrayWords.length;

			for (int i = 0; i < numbWords; i++) {
				for (String curr : invertedIndex.keySet()) {
					if (curr.startsWith(arrayWords[i])) {
						TreeMap<String, TreeSet<Integer>> map = invertedIndex.get(curr);
						query.addWordMap(curr, map);
					}
				}
			}
			queryList.add(query);
		}
		return queryList;
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

	/**
	 * Determines if a part of a specified word is contained within the inverted
	 * index
	 * 
	 * @param word
	 *            - the word to be searched for
	 * @return boolean true or false
	 */
	public boolean containsPartial(String word) {
		for (String key : invertedIndex.keySet()) {
			if (key.contains(word)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the string found as a partial match for a word
	 * 
	 * @param word
	 *            - word to be searched for
	 * @return key - the string found at the partial match
	 */
	public String returnsPartial(String word) {
		for (String key : invertedIndex.keySet()) {
			if (key.contains(word)) {
				return key;
			}
		}
		return null;
	}

	/**
	 * Wrapper method used to set the string or strings that comprise a Query's
	 * word/phrase to search for
	 * 
	 * @param stringQuery
	 */
	public void setStringQuery(String stringQuery) {
		this.stringQuery = stringQuery;
	}
}
