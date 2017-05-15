package com.godziatkowski.pagecomparator;

import com.godziatkowski.pagecomparator.model.PageSimilarity;
import com.godziatkowski.pagecomparator.model.UrlPair;
import com.godziatkowski.pagecomparator.model.WordCount;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Main {

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
    private static final List<String> PAGE_URLS = initUrlList();
    private static final int TEN = 10;

    public static void main(String[] args) {
        if (PAGE_URLS.size() < 2) {
            throw new IllegalArgumentException("Not enough urls given");
        }
        List<UrlPair> pairs = pairUrls();
        Map<String, List<WordCount>> wordCountsForPage = PAGE_URLS.stream()
                .map(url -> {
                    List<WordCount> wordsOnPage = new ArrayList<>();
                    try {
                        wordsOnPage.addAll(PageLoader.getWordsOnPage(url));
                    } catch (IOException ex) {
                        LOGGER.log(Level.SEVERE, null, ex);
                    }

                    return new AbstractMap.SimpleEntry<>(url, wordsOnPage);
                }).collect(
                Collectors.toMap(
                        AbstractMap.SimpleEntry::getKey,
                        AbstractMap.SimpleEntry::getValue
                ));
        List<PageSimilarity> pageSimilarities = new ArrayList<>();
        pairs.forEach(pair -> {
            double pageSimilarity = PageComparator.comparePages(
                    wordCountsForPage.get(pair.getFirstPage()),
                    wordCountsForPage.get(pair.getSecondPage()));
            pageSimilarities.add(
                    new PageSimilarity(
                            pair.getFirstPage(),
                            pair.getSecondPage(),
                            pageSimilarity));
        });
        
        System.out.println("The least similar ");
        pageSimilarities.stream()
                .sorted((sim1, sim2) -> Double.compare(sim1.getSimilarity(), sim2.getSimilarity()))
                .limit(TEN)
                .collect(Collectors.toList())
                .forEach(similarity -> System.out.println(similarity.toString()));

        System.out.println("\n\n");
        System.out.println("The most similar");
        pageSimilarities.stream()
                .sorted((sim1, sim2) -> (-1) * Double.compare(sim1.getSimilarity(), sim2.getSimilarity()))
                .limit( TEN)
                .collect(Collectors.toList())
                .forEach(similarity -> System.out.println(similarity.toString()));
    }

    private static List<String> initUrlList() {
        List<String> urls = new ArrayList<>();
        //animals
        urls.add("https://pl.wikipedia.org/wiki/Kot_domowy");
        urls.add("https://pl.wikipedia.org/wiki/Pies_domowy");
        urls.add("https://pl.wikipedia.org/wiki/Tygrys_syberyjski");
        urls.add("https://pl.wikipedia.org/wiki/Lew_afrykański");
        urls.add("https://pl.wikipedia.org/wiki/Słoń_indyjski");
        //music
        urls.add("https://pl.wikipedia.org/wiki/Fryderyk_Chopin");
        urls.add("https://pl.wikipedia.org/wiki/Antonio_Vivaldi");
        urls.add("https://pl.wikipedia.org/wiki/Ludwig_van_Beethoven");
        urls.add("https://pl.wikipedia.org/wiki/Wolfgang_Amadeus_Mozart");
        urls.add("https://pl.wikipedia.org/wiki/Johann_Sebastian_Bach");
        //computer science
        urls.add("https://pl.wikipedia.org/wiki/Java");
        urls.add("https://pl.wikipedia.org/wiki/HTML");
        urls.add("https://pl.wikipedia.org/wiki/Android_(system_operacyjny)");
        urls.add("https://pl.wikipedia.org/wiki/Chmura_obliczeniowa");
        urls.add("https://pl.wikipedia.org/wiki/System_czasu_rzeczywistego");

        return urls;
    }

    private static List<UrlPair> pairUrls() {
        List<UrlPair> pairs = new ArrayList<>();
        for (int i = 0; i < PAGE_URLS.size(); i++) {
            for (int j = i + 1; j < PAGE_URLS.size(); j++) {
                pairs.add(new UrlPair(PAGE_URLS.get(i), PAGE_URLS.get(j)));
            }
        }
        return pairs;
    }

}

