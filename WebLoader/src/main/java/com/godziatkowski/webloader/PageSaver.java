package com.godziatkowski.webloader;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class PageSaver {

    private static final String HTML = ".html";
    private static final String TXT = ".txt";

    public static void savePageAsHtml(String fileName, String content, Charset charset) throws IOException {
        Path filePath = Paths.get(fileName + HTML);
        Files.write(filePath, castStringToList(content), charset);
    }

    public static void savePageAsTxt(String fileName, String content, Charset charset) throws IOException {
        Path filePath = Paths.get(fileName + TXT);
        Files.write(filePath, castStringToList(content), charset);
    }

    private static List<String> castStringToList(String s) {
        List<String> temp = new ArrayList<>();
        temp.add(s);
        return temp;
    }

}
