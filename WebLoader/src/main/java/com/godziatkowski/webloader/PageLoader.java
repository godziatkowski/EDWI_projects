package com.godziatkowski.webloader;

import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class PageLoader {

    public static Document loadPage(String url) throws IOException {
        return Jsoup.connect(url).get();

    }

}
