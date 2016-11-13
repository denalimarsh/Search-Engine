import java.io.BufferedWriter;
import java.io.IOException;
import java.util.TreeMap;
import java.util.TreeSet;

//TODO Each query doesn't need its own inverted index
//TODO this class should be like a query helper, query builder class

//TODO You should have one class to read in the query file
//TODO Perform queries on inverted index
	//The query helper class will have an inverted index, and a Map which maps the string query to the print result objects
public class Query {

	int frequency;
	String location;
	int initalIndex;
	String word;
	InvertedIndex queryIndex;

	/**
	 * Initializes a new empty Query object
	 */
	public Query() {
		queryIndex = new InvertedIndex();
		frequency = 0;
		location = null;
		initalIndex = -1;
	}

	/**
	 * Initializes a new Query object, assigning it a string as the word or
	 * phrase to be searched for
	 * 
	 * @param searchString
	 *            - the string contained within the Query object that is to be
	 *            searched for
	 */
	public Query(String searchString) {
		queryIndex = new InvertedIndex();
		queryIndex.setStringQuery(searchString);
		frequency = 0;
		location = null;
		initalIndex = -1;
	}

	/**
	 * Sets a query's count, location, and initial index variables
	 * 
	 * @param count
	 *            - the number of times the query occurred within the specified
	 *            file
	 * @param located
	 *            - the name of the file in which the query was found
	 * @param firstOccurence
	 *            - the index representing the first place the query was found
	 *            within the file
	 */
	public void setQuery(int count, String located, int firstOccurence) {
		frequency = count;
		location = located;
		initalIndex = firstOccurence;
	}

	/**
	 * Wrapper method that adds the TreeMap associated with each word in the
	 * inverted index to the query's inverted index
	 * 
	 * @param word
	 *            - the word found in the inverted index
	 * @param map
	 *            - a map of the filenames and positions associated with the
	 *            word in the inverted index
	 * 
	 */
	public void addWordMap(String word, TreeMap<String, TreeSet<Integer>> map) {
		queryIndex.addWordMapIndex(word, map);
	}

	/**
	 * Wrapper method for printQueryIndex, allows queryPrint to be called upon
	 * each individual Query from main.
	 * 
	 * @param bufferedWriter
	 *            - the bufferedWriter instantiated in main used for print
	 *            queries to the out file
	 * @throws IOException
	 *             - Throws exception if the bufferedWriter is null
	 */
	public void queryPrint(BufferedWriter bufferedWriter) throws IOException {
		if (bufferedWriter != null) {
			queryIndex.printQueryIndex(bufferedWriter);
		}
	}

	/**
	 * Getter method which returns the Query's frequency
	 * 
	 * @return frequency
	 */
	public int getFrequency() {
		return frequency;
	}

	/**
	 * Getter method which returns the Query's inital index
	 * 
	 * @return initalIndex
	 */
	public int getInitalIndex() {
		return initalIndex;
	}

	/**
	 * Getter method which returns the Query's location's file name
	 * 
	 * @return location
	 */
	public String getLocation() {
		return location;
	}
}