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

public class queryBuilder {

	private final TreeMap<String, List<Query>> buildQuery;

	/**
	 * Initializes a new queryBuilder object
	 */
	public queryBuilder() {
		buildQuery = new TreeMap<String, List<Query>>();
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
				String[] words = line.split("\n");
				String cleaned = null;
				for (int i = 0; i < words.length; i++) {

					// clean each word, removing all non-alphanumerics
					String holder = words[i];
					cleaned = holder.replaceAll("\\p{Punct}+", "");
					String trimmed = cleaned.trim();
					String lowerCase = trimmed.toLowerCase();

					if (lowerCase.compareTo("") != 0) {
						
						//if multiple word query, sort the words
						if (lowerCase.matches(".*\\s++.*")) {
							String multiWordQuery = new String();
							lowerCase = lowerCase.replaceAll("\\s+", " ");
							String[] multiWordArray = lowerCase.split("\\s+");
							Arrays.sort(multiWordArray);
							
							//recombine multiple word query
							for (int p = 0; p < multiWordArray.length; p++) {
								if (p == (multiWordArray.length - 1)) {
									multiWordQuery += multiWordArray[p];
								} else {
									multiWordQuery += multiWordArray[p] + " ";
								}
							}
							
							//instantiate list to hold results of search
							ArrayList<Query> list = new ArrayList<>();
							if(searchFlag == 0){
								list = index.exactSearch(multiWordQuery);
							}else{
								list = index.partialSearch(multiWordQuery);
							}
							getbuildQuery().put(multiWordQuery, list);
							
						//if single word query	
						} else {
							
							//instantiate list to hold results of search
							ArrayList<Query> list = new ArrayList<>();
							if(searchFlag == 0){
								list = index.exactSearch(lowerCase);
							}else{
								list = index.partialSearch(lowerCase);
							}
							getbuildQuery().put(lowerCase, list);
						}
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
	 * 			- the file location to print the results to
	 */
	public void printHelper(Path path){
		queryBuilder.printQuery(path, getbuildQuery());
	}

	/**
	 * Prints the fully populated buildQuery to the out path
	 * 
	 * @param path
	 * 			- the file location to print the results to
	 * @param buildQuery2
	 * 			- the data structure containing the results to be printed
	 */
	private static void printQuery(Path path, TreeMap<String, List<Query>> finishedBuildQuery) {
		try(BufferedWriter writer = Files.newBufferedWriter(path);){
			writer.write("{\n");
			int wordCount = 0;
			
			if(!finishedBuildQuery.isEmpty()){
				for(String key: finishedBuildQuery.keySet()){
					int queryCount = 0;
					if(wordCount == 0){
						writer.write("\t" + quote(key) + ": [");
						wordCount++;
					} else {
						writer.write(",\n\t" + quote(key) + ": [");
					}
					for (Query qq: finishedBuildQuery.get(key)){
						int size = finishedBuildQuery.get(key).size();
						if(queryCount == size - 1){
							writer.write("\n\t\t{\n\t\t\t" + quote("where") + ": " + quote(qq.getFile()) + ",\n\t\t\t" + quote("count") + ": " + qq.getCount()
							+ ",\n\t\t\t" + quote("index") + ": " + qq.getPosition() + "\n\t\t}");
						}else{
							writer.write("\n\t\t{\n\t\t\t" + quote("where") + ": " + quote(qq.getFile()) + ",\n\t\t\t" + quote("count") + ": " + qq.getCount()
							+ ",\n\t\t\t" + quote("index") + ": " + qq.getPosition() + "\n\t\t},");
						}
						queryCount++;
					}
					writer.write("\n\t]");
				}
				writer.write("\n}");
			}
			wordCount = 0;
			writer.write("\n");
		} catch (IOException e) {
			System.err.println("Caught exception: " + path.toString());
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
	public TreeMap<String, List<Query>> getbuildQuery() {
		return buildQuery;
	}		
}