package edu.usfca.cs272;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;

/**
 * Class responsible for running this project based on the provided command-line
 * arguments. See the README for details.
 *
 * @author Ryan Kennedy
 * @author CS 272 Software Development (University of San Francisco)
 * @version Fall 2022
 */
public class Driver {

	/**
	 * Determines whether or not the path is a file or directory and calls
	 * createInvertedIndex
	 * 
	 * @param parser        ArgumentParser object that can parse the command line
	 *                      arguments
	 * @param invertedIndex InvertedIndex object to reference the inverted index
	 *                      data structure
	 * @param queryHandler  the queryHandler object to access QueryHandler methods
	 * @throws IOException throws an IO exception
	 */
	public static void getInputPath(ArgumentParser parser, InvertedIndex invertedIndex, QueryHandler queryHandler)
			throws IOException {
		Path path = parser.getPath("-text");
		IndexBuilder.build(path, invertedIndex);
		if (parser.hasFlag("-query") && parser.hasValue("-query")) {
			try {
				// buildQuery(parser, invertedIndex, parser.hasFlag("-exact"), queryHandler);
				Path queryPath = Path.of(parser.getString("-query"));
				queryHandler.processQuery(queryPath, parser.hasFlag("-exact"));
			} catch (IOException e) {
				System.out.println("Invalid path, not searching.");
			}
		}
	}

	/**
	 * Determines whether or not the path is a file or directory and calls
	 * createInvertedIndex
	 * 
	 * @param argParser               ArgumentParser object that can parse the
	 *                                command line arguments
	 * @param threadSafeInvertedIndex InvertedIndex object reference to reference
	 *                                the inverted index data structure
	 * @param workQueue               work queue object
	 * @param threadSafeQueryHandler  the ThreadSafeQueryHandler object
	 * @param exact                   determines exact or partial search
	 * @throws IOException throws an IOException
	 */
	public static void multiThreadedGetInputPath(ArgumentParser argParser,
			ThreadSafeInvertedIndex threadSafeInvertedIndex, WorkQueue workQueue,
			ThreadSafeQueryHandler threadSafeQueryHandler, boolean exact) throws IOException {
		System.out.println("in multiThreadedGetInputPath");
		Path path = argParser.getPath("-text");
		ThreadSafeInvertedIndexBuilder.build(path, threadSafeInvertedIndex, workQueue);
		if (argParser.hasFlag("-query") && argParser.hasValue("-query")) {
			try {
				Path queryPath = Path.of(argParser.getString("-query"));
				threadSafeQueryHandler.processQuery(queryPath, exact);
			} catch (IOException e) {
				System.out.println("Invalid path, not searching.");
			}
		}
	}

	/**
	 * This method checks for the output flag
	 * 
	 * @param parser        ArgumentParser object
	 * @param invertedIndex the invertedIndex object
	 * @param queryHandler  queryHandler object
	 * @throws IOException throws an IOException
	 */
	public static void writeOutput(ArgumentParser parser, InvertedIndex invertedIndex, QueryHandlerInterface queryHandler)
			throws IOException {
		if (parser.hasFlag("-index")) {
			Path path = parser.getPath("-index", Path.of("index.json"));
			invertedIndex.toJson(path);
		}
		if (parser.hasFlag("-counts")) {
			try {
				Path countsPath = parser.getPath("-counts", Path.of("counts.json"));
				invertedIndex.countsToJson(countsPath);
			} catch (IOException e) {
				System.out.println("Invalid counts path");
			}
		}
		if (parser.hasFlag("-results")) {
			try {
				Path searchResultPath = parser.getPath("-results", Path.of("results.json"));
				queryHandler.searchResultsToJson(searchResultPath);
			} catch (IOException e) {
				System.out.println("Error writing search results!");
			}
		}
	}

	
	/**
	 * Initializes the classes necessary based on the provided command-line
	 * arguments. This includes (but is not limited to) how to build or search an
	 * inverted index.
	 *
	 * @param args flag/value pairs used to start this program
	 */
	public static void main(String[] args) {
		
		ArgumentParser argParser = new ArgumentParser(args);
		
		InvertedIndex invertedIndex = null;
		QueryHandlerInterface queryHandler = null;		
		WorkQueue workQueue = null;
		ThreadSafeInvertedIndex threadSafeInvertedIndex = null;
		boolean multithreading = argParser.hasFlag("-threads");
		boolean html = argParser.hasFlag("-html");
		String seedUrl = argParser.getString("-html");
		int maxCrawls = argParser.getInteger("-max", 1);
		
		
		
		if (multithreading || html) {
			int threads = argParser.getInteger("-threads");
			if (!argParser.hasValue("-threads") || threads < 1) {
				threads = 5;
			}
			
			workQueue = new WorkQueue(threads);
			threadSafeInvertedIndex = new ThreadSafeInvertedIndex();
			invertedIndex = threadSafeInvertedIndex;
			queryHandler = new ThreadSafeQueryHandler(threadSafeInvertedIndex, workQueue);
		}
		else {
			invertedIndex = new InvertedIndex();
			queryHandler = new QueryHandler(invertedIndex);
		}
		
		if (argParser.hasFlag("-text") && argParser.hasValue("-text")) {
			Path path = argParser.getPath("-text");
			try {
				if (threadSafeInvertedIndex != null && workQueue != null) {
					ThreadSafeInvertedIndexBuilder.build(path, threadSafeInvertedIndex, workQueue);
				} 
				else {
					IndexBuilder.build(path, invertedIndex);
				}
			}
			catch (IOException e) {
				
			}
		}
		
		if(argParser.hasFlag("-html") && argParser.hasValue("-html")) {
			WebIndexBuilder webIndexBuilder = new WebIndexBuilder(threadSafeInvertedIndex, workQueue,
					 seedUrl, maxCrawls);
//			System.out.println("Max crawls: " + maxCrawls);
//			System.out.println("Seed URL: " + seedUrl);
			try {
				webIndexBuilder.crawlWeb();
			} catch (MalformedURLException e) {
				System.out.println("MalformedURLException");
			}
		}
		if (argParser.hasFlag("-query") && argParser.hasValue("-query")) {
			try {
				Path queryPath = Path.of(argParser.getString("-query"));
				queryHandler.processQuery(queryPath, argParser.hasFlag("-exact"));
			} catch (IOException e) {
				System.out.println("Invalid path, not searching.");
			}
		}
				
		try {
			writeOutput(argParser, invertedIndex, queryHandler);
		} catch (IOException e) {
			System.out.println("IOException trying to write output");
		}
		
		if (workQueue != null) {
			workQueue.shutdown();
		}
		
	}
}
