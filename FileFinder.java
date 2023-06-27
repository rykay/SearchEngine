package edu.usfca.cs272;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

/**
 * class responsible for finding text files
 * 
 * @author ryank
 *
 */
public class FileFinder {

	/**
	 * This methods finds all the text files in a directory and its subdirectories
	 * (case insensitive).
	 * 
	 * @param path the path of the directory
	 * @return A list of all the .txt and .text files found
	 * @throws IOException throws an IO exception
	 */
	public static List<Path> getAllTextFiles(Path path) throws IOException {
		try (Stream<Path> walk = Files.walk(path)) {
			return walk.filter(Files::isRegularFile).filter(FileFinder::isTextFile).toList();
		}
	}

	/**
	 * This method determines whether or not a specific path is a text file
	 * 
	 * @param path the path you are checking
	 * @return true if a text file, false otherwise.
	 */
	public static boolean isTextFile(Path path) {
		String lower = path.toString().toLowerCase();
		return lower.endsWith(".text") || lower.endsWith(".txt");
	}

}
