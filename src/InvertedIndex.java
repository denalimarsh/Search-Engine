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
	 * Adds a node containing the word, file the word is located in,
	 * and the position of the word within the file to an inverted index
	 * 
	 * @param wordUpper - the cleaned word from the parseFile function
	 * @param file - the file which the specified word is located within
	 * @param position - the location of the word within the designated file
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
	 * Prints the inverted index by iterating through the TreeMap of words,
	 * the TreeMap of files, and the TreeSet of integer positions, writing 
	 * them to the designated output location in a pretty print JSON format
	 * 
	 * @param index - the complete inverted index, populated with data
	 * @param path - the designated location which the function will print to
	 * @throws IOException - the exception through if the bufferedReader is 
	 * 						 unable to print to the path
	 */
	public void print(Path path) throws IOException {
			
		try (BufferedWriter bufferedWriter = Files.newBufferedWriter(path, Charset.forName("UTF-8"));) {
			bufferedWriter.write("{\n");
			
			int holderOne = 0; // TODO keys
			for (String key : invertedIndex.keySet()) {
				// TODO Hopefully should not be putting the empty string into the index...
				if (key.compareTo("") != 0) {
					bufferedWriter.write("\t" + quote(key) + ": {\n");
					
					TreeMap<String, TreeSet<Integer>> map = invertedIndex.get(key);
					
					int holderTwo = 0; // TODO files
					for (String fileName : map.navigableKeySet()) {
						bufferedWriter.write("\t\t" + quote(fileName) + ": [\n");
					
						TreeSet<Integer> set = map.get(fileName);
						int holderThree = 0; // TODO positions
						
						for (int a : set) {
							if (holderThree == set.size() - 1) {
								bufferedWriter.write("\t\t\t" + a + "\n");
							} else {
								bufferedWriter.write("\t\t\t" + a + ",\n");
							}
							holderThree++;
						}
						
						if (holderTwo == map.navigableKeySet().size() - 1) {
							bufferedWriter.write("\t\t]\n");
						} else {
							bufferedWriter.write("\t\t],\n");
						}
						holderTwo++;
					}
					
					if (holderOne == invertedIndex.keySet().size() - 1) {
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
			// TODO Output user-friendly (informative) error messages 
			e.printStackTrace();
		}
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
	
	// TODO Need to think more generally about what other methods are useful for an inverted index
	// TODO Contains methods or numWords, etc. would be good to add
}

