import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.TreeMap;

public class queryList extends ArrayList<Query> {
	
private ArrayList<Query> qList;
	
	public queryList() {
		qList = new ArrayList<>();
	}
	
	public boolean addQuery(Query qq){
		
		if(qq.getClass().toString().equals("class Query")){
			System.out.println("ITS A QUERY");
			qList.add(qq);
			return true;
		}
		return false;
	}
	
	public static ArrayList<String> parseQuery(Path path) {
		
		ArrayList<String> list = new ArrayList<>();
		TreeMap<String, Query> map = new TreeMap<>();

		Charset charset = java.nio.charset.StandardCharsets.UTF_8;
	
		try (BufferedReader br = Files.newBufferedReader(path, charset)) {
			String line = br.readLine();
			while ((line) != null) {
				String[] words = line.split("\n");
				String x = null;
				for (int i = 0; i < words.length; i++) {
					String holder = words[i];
					x = holder.replaceAll("\\p{Punct}+", "");
					String m = x.trim();
					String y = m.toLowerCase();
					
					//split by space and input as different queries
					
					if (y.compareTo("") != 0) {	
						String realQuery = new String();
						
						//gets rid of multiple spaces
						if( y.matches(".*\\s++.*")){
							y = y.replaceAll("\\s+", " ");
							String [] secondWords = y.split("\\s+");
							Arrays.sort(secondWords);	
							
							//System.out.println(secondWords.toString());
							for(int p = 0; p < secondWords.length; p++){
								if(p == (secondWords.length - 1)){
									realQuery += secondWords[p];	
								}else{
									realQuery += secondWords[p] + " ";
								}
							}
							list.add(realQuery);
						}else{
							list.add(y);
						}
					}
				}
				line = br.readLine();
			}
			br.close();
		} catch (IOException ex) {
			System.out.println(ex.toString());
			System.out.println("Could not find file " + path.toString());
		}
		
		ArrayList<String> uniqueList = removeDuplicates(list);
		Collections.sort(uniqueList);
		System.out.println("Sorted list from parser: " + uniqueList.toString());
//		for(String xer: list){
//			map.put(xer, null);
//		}
		return uniqueList;
	}
	
	static ArrayList<String> removeDuplicates(ArrayList<String> list) {

		// Store unique items in result.
		ArrayList<String> result = new ArrayList<>();

		// Record encountered Strings in HashSet.
		HashSet<String> set = new HashSet<>();

		// Loop over argument list.
		for (String item : list) {

		    // If String is not in set, add it to the list and the set.
		    if (!set.contains(item)) {
			result.add(item);
			set.add(item);
		    }
		}
		return result;
	    }
}