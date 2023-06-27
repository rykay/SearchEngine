package edu.usfca.cs272;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

/**
 * thread safe class
 * @author ryank
 *
 */
public class ThreadSafeInvertedIndex extends InvertedIndex {
	
	/**
	 * ReadWriteLock object
	 */
	private final ReadWriteLock lock;
	
	/**
	 * constructor
	 */
	public ThreadSafeInvertedIndex() {
		this.lock = new ReadWriteLock();
	}


	@Override
	public List<SearchResult> exactSearch(Set<String> query) {
		lock.read().lock();
		try {
			return super.exactSearch(query);
		}
		finally {
			lock.read().unlock();
		}
	}

	@Override
	public List<SearchResult> partialSearch(Set<String> query) {
		lock.read().lock();
		try {
			return super.partialSearch(query);
		}
		finally {
			lock.read().unlock();
		}
	}

	@Override
	public void add(String word, String path, int position) {
		lock.write().lock(); 
		try {
			super.add(word, path, position);
		}
		finally {
			lock.write().unlock();
		}
	}

	@Override
	public String toString() {
		lock.read().lock();
		try {
			return super.toString();
		}
		finally {
			lock.read().unlock();
		}
	}

	@Override
	public void toJson(Path path) throws IOException {
		lock.read().lock();
		try {
			super.toJson(path);
		}
		finally {
			lock.read().unlock();
		}
	}

	@Override
	public void countsToJson(Path path) throws IOException {
		lock.read().lock();
		try {
			super.countsToJson(path);
		}
		finally {
			lock.read().unlock();
		}
		
	}

	@Override
	public void addAll(List<String> words, String path) {
		lock.write().lock(); 
		try {
			super.addAll(words, path);
		}
		finally {
			lock.write().unlock();
		}
	}
	
	@Override
	public void addAll(InvertedIndex local) {
		lock.write().lock();
		try {
			super.addAll(local);
		}
		finally {
			lock.write().unlock();
		}
	}

	@Override
	public int size() {
		lock.read().lock();
		try {
			return super.size();
		}
		finally {
			lock.read().unlock();
		}
	}

	@Override
	public int size(String word) {
		lock.read().lock();
		try {
			return super.size(word);
		}
		finally {
			lock.read().unlock();
		}
	}

	@Override
	public int size(String word, String path) {
		lock.read().lock();
		try { 
			return super.size(word, path);
		}
		finally {
			lock.read().unlock();
		}
	}

	@Override
	public boolean has(String word) {
		lock.read().lock();
		try {
			return super.has(word);
		}
		finally {
			lock.read().unlock();
		}
	}

	@Override
	public boolean has(String word, String location) {
		lock.read().lock();
		try {
			return super.has(word, location);
		}
		finally {
			lock.read().unlock();
		}
		
	}
		

	@Override
	public boolean has(String word, String location, int position) {
		lock.read().lock();
		try {
			return super.has(word, location, position);
		}
		finally {
			lock.read().unlock();
		}
	}

	@Override
	public Set<String> getWords() {
		lock.read().lock();
		try {
			return super.getWords();
		}
		finally {
			lock.read().unlock();
		}
	}

	@Override
	public Set<String> getLocations() {
		lock.read().lock();
		try {
			return super.getLocations();
		}
		finally {
			lock.read().unlock();
		}
	}

	@Override
	public Set<String> getLocations(String word) {
		lock.read().lock();
		try{
			return super.getLocations(word);
		}
		finally {
			lock.read().unlock();
		}
	}

	@Override
	public Set<Integer> get(String word, String location) {
		lock.read().lock();
		try{
			return super.get(word, location);
		}
		finally {
			lock.read().unlock();
		}
	}

	@Override
	public int getFilesTotalWords(String location) {
		lock.read().lock();
		try{
			return super.getFilesTotalWords(location);
		}
		finally {
			lock.read().unlock();
		}
	}
}
