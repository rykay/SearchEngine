package edu.usfca.cs272;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Class responsible for creating invertedIndex data structure
 * 
 * @author ryank
 *
 */
public class InvertedIndex {

	/**
	 * inverted index data structure
	 */
	private final TreeMap<String, TreeMap<String, TreeSet<Integer>>> invertedIndex;

	/**
	 * word count for each file data structure
	 */
	private final TreeMap<String, Integer> wordCounts;

	/**
	 * InvertedIndex constructor
	 */
	public InvertedIndex() {
		invertedIndex = new TreeMap<>();
		wordCounts = new TreeMap<>();
	}

	/**
	 * Class responsible for creating search result objects
	 * 
	 * @author ryank
	 *
	 */
	public class SearchResult implements Comparable<SearchResult> {

		/**
		 * score of the search result
		 */
		private double score;

		/**
		 * path location of the search result
		 */
		private final String location; //

		/**
		 * count of matches of search result
		 */
		private int count;

		/**
		 * Search Result constructor
		 * 
		 * @param location the location of the search result
		 */
		public SearchResult(String location) {
			this.score = 0;
			this.location = location;
			this.count = 0;
		}

		/**
		 * Update method to update the count and score of a search result with the same
		 * location
		 * 
		 * @param word a specific word of the query
		 */
		private void update(String word) {
			this.count += invertedIndex.get(word).get(this.location).size();
			this.score = this.count / (double) getFilesTotalWords(this.location);
		}

		/**
		 * String representation of a SearchResults object
		 */
		@Override
		public String toString() {
			return "{count=" + count + ", score=" + score + ", where=" + location + "}";
		}

		/**
		 * Getter method for search result score
		 * 
		 * @return score the score of the search result
		 */
		public double getScore() {
			return score;
		}

		/**
		 * Getter method for the search result location
		 * 
		 * @return location the location of the search result
		 */
		public String getLocation() {
			return location;
		}

		/**
		 * Getter method for the search result count
		 * 
		 * @return count the count of the search result
		 */
		public int getCount() {
			return count;
		}

		/**
		 * This method compares this instance of a search result against another based
		 * on score, count, and then location
		 */
		@Override
		public int compareTo(SearchResult o) {
			if (this.score != o.score) {
				return Double.compare(o.score, this.score);
			} else if (this.count != o.count) {
				return Integer.compare(o.count, this.count);
			} else {
				return this.location.compareToIgnoreCase(o.location);
			}
		}
	}

	/**
	 * This method conducts search on a set of queries, exact search if bool exact
	 * is true, else partial search
	 * 
	 * @param queries set of queries
	 * @param exact   whether or not the search is exact
	 * @return a list of search results
	 */
	public List<SearchResult> search(Set<String> queries, boolean exact) {
		return exact ? exactSearch(queries) : partialSearch(queries);
	}

	/**
	 * This method performs exact search on the inverted index data structure
	 * 
	 * @param query the set of query words to perform partial search with
	 * @return a list of sorted SearchResults objects
	 */

	public List<SearchResult> exactSearch(Set<String> query) {
		List<SearchResult> searchResultList = new ArrayList<SearchResult>();
		HashMap<String, SearchResult> matches = new HashMap<>(); // path and search result

		for (String queryWord : query) {
			if (invertedIndex.containsKey(queryWord)) {
				searchHelper(queryWord, searchResultList, matches);
			}
		}

		Collections.sort(searchResultList);
		return searchResultList;
	}

	/**
	 * This method performs partial search on the inverted index data structure
	 * 
	 * @param query the set of query words to perform partial search with
	 * @return a list of sorted SearchResults objects
	 */
	public List<SearchResult> partialSearch(Set<String> query) {
		List<SearchResult> searchResultList = new ArrayList<SearchResult>();
		HashMap<String, SearchResult> matches = new HashMap<>(); // path and search result
		for (String queryWord : query) {
			for (var mapping : invertedIndex.tailMap(queryWord).entrySet()) {
				String invertedIndexWord = mapping.getKey();
				if (invertedIndexWord.startsWith(queryWord)) {
					searchHelper(invertedIndexWord, searchResultList, matches);
				} else {
					break;
				}
			}
		}
		Collections.sort(searchResultList);
		return searchResultList;
	}

	/**
	 * This is a private method to perform the search operation
	 * 
	 * @param queryWord        the word to update
	 * @param searchResultList the list to update
	 * @param matches          the hashmap storing the matches
	 */
	private void searchHelper(String queryWord, List<SearchResult> searchResultList,
			HashMap<String, SearchResult> matches) {
		for (String location : invertedIndex.get(queryWord).keySet()) {
			if (!matches.containsKey(location)) {
				SearchResult result = new SearchResult(location);
				searchResultList.add(result);
				matches.put(location, result);
			}
			matches.get(location).update(queryWord);
		}
	}

	/**
	 * this method adds the word to the invertedIndex data structure at its
	 * specified position
	 * 
	 * @param path     the path of the file
	 * @param word     the word to add
	 * @param position the position of the word
	 */
	public void add(String word, String path, int position) {
		this.invertedIndex.putIfAbsent(word, new TreeMap<String, TreeSet<Integer>>());
		this.invertedIndex.get(word).putIfAbsent(path, new TreeSet<Integer>());
		boolean modified = this.invertedIndex.get(word).get(path).add(position);

		if (modified) {
			this.wordCounts.putIfAbsent(path, 0);
			this.wordCounts.put(path, this.wordCounts.get(path) + 1);
		}
	}

