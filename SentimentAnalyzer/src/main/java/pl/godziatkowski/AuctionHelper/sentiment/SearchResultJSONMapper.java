package pl.godziatkowski.AuctionHelper.sentiment;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;

import pl.godziatkowski.AuctionHelper.dto.Site;
import pl.godziatkowski.AuctionHelper.dto.SearchResult;

@Service
class SearchResultJSONMapper {

    List<Site> getPagesFromJSONString(String jsonObject) {
        ObjectMapper objectMapper = new ObjectMapper();
        SearchResult searchResult;
        try {
            searchResult = objectMapper.readValue(jsonObject, SearchResult.class);
            return searchResult.items;
        } catch (IOException ex) {
            Logger.getLogger(SearchResultJSONMapper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

}
