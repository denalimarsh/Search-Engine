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
	 * 			- the string to be searched for
	 * @return list
	 * 			- list of Query objects found as the result of the search
	 */
	public ArrayList<Query> exactSearch(String stringQuery) {

			HashMap<String, Query> resultMap = new HashMap<>();
			ArrayList<Query> list = new ArrayList<>();
			
			String[] arrayWords = stringQuery.split(" ");

				for(int i = 0; i < arrayWords.length; i++){
					if(containsWord(arrayWords[i])) {
						TreeMap<String, TreeSet<Integer>> map = invertedIndex.get(arrayWords[i]);			
						for (Map.Entry<String, TreeSet<Integer>> entry : map.entrySet())
						{
							Query query = new Query();
							TreeSet<Integer> set = entry.getValue();
							
							int count = set.size();
							int initalIndex = Collections.min(set);
							query.setFile(entry.getKey());
							query.setCount(count);
							query.setPosition(initalIndex);
							
							if(!resultMap.containsKey(entry.getKey())){
								resultMap.put(entry.getKey(), query);
								list.add(query);
							}else{
								Query oldQuery = resultMap.get(entry.getKey());
								oldQuery.updateQuery(query);
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
	 * 			- the string to be searched for
	 * @return list
	 * 			- list of Query objects found as the result of the search
	 */
	public ArrayList<Query> partialSearch(String stringQuery) {

		HashMap<String, Query> resultMap = new HashMap<>();
		ArrayList<Query> list = new ArrayList<>();
		
		String[] arrayWords = stringQuery.split(" ");

			for(int i = 0; i < arrayWords.length; i++){
				if(containsPartial(arrayWords[i])) {
					List<String> partialMatches = returnsPartial(arrayWords[i]);
					for(String match: partialMatches){
						TreeMap<String, TreeSet<Integer>> map = invertedIndex.get(match);
						for (Map.Entry<String, TreeSet<Integer>> entry : map.entrySet())
						{
							Query query = new Query();
							TreeSet<Integer> set = entry.getValue();
							
							int count = set.size();
							int initalIndex = Collections.min(set);
							query.setFile(entry.getKey());
							query.setCount(count);
							query.setPosition(initalIndex);
							
							if(!resultMap.containsKey(entry.getKey())){
								resultMap.put(entry.getKey(), query);
								list.add(query);
							}else{
								Query oldQuery = resultMap.get(entry.getKey());
								oldQuery.updateQuery(query);
							}
						}
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
			if (key.startsWith(word)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns all the strings found as a partial match for a word
	 * 
	 * @param word
	 *          - word to be searched for
	 * @return list 
	 * 			- list of strings found to have a partial match
	 */
	public ArrayList<String> returnsPartial(String word) {
		ArrayList<String> list = new ArrayList<>();
		for (String key : invertedIndex.keySet()) {
			if (key.startsWith(word)) {
				list.add(key);
			}
		}
		return list;
	}
	
	/**
	 * Gets the keySet of the inverted index
	 * 
	 * @return
	 * 		- the keySet of the inverted index
	 */
	public Object keySet() {
		return invertedIndex.keySet();
	}
	
	/**
	 * Gets the specified key of the inverted index
	 * 
	 * @param key
	 * 			- the key whose map we want to return
	 * @return
	 * 			- the map associated with the specified key
	 */
	public TreeMap<String, TreeSet<Integer>> get(String key) {
		return invertedIndex.get(key);
	}
}
