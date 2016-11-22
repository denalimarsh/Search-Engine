
// TODO Rename it to SearchResult.
public class Query implements Comparable<Query> {

	/**
	 * Implemented Comparable, sorts the Queries according to their
	 * frequency, then their initial index, then their file name
	 */
	@Override
	// TODO Bring this thing after constructor.
	public int compareTo(Query o) {

		int frequencyHolder = Integer.compare(o.getCount(), count);
		if (frequencyHolder == 0) {
			int positionHolder = Integer.compare(position, o.getPosition());
			if (positionHolder == 0) {
				int locationHolder = file.compareTo(o.getFile());
				return locationHolder;
			}
			return positionHolder;
		}
		return frequencyHolder;
	}

	// TODO Thid should be private. And file can be final.
	String file;
	// TODO Rename this to "frequency".
	int count;
	int position;

	/**
	 * Initialize new Query
	 */
	public Query(){
		file = null;
		count = 0;
		position = 0;
	}
	// TODO Implement this.
//	public Query(Stirng file, int count, int position){
//		
//	}
	
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
	// TODO Remove this.
	public void setQuery(int count, String located, int firstOccurence) {
		this.count = count;
		this.file = located;
		this.position = firstOccurence;
	}
	
	/**
	 * Updates a Query object's position and count from the information
	 * contained within another Query object, resulting in one combined
	 * Query object
	 * 
	 * @param query
	 * 			- the new Query whose values will be used to update the
	 * 			  existing Query object
	 */
	// TODO Remove this.
	public void updateQuery(Query query){
		int frequency = query.getCount();
		int initalIndex = query.getPosition();
		
		this.count = count + frequency;
		if(initalIndex < this.position){
			this.position = initalIndex;
		}
	}
	
	// TODO Use this.
	public void updateQuery(int frequency, int initalIndex){
		this.count = count + frequency;
		if(initalIndex < this.position){
			this.position = initalIndex;
		}
	}
	
	/**
	 * Getter method for file name
	 * 
	 * @return the location of the Query
	 */
	public String getFile() {
		return file;
	}

	/**
	 * Getter method for frequency
	 * 
	 * @return the Query's frequency
	 */
	public int getCount() {
		return count;
	}

	/**
	 * Getter method for initial index
	 * 
	 * @return the Query's initial index
	 */

	public int getPosition() {
		return position;
	}

	/**
	 * Setter method for initial index
	 * 
	 * @param index
	 *            - the initial index to be set as the Query's position
	 */
	public void setPosition(int index) {
		position = index;
	}

	/**
	 * Setter method for word frequency
	 * 
	 * @param counter
	 *            - the frequency of word occurrence to be set as the Query's count
	 */
	public void setCount(int counter) {
		count = counter;
	}

	/**
	 * Setter method for file name of location
	 * 
	 * @param fileName
	 *            - the file name to be set as the Query's file location
	 */
	// TODO Remove all unused method.
	public void setFile(String fileName) {
		file = fileName;
	}

	/**
	 * Turns the Query into a string
	 * 
	 * @return a string representing the data contained within the Query
	 */
	public String toString() {
		return "\n\t\t{\n\t\t\t" + quote("where") + ": " + quote(file) + ",\n\t\t\t" + quote("count") + ": " + count
				+ ",\n\t\t\t" + quote("index") + ": " + position + "\n\t\t}";
	}

	/**
	 * Adds quotes to any string, useful in pretty printing JSON
	 * 
	 * @param text
	 *            - the string to be quoted
	 * @return the string with quotations on it
	 */
	public String quote(String text) {
		return String.format("\"%s\"", text);
	}
}
