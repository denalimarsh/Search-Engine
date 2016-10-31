import java.nio.file.Path;
import java.nio.file.Paths;

public class Driver {

	//TODO: Remove this method
	/**
	 * Quotes any string given to the function
	 * 
	 * @param text - the string to be quoted
	 * @return - the string within quotes
	 */
	public static String quote(String text) {
		return String.format("\"%s\"", text);
	}

	/**
	 * The main driver method which reads in the input arguments,
	 * instantiates the main InvertedIndex data structure, and if
	 * appropriate, calls traverse
	 * 
	 * @param args - the command line arguments which designate 
	 * 				 where the input and output paths are
	 */
	public static void main(String[] args) {		

		Path inPath = null;
		Path outPath = null;
		
		ArgumentParser parser = new ArgumentParser(args);
		
		for (int i = 0; i < args.length; i++) {
			if(parser.hasFlag(args[i])){
				if(args[i].equals("-dir")){
					if(parser.hasValue(args[i])){
						inPath = Paths.get(parser.getValue(args[i]));
					}
				}else if(args[i].equals("-index")){
						String indexy = parser.getValue("-index", "index.json");
						outPath = Paths.get(indexy);
				}
			}
		}
		InvertedIndex bigIndex = new InvertedIndex();
		if (inPath != null) {
			System.out.println(inPath.toString());
			InvertedIndexBuilder.traverse(inPath, bigIndex, outPath);	
		}
	}
}
