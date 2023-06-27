package edu.usfca.cs272;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Outputs several simple data structures in "pretty" JSON format where newlines
 * are used to separate elements and nested elements are indented using spaces.
 *
 * Warning: This class is not thread-safe. If multiple threads access this class
 * concurrently, access must be synchronized externally.
 *
 * @author CS 272 Software Development (University of San Francisco)
 * @version Fall 2022
 */
public class PrettyJsonWriter {
	/**
	 * Indents the writer by the specified number of times. Does nothing if the
	 * indentation level is 0 or less.
	 *
	 * @param writer the writer to use
	 * @param indent the number of times to indent
	 * @throws IOException if an IO error occurs
	 */
	public static void writeIndent(Writer writer, int indent) throws IOException {
		while (indent-- > 0) {
			writer.write("  ");
		}
	}

	/**
	 * Indents and then writes the String element.
	 *
	 * @param element the element to write
	 * @param writer  the writer to use
	 * @param indent  the number of times to indent
	 * @throws IOException if an IO error occurs
	 */
	public static void writeIndent(String element, Writer writer, int indent) throws IOException {
		writeIndent(writer, indent);
		writer.write(element);
	}

	/**
	 * Indents and then writes the text element surrounded by {@code " "} quotation
	 * marks.
	 *
	 * @param element the element to write
	 * @param writer  the writer to use
	 * @param indent  the number of times to indent
	 * @throws IOException if an IO error occurs
	 */
	public static void writeQuote(String element, Writer writer, int indent) throws IOException {
		writeIndent(writer, indent);
		writer.write('"');
		writer.write(element);
		writer.write('"');
	}

	/**
	 * Writes the elements as a pretty JSON array.
	 *
	 * @param elements the elements to write
	 * @param writer   the writer to use
	 * @param indent   the initial indent level; the first bracket is not indented,
	 *                 inner elements are indented by one, and the last bracket is
	 *                 indented at the initial indentation level
	 * @throws IOException if an IO error occurs
	 *
	 * @see Writer#write(String)
	 * @see #writeIndent(Writer, int)
	 * @see #writeIndent(String, Writer, int)
	 */
	public static void writeArray(Collection<? extends Number> elements, Writer writer, int indent) throws IOException {
		writer.write("[");
		Iterator<? extends Number> iterator = elements.iterator();
		if (iterator.hasNext()) {
			newLine(writer);
			writeIndent(iterator.next().toString(), writer, indent + 1);
		}
		while (iterator.hasNext()) {
			writer.write(",\n");
			writeIndent(iterator.next().toString(), writer, indent + 1);
		}
		newLine(writer);
		writeIndent("]", writer, indent);

	}

