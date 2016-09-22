import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.TreeMap;
import java.util.TreeSet;

// TODO Run the code formatter in Eclipse (can configure to run every time you save)
// TODO Add Javadoc comments to all your classes and methods

public class Driver {

	public static String quote(String text) {
		return String.format("\"%s\"", text);
	}

	public static void main(String[] args) {

		int deepness = 0;

		// Reads -dir and -index arguments and responds appropriately

		File inFile = null;
		File outFile = null;

		boolean divFlag = false;
		boolean indexFlag = false;

		// TODO Use ArgumentParser
		for (int i = 0; i < args.length; i++) {
			if (divFlag == true) {
				inFile = new File(args[i]);
				divFlag = false;
			}
			if (indexFlag == true && (!args[i].equals("-dir"))) {
				outFile = new File(args[i]);
				indexFlag = false;
			} else if (indexFlag == true && args[i].equals("-dir")) {
				outFile = new File("index.json");
				indexFlag = false;
			}
			if (args[i].equals("-dir")) {
				divFlag = true;
			}
			if (args[i].equals("-index")) {
				indexFlag = true;
			}
		}
		if (indexFlag == true) {
			outFile = new File("index.json");
			indexFlag = false;
		}

		// instantiate the main data structure
		TreeMap<String, TreeMap<String, TreeSet<Integer>>> godzilla = new TreeMap<>();

		if (inFile != null) {
			traverse(inFile, deepness, godzilla, outFile);
		}
	}

	// Traverse:
	// iterates recursively through input directory
	// if it finds a file, call textToList() on the file
	// when depth is 0 (We have reached the root), call
	// printDataStructureBuffered()

	// in:
	// (File) input file from command line
	// (Integer) depth declared in main (for printing)
	// (TreeMap<....>) complete data structure
	// (File) out file from command line

	// out: none

	public static void traverse(File originalFile, int depth,
			TreeMap<String, TreeMap<String, TreeSet<Integer>>> godzilla, File outFile) {

		// TODO Use Path and DirectoryStream
		// TODO https://github.com/usf-cs212-2016/lectures/blob/master/Files%20and%20Exceptions/src/DirectoryStreamDemo.java#L45
		File[] theFiles = originalFile.listFiles();

		boolean b = theFiles instanceof File[];
		if (b) {
			for (int i = 0; i < theFiles.length; i++) {
				String thisFile = theFiles[i].getName();
				if (theFiles[i].isFile()) {
					if (thisFile.endsWith(".txt") || thisFile.endsWith(".TXT")) {
						textToList(theFiles[i], godzilla);
					}
				} else if (theFiles[i].isDirectory()) {
					traverse(theFiles[i], depth + 1, godzilla, outFile);
				}
			}
			if (depth == 0) {
				printDataStructureBuffered(godzilla, outFile);
			}
		}
	}

	// PrintDataStructureBuffered:
	// prints out the complete data structure to
	// the outputfile in a pretty JSON format

	// in:
	// TreeMap<...> complete data structure
	// the out file from the command line

	// out: none

