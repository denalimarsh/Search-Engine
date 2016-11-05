import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.TreeMap;
import java.util.TreeSet;

public class Query implements Comparable<Query>{
	   
	@Override
	public int compareTo(Query o) {
			
		int frequencyHolder = Long.compare(o.getFrequency(), frequency);
		if(frequencyHolder == 0){
			int positionHolder = Long.compare(initalIndex, o.getInitalIndex());
			if(positionHolder == 0){
				int locationHolder = o.getLocation().compareTo(location);
				return locationHolder;
			}
			return positionHolder;
		}
		return frequencyHolder;
	
	}
		
		
//		System.out.println("\nWord: " + word);
//		System.out.println("Location: " + location);
//		System.out.println("InitalIndex: " + initalIndex);
//		System.out.println("Location: " + location + "\n");
//		System.out.println("OLocation: " + o.getLocation() + "\n");
	
	 int frequency;
	 String location;
	 int initalIndex;
	 String word;
	 InvertedIndex queryIndex;
	
	public Query(){
		word = null;
		queryIndex = new InvertedIndex();
		frequency = 0;
		location = null;
		initalIndex = -1;
	}
	
	public void addWordMap(String word, TreeMap<String, TreeSet<Integer>> map){
		queryIndex.addWordMapIndex(word, map);
	}
	
	
	public void add(String wordUpper, String path, int position) {
		queryIndex.add(wordUpper, path, position);
	}
	
	public void queryPrint(BufferedWriter bufferedWriter) throws IOException{
		if(bufferedWriter != null){
			queryIndex.printQueryIndex(bufferedWriter);
		}
	}
	
	public Query(String searchFor){
		queryIndex = new InvertedIndex();
		queryIndex.setStringQuery(searchFor);
		word = searchFor;
		frequency = 0;
		location = null;
		initalIndex = -1;
	}
	
	public Query(int count, String located, int firstOccurence){
		frequency = count;
		location = located; 
		initalIndex = firstOccurence;
	}
	
	public Query(Query qz){
		initalIndex = getInitalIndex();
		frequency = getFrequency();
		location = getLocation();
	}
	
	public String toString(){
		StringBuffer sb = new StringBuffer();
		
		sb.append(frequency);
		sb.append(location);
		sb.append(initalIndex);
		
		return new String(sb);
		
	}
	
	public int getFrequency(){
		return frequency;
	}
	
	public int getInitalIndex(){
		return initalIndex;
	}
	
	public String getLocation(){
		return location;
	}
	
	public String getWord(){
		return word;
	}
	
//	public TreeMap<String, TreeSet<Integer>> getMap(String word){
//		
//		
//	}
	
	
	public void setQuery(int count, String located, int firstOccurence){
		frequency = count;
		location = located; 
		initalIndex = firstOccurence;
	}
	
	
	
	public Query combineQuery(Query A, Query B){
		Query C = new Query();
		
		C.setQuery(A.frequency + B.frequency, A.location, Math.min(A.initalIndex,B.initalIndex));
		return C;
	}

	
//	public void babyPrint(){
//		System.out.println("Word: " + Query.word);
//		System.out.println("Path: " + Query.location);
//		System.out.println("First Occurence: " + Query.initalIndex);
//		System.out.println("Count: " + Query.frequency);
//	}
}