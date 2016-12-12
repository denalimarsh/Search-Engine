import java.util.List;
import java.nio.file.Path;

public class ThreadSafeInvertedIndex extends InvertedIndex {

	private ReadWriteLock lock;

	public ThreadSafeInvertedIndex() {
		super();
		this.lock = new ReadWriteLock();
	}

	@Override
	public void add(String word, String line, int position) {
		lock.lockReadWrite();
		try {
			super.add(word, line, position);

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
			lock.unlockReadOnly();
		}
	}

	@Override
	public boolean containsWord(String word) {
		lock.lockReadOnly();
		try {
			return super.containsWord(word);

		} finally {
			lock.unlockReadOnly();
		}
	}

	@Override
	public int size() {
		lock.lockReadOnly();
		try {
			return super.size();

		} finally {
			lock.unlockReadOnly();
		}
	}

	@Override
	public List<SearchResult> partialSearch(String[] words) {
		lock.lockReadOnly();
		try {
			return super.partialSearch(words);

		} finally {
			lock.unlockReadOnly();
		}
	}

	@Override
	public List<SearchResult> exactSearch(String[] words) {
		lock.lockReadOnly();
		try {
			return super.exactSearch(words);

		} finally {
			lock.unlockReadOnly();
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

	@Override
	public void addIndex(InvertedIndex partialIndex) {
		lock.lockReadWrite();
		try {
			super.addIndex(partialIndex);

		} finally {
			lock.unlockReadWrite();
		}
	}

}
