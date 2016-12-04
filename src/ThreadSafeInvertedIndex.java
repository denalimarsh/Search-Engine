import java.util.List;
import java.nio.file.Path;

public class ThreadSafeInvertedIndex extends InvertedIndex {

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

	@Override
	public void print(Path path) {
		lock.lockReadWrite();
		try {
			super.print(path);

		} finally {
			lock.unlockReadWrite();
		}
	}

	@Override
	public List<SearchResult> partialSearch(String[] querywords) {
		lock.lockReadWrite();
		try {
			return super.partialSearch(querywords);

		} finally {
			lock.unlockReadWrite();
		}
	}

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
