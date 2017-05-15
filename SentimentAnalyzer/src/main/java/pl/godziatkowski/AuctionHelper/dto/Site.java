package pl.godziatkowski.AuctionHelper.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Site {

    private final String link;
    private final String displayLink;
    private final String snippet;

}
