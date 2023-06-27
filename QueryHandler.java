package edu.usfca.cs272;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Java class to handle queries
 * 
 * @author ryank
 *
 */
public class QueryHandler implements QueryHandlerInterface {

	/**
	 * data structure to store search results
	 */
	private final Map<String, List<InvertedIndex.SearchResult>> searchResults;

	/**
	 * inverted index object
	 */
	private final InvertedIndex invertedIndex;

	/**
	 * Constructor!!
	 * 
	 * @param index pass in my invertedIndex!!
	 */
	public QueryHandler(InvertedIndex index) {
		searchResults = new TreeMap<String, List<InvertedIndex.SearchResult>>();
		this.invertedIndex = index;
	}


	/**
	 * This method calls the exact and partial search methods and builds the query
	 * line
	 * 
	 * @param line  the line to search on
	 * @param exact boolean variable to determine whether or not the search will be
	 *              exact
	 */
	public void processQuery(String line, boolean exact) {
		TreeSet<String> uniqueStems = WordCleaner.uniqueStems(line);
		if (!uniqueStems.isEmpty()) {
			String queryLine = String.join(" ", uniqueStems);
			if (!searchResults.containsKey(queryLine)) {
				searchResults.put(queryLine, invertedIndex.search(uniqueStems, exact));
			}
		}
	}

	/**
	 * This method calls the PrettyJSONWriter method to write the search results to
	 * a file
	 * 
	 * @param path the path to write to
	 * @throws IOException throws an IOException
	 */
	public void searchResultsToJson(Path path) throws IOException {
		PrettyJsonWriter.writeSearchResults(searchResults, path);
	}

	/**
	 * toString implementation for QueryHandler object
	 */
	@Override
	public String toString() {
		return "[searchResults=" + searchResults + ", invertedIndex=" + invertedIndex + "]";
	}

	/**
	 * This method returns an unmodifiable view of the search results map
	 * 
	 * @return a unmodifiable view of the search results map
	 */
	public Set<String> getQueries() {
		return Collections.unmodifiableSet(searchResults.keySet());
	}

	/**
	 * This method returns the list of search results associated with a specific
	 * query
	 * 
	 * @param line the query you are getting results of
	 * @return an unmodifiable view of the search result list!
	 */
	public List<InvertedIndex.SearchResult> getResults(String line) {
		TreeSet<String> uniqueStems = WordCleaner.uniqueStems(line);
		if (!uniqueStems.isEmpty()) {
			String queryLine = String.join(" ", uniqueStems);
			return Collections.unmodifiableList(searchResults.get(queryLine));
		}
		return Collections.emptyList();

	}

}
