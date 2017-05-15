package com.godziatkowski.webloader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class FileSaver {

    private static final String TXT = ".txt";

    public static void savePageAsTxt(String fileName, List<String> content) throws IOException {
        Path filePath = Paths.get(fileName + TXT);
        Files.write(filePath, content);
    }

}
