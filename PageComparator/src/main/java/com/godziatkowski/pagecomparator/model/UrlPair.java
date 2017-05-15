package com.godziatkowski.pagecomparator.model;

public class UrlPair {

    private String firstPage;
    private String secondPage;

    public UrlPair(String firstPage, String secondPage) {
        this.firstPage = firstPage;
        this.secondPage = secondPage;
    }

    public String getFirstPage() {
        return firstPage;
    }

    public String getSecondPage() {
        return secondPage;
    }

}
