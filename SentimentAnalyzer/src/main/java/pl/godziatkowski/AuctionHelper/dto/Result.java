package pl.godziatkowski.AuctionHelper.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Result {

    private final String link;
    private final String displayLink;
    private final String snippet;
    private Sentiment sentiment;

    public Result(Site site) {
        this.link = site.getLink();
        this.displayLink = site.getDisplayLink();
        this.snippet = site.getSnippet();
    }

    public void setSentiment(Sentiment sentiment) {
        this.sentiment = sentiment;
    }

}
