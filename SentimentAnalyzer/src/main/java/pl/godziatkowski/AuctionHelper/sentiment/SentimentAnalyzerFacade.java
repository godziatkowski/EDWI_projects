package pl.godziatkowski.AuctionHelper.sentiment;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import pl.godziatkowski.AuctionHelper.dto.Result;
import pl.godziatkowski.AuctionHelper.dto.Site;

@Service
public class SentimentAnalyzerFacade {

    private final SearchEngineService searchEngineService;
    private final SearchResultJSONMapper searchResultJSONMapper;
    private final SentimentAnalyzer sentimentAnalyzer;

    SentimentAnalyzerFacade(SearchEngineService searchEngineService, SearchResultJSONMapper searchResultJSONMapper, SentimentAnalyzer sentimentAnalyzer) {
        this.searchEngineService = searchEngineService;
        this.searchResultJSONMapper = searchResultJSONMapper;
        this.sentimentAnalyzer = sentimentAnalyzer;
    }

    public List<Result> searchFor(String query, int pageNumber)
            throws URISyntaxException,
            UnsupportedEncodingException,
            IOException {
        List<Site> sites = getSites(query, pageNumber);
        List<Result> results = sites.stream()
                .map(Result::new)
                .collect(Collectors.toList());
        results.stream()
                .forEach(supplySentiment());
        return results;
    }

    private Consumer<Result> supplySentiment() {
        return result -> result.setSentiment(sentimentAnalyzer.analyze(result.getSnippet()));
    }

    private List<Site> getSites(String query, int pageNumber)
            throws UnsupportedEncodingException,
            URISyntaxException {
        String result = searchEngineService.search(query, pageNumber);
        return searchResultJSONMapper.getPagesFromJSONString(result);
    }

}
