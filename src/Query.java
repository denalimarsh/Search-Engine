public class Query implements Comparable<Query> {

	/**
	 * Implemented Comparable, sorts the print results according to their
	 * frequency, then their initial index, then their file name
	 */
	@Override
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

	String file;
	int count;
	int position;

	/**
	 * Initialize new print result
	 */
	public Query(){
		file = null;
		count = 0;
		position = 0;
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
	public void updateQuery(Query query){
		int frequency = query.getCount();
		int initalIndex = query.getPosition();
		
		this.count = count + frequency;
		if(initalIndex < this.position){
			this.position = initalIndex;
		}
	}
	
	/**
	 * Getter method for file name
	 * 
	 * @return the location of the print result
	 */
	public String getFile() {
		return file;
	}

	/**
	 * Getter method for frequency
	 * 
	 * @return the print result's frequency
	 */
	public int getCount() {
		return count;
	}

	/**
	 * Getter method for initial index
	 * 
	 * @return the print result's initial index
	 */

	public int getPosition() {
		return position;
	}

	/**
	 * Setter method for initial index
	 * 
	 * @param index
	 *            - the initial index to be set as the print result's position
	 */
	public void setPosition(int index) {
		position = index;
	}

	/**
	 * Setter method for word frequency
	 * 
	 * @param counter
	 *            - the frequency of word occurrence to be set as the print
	 *            result's count
	 */
	public void setCount(int counter) {
		count = counter;
	}

	/**
	 * Setter method for file name of location
	 * 
	 * @param fileName
	 *            - the file name to be set as the print result's file location
	 */
	public void setFile(String fileName) {
		file = fileName;
	}

	/**
	 * Turns the print result into a string
	 * 
	 * @return a string representing the data contained within the print result
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
