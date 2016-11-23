public class SearchResult implements Comparable<SearchResult> {

	private final String file;
	private int frequency;
	private int position;

	/**
	 * Constructor for new SearchResult
	 * 
	 * @param file
	 *            - the file location, stored as a String
	 * @param frequency
	 *            - the number of times the word occurs within a specified file
	 * @param position
	 *            - the initial index, or first place the word is found at
	 */
	public SearchResult(String file, int frequency, int position) {
		this.frequency = frequency;
		this.file = file;
		this.position = position;
	}

	/**
	 * Implemented Comparable, sorts the Queries according to their frequency,
	 * then their initial index, then their file name
	 */
	@Override
	public int compareTo(SearchResult o) {

		int frequencyHolder = Integer.compare(o.getFrequency(), frequency);
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

	/**
	 * Updates a SearchResult object's position and frequency from the frequency
	 * and position of another SearchResult object, resulting in one combined
	 * SearchResult object
	 * 
	 * @param SearchResult
	 *            - the new SearchResult whose values will be used to update the
	 *            existing SearchResult object
	 */
	public void updateSearchResult(int frequency, int initalIndex) {
		this.frequency += frequency;
		if (initalIndex < this.position) {
			this.position = initalIndex;
		}
	}

	/**
	 * Getter method for file name
	 * 
	 * @return the location of the SearchResult
	 */
	public String getFile() {
		return file;
	}

	/**
	 * Getter method for frequency
	 * 
	 * @return the SearchResult's frequency
	 */
	public int getFrequency() {
		return frequency;
	}

	/**
	 * Getter method for initial index
	 * 
	 * @return the SearchResult's initial index
	 */

	public int getPosition() {
		return position;
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
