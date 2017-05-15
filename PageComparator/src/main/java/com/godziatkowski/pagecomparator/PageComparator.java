package com.godziatkowski.pagecomparator;

import com.godziatkowski.pagecomparator.model.WordCount;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class PageComparator {

    public static double comparePages(List<WordCount> firstPageWordCount, List<WordCount> secondPageWordCount) {
        Set<String> dictionary = getWordsOnPages(firstPageWordCount, secondPageWordCount);
        List<Double> firstPageVector = calculateVectorForPage(firstPageWordCount, dictionary);
        List<Double> secondPageVector = calculateVectorForPage(secondPageWordCount, dictionary);
        return calculateSimilarity(firstPageVector, secondPageVector);
    }

    private static int getOveralWordCount(List<WordCount> firstPageWordCount) {
        return firstPageWordCount.stream().map(WordCount::getCount).mapToInt(Integer::intValue).sum();
    }

    private static Set<String> getWordsOnPages(List<WordCount> firstPageWordCount, List<WordCount> secondPageWordCount) {
        Set<String> dictionary = firstPageWordCount.stream().map(WordCount::getWord).collect(Collectors.toSet());
        dictionary.addAll(secondPageWordCount.stream().map(WordCount::getWord).collect(Collectors.toSet()));
        return dictionary;
    }

    private static List<Double> calculateVectorForPage(List<WordCount> pageWordCounts, Set<String> dictionary) {
        int overalWordCountOnPage = getOveralWordCount(pageWordCounts);
        Map<String, Double> wordCountsIndexedByWord = pageWordCounts.stream()
                .collect(Collectors.toMap(WordCount::getWord, wordCount -> (double) wordCount.getCount()));
        return dictionary.stream()
                .map((String word) -> 
                        wordCountsIndexedByWord.get(word) != null ? wordCountsIndexedByWord.get(word) / overalWordCountOnPage : 0
                )
                .collect(Collectors.toList());
    }

    private static double calculateSimilarity(List<Double> firstPageVector, List<Double> secondPageVector) {
        double scalarProductOfVectors = 0.0;
        double firstVectorLength = 0.0;
        double secondVectorLength = 0.0;
        double cosineSimilarity = 0.0;

        for (int i = 0; i < firstPageVector.size(); i++) //both vectors have the same length
        {
            scalarProductOfVectors += firstPageVector.get(i) * secondPageVector.get(i);
            firstVectorLength += Math.pow(firstPageVector.get(i), 2);
            secondVectorLength += Math.pow(secondPageVector.get(i), 2);
        }

        firstVectorLength = Math.sqrt(firstVectorLength);
        secondVectorLength = Math.sqrt(secondVectorLength);

        if (firstVectorLength != 0.0 && secondVectorLength != 0.0) {
            cosineSimilarity = scalarProductOfVectors / (firstVectorLength * secondVectorLength);
        } else if (firstVectorLength == 0.0) {
            throw new IllegalArgumentException("Vector length for first page is 0");
        } else {
            throw new IllegalArgumentException("Vector length for secondpage is 0");
        }
        
        return cosineSimilarity;
    }

}
