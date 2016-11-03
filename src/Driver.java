import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Driver {

	/**
	 * The main driver method which reads in the input arguments,
	 * instantiates the main InvertedIndex data structure, and if
	 * appropriate, calls traverse
	 * 
	 * @param args - the command line arguments which designate 
	 * 				 where the input and output paths are
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {		// TODO Cannot throw exceptions from main

		Path inPath = null;
		Path outPath = null;
		
		ArgumentParser parser = new ArgumentParser(args);
		
		// TODO Should not need this loop at all?
		for (int i = 0; i < args.length; i++) {
			if(parser.hasFlag(args[i])){
				if(args[i].equals("-dir")){
					if(parser.hasValue(args[i])){
						inPath = Paths.get(parser.getValue(args[i]));
					}
				}else if(args[i].equals("-index")){
						String indexHolder = parser.getValue("-index", "index.json");
						outPath = Paths.get(indexHolder);
				}
			}
		}
		
		// TODO Refactor to just "index" or something shorter
		InvertedIndex index = new InvertedIndex();
		
		/* TODO
		if (parser.hasFlag("-dir")) {
			do stuff
		}
		
		if (parser.hasFlag("-index")) {
			Path output = parser.getValue("-index", "index.json");
			do stuff
		}
		*/
		
		if (inPath != null) {
			InvertedIndexBuilder.traverse(inPath, index, outPath);
			
			if(outPath != null){
				index.print(outPath);
			}
			
		}
	}
}