import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class Driver {

	/**
	 * The main driver method which reads in the input arguments, instantiates
	 * the main InvertedIndex data structure, and if appropriate, calls traverse
	 * 
	 * @param args
	 *            - the command line arguments which designate where the input
	 *            and output paths are
	 * @throws IOException
	 */
	public static void main(String[] args) {

		ArgumentParser parser = new ArgumentParser(args);

		InvertedIndex index = new InvertedIndex();
		
		//InvertedIndex queryIndex = new InvertedIndex();

		if (parser.hasFlag("-dir")) {
			if (parser.hasValue("-dir")) {
				Path input = Paths.get(parser.getValue("-dir"));
				System.out.println(input.toString());
				InvertedIndexBuilder.traverse(input, index);
			}
		}

		if (parser.hasFlag("-index")) {
			Path output = Paths.get(parser.getValue("-index", "index.json"));
			index.print(output);
		}
		
		if(parser.hasFlag("-query")){
			if (parser.hasValue("-query")){
				Path query = Paths.get(parser.getValue("-query"));
				
				ArrayList<String> parsedList = new ArrayList<>();
				parsedList = queryList.parseQuery(query);
						
				ArrayList<Query> queryList = index.partialSearch(parsedList);
				
				if (parser.hasFlag("-results")) {
					Path results = Paths.get(parser.getValue("-results", "results.json"));
					
					try (BufferedWriter bufferedWriter = Files.newBufferedWriter(results, Charset.forName("UTF-8"));){
						bufferedWriter.write("{\n");
						int i = 0;
						for(Query qqq: queryList){
							try {
							i++;	
								qqq.queryPrint(bufferedWriter);
								if(queryList.size() != i){
									bufferedWriter.write("\n\t],\n");
								}else{
									bufferedWriter.write("\n\t]\n");
								}
								
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						bufferedWriter.write("}");
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
				}
			}
		}
		
		if(parser.hasFlag("-exact")){
			if (parser.hasValue("-exact")){
				Path exact = Paths.get(parser.getValue("-exact"));
				
				ArrayList<String> parsedList = new ArrayList<>();
				parsedList = queryList.parseQuery(exact);
						
				ArrayList<Query> queryList = index.exactSearch(parsedList);
				
				if (parser.hasFlag("-results")) {
					Path results = Paths.get(parser.getValue("-results", "results.json"));
					
					try (BufferedWriter bufferedWriter = Files.newBufferedWriter(results, Charset.forName("UTF-8"));){
						bufferedWriter.write("{\n");
						int i = 0;
						for(Query qqq: queryList){
							try {
							i++;	
								qqq.queryPrint(bufferedWriter);
								if(queryList.size() != i){
									bufferedWriter.write("\n\t],\n");
								}else{
									bufferedWriter.write("\n\t]\n");
								}
								
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						bufferedWriter.write("}");
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
				}
			}
		}

	}
}