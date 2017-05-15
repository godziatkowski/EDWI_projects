package com.godziatkowski.pagecomparator;

import com.godziatkowski.pagecomparator.model.WordCount;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

public class WordCountAnalyzer {

    public static List<WordCount> simpleCountWordsInBody(String content) {
        Map<String, Integer> words = new HashMap<>();
        Set<String> separatedWords = Arrays.asList(content.split(" "))
                .stream()
                .map(word -> word.trim())
                .filter(word -> !word.isEmpty())
                .collect(Collectors.toSet());
        separatedWords.forEach(word -> {
            int count = StringUtils.countMatches(content, " " + word + " ");
            words.put(word, count);
        });

        return words.entrySet()
                .stream()
                .map(entry -> new WordCount(entry.getKey(), entry.getValue()))
                .sorted()
                .collect(Collectors.toList());
    }

}
