package pl.godziatkowski.webrobot.query;

import java.util.List;
import org.apache.lucene.document.Document;

public class SearchResult {

    private final int documentId;
    private Document document;
    private Document similiarDocument;
    private List<String> matchedFragments;

    public SearchResult(int documentId) {
        this.documentId = documentId;
    }

    public int getDocumentId() {
        return documentId;
    }

    public Document getDocument() {
        return document;
    }

    void setDocument(Document document) {
        this.document = document;
    }

    public Document getSimiliarDocument() {
        return similiarDocument;
    }

    public void setSimiliarDocument(Document similiarDocument) {
        this.similiarDocument = similiarDocument;
    }

    public List<String> getMatchedFragments() {
        return matchedFragments;
    }

    void setMatchedFragments(List<String> matchedFragments) {
        this.matchedFragments = matchedFragments;
    }

}
