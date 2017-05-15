package com.godziatkowski.pagecomparator.model;

public class PageSimilarity {

    private final String firstPageUrl;
    private final String secondPageUrl;
    private final double similarity;

    public PageSimilarity(String firstPageUrl, String secondPageUrl, double similarity) {
        this.firstPageUrl = firstPageUrl;
        this.secondPageUrl = secondPageUrl;
        this.similarity = similarity;
    }

    public String getFirstPageUrl() {
        return firstPageUrl;
    }

    public String getSecondPageUrl() {
        return secondPageUrl;
    }

    public double getSimilarity() {
        return similarity;
    }

    @Override
    public String toString() {
        return firstPageUrl + " : " + secondPageUrl + ", similarity=" + similarity;
    }

}
