package com.godziatkowski.webloader;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.swing.JOptionPane;

public class Main {

    private static final String EXTERNAL = "external";
    private static final String INTERNAL = "internal";

    public static void main(String[] args) throws IOException {
        String homePageUrl = JOptionPane.showInputDialog("Url:");
        LinkCollector linkCollector = new LinkCollector();
        linkCollector.collectLinksFromUrl(homePageUrl);

        FileSaver.savePageAsTxt(INTERNAL, castSetToString(linkCollector.getFinalInternalLinks()));
        FileSaver.savePageAsTxt(EXTERNAL, castSetToString(linkCollector.getFinalExternalLinks()));
        
        System.out.println("Internal link size: " + linkCollector.getFinalInternalLinks().size());
        System.out.println("External link size: " + linkCollector.getFinalExternalLinks().size());

    }
    
    private static List<String> castSetToString(Set<String> set){
        return set.stream()
                .collect(Collectors.toList());
    }

}
