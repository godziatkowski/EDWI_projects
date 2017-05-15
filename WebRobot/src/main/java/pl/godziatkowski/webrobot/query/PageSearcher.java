package pl.godziatkowski.webrobot.query;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queries.mlt.MoreLikeThis;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.TextFragment;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import pl.godziatkowski.webrobot.shared.IndexField;

import static pl.godziatkowski.webrobot.shared.StaticVariables.INDEX_PATH;

public class PageSearcher {

    private static final int MAX_MATCHED_FRAGMENTS = 10;
    private static final boolean MERGE_FRAGMENTS = false;

    private final StandardAnalyzer analyzer;
    private final Directory index;

    public PageSearcher() throws IOException {
        this.analyzer = new StandardAnalyzer();
        this.index = FSDirectory.open(INDEX_PATH);
    }

    public List<SearchResult> searchForPage(String query, IndexField queryType, Modificator modificator) throws IOException {
        List<Query> queries = getQueriesFromString(query, queryType.value, modificator);
        BooleanQuery.Builder queryBuilder = new BooleanQuery.Builder();
        queries.forEach(innerQuery -> queryBuilder.add(innerQuery, BooleanClause.Occur.MUST));
        List<SearchResult> result;
        int hitsPerPage = 5;
        try (IndexReader reader = DirectoryReader.open(index)) {
            SimpleHTMLFormatter htmlFormatter = new SimpleHTMLFormatter();
            IndexSearcher searcher = new IndexSearcher(reader);

            MoreLikeThis moreLikeThis = new MoreLikeThis(reader);
            moreLikeThis.setAnalyzer(analyzer);
            moreLikeThis.setFieldNames(new String[]{IndexField.CONTENT.value});

            BooleanQuery buildedQuery = queryBuilder.build();

            Highlighter highlighter = new Highlighter(htmlFormatter, new QueryScorer(buildedQuery));

            TopDocs docs = searcher.search(buildedQuery, hitsPerPage);

            result = Arrays.asList(docs.scoreDocs)
                    .stream()
                    .map(hit -> hit.doc)
                    .map(SearchResult::new)
                    .map(supplyDocument(searcher))
                    .filter(searchResult -> Objects.nonNull(searchResult.getDocument()))
                    .map(findSimiliar(moreLikeThis, searcher))
                    .map(searchResult -> {
                        if (queryType.equals(IndexField.CONTENT)) {
                            try {
                                String content = searchResult.getDocument().get(queryType.value);
                                TokenStream tokenStream = analyzer.tokenStream(queryType.value, new StringReader(content));
                                TextFragment[] textFragments = highlighter.getBestTextFragments(tokenStream, content, MERGE_FRAGMENTS, MAX_MATCHED_FRAGMENTS);
                                List<String> matchedFragments = Arrays.asList(textFragments)
                                        .stream()
                                        .filter(Objects::nonNull)
                                        .filter(textFragment -> textFragment.getScore() > 0)
                                        .map(Object::toString)
                                        .collect(Collectors.toList());
                                searchResult.setMatchedFragments(matchedFragments);
                            } catch (IOException | InvalidTokenOffsetsException ex) {
                            }
                        }
                        return searchResult;
                    })
                    .collect(Collectors.toList());
        }
        return result;
    }

    private Function<SearchResult, SearchResult> findSimiliar(MoreLikeThis moreLikeThis, IndexSearcher searcher) {
        return searchResult -> {
            try {
                Query likeQuery = moreLikeThis.like(searchResult.getDocumentId());
                TopDocs similarDoc = searcher.search(likeQuery, 1);
                List<ScoreDoc> similiarResults = Arrays.asList(similarDoc.scoreDocs)
                        .stream()
                        .filter(sc -> sc.doc != searchResult.getDocumentId())
                        .collect(Collectors.toList());
                if (!similiarResults.isEmpty()) {
                    Document similiarDocument = getDocumentById(similiarResults.get(0).doc, searcher);
                    searchResult.setSimiliarDocument(similiarDocument);
                }
            } catch (IOException ex) {
            }
            return searchResult;
        };
    }

    private Function<SearchResult, SearchResult> supplyDocument(IndexSearcher searcher) {
        return searchResult -> {
            Document document = getDocumentById(searchResult.getDocumentId(), searcher);
            searchResult.setDocument(document);
            return searchResult;
        };
    }

    private Document getDocumentById(int docId, IndexSearcher searcher) {
        try {
            return searcher.doc(docId);
        } catch (IOException ex) {
            return null;
        }
    }

    private List<Query> getQueriesFromString(String queryString, String queryType, Modificator modificator) {
        if (modificator.equals(Modificator.AND) && queryString.contains(" ")) {
            return Arrays.asList(queryString.split(" "))
                    .stream()
                    .map(word -> converStringToQuery(word, queryType))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } else if (modificator.equals(Modificator.EXACT)) {
            return Collections.singletonList(converStringToQueryWithWrapping(queryString, queryType));
        } else {
            return Collections.singletonList(converStringToQuery(queryString, queryType));
        }
    }

    private Query converStringToQuery(String word, String queryType) {
        try {
            return new QueryParser(queryType, analyzer).parse(QueryParser.escape(word));
        } catch (ParseException ex) {
            return null;
        }
    }

    private Query converStringToQueryWithWrapping(String word, String queryType) {
        try {
            return new QueryParser(queryType, analyzer).parse("\"" + QueryParser.escape(word) + "\"");
        } catch (ParseException ex) {
            return null;
        }
    }
}
