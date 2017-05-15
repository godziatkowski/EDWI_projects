package pl.godziatkowski.AuctionHelper.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Probability {
    private final float neg;
    private final float neutral;
    private final float pos;
    
}
