import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class MultithreadedInvertedIndexBuilder{

	public static void traverse(Path directory, ThreadSafeInvertedIndex index, WorkQueue workers) throws IOException {
		try (DirectoryStream<Path> listing = Files.newDirectoryStream(directory)) {
			for (Path file : listing) {
				if (Files.isRegularFile(file) && file.getFileName().toString().toLowerCase().endsWith(".txt")) {
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
