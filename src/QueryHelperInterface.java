import java.nio.file.Path;

public interface QueryHelperInterface {

	public void parseQuery(Path file, boolean searchFlag);
	
	/**
	 * Writes out the query map in JSON format to the output path.
	 * 
	 * @param path
	 *            path of the output file.
	 */	
	public void printHelper(Path path);
	
}
