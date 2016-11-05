import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

public class InvertedIndex {

	private final TreeMap<String, TreeMap<String, TreeSet<Integer>>> invertedIndex;
	private String stringQuery;

	
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
	
	public void addWord(String wordUpper) {

		String word = wordUpper.toLowerCase();

		if (invertedIndex.get(word) == null) {
			invertedIndex.put(word, new TreeMap<String, TreeSet<Integer>>());
		} 
	}

	
	public void printQueryIndex(BufferedWriter bufferedWriter){
		try {
			TreeSet<PrintResult> resultSet = new TreeSet<>();
			TreeMap<String, PrintResult> resultMap = new TreeMap<>();
			
			int a = invertedIndex.keySet().size();
			
			for(String key: invertedIndex.keySet()){			
				
				TreeMap<String, TreeSet<Integer>> files = invertedIndex.get(key);
				for (String fileName : files.navigableKeySet()){
					if(!resultMap.containsKey(fileName)){
						
						PrintResult printResult = new PrintResult();
						
						TreeSet<Integer> set = files.get(fileName);
						
						
						int count = set.size();
						int position = -1;
						position = Collections.min(set);
	
						printResult.setFile(fileName);
						printResult.setCount(count);
						printResult.setPosition(position);
						
						resultSet.add(printResult);
						resultMap.put(fileName, printResult);
					}else{
						PrintResult printResult = resultMap.get(fileName);
						TreeSet<Integer> set = files.get(fileName);
						
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
			
			TreeSet<PrintResult> finalResult = new TreeSet<>();
			for (PrintResult curr : resultSet) {
				finalResult.add(curr);
			}
			
			bufferedWriter.write("\t" + quote(stringQuery) + ": [");
			
			int i = 0;
			for(PrintResult printResult: finalResult){
				i++;
				if(i == finalResult.size()){
					bufferedWriter.write(printResult.toString());
				}else{
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
	 * 
	 * @param importMap
	 * @return
	 */
	public ArrayList<Query> exactSearch(ArrayList<String> fullQueries){
		 
		int numbWords;
		
		ArrayList<Query> queryList = new ArrayList<>();
		
		for(String stringQuery: fullQueries){
			
			Query query = new Query(stringQuery);
			
			String [] arrayWords = stringQuery.split(" ");
			numbWords = arrayWords.length;
			
			for(int i = 0; i < numbWords; i++){
				if(invertedIndex.containsKey(arrayWords[i])){	
					TreeMap<String, TreeSet<Integer>> map = invertedIndex.get(arrayWords[i]);
					
					query.addWordMap(arrayWords[i], map);
				}				
			}
			queryList.add(query);
		}
		return queryList;
	}
	
	/**
	 * 
	 * @param word
	 * @param map
	 */
	public void addWordMapIndex(String word, TreeMap<String, TreeSet<Integer>> map){
		if (invertedIndex.get(word) == null){
			invertedIndex.put(word, map);
		}
	}
	public ArrayList<Query> partialSearch(ArrayList<String> fullQueries){
		 
		int numbWords;
		
		ArrayList<Query> queryList = new ArrayList<>();
		
		for(String stringQuery: fullQueries){
			
			Query query = new Query(stringQuery);
			
			String [] arrayWords = stringQuery.split(" ");
			numbWords = arrayWords.length;
			
			for(int i = 0; i < numbWords; i++){
				for(String curr: invertedIndex.keySet()){
					if(curr.startsWith(arrayWords[i])){	
						
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
	 * Determines if a part of a specified word is contained within the inverted index
	 * 
	 * @param word
	 *            - the word to be searched for
	 * @return boolean true or false
	 */
	public boolean containsPartial(String word) {
		for (String key : invertedIndex.keySet()) {
			if(key.contains(word)){
				return true;
			}
		}
		return false;
	}
	
	public String returnsPartial(String word) {
		for (String key : invertedIndex.keySet()) {
			if(key.contains(word)){
				return key;
			}
		}
		return null;
	}

	public String getStringQuery() {
		return stringQuery;
	}

	public void setStringQuery(String stringQuery) {
		this.stringQuery = stringQuery;
	}
}
