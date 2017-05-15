package pl.godziatkowski.AuctionHelper.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchResult {

    public List<Site> items;

}
