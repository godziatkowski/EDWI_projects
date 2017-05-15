package com.godziatkowski.bookindexer;

import java.io.IOException;
import java.util.List;
import javax.swing.JOptionPane;
import org.apache.lucene.queryparser.classic.ParseException;

public class Main {

    private static final long NANO_SECONDS_IN_MINUTE = 1000000000L * 60L;
    
    public static void main(String[] args) throws IOException, ParseException {
        long start = System.nanoTime();
        BookUnpacker bookUnpacker = new BookUnpacker();
        List<UnpackedBook> unpackedBooks = bookUnpacker.unpackBooks();
        long unpackTime = System.nanoTime();
        BookIndexer bookIndexer = new BookIndexer();
        bookIndexer.indexBooks(unpackedBooks);
        long indexTime = System.nanoTime();
        bookIndexer.printSize();
        long end = System.nanoTime();
        long elapsedTime = end - start;
        long timeOfUnpack = unpackTime - start;
        long timeOfIndexing = indexTime - unpackTime;
        System.out.println("Time: " + convertToMinute(elapsedTime) );
        System.out.println("Time for unpacking: " + convertToMinute(timeOfUnpack) );
        System.out.println("Time for indexing: " + convertToMinute(timeOfIndexing) );
        while (true) {
            QueryType queryType = (QueryType) JOptionPane.showInputDialog(null, "Query based on", "Selection", JOptionPane.DEFAULT_OPTION, null, QueryType.values(), "0");
            if (queryType != null) {
                String query = JOptionPane.showInputDialog("Query:");
                if (query == null || query.isEmpty()) {
                    break;
                } else {
                    bookIndexer.query(query, queryType);
                    System.out.println("\n\n");
                }
            } else {
                break;
            }
        }
        
    }

    private static long convertToMinute(long elapsedTime) {
        return elapsedTime / NANO_SECONDS_IN_MINUTE;
    }

}
