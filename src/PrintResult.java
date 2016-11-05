
public class PrintResult implements Comparable<PrintResult> {

	
	@Override
	public int compareTo(PrintResult o) {
			
		int frequencyHolder = Integer.compare(o.getCount(), count);
		if(frequencyHolder == 0){
			int positionHolder = Integer.compare(position, o.getPosition());
			if(positionHolder == 0){
				int locationHolder = file.compareTo(o.getFile());
				return locationHolder;
			}
			return positionHolder;
		}
		return frequencyHolder;
	
	}
	String file;
	int count;
	int position;
	
	public PrintResult(){
		file = null;
		count = 0;
		position = 0;
	}
	
	public String getFile(){
		return file;
	}
	
	public boolean containsFile(String word){
		if(file.equals(word)){
			return true;
		}else{
			return false;
		}
	}
	
	
	public int getCount(){
		return count;
	}
	public int getPosition(){
		return position;
	}
	
	public void setPosition(int index){
		position = index;
	}
	
	public void setCount(int counter){
		count = counter;
	}
	
	public void setFile(String fileName){
		file = fileName;
	}
	
	public String toString(){
		return "\n\t\t{\n\t\t\t" + quote("where") + ": " + quote(file) +
				",\n\t\t\t" + quote("count") + ": " + count +
				",\n\t\t\t" + quote("index") + ": " + position + "\n\t\t}";
	}
	
	/**
	 * Adds quotes to any string, useful in pretty printing JSON
	 * 
	 * @param text
	 *            - the string to be quoted
	 * @return the string with quotations on it
	 */
	public String quote(String text) {
		return String.format("\"%s\"", text);
	}
}
