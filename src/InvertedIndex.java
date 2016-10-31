import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.NavigableSet;
import java.util.TreeMap;
import java.util.TreeSet;

public class InvertedIndex {

	//TODO: Should not be static
	//TODO: But should be final
	private static TreeMap<String, TreeMap<String, TreeSet<Integer>>> invertedIndexy;

	//TODO: fix name
	public InvertedIndex() {
		invertedIndexy = new TreeMap<String, TreeMap<String, TreeSet<Integer>>>();
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
		
		if (invertedIndexy.get(word) == null) {
			invertedIndexy.put(word, new TreeMap<String, TreeSet<Integer>>());
			invertedIndexy.get(word).put(file, new TreeSet<Integer>());
			invertedIndexy.get(word).get(file).add(position);
		} else {
			if (invertedIndexy.get(word).get(file) == null) {
				invertedIndexy.get(word).put(file, new TreeSet<Integer>());
				invertedIndexy.get(word).get(file).add(position);
			} else {
				invertedIndexy.get(word).get(file).add(position);
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
	public NavigableSet<String> getKeySet(){
		return invertedIndexy.navigableKeySet();
	}
	
	
	
	//TODO: Breaking encapsulation
	//TODO: Remove this method
	/**
	 * 
	 * @param key - the word which is mapped to the second treeMap
	 * @return the second keyMap
	 */
	public TreeMap<String, TreeSet<Integer>> getKey(String key){
		TreeMap<String, TreeSet<Integer>> mappy = invertedIndexy.get(key);
		return mappy;
	}
	
	//TODO: Only returns false, so have it do something
	/**
	 * 
	 * @param word - the word to be searched for in the inverted index
	 * @return boolean true or false for contains word
	 */
	public boolean containsWord(String word) {
		return false;
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
	
	@Override
	public String toString() {
		return invertedIndexy.toString();
	}
}
