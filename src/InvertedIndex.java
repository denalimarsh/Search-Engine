import java.util.TreeMap;
import java.util.TreeSet;

public class InvertedIndex {

	// TODO Move your nested treemap as a member (godzilla)
	private TreeMap<String, TreeMap<String, TreeSet<Integer>>> godzilla;
	
	// TODO Move your add method
	
	public InvertedIndex() {
		godzilla = new TreeMap<>();
	}
	
	public void add(String word, String file, int position) {
		
	}
	
	public boolean containsWord(String word) {
		return false;
	}
	
	public int size() {
		return -1;
	}
	
	@Override
	public String toString() {
		return godzilla.toString();
	}
	
}
