package com.godziatkowski.webloader;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class LinkCollector {

    private static final Logger LOGGER = Logger.getLogger(LinkCollector.class.getName());
    private static final int MAX_DIG_LEVEL = 3;
    private static final String EXTERNAL = "external";
    private static final String INTERNAL = "internal";
    private final Set<String> finalInternalLinks;
    private final Set<String> finalExternalLinks;
    private int currentDigLevel;

    public LinkCollector() {
        finalInternalLinks = new HashSet<>();
        finalExternalLinks = new HashSet<>();
    }

    public void collectLinksFromUrl(String homePageUrl) {
        try {
            Set<String> links = getLinksFromUrl(homePageUrl);
            String homePageIp = getLinkIp(homePageUrl);
            currentDigLevel = 1;

            getLinksRecursive(homePageIp, links);

        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    public Set<String> getFinalInternalLinks() {
        return finalInternalLinks;
    }

    public Set<String> getFinalExternalLinks() {
        return finalExternalLinks;
    }

    private String getLinkIp(String link) throws UnknownHostException, MalformedURLException {
        InetAddress address = InetAddress.getByName(new URL(link).getHost());
        return address.getHostAddress();
    }

    private Set<String> getLinksFromUrl(String url) {
        if (!linkIsToFile(url) || isKnownIncorrectLink(url)) {
            try {
                Document doc = Jsoup.connect(url).get();
                Elements anchorTags = doc.select("a[href]");
                return anchorTags.stream()
                        .map(link -> link.attr("abs:href"))
                        .filter(link -> !link.contains("@"))
                        .collect(Collectors.toSet());
            } catch (IOException ex) {
                LOGGER.log(Level.WARNING, "Cannot load links for {0}", url);
            }
        }
        return new HashSet<>();
    }

    private static boolean linkIsToFile(String url) {
        return url.endsWith(".pdf") 
                || url.endsWith(".zip") 
                || url.endsWith(".docx") 
                || url.endsWith(".doc")
                || url.endsWith(".mp3")
                || url.endsWith(".mp4")
                || url.endsWith(".rtf")
                || url.endsWith(".tif")
                || url.endsWith(".jpg")
                || url.endsWith(".rar")
                ;
    }

    private Map<String, Set<String>> distinguishExternalAndInternalLinks(Set<String> links, String homePageIp) {
        Set<String> internalLinks = new HashSet<>();
        Set<String> externalLinks = new HashSet<>();
        links.forEach(link -> {
            try {
                if (homePageIp.equals(getLinkIp(link))) {
                    internalLinks.add(link);
                } else {
                    externalLinks.add(link);
                }
            } catch (UnknownHostException | MalformedURLException e) {

            }
        });
        Map<String, Set<String>> map = new HashMap<>();
        map.put(INTERNAL, internalLinks);
        map.put(EXTERNAL, externalLinks);
        return map;
    }

    private void getLinksRecursive(String homePageIp, Set<String> links) {
        currentDigLevel++;
        Map<String, Set<String>> splittedLinks = distinguishExternalAndInternalLinks(links, homePageIp);
        finalExternalLinks.addAll(splittedLinks.get(EXTERNAL));
        finalInternalLinks.addAll(splittedLinks.get(INTERNAL));
        LOGGER.log(Level.INFO, "kuku, current dig level{0}, externalLinks: {1}, internalLinks: {2}", new Object[]{currentDigLevel, finalExternalLinks.size(), finalInternalLinks.size()});
        if (currentDigLevel <= MAX_DIG_LEVEL) {
            Set<String> collectedLinks = splittedLinks.get(INTERNAL)
                    .stream()
                    .map(link -> getLinksFromUrl(link))
                    .flatMap(Set::stream)
                    .collect(Collectors.toSet());
            getLinksRecursive(homePageIp, collectedLinks);
        }
    }

    private boolean isKnownIncorrectLink(String url) {
        return url.trim().equalsIgnoreCase("destine.eu");
    }

}
