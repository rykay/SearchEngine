package edu.usfca.cs272;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

/**
 * QueryHandler Interface!
 * 
 * @author ryank
 *
 */
public interface QueryHandlerInterface {

	/**
	 * This method calls the PrettyJSONWriter method to write the search results to
	 * a file
	 * 
	 * @param path the path to write to
	 * @throws IOException throws an IOException
	 */
	public void searchResultsToJson(Path path) throws IOException;

	/**
	 * Method to process query file
	 * 
	 * @param path  the path where the query file lives
	 * @param exact boolean variable to determine whether or not the search will be
	 *              exact
	 * @throws IOException throws an IOException
	 */
	public default void processQuery(Path path, boolean exact) throws IOException {
		try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);) {
			String line = null;
			while ((line = reader.readLine()) != null) {
				processQuery(line, exact);
			}
		}
	}

	/**
	 * This method calls the exact and partial search methods and builds the query
	 * line
	 * 
	 * @param line  the line to search on
	 * @param exact boolean variable to determine whether or not the search will be
	 *              exact
	 */
	public void processQuery(String line, boolean exact);

	
	/**
	 * This method returns an unmodifiable view of the search results map
	 * 
	 * @return a unmodifiable view of the search results map
	 */
	public Set<String> getQueries();
	
	/**
	 * This method returns the list of search results associated with a specific
	 * query
	 * 
	 * @param line the query you are getting results of
	 * @return an unmodifiable view of the search result list!
	 */
	public List<InvertedIndex.SearchResult> getResults(String line);
	
	
	
}
