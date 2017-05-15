package com.godziatkowski.webloader;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class WebLoader {

    private static final Logger LOGGER = Logger.getLogger(WebLoader.class.getName());

    public static void main(String[] args) {
        try {
            int thresh = Integer.parseInt(JOptionPane.showInputDialog("Thresh: "));
            String fileName = JOptionPane.showInputDialog("Filename:");
            String url = JOptionPane.showInputDialog("Url:");
            Document doc = Jsoup.connect(url).get();
            String body = doc.body().toString();
            Charset charset = doc.charset();
            PageSaver.savePageAsHtml(fileName, body, charset);
            body = removeSpecialCharacters(body);
            PageSaver.savePageAsTxt(fileName, body, charset);

            List<WordCount> simpleCountWordsInBody = WordCountAnalyzer.simpleCountWordsInBody(body, thresh);

            printList(simpleCountWordsInBody);

        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    private static String removeSpecialCharacters(String body) {
        body = body.replaceAll("\\<[^>]*>", "")
                .replaceAll("[^\\p{L}\\s]", " ")
                .replaceAll("[\n\t]", " ")
                .toLowerCase();
        return body;
    }

    private static void printList(List<WordCount> simpleCountWordsInBody) {
        simpleCountWordsInBody.forEach(wordCount -> System.out.println(wordCount.getWord() + ": " + wordCount.getCount()));
    }
}
