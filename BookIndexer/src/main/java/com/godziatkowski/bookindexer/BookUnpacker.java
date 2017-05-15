package com.godziatkowski.bookindexer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.zip.ZipFile;

public class BookUnpacker {

    private static final Logger LOGGER = Logger.getLogger(BookUnpacker.class.getName());
    private static final String STRING_PATH = "d:\\studia\\magisterskie_s3\\webMining\\pgdvd042010";
    private static final String ZIP = ".zip";
    private static final String TXT = ".txt";
    private static final String ETEXT = "etext";

    public List<UnpackedBook> unpackBooks() throws IOException {
        Path path = Paths.get(STRING_PATH);
        List<UnpackedBook> unpackedBooks = Files.walk(path)
                .filter(file -> isZipFile(file) && isNotEText(file))
                .map(file -> getInputStreamsFromZip(file))
                .flatMap(collection -> collection.stream())
                .collect(Collectors.toList());

        unpackedBooks.addAll(Files.walk(path)
                .filter(file -> isTxtFile(file) && isNotEText(file))
                .map(file -> getInputStreamOfFile(file))
                .collect(Collectors.toList()));

        return unpackedBooks;
    }

    private List<UnpackedBook> getInputStreamsFromZip(Path file) {
        try {
            ZipFile zipFile = new ZipFile(
                    file.toAbsolutePath().toString()
            );
            return Collections.list(zipFile.entries())
                    .stream()
                    .map(entry -> {
                        if (entry.getName().endsWith(TXT)) {
                            try {
                                return new UnpackedBook(entry.getName(), zipFile.getInputStream(entry));
                            } catch (IOException ex) {
                                LOGGER.log(Level.SEVERE, null, ex);
                            }
                        }
                        return null;
                    }).filter(inputStream -> inputStream != null)
                    .collect(Collectors.toList());
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private static boolean isZipFile(Path file) {
        return file.getFileName().toString().toLowerCase().endsWith(ZIP);
    }

    private boolean isNotEText(Path file) {
        return !file.toAbsolutePath().toString().toLowerCase().contains(ETEXT);
    }

    private boolean isTxtFile(Path file) {
        return file.getFileName().toString().toLowerCase().endsWith(TXT);
    }

    private UnpackedBook getInputStreamOfFile(Path path) {
        try {
            File file = path.toFile();
            return new UnpackedBook(path.getFileName().toString(), new FileInputStream(file));
        } catch (FileNotFoundException e) {
            return null;
        }
    }

}