	/**
	 * ToString implementation of the inverted index data structure
	 */
	@Override
	public String toString() {
		return "InvertedIndex: " + invertedIndex + "";
	}

	/**
	 * This method calls the PrettyJSONWriter method to write to the file
	 * 
	 * @param path the path of the file to write to
	 * @throws IOException throws an IOException
	 */
	public void toJson(Path path) throws IOException {
		PrettyJsonWriter.writeJSON(this.invertedIndex, path);
	}

	/**
	 * This method calls the PrettyJSONWriter method to write the counts to a file
	 * 
	 * @param path the path to write to
	 * @throws IOException throws an IOException
	 */
	public void countsToJson(Path path) throws IOException {
		PrettyJsonWriter.writeObject(wordCounts, path);
	}

	/**
	 * This method adds a list of words to the inverted index
	 * 
	 * @param words the list of words to add
	 * @param path  the path of the file
	 */
	public void addAll(List<String> words, String path) {
		int position = 1;
		for (String word : words) {
			add(word, path, position);
			position++;
		}
	}

	/**
	 * This method adds an existing invertedIndex into the original. The local
	 * inverted index and the existing inverted index should not overlap in files.
	 * 
	 * @param local the inverted index to add to the original
	 */
	public void addAll(InvertedIndex local) {
		for (var entry : local.invertedIndex.entrySet()) {
			if (!this.invertedIndex.containsKey(entry.getKey())) {
				this.invertedIndex.put(entry.getKey(), local.invertedIndex.get(entry.getKey()));
			} else {
				for (var location : local.invertedIndex.get(entry.getKey()).entrySet()) {
					TreeSet<Integer> positions = local.invertedIndex.get(entry.getKey()).get(location.getKey());
					if (!this.invertedIndex.get(entry.getKey()).containsKey(location.getKey())) {
						this.invertedIndex.get(entry.getKey()).put(location.getKey(), positions);
					} else {
						this.invertedIndex.get(entry.getKey()).get(location.getKey()).addAll(positions);
					}
				}
			}

		}
		for (var location : local.wordCounts.entrySet()) {
			if (!this.wordCounts.containsKey(location.getKey())) {
				this.wordCounts.put(location.getKey(), local.wordCounts.get(location.getKey()));
			} else {
				int wordCount = this.wordCounts.getOrDefault(location.getKey(), 0) + local.wordCounts.get(location.getKey());
				this.wordCounts.put(location.getKey(), wordCount);
			}
		}
	}

	/**
	 * This method gets the whole inverted index size
	 * 
	 * @return size of the invertedIndex
	 */
	public int size() {
		return this.invertedIndex.size();
	}

	/**
	 * This method returns the inner map
	 * 
	 * @param word the word you are checking for
	 * @return the size of the inner map
	 */
	public int size(String word) {
		return has(word) ? this.invertedIndex.get(word).size() : 0;
	}

	/**
	 * This method returns the size of the inner set of the inverted index
	 * 
	 * @param word the word you are checking
	 * @param path the path you are checking
	 * @return the size of the inner map
	 */
	public int size(String word, String path) {
		return has(word, path) ? this.invertedIndex.get(word).get(path).size() : 0;
	}

	/**
	 * This method checks if a certain word exists in the inverted index
	 * 
	 * @param word the word you are checking for
	 * @return true if the inverted index contains the word, otherwise false.
	 */
	public boolean has(String word) {
		return this.invertedIndex.containsKey(word);
	}

	/**
	 * This method checks if a certain location is in the inverted index
	 * 
	 * @param word     the word you are checking
	 * @param location the location you are checking for
	 * @return true if the inverted index contains the word, otherwise false.
	 */
	public boolean has(String word, String location) {
		return has(word) && this.invertedIndex.get(word).containsKey(location);
	}

	/**
	 * This method checks if a certain position exists for a certain word and
	 * location
	 * 
	 * @param word     the word you are checking
	 * @param location the location you are checking
	 * @param position the position you are checking
	 * @return true if the inverted index contains the position, otherwise false.
	 */
	public boolean has(String word, String location, int position) {
		return has(word, location) && this.invertedIndex.get(word).get(location).contains(position);
	}

	/**
	 * This method returns the outer key set (all of the words in the inverted
	 * index)
	 * 
	 * @return a set of all the words
	 */
	public Set<String> getWords() {
		return Collections.unmodifiableSet(this.invertedIndex.keySet());
	}

	/**
	 * This method returns an unmodifiable keyset of all the locations in the
	 * inverted index
	 * 
	 * @return a set of all the locations
	 */
	public Set<String> getLocations() {
		return Collections.unmodifiableSet(this.wordCounts.keySet());
	}

	/**
	 * This method returns the inner keyset (all of the locations)
	 * 
	 * @param word the word you are checking locations of
	 * @return a set of all the locations
	 */
	public Set<String> getLocations(String word) {
		if (has(word))
			return Collections.unmodifiableSet(this.invertedIndex.get(word).keySet());
		return Collections.emptySet();
	}

	/**
	 * This method returns a set of all the positions of a word at a specific path
	 * 
	 * @param word     the word you are using
	 * @param location the location/path you are using
	 * @return a set of all the positions of the word and the specific location
	 */
	public Set<Integer> get(String word, String location) {
		if (has(word, location))
			return Collections.unmodifiableSet(this.invertedIndex.get(word).get(location));
		return Collections.emptySet();
	}

	/**
	 * This method adds the total words
	 * 
	 * @param location the path of the file
	 * @return the total word count
	 */
	public int getFilesTotalWords(String location) {
		return this.wordCounts.getOrDefault(location, 0);
	}

}