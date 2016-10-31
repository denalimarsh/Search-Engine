import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class InvertedIndexBuilder {
	
	
	//TODO: Remove printing into different method.
	
	/**
	 * Traverses the input directory recursively, locating all folders and files inside.
	 * If the file is a text file, call the "parseFile" function
	 * 
	 * @param originalPath - the original input directory to be searched
	 * @param index - the inverted index to be added to
	 * @param outPath - the final destination for the inverted index to be written to
	 */
	
	//TODO: Remove use of File.io package
	public static void traverse(Path originalPath, InvertedIndex index, Path outPath) {

		 try (DirectoryStream<Path> listing = Files.newDirectoryStream(originalPath)) {
			 for (Path path : listing) {
	                
	                File fileName = path.toFile();
                	String extension = "";
                	int i =  (fileName.toString()).lastIndexOf('.');
                	if (i > 0) {
                	    extension = fileName.toString().substring(i+1);
                	}
	                if (Files.isDirectory(path)) {
	                    traverse(path, index, outPath);
	                }
	                
	                //TODO: What if extension is .tXt or .tXT
	                else if (extension.equals("txt")||extension.equals("TXT")){
	                		parseFile(path, index);
	                }
	         }
			 if(outPath != null){
				 InvertedIndex.print(index, outPath);
			 }
		 } catch (IOException e) {
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
		Path path1 = path;
		
			try (BufferedReader br = Files.newBufferedReader(path, Charset.forName("UTF-8"))) {
		        String line = br.readLine();
				while ((line) != null) {
					String[] words = line.split(" ");
					String x = null;
					for (int i = 0; i < words.length; i++) {
						String holder = words[i];
						x = holder.replaceAll("\\p{Punct}+", "");
						String m = x.trim();
						if (m.compareTo("") != 0) {
							positionHolder++;
							index.add(m, path1.normalize().toString(), positionHolder);
						}
					}
					line = br.readLine();
				}
				br.close();			
			} catch (IOException ex) {
				System.out.println(ex.toString());
				System.out.println("Could not find file " + path1.toString());
			}
	    }
	}