	/**
	 * Writes the elements as a pretty JSON array to file.
	 *
	 * @param elements the elements to write
	 * @param path     the file path to use
	 * @throws IOException if an IO error occurs
	 *
	 * @see Files#newBufferedReader(Path, java.nio.charset.Charset)
	 * @see StandardCharsets#UTF_8
	 * @see #writeArray(Collection, Writer, int)
	 */
	public static void writeArray(Collection<? extends Number> elements, Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, UTF_8)) {
			writeArray(elements, writer, 0);
		}
	}

	/**
	 * Returns the elements as a pretty JSON array.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 *
	 * @see StringWriter
	 * @see #writeArray(Collection, Writer, int)
	 */
	public static String writeArray(Collection<? extends Number> elements) {
		try {
			StringWriter writer = new StringWriter();
			writeArray(elements, writer, 0);
			return writer.toString();
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Writes the elements as a pretty JSON object.
	 *
	 * @param elements the elements to write
	 * @param writer   the writer to use
	 * @param indent   the initial indent level; the first bracket is not indented,
	 *                 inner elements are indented by one, and the last bracket is
	 *                 indented at the initial indentation level
	 * @throws IOException if an IO error occurs
	 *
	 * @see Writer#write(String)
	 * @see #writeIndent(Writer, int)
	 * @see #writeIndent(String, Writer, int)
	 */
	public static void writeObject(Map<String, ? extends Number> elements, Writer writer, int indent)
			throws IOException {
		writer.write("{");
		var iterator = elements.entrySet().iterator();
		if (iterator.hasNext()) {
			var entry = iterator.next();
			writer.write("\n");
			writeQuote(entry.getKey(), writer, indent + 1);
			writer.write(": ");
			writer.write(entry.getValue().toString());
		}
		while (iterator.hasNext()) {
			writer.write(",");
			var entry = iterator.next();
			writer.write("\n");
			writeQuote(entry.getKey(), writer, indent + 1);
			writer.write(": ");
			writer.write(entry.getValue().toString());
		}
		writer.write("\n");
		writeIndent("}", writer, indent);

	}

	/**
	 * Writes the elements as a pretty JSON object to file.
	 *
	 * @param elements the elements to write
	 * @param path     the file path to use
	 * @throws IOException if an IO error occurs
	 *
	 * @see Files#newBufferedReader(Path, java.nio.charset.Charset)
	 * @see StandardCharsets#UTF_8
	 * @see #writeObject(Map, Writer, int)
	 */
	public static void writeObject(Map<String, ? extends Number> elements, Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, UTF_8)) {
			writeObject(elements, writer, 0);
		}
	}

	/**
	 * Returns the elements as a pretty JSON object.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 *
	 * @see StringWriter
	 * @see #writeObject(Map, Writer, int)
	 */
	public static String writeObject(Map<String, ? extends Number> elements) {
		try {
			StringWriter writer = new StringWriter();
			writeObject(elements, writer, 0);
			return writer.toString();
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Writes the elements as a pretty JSON object with nested arrays. The generic
	 * notation used allows this method to be used for any type of map with any type
	 * of nested collection of number objects.
	 *
	 * @param elements the elements to write
	 * @param writer   the writer to use
	 * @param indent   the initial indent level; the first bracket is not indented,
	 *                 inner elements are indented by one, and the last bracket is
	 *                 indented at the initial indentation level
	 * @throws IOException if an IO error occurs
	 *
	 * @see Writer#write(String)
	 * @see #writeIndent(Writer, int)
	 * @see #writeIndent(String, Writer, int)
	 * @see #writeArray(Collection)
	 */
	public static void writeNestedArrays(Map<String, ? extends Collection<? extends Number>> elements, Writer writer,
			int indent) throws IOException {

		writer.write("{");
		var iterator = elements.entrySet().iterator();
		if (iterator.hasNext()) {
			writeNestedArraysHelper(iterator, writer, indent);
		}
		while (iterator.hasNext()) {
			writer.write(",");
			writeNestedArraysHelper(iterator, writer, indent);
		}
		writer.write("\n");
		writeIndent("}", writer, indent);

	}

	/**
	 * helps write the nested arrays for driver class
	 * 
	 * @param iterator iterator to iterate data structure
	 * @param writer   writer to write to file
	 * @param indent   number of spaces to indent
	 * @throws IOException throws IO exception
	 */
	public static void writeNestedArraysHelper(
			Iterator<? extends Entry<String, ? extends Collection<? extends Number>>> iterator, Writer writer,
			int indent) throws IOException {
		var entry = iterator.next();
		writer.write("\n");
		writeQuote(entry.getKey(), writer, indent + 1);
		writer.write(": ");
		writeArray(entry.getValue(), writer, indent + 1);
	}

	/**
	 * Writes the elements as a pretty JSON object with nested arrays to file.
	 *
	 * @param elements the elements to write
	 * @param path     the file path to use
	 * @throws IOException if an IO error occurs
	 *
	 * @see Files#newBufferedReader(Path, java.nio.charset.Charset)
	 * @see StandardCharsets#UTF_8
	 * @see #writeNestedArrays(Map, Writer, int)
	 */
	public static void writeNestedArrays(Map<String, ? extends Collection<? extends Number>> elements, Path path)
			throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, UTF_8)) {
			writeNestedArrays(elements, writer, 0);
		}
	}

	/**
	 * Returns the elements as a pretty JSON object with nested arrays.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 *
	 * @see StringWriter
	 * @see #writeNestedArrays(Map, Writer, int)
	 */
	public static String writeNestedArrays(Map<String, ? extends Collection<? extends Number>> elements) {
		try {
			StringWriter writer = new StringWriter();
			writeNestedArrays(elements, writer, 0);
			return writer.toString();
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Writes the elements as a pretty JSON array with nested objects. The generic
	 * notation used allows this method to be used for any type of collection with
	 * any type of nested map of String keys to number objects.
	 *
	 * @param elements the elements to write
	 * @param writer   the writer to use
	 * @param indent   the initial indent level; the first bracket is not indented,
	 *                 inner elements are indented by one, and the last bracket is
	 *                 indented at the initial indentation level
	 * @throws IOException if an IO error occurs
	 *
	 * @see Writer#write(String)
	 * @see #writeIndent(Writer, int)
	 * @see #writeIndent(String, Writer, int)
	 * @see #writeObject(Map)
	 */
	public static void writeNestedObjects(Collection<? extends Map<String, ? extends Number>> elements, Writer writer,
			int indent) throws IOException {
		writer.write("[");
		var iterator = elements.iterator();
		if (iterator.hasNext()) {
			newLine(writer);
			Map<String, ? extends Number> firstMap = iterator.next();
			writeIndent(writer, indent + 1);
			writeObject(firstMap, writer, indent + 1);
			while (iterator.hasNext()) {
				Map<String, ? extends Number> map = iterator.next();
				writer.write(",");
				writer.write("\n");
				writeIndent(writer, indent + 1);
				writeObject(map, writer, indent + 1);
			}
		}
		writer.write("\n");
		writeIndent("]", writer, indent);
	}

	/**
	 * Writes the elements as a pretty JSON array with nested objects to file.
	 *
	 * @param elements the elements to write
	 * @param path     the file path to use
	 * @throws IOException if an IO error occurs
	 *
	 * @see Files#newBufferedReader(Path, java.nio.charset.Charset)
	 * @see StandardCharsets#UTF_8
	 * @see #writeNestedObjects(Collection)
	 */
	public static void writeNestedObjects(Collection<? extends Map<String, ? extends Number>> elements, Path path)
			throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, UTF_8)) {
			writeNestedObjects(elements, writer, 0);
		}
	}

	/**
	 * Returns the elements as a pretty JSON array with nested objects.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 *
	 * @see StringWriter
	 * @see #writeNestedObjects(Collection)
	 */
	public static String writeNestedObjects(Collection<? extends Map<String, ? extends Number>> elements) {
		try {
			StringWriter writer = new StringWriter();
			writeNestedObjects(elements, writer, 0);
			return writer.toString();
		} catch (IOException e) {
			return null;
		}
	}


	/**
	 * This method creates a BufferedWriter and calls writeJSON with the writer as a
	 * param
	 * 
	 * @param map  inverted index to print as JSON
	 * @param path the path to output to
	 * @throws IOException throws an IOException
	 */

	public static void writeJSON(Map<String, ? extends Map<String, ? extends Collection<? extends Number>>> map, Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, UTF_8)) {
			writeJSON(map, writer, 0);
		}

	}

	/**
	 * This method writes the JSON for an inverted Index
	 * 
	 * @param map    the inverted index to write
	 * @param writer the writer
	 * @param indent number of spaces to indent
	 * @throws IOException throws an IOException
	 */
	public static void writeJSON(Map<String, ? extends Map<String, ? extends Collection<? extends Number>>> map, Writer writer, int indent)
			throws IOException {
		writer.write("{");
		var iterator = map.entrySet().iterator(); //extends ENTRY
		if (iterator.hasNext()) {
			innerArrays(iterator, writer, indent);
		}
		while (iterator.hasNext()) {
			writer.write(",");
			innerArrays(iterator, writer, indent);
		}
		PrettyJsonWriter.writeIndent("\n}", writer, indent);

	}

	/**
	 * Helper method for writeJSON. This method writes nested arrays
	 * 
	 * @param iterator iterator to iterate the data structure
	 * @param writer   writer to write to the file
	 * @param indent   number of spaces to indent
	 * @throws IOException throws an IOException
	 */

	// public static void innerArrays(Iterator<Entry<String, TreeMap<String, ?
	// extends Collection<? extends Number>>>> iterator, Writer writer,

	// what i have for generics: Map<String, Collection<? extends Map<String, ?
	// extends Collection< ? extends Number>>>>

	public static void innerArrays(Iterator<? extends Entry<String, ? extends Map<String, ? extends Collection<? extends Number>>>> iterator, Writer writer,
			int indent) throws IOException {
		var entry = iterator.next();
		writer.write("\n");
		PrettyJsonWriter.writeQuote(entry.getKey(), writer, indent + 1);
		writer.write(": ");
		PrettyJsonWriter.writeNestedArrays(entry.getValue(), writer, indent + 1);
	}

	/**
	 * This method creates a writer and calls writeSearchResults with the write
	 * 
	 * @param searchResults the search results data structure you will write to file
	 * @param path          the path the data structure will be written to
	 * @throws IOException throws an IOException
	 */
	public static void writeSearchResults(Map<String, ? extends Collection<InvertedIndex.SearchResult>> searchResults, Path path)
			throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, UTF_8)) {
			writeSearchResults(searchResults, writer);
		}
	}

	/**
	 * This method writes the search results as JSON to a file
	 * 
	 * @param searchResults the search results data structure
	 * @param writer        writer object
	 * @throws IOException throws an IOException
	 */
	public static void writeSearchResults(Map<String, ? extends Collection<InvertedIndex.SearchResult>> searchResults, Writer writer)
			throws IOException {
		writer.write("{");
		writer.write("\n");
		var query = searchResults.keySet().iterator();
		if (query.hasNext()) {
			writeSearchHelper(searchResults, query, writer);
		}
		while (query.hasNext()) {
			writeSearchHelper(searchResults, query, writer);
		}
		writer.write("}");
	}

	/**
	 * This method is a helper method for the writeSearchResults method
	 * 
	 * @param searchResults the search results data structure
	 * @param iterator      the iterator to iterate through
	 * @param writer        the write to write to a file
	 * @throws IOException throws an IOException
	 */
	public static void writeSearchHelper(Map<String, ? extends Collection<InvertedIndex.SearchResult>> searchResults, Iterator<String> iterator,
			Writer writer) throws IOException {
		var temp = iterator.next();
		String query = temp.toString();
		writeQuote(query, writer, 1);
		writer.write(": ");
		writer.write("[");
		if (searchResults.get(query) != null) {
			writeSearchResults(searchResults.get(query).iterator(), writer);
		}
		if (iterator.hasNext()) {
			writer.write("\n");
			writeIndent("]", writer, 1);
			writer.write(",");
		} else {
			writer.write("\n");
			writeIndent("]", writer, 1);
		}
		writer.write("\n");
	}

	/**
	 * This method writes every search result object in the list
	 * 
	 * @param searchResultsIterator the iterator for the list
	 * @param writer                the writer to write to file
	 * @throws IOException throws an IOException
	 */
	public static void writeSearchResults(Iterator<InvertedIndex.SearchResult> searchResultsIterator, Writer writer)
			throws IOException {
		var result = searchResultsIterator;
		if (result.hasNext()) {
			var temp = result.next();
			searchResultArrayHelper(temp, writer);
			while (result.hasNext()) {
				temp = result.next();
				writer.write(",");
				searchResultArrayHelper(temp, writer);
			}
		}
	}
	
	
	/**
	 * this method is a helper method designed to write multiple search result objects
	 * @param temp search result to write
	 * @param writer writer to write
	 * @throws IOException throws an IOException
	 */
	private static void searchResultArrayHelper(InvertedIndex.SearchResult temp, Writer writer)
            throws IOException { 
		writer.write("\n");
		writeIndent("{", writer, 2);
		writer.write("\n");
		writeQuote("count", writer, 3);
		writer.write(": ");
		int count = temp.getCount();
		writer.write(String.valueOf(count));
		writer.write(",");
		writer.write("\n");
		writeQuote("score", writer, 3);
		double score = temp.getScore();
		writer.write(": " + String.format("%.8f", score));
		writer.write(",");
		writer.write("\n");
		writeQuote("where", writer, 3);
		writer.write(": \"");
		writer.write(temp.getLocation());
		writer.write("\"");
		writer.write("\n");
		writeIndent("}", writer, 2);
	}

	/**
	 * Writes a curly brace at the beginning
	 * 
	 * @param writer writer to write
	 * @throws IOException if an IO occurs
	 */

	public static void writeBeginning(Writer writer) throws IOException {
		writer.write("{");
		writer.write("\n");
	}

	/**
	 * writes a new line
	 * 
	 * @param writer writer to write
	 * @throws IOException if an IO occurs
	 */
	public static void newLine(Writer writer) throws IOException {
		writer.write("\n");
	}

	/**
	 * writes the end with a curly brace
	 * 
	 * @param writer to write
	 * @throws IOException if an IO occurs
	 */
	public static void writeEnd(Writer writer) throws IOException {
		writer.write("}");
	}

	/**
	 * writes a space
	 * 
	 * @param writer to write
	 * @throws IOException if an IO occurs
	 */
	public static void writeSpace(Writer writer) throws IOException {
		writer.write(" ");
	}

}
