package edu.usfca.cs272;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Thread safe inverted index builder!!!
 * 
 * @author ryank
 *
 */
public class ThreadSafeInvertedIndexBuilder extends IndexBuilder {
	

	/**
	 * This method builds the inverted index
	 * 
	 * @param path          the path
	 * @param invertedIndex the inverted index you are creating
	 * @param workQueue     the workQueue
	 * @throws IOException this method throws an IO exception
	 */
	public static void build(Path path, ThreadSafeInvertedIndex invertedIndex, WorkQueue workQueue) throws IOException {
		try {
			if (Files.isDirectory(path)) {
				List<Path> allTextFiles = FileFinder.getAllTextFiles(path);
				for (Path file : allTextFiles) {
					workQueue.execute(new Task(file, invertedIndex));
				}
			} else {
				workQueue.execute(new Task(path, invertedIndex));
			}
		}
		finally {
			workQueue.finish();
		}
	}

	/**
	 * task class!
	 * 
	 * @author ryank
	 *
	 */
	private static class Task implements Runnable {

		/**
		 * path of the file
		 */
		private final Path path;
		/**
		 * the invertedIndex to build
		 */
		private final ThreadSafeInvertedIndex invertedIndex; 

		/**
		 * constructor
		 * 
		 * @param path          path of the file
		 * @param invertedIndex the invertedIndex to build
		 */
		public Task(Path path, ThreadSafeInvertedIndex invertedIndex) {
			this.path = path;
			this.invertedIndex = invertedIndex;
		}

		@Override
		public void run() {
				InvertedIndex local = new InvertedIndex();
				try {
					IndexBuilder.buildInvertedIndex(path, local);
				}
				catch(IOException e) {
					throw new UncheckedIOException(e);
				}
				invertedIndex.addAll(local);
		}

	}

}
