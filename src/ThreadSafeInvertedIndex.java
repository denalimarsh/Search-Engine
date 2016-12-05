import java.util.List;
import java.nio.file.Path;

//TODO If method doesn't change index data, just use read lock
public class ThreadSafeInvertedIndex extends InvertedIndex {

	//TODO make final
	private ReadWriteLock lock;

	public ThreadSafeInvertedIndex() {
		super();
		this.lock = new ReadWriteLock();
	}

	@Override
	public void add(String word, String path, int position) {
		lock.lockReadWrite();
		try {
			super.add(word, path, position);

		} finally {
			lock.unlockReadWrite();
		}
	}

	//TODO print only needs lock read unless you are changing the data in the index
	@Override
	public void print(Path path) {
		lock.lockReadWrite();
		try {
			super.print(path);

		} finally {
			lock.unlockReadWrite();
		}
	}

	//TODO Lock just read
	@Override
	public List<SearchResult> partialSearch(String[] querywords) {
		lock.lockReadWrite();
		try {
			return super.partialSearch(querywords);

		} finally {
			lock.unlockReadWrite();
		}
	}

	//TODO Same here
	@Override
	public List<SearchResult> exactSearch(String[] querywords) {
		lock.lockReadWrite();
		try {
			return super.exactSearch(querywords);

		} finally {
			lock.unlockReadWrite();
		}
	}

	@Override
	public void addIndex(InvertedIndex partialindex) {
		lock.lockReadWrite();
		try {
			super.addIndex(partialindex);

		} finally {
			lock.unlockReadWrite();
		}
	}
	

	@Override
	public boolean containsWord(String word) {
		lock.lockReadWrite();
		try {
			return super.containsWord(word);

		} finally {
			lock.unlockReadWrite();
		}
	}

	@Override
	public int size() {
		lock.lockReadWrite();
		try {
			return super.size();

		} finally {
			lock.unlockReadWrite();
		}
	}

	@Override
	public String toString() {
		lock.lockReadWrite();
		try {
			return super.toString();

		} finally {
			lock.unlockReadWrite();
		}
	}

}
