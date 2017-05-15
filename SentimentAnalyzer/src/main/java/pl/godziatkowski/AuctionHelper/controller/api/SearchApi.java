package pl.godziatkowski.AuctionHelper.controller.api;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import pl.godziatkowski.AuctionHelper.dto.Result;
import pl.godziatkowski.AuctionHelper.sentiment.SentimentAnalyzerFacade;

@Controller
public class SearchApi {

    @Autowired
    private SentimentAnalyzerFacade searchFacade;

    @GetMapping("/api/search")
    public HttpEntity<List<Result>> search(String searchQuery, Integer page) {
        page = Objects.isNull(page) ? 0 : page;
        try {
            List<Result> results = searchFacade.searchFor(searchQuery, page);
            return ResponseEntity.ok()
                    .body(results);
        } catch (URISyntaxException | IOException ex) {
            return ResponseEntity.badRequest()
                    .build();
        }
    }

}
