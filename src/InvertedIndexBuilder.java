import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class InvertedIndexBuilder {

	/**
	 * Traverses the input directory recursively, locating all folders and files inside.
	 * If the file is a text file, call the "parseFile" function
	 * 
	 * @param originalPath - the original input directory to be searched
	 * @param index - the inverted index to be added to
	 * @param outPath - the final destination for the inverted index to be written to
	 */
	
	public static void traverse(Path originalPath, InvertedIndex index, Path outPath) {

		 try (DirectoryStream<Path> listing = Files.newDirectoryStream(originalPath)) {
			 for (Path path : listing) {
				 // TODO if (path.toString().toLowerCase().endsWith(".txt")), then parseFile
				 
				 
                	String extension = "";
                	int i =  (path.toString()).lastIndexOf('.');
                	if (i > 0) {
                	    extension = path.toString().substring(i+1);
                	}
	                if (Files.isDirectory(path)) { // TODO Still need this check
	                    traverse(path, index, outPath);
	                }
	                else if (extension.equalsIgnoreCase("txt")){
	                		parseFile(path, index);
	                }
	         }
		 } catch (IOException e) {
			 // TODO Fix this
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * Reads a text file in line by line, 'cleaning' the words as it goes by removing
	 * all non-alphanumeric characters. For each cleaned word, add it to the inverted 
	 * index.
	 * 
	 * @param path - the text file to be read in
	 * @param index - the inverted index to be added to
	 * @throws IOException - thrown if the bufferedReader is unable to write to the 
	 * 						 designated path
	 */
	
	public static void parseFile(Path path, InvertedIndex index) throws IOException {

		int positionHolder = 0;
		
			try (BufferedReader br = Files.newBufferedReader(path, Charset.forName("UTF-8"))) {
				// TODO Save the normalized toString path here
				
		        String line = br.readLine();
				while ((line) != null) {
					String[] words = line.split(" "); // TODO split("\\s+")
					String x = null;
					// TODO 1 letter variable names are really only used for counters in for statements
					
					for (int i = 0; i < words.length; i++) {
						String holder = words[i];
						x = holder.replaceAll("\\p{Punct}+", ""); // TODO String cleaned instead of x
						String m = x.trim();
						// TODO if (!cleaned.isEmpty()) then add
						if (m.compareTo("") != 0) {
							positionHolder++;
							// TODO Constantly normalize and toString the path... which takes time but never changes
							index.add(m, path.normalize().toString(), positionHolder);
						}
					}
					line = br.readLine();
				}
				br.close(); // TODO Don't need anymore?
			} catch (IOException ex) {
				
				// TODO ex.ToString() will still not be understandable to users
				System.out.println(ex.toString());
				
				// TODO Something like this is closer... "Unable to parse " + path + " into index."
				System.out.println("Could not find file " + path.toString());
			}
	    }
	}
