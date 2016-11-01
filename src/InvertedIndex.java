import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.NavigableSet;
import java.util.TreeMap;
import java.util.TreeSet;

public class InvertedIndex {

	private final TreeMap<String, TreeMap<String, TreeSet<Integer>>> invertedIndex;

	public InvertedIndex() {
		invertedIndex = new TreeMap<String, TreeMap<String, TreeSet<Integer>>>();
	}
	
	/**
	 * 
	 * Adds a node containing the word, file the word is located in,
	 * and the position of the word within the file to an inverted index
	 * 
	 * @param wordUpper - the cleaned word from the parseFile function
	 * @param file - the file which the specified word is located within
	 * @param position - the location of the word within the designated file
	 */
	public void add(String wordUpper, String file, int position) {
		
		String word = wordUpper.toLowerCase();
		
		if (invertedIndex.get(word) == null) {
			invertedIndex.put(word, new TreeMap<String, TreeSet<Integer>>());
			invertedIndex.get(word).put(file, new TreeSet<Integer>());
			invertedIndex.get(word).get(file).add(position);
		} else {
			if (invertedIndex.get(word).get(file) == null) {
				invertedIndex.get(word).put(file, new TreeSet<Integer>());
				invertedIndex.get(word).get(file).add(position);
			} else {
				invertedIndex.get(word).get(file).add(position);
			}
		}
	}

	/**
	 * Prints the inverted index by iterating through the TreeMap of words,
	 * the TreeMap of files, and the TreeSet of integer positions, writing 
	 * them to the designated output location in a pretty print JSON format
	 * 
	 * @param index - the complete inverted index, populated with data
	 * @param path - the designated location which the function will print to
	 * @throws IOException - the exception through if the bufferedReader is 
	 * 						 unable to print to the path
	 */
	public static void print(InvertedIndex index, Path path) throws IOException {
			
		try (BufferedWriter bufferedWriter = Files.newBufferedWriter(path, Charset.forName("UTF-8"));) {
			bufferedWriter.write("{\n");
			
			int holderOne = 0;
			for (String key : index.getKeySet()) {
				if (key.compareTo("") != 0) {
					bufferedWriter.write("\t" + quote(key) + ": {\n");
					
					TreeMap<String, TreeSet<Integer>> mappy = index.getKey(key);
					int holderTwo = 0;
					for (String fileName : mappy.navigableKeySet()) {
						bufferedWriter.write("\t\t" + quote(fileName) + ": [\n");
					
						TreeSet<Integer> setty = mappy.get(fileName);
						int holderThree = 0;
						for (int a : setty) {
							if (holderThree == setty.size() - 1) {
								bufferedWriter.write("\t\t\t" + a + "\n");
							} else {
								bufferedWriter.write("\t\t\t" + a + ",\n");
							}
							holderThree++;
						}
						if (holderTwo == mappy.navigableKeySet().size() - 1) {
							bufferedWriter.write("\t\t]\n");
						} else {
							bufferedWriter.write("\t\t],\n");
						}
						holderTwo++;
					}
					if (holderOne == index.getKeySet().size() - 1) {
						bufferedWriter.write("\t}\n");
					} else {
						bufferedWriter.write("\t},\n");
					}
					holderOne++;
				}
			}
			bufferedWriter.write("}");		
			bufferedWriter.flush();
			bufferedWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	//TODO: Remove this too
	/**
	 * Returns a navigable keySet from the inverted index
	 * 
	 * @return the navigable keySet
	 */
	private NavigableSet<String> getKeySet(){
		return invertedIndex.navigableKeySet();
	}
	
	
	/**
	 * 
	 * @param key - the word which is mapped to the second treeMap
	 * @return the second keyMap
	 */
	private TreeMap<String, TreeSet<Integer>> getKey(String key){
		TreeMap<String, TreeSet<Integer>> mappy = invertedIndex.get(key);
		return mappy;
	}
	
	/**
	 * Adds quotes to any string, useful in pretty printing JSON
	 * 
	 * @param text - the string to be quoted
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
