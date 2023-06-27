package edu.usfca.cs272;

import static opennlp.tools.stemmer.snowball.SnowballStemmer.ALGORITHM.ENGLISH;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * class responsible for filling up an invertedIndex with stemmed words from a
 * file
 * 
 * @author ryank
 *
 */
public class IndexBuilder {

	/**
	 * This method builds the invertedIndex
	 * 
	 * @param path          the path of the file
	 * @param invertedIndex the invertedIndex object
	 * @throws IOException throws an IOException
	 */
	public static void buildInvertedIndex(Path path, InvertedIndex invertedIndex) throws IOException {
		Stemmer stemmer = new SnowballStemmer(ENGLISH);
		try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);) {
			String line = null;
			int position = 1;
			String location = path.toString();
			while ((line = reader.readLine()) != null) {
				String[] parsedLine = WordCleaner.parse(line);
				for (String word : parsedLine) {
					word = stemmer.stem(word).toString();
					invertedIndex.add(word, location, position);
					position++;
				}
			}
		}
	}

	/**
	 * This method builds the inverted index
	 * 
	 * @param path          the path
	 * @param invertedIndex the inverted index you are creating
	 * @throws IOException this method throws an IO exception
	 */
	public static void build(Path path, InvertedIndex invertedIndex) throws IOException {
		if (Files.isDirectory(path)) {
			List<Path> allTextFiles = FileFinder.getAllTextFiles(path);
			for (Path file : allTextFiles) {
				buildInvertedIndex(file, invertedIndex);
			}
		} else {
			buildInvertedIndex(path, invertedIndex);
		}
	}
}
