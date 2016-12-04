import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class MultithreadedInvertedIndexBuilder extends InvertedIndexBuilder {

	private final ThreadSafeInvertedIndex multipleIndex;
	private final WorkQueue workers;

	public MultithreadedInvertedIndexBuilder(ThreadSafeInvertedIndex index, WorkQueue workers) {
		super();
		multipleIndex = index;
		this.workers = workers;
	}

	public void traverse(Path originalPath, InvertedIndex index) {
		try (DirectoryStream<Path> listing = Files.newDirectoryStream(originalPath)) {
			for (Path path : listing) {
				if (Files.isDirectory(path)) {
					traverse(path, index);
				} else if (path.toString().toLowerCase().endsWith(".txt")) {
					workers.execute(new BuilderRun(path));
				}
			}
		} catch (IOException e) {
			System.out.println("Unable to access " + originalPath.toString() + " to parse.");
		}
	}

	private class BuilderRun implements Runnable {

		private InvertedIndex local;
		private Path file;

		BuilderRun(Path file) {
			this.file = file;
			local = new InvertedIndex();
		}

		@Override
		public void run() {
			InvertedIndexBuilder.parseFile(this.file, local);
			multipleIndex.addIndex(local);
		}
	}
}
