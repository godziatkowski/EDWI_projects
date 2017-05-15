package com.godziatkowski.pagecomparator;

import com.godziatkowski.pagecomparator.model.WordCount;
import java.io.IOException;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class PageLoader {

    private static Document loadPage(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();
        return doc;
    }

    private static String getPageBody(Document doc) {
        String body = doc.body().toString();
        body = removeSpecialCharacters(body);
        return body;
    }

    private static String removeSpecialCharacters(String body) {
        body = body.replaceAll("\\<[^>]*>", "").replaceAll("[^\\p{L}\\s]", " ").replaceAll("[\n\t]", " ").toLowerCase();
        return body;
    }

    public static List<WordCount> getWordsOnPage(String pageUrl) throws IOException {
        Document doc = PageLoader.loadPage(pageUrl);
        String body = getPageBody(doc);
        List<WordCount> simpleCountWordsInBody = WordCountAnalyzer.simpleCountWordsInBody(body);
        return simpleCountWordsInBody;
    }

}
