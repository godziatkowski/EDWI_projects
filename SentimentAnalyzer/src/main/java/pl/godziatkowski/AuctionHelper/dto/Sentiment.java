package pl.godziatkowski.AuctionHelper.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Sentiment {
    private final Probability probability;
    private final String label;
}