	/**
	 * TODO Type "/**" and enter in Eclipse to get this kind of template
	 * @param godzilla
	 * @param outFile
	 */
	public static void printDataStructureBuffered(TreeMap<String, TreeMap<String, TreeSet<Integer>>> godzilla,
			File outFile) {

		if (outFile == null) {
			return;
		}

		try {
			File file = new File(outFile.toString());
			if (!file.exists()) {
				file.createNewFile();
			}
			
			// TODO Use Java NIO, try-with-resources, Path, Files
			// TODO https://github.com/usf-cs212-2016/lectures/blob/master/Files%20and%20Exceptions/src/EliteFileConverter.java#L127
			try {
				Writer bufferedWriter = new BufferedWriter(
						new OutputStreamWriter(new FileOutputStream(outFile), "UTF-8"));
				bufferedWriter.write("{\n");

				// PRINTS THE WORDS
				int holderOne = 0;
				for (String key : godzilla.navigableKeySet()) {
					if (key.compareTo("") != 0) {
						bufferedWriter.write("\t" + quote(key) + ": {\n");

						// PRINTS THE FILENAMES
						TreeMap<String, TreeSet<Integer>> mappy = godzilla.get(key);
						int holderTwo = 0;
						for (String fileName : mappy.navigableKeySet()) {
							bufferedWriter.write("\t\t" + quote(fileName) + ": [\n");

							// PRINTS THE POSITIONS
							TreeSet<Integer> setty = mappy.get(fileName);
							int holderThree = 0;
							for (int a : setty) {
								if (holderThree == setty.size() - 1) {
									bufferedWriter.write("\t\t\t" + a + "\n");
								} else {
									bufferedWriter.write("\t\t\t" + a + ",\n");
								}
								holderThree++;
							}
							if (holderTwo == mappy.navigableKeySet().size() - 1) {
								bufferedWriter.write("\t\t]\n");
							} else {
								bufferedWriter.write("\t\t],\n");
							}
							holderTwo++;
						}
						if (holderOne == godzilla.navigableKeySet().size() - 1) {
							bufferedWriter.write("\t}\n");
						} else {
							bufferedWriter.write("\t},\n");
						}
						holderOne++;
					}
				}
				bufferedWriter.write("}");
				bufferedWriter.flush();
				bufferedWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// fullStructure:
	// combines the various pieces of the structure into a complete nested data
	// structure

	// in:
	// (String) wordUpper is the word, case is maintained from the text
	// (String) file is the name of the file where the word was found
	// (Integer) position is this individual position of the word
	// (TreeMap<....>()) godzilla is the full data structure

	// out:
	// returns the full TreeMap<String, TreeMap<String, TreeSet<Integer>>>
	// updated structure

	public static TreeMap<String, TreeMap<String, TreeSet<Integer>>> fullStructure(String wordUpper, String file,
			int position, TreeMap<String, TreeMap<String, TreeSet<Integer>>> godzilla) {

		String word = wordUpper.toLowerCase();

		if (godzilla.get(word) == null) {
			godzilla.put(word, new TreeMap<String, TreeSet<Integer>>());
			godzilla.get(word).put(file, new TreeSet<Integer>());
			godzilla.get(word).get(file).add(position);
		} else {
			if (godzilla.get(word).get(file) == null) {
				godzilla.get(word).put(file, new TreeSet<Integer>());
				godzilla.get(word).get(file).add(position);
			} else {
				godzilla.get(word).get(file).add(position);
			}
		}
		return godzilla;
	}

	// textToList:
	// reads each line of the each, seperating it into a string array of words
	// trims and cleans each word in the array, removing all non alphanumerics
	// calls fullStructure on each respective word/position pair

	// in:
	// (File) input file from the command line
	// (TreeMap<...>()) godzilla is the full data structure

	public static void textToList(File file, TreeMap<String, TreeMap<String, TreeSet<Integer>>> godzilla) {

		int positionHolder = 0;

		Charset charset = java.nio.charset.StandardCharsets.UTF_8;
		Path path1 = file.toPath();
		try (BufferedReader br = Files.newBufferedReader(path1, charset)) {
			String line = br.readLine();
			while ((line) != null) {
				String[] words = line.split(" ");
				String x = null;
				for (int i = 0; i < words.length; i++) {
					String holder = words[i];
					x = holder.replaceAll("\\p{Punct}+", "");
					String m = x.trim();
					if (m.compareTo("") != 0) {
						positionHolder++;
						godzilla = fullStructure(m, path1.normalize().toString(), positionHolder, godzilla);
					}
				}
				line = br.readLine();
			}
			br.close();
		} catch (IOException ex) {
			System.out.println(ex.toString());
			System.out.println("Could not find file " + file.toString());
		}
	}
}
