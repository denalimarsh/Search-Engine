import java.nio.file.Path;

public interface QueryHelperInterface {

	/**
	 * Reads a text file, cleans the words, adds them to a list
	 * 
	 * @param path
	 *            - the path to be read and parsed into queries
	 * @return uniqueList - a sorted, unique list of Strings
	 */
	public void parseQuery(Path file, boolean searchFlag);

	/**
	 * Writes out the query map in JSON format to the output path.
	 * 
	 * @param path
	 *            path of the output file.
	 */
	public void printHelper(Path path);

}
