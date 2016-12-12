import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class MultithreadedInvertedIndexBuilder {

	/**
	 * 
	 * Recursively searches the input directory for other directories and files,
	 * which are parsed by different threads using multithreading
	 * 
	 * @param originalPath
	 *            the original directory to be searched
	 * @param index
	 *            the global inverted index
	 * @param workers
	 *            the work queue
	 */
	public static void traverse(Path originalPath, ThreadSafeInvertedIndex index, WorkQueue workers)
			throws IOException {
		try (DirectoryStream<Path> listing = Files.newDirectoryStream(originalPath)) {
			for (Path file : listing) {
				if (file.getFileName().toString().toLowerCase().endsWith(".txt")) {
					workers.execute(new BuildRunner(file, index));
				} else if (Files.isDirectory(file)) {
					traverse(file, index, workers);
				}
			}
		}
		workers.finish();
	}

	private static class BuildRunner implements Runnable {

		private ThreadSafeInvertedIndex global;
		private InvertedIndex local;
		private Path file;

		public BuildRunner(Path file, ThreadSafeInvertedIndex index) {
			this.file = file;
			this.global = index;
			local = new InvertedIndex();
		}

		@Override
		public void run() {
			InvertedIndexBuilder.parseFile(this.file, local);
			global.addIndex(local);
		}
	}
}