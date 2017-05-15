package com.godziatkowski.bookindexer;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class UnpackedBook {

    private static final String DASH = "-";
    private static final String EMPTY_STRING = "";
    private static final String TXT = ".txt";
    private final String fileName;
    private final BufferedReader bufferedReader;

    public UnpackedBook(String fileName, InputStream inputStream) {
        String name = fileName.replace(TXT, EMPTY_STRING);
        int indexOf = name.indexOf(DASH);
        if (indexOf > -1) {
            name = name.substring(0, indexOf);
        }
        indexOf = name.lastIndexOf("/");
        if (indexOf > -1) {
            name = name.substring(indexOf+1);
        }
        this.fileName = name;
        bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
    }

    public String getFileName() {
        return fileName;
    }

    public BufferedReader getBufferedReader() {
        return bufferedReader;
    }

}
