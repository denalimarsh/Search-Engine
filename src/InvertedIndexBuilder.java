import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class InvertedIndexBuilder {

	/**
	 * Traverses the input directory recursively, locating all folders and files
	 * inside. If the file is a text file, call the "parseFile" function
	 * 
	 * @param originalPath
	 *            - the original input directory to be searched
	 * @param index
	 *            - the inverted index to be added to
	 * @param outPath
	 *            - the final destination for the inverted index to be written
	 *            to
	 */

	public static void traverse(Path originalPath, InvertedIndex index) {
		try (DirectoryStream<Path> listing = Files.newDirectoryStream(originalPath)) {
			for (Path path : listing) {
				if (Files.isDirectory(path)) {
					traverse(path, index);
				} else if (path.toString().toLowerCase().endsWith(".txt")) {
					parseFile(path, index);
				}
			}
		} catch (IOException e) {
			System.out.println("Unable to access " + originalPath.toString() + " to parse.");
		}
	}

	/**
	 * 
	 * Reads a text file in line by line, 'cleaning' the words as it goes by
	 * removing all non-alphanumeric characters. For each cleaned word, add it
	 * to the inverted index.
	 * 
	 * @param path
	 *            - the text file to be read in
	 * @param index
	 *            - the inverted index to be added to
	 * @throws IOException
	 *             - thrown if the bufferedReader is unable to write to the
	 *             designated path
	 */

	public static void parseFile(Path path, InvertedIndex index) {

		String pathName = path.normalize().toString();
		int positionHolder = 0;

		try (BufferedReader br = Files.newBufferedReader(path, Charset.forName("UTF-8"))) {
			String line = br.readLine();
			while ((line) != null) {
				String[] words = line.split("\\s+");
				String cleanedWord = null;
				for (int i = 0; i < words.length; i++) {
					String holder = words[i];
					cleanedWord = holder.replaceAll("\\p{Punct}+", "");
					String trimmedWord = cleanedWord.trim();
					if (!cleanedWord.isEmpty()) {
						positionHolder++;
						index.add(trimmedWord, pathName, positionHolder);
					}
				}
				line = br.readLine();
			}
		} catch (IOException ex) {
			System.out.println("Unable to parse " + pathName + " into an inverted index.");
		}
	}
}
