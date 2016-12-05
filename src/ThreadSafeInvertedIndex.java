import java.util.List;
import java.nio.file.Path;

public class ThreadSafeInvertedIndex extends InvertedIndex {

	private final ReadWriteLock lock;

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
		lock.lockReadOnly();
		try {
			super.print(path);

		} finally {
			lock.lockReadOnly();
		}
	}

	@Override
	public List<SearchResult> partialSearch(String[] querywords) {
		lock.lockReadOnly();
		try {
			return super.partialSearch(querywords);

		} finally {
			lock.lockReadOnly();
		}
	}

	@Override
	public List<SearchResult> exactSearch(String[] querywords) {
		lock.lockReadOnly();
		try {
			return super.exactSearch(querywords);

		} finally {
			lock.lockReadOnly();
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
		lock.lockReadOnly();
		try {
			return super.containsWord(word);

		} finally {
			lock.lockReadOnly();
		}
	}

	@Override
	public int size() {
		lock.lockReadOnly();
		try {
			return super.size();

		} finally {
			lock.lockReadOnly();
		}
	}

	@Override
	public String toString() {
		lock.lockReadOnly();
		try {
			return super.toString();

		} finally {
			lock.unlockReadOnly();
		}
	}

}
