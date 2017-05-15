package pl.godziatkowski.AuctionHelper.sentiment;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import pl.godziatkowski.AuctionHelper.dto.Sentiment;

@Service
public class SentimentAnalyzer {

    private static final Logger LOGGER = Logger.getLogger(SentimentAnalyzer.class.getName());

    private final String sentimentAnalyzerAddress;

    @Autowired
    SentimentAnalyzer(@Value("${sentiment.analyzer}") String sentimentAnalyzerAddress) {
        this.sentimentAnalyzerAddress = sentimentAnalyzerAddress;
    }

    public Sentiment analyze(String text) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            URI uri = new URI(sentimentAnalyzerAddress);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("language", "english");
            map.add("text", text);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

            ResponseEntity<Sentiment> response = restTemplate.postForEntity(uri, request, Sentiment.class);
            return response.getBody();
        } catch (URISyntaxException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            return null;
        }
    }

}
