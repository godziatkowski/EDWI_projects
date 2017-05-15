package com.godziatkowski.webloader;

public class WordCount implements Comparable<WordCount> {

    private final String word;
    private final int count;

    public WordCount(String word, int count) {
        this.word = word;
        this.count = count;
    }

    public String getWord() {
        return word;
    }

    public int getCount() {
        return count;
    }

    @Override
    public int compareTo(WordCount wordCount) {
        return wordCount.getCount() - this.count;
    }

}
