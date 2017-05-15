package pl.godziatkowski.AuctionHelper.sentiment;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
class SearchEngineService {

    private static final String SEARCH = "&q=";
    private static final String START = "&start=";

    private final String searchEngineUrl;

    @Autowired
    SearchEngineService(@Value("${google.search.engine}") String searchEngineUrl) {
        this.searchEngineUrl = searchEngineUrl;
    }

    String search(String query, int page) throws URISyntaxException, UnsupportedEncodingException {
        RestTemplate restTemplate = new RestTemplate();
        URI uri = new URI(constructUrl(query, page));
        String result = restTemplate.getForObject(uri, String.class);
        return result;
    }

    private String constructUrl(String query, int page) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder(searchEngineUrl);
        sb.append(SEARCH).append(convertQueryToProperFormat(query));
        if (page > 0) {
            sb.append(START).append(page);
        }
        return sb.toString();
    }

    private String convertQueryToProperFormat(String query) throws UnsupportedEncodingException {
        return URLEncoder.encode(query, "UTF-8");
    }
}
