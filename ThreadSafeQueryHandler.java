package edu.usfca.cs272;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import edu.usfca.cs272.InvertedIndex.SearchResult;


/**
 * thread safe query handler class
 * 
 * @author ryank
 *
 */
public class ThreadSafeQueryHandler implements QueryHandlerInterface {

	/**
	 * work queue member
	 */
	private final WorkQueue workQueue; 

	/**
	 * invertedIndex member
	 */
	private final ThreadSafeInvertedIndex threadSafeInvertedIndex;  

	/**
	 * searchResults member
	 */
	private final Map<String, List<ThreadSafeInvertedIndex.SearchResult>> searchResults;

	/**
	 * constructor
	 * 
	 * @param threadSafeInvertedIndex the invertedIndex member
	 * @param workQueue     the workQueue member
	 */
	public ThreadSafeQueryHandler(ThreadSafeInvertedIndex threadSafeInvertedIndex, WorkQueue workQueue) {
		this.threadSafeInvertedIndex = threadSafeInvertedIndex;
		this.workQueue = workQueue;
		searchResults = new TreeMap<>();
	}

	/**
	 * Method to process query file
	 * 
	 * @param path          the path where the query file lives
	 * @param exact         boolean variable to determine whether or not the search
	 *                      will be exact
	 * @throws IOException throws an IOException
	 */
	@Override
	public void processQuery(Path path, boolean exact)
			throws IOException {
		try{
			QueryHandlerInterface.super.processQuery(path, exact);
		}
		finally{
			workQueue.finish();
		}
	}
	
	@Override
	public void processQuery(String line, boolean exact) {
		workQueue.execute(new Task(line, exact));
	}

	/**
	 * This method calls the PrettyJSONWriter method to write the search results to
	 * a file
	 * 
	 * @param path the path to write to
	 * @throws IOException throws an IOException
	 */
	public void searchResultsToJson(Path path) throws IOException {
		synchronized(searchResults) {
			PrettyJsonWriter.writeSearchResults(searchResults, path);
		}
	}

	/**
	 * task class to perform search on specific queries
	 * 
	 * @author ryank
	 *
	 */
	private class Task implements Runnable {		
		/**
		 * line to stem 
		 */
		private final String line; 
	
		/**
		 * exact variable to either conduct exact or partial search
		 */
		private final boolean exact;

		/**
		 * task constructor
		 * @param line the query line to process
		 * @param exact exact var to conduct exact or partial search
		 */
		public Task(String line, boolean exact) {
			this.line = line;
			this.exact = exact;
		}

		@Override
		public void run() {
			TreeSet<String> uniqueStems = WordCleaner.uniqueStems(line);
			if(!uniqueStems.isEmpty()) {
				String queryLine = String.join(" ", uniqueStems);
				
				synchronized(searchResults) {
					if(searchResults.containsKey(queryLine)) {
						return;
					}
				}
				var local = threadSafeInvertedIndex.search(uniqueStems, exact);
				
				synchronized(searchResults) {
					searchResults.put(queryLine, local);
				}
			}
			
		}

	}

	
	/**
	 * This method returns an unmodifiable view of the search results map
	 * 
	 * @return a unmodifiable view of the search results map
	 */
	@Override
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
	@Override
	public List<SearchResult> getResults(String line) {
		TreeSet<String> uniqueStems = WordCleaner.uniqueStems(line);
		if (!uniqueStems.isEmpty()) {
			String queryLine = String.join(" ", uniqueStems);
			return Collections.unmodifiableList(searchResults.get(queryLine));
		}
		return Collections.emptyList();
	}


}
