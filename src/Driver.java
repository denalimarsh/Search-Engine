import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;


public class Driver {

	public static String quote(String text) {
        return String.format("\"%s\"", text);
    }
	
	public static void main (String[] args){
		
		int deepness = 0;
		
		//Reads -dir and -index arguments and responds appropriately
		
		File inFile = null;
		File outFile = null;
		
		boolean divFlag = false;
		boolean indexFlag = false;
		
		for (int i = 0; i < args.length; i++) {
			if(divFlag == true){
				inFile = new File(args[i]); 
				divFlag = false;
			}
			if(indexFlag == true && (!args[i].equals("-dir"))){
				outFile = new File(args[i]);
				indexFlag = false;
			}else if(indexFlag == true && args[i].equals("-dir")){
				outFile = new File("index.json");
				indexFlag = false;
			}
			if(args[i].equals("-dir")){
				divFlag = true;
			}
			if(args[i].equals("-index")){
				indexFlag = true;
			}	
        }
		if(indexFlag == true){
			outFile = new File("index.json");
			indexFlag = false;
		}
		
		//construct the main data structure
		TreeMap<String, TreeMap<String, TreeSet<Integer>>> godzilla = new TreeMap<>();
		
		if(inFile!=null){
			traverse(inFile, deepness, godzilla, outFile);
		}
		
	}
	
	
	public static void traverse(File originalFile, int depth, 
			 TreeMap<String, TreeMap<String, TreeSet<Integer>>> godzilla, File outFile){
		
		TreeSet<Integer> positions = null;
		File[] theFiles = originalFile.listFiles();
		 	 
		boolean b = theFiles instanceof File[];
		if(b){
		
		for (int i = 0; i < theFiles.length; i++) {
		       String thisFile = theFiles[i].getName();
		       if (theFiles[i].isFile()) {
		           if (thisFile.endsWith(".txt")||thisFile.endsWith(".TXT")) {
		               ArrayList<String> list = textToList(theFiles[i]);
		               String[] finishedFileText = list.toArray(new String[0]);
		               for(String n: finishedFileText){		     				
		        			positions = getPositions(finishedFileText, n);	
		        			godzilla = fullStructure(n, theFiles[i].toString(), positions, godzilla);		
		               }   		
		           }
		       }
		       else if (theFiles[i].isDirectory()) {
		           traverse(theFiles[i], depth+1, godzilla, outFile);
		        }
		    }
		 if(depth == 0){
			 printDataStructureBuffered(godzilla, outFile);
		 	}
		 }	 
	}
	
	public static void printDataStructureBuffered(TreeMap<String, TreeMap<String, TreeSet<Integer>>> godzilla, File outFile){	
		
		if(outFile == null){
			return;
		}
		
		String finalFile = ".\\";
		finalFile += outFile.toString();
		
		try {
			File file = new File(finalFile.toString());
			if (!file.exists()) {
				file.createNewFile();
			}	
			try {
				Writer bufferedWriter = new BufferedWriter(new OutputStreamWriter(
					    new FileOutputStream(outFile), "UTF-8"));
	 			bufferedWriter.write("{\n");
	 			
	 			//PRINTS THE WORDS
	 			int holderOne = 0;
	 			for(String key: godzilla.navigableKeySet()){
	 				if(key.compareTo("") != 0){
	 					bufferedWriter.write("\t" + quote(key) + ": {\n");
	 					
		 				//PRINTS THE FILENAMES
		 				TreeMap<String, TreeSet<Integer>> mappy = godzilla.get(key);	
		 				int holderTwo = 0;
		 				for(String fileName: mappy.navigableKeySet()){
		 					bufferedWriter.write("\t\t" + quote(fileName) + ": [\n");			
		 					
		 					//PRINTS THE POSITIONS
		 					TreeSet<Integer> setty = mappy.get(fileName);	
		 					int holderThree = 0;
		 					for(int a: setty){
		 						if(holderThree == setty.size() - 1){
		 							bufferedWriter.write("\t\t\t" + a + "\n");	
		 						}else{
		 							bufferedWriter.write("\t\t\t" + a + ",\n");
		 						}						
		 						holderThree++;
		 					}			
		 					if(holderTwo == mappy.navigableKeySet().size() - 1){
		 						bufferedWriter.write("\t\t]\n");
	 						}else{
	 							bufferedWriter.write("\t\t],\n");
	 						}	
		 					holderTwo++;
		 				}
		 				if(holderOne == godzilla.navigableKeySet().size() - 1){
		 					bufferedWriter.write("\t}\n");
							}else{
								bufferedWriter.write("\t},\n");
							}	
		 				holderOne++;
		 				}
	 				}	
	 			bufferedWriter.write("}");	
	 			bufferedWriter.flush();	
	 			bufferedWriter.close();
			}   
			catch (IOException e) {
				e.printStackTrace();
			}		
		} catch (IOException e) {
			e.printStackTrace();			
		}
	}

	//Takes in word, file, and positions to return FULL STRUCTURE
	public static TreeMap<String, TreeMap<String, TreeSet<Integer>>> fullStructure(String wordUpper, String file, 
				  	TreeSet<Integer> inPosition, TreeMap<String, TreeMap<String, TreeSet<Integer>>> godzilla)
	{	
		String word = wordUpper.toLowerCase();
		
		if(godzilla.get(word) == null){
			godzilla.put(word, new TreeMap<String, TreeSet<Integer>>());
		    godzilla.get(word).put(file, new TreeSet<Integer>());
		    for(int n: inPosition){
		    	godzilla.get(word).get(file).add(n);
		    } 			
		}else{
			godzilla.get(word).put(file, new TreeSet<Integer>());
			for(int n: inPosition){
		    	godzilla.get(word).get(file).add(n);
		    } 	
		}
		return godzilla;
	}
	
	//Takes in text in file to return a list of words
	public static ArrayList<String> textToList(File file){
		
		Charset charset = java.nio.charset.StandardCharsets.UTF_8;
		ArrayList<String> fullList = new ArrayList<>();
		Path path1 = file.toPath();
		
		try (BufferedReader br = Files.newBufferedReader(path1, charset)) {
		    String line = br.readLine();
		    while ((line) != null) {
		    	String[] words = line.split(" ");
		    	String x = null;
		    	for(int i = 0; i < words.length; i++){
		    		String holder = words[i];
		    		if(!isAlphaNumeric(holder)){
		    			x = holder.replaceAll("\\p{Punct}+", "");
		    			words[i] = x.trim();
		    		}
		    	} 
                for(int xx = 0; xx < words.length; xx++){
                	if(words[xx].compareTo("") != 0){
                		fullList.add(words[xx]);           	
                	}
                }
		    	line = br.readLine();
		    }
		br.close();
		}catch(IOException ex){
			System.out.println(ex.toString());
			System.out.println("Could not find file " + file.toString());
		}
		return fullList;
	}	
	
	//returns true if the string only contains letters and digits
	public static boolean isAlphaNumeric(String s){
		String pattern= "^[a-zA-Z0-9]*$";
        	if(s.matches(pattern)){
        		return true;
        	}
        	return false;  
	}
	
	//takes in text array and word to return positions of word in the text
	public static TreeSet<Integer> getPositions(String[] text, String word){
		int position;
		TreeSet<Integer> n = new TreeSet<>();
		for(int i = 0; i < text.length; i++){
			position = i + 1;
			if(word.compareToIgnoreCase(text[i]) == 0){
				n.add(position);
			}
		}
		return n;			
	}
}
