package com.godziatkowski.bookindexer;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.apache.commons.io.IOUtils;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class BookIndexer {

    private static final Path INDEX_PATH = Paths.get("index");
    private static final Logger LOGGER = Logger.getLogger(BookIndexer.class.getName());
    private static final String TITLE = "Title: ";
    private static final String TITLE_FIELD = "title";
    private static final String CONTENT_FIELD = "content";

    private final StandardAnalyzer analyzer;
    private final Directory index;
    private final IndexWriterConfig config;
    private Set<String> addedFiles;
    private IndexWriter indexWriter;

    public BookIndexer() throws IOException {
        this.analyzer = new StandardAnalyzer();
        this.index = FSDirectory.open(INDEX_PATH);
        this.config = new IndexWriterConfig(analyzer);
    }

    public void indexBooks(List<UnpackedBook> bufferedBooks) throws IOException {
        addedFiles = new HashSet<>();
        indexWriter = new IndexWriter(index, config);
        bufferedBooks.stream()
                .parallel()
                .forEach(unpackedBook -> indexBook(unpackedBook));
        indexWriter.close();
    }

    private void indexBook(UnpackedBook unpackedBook) {
        if (!addedFiles.contains(unpackedBook.getFileName())) {
            try {
                StringBuilder title = new StringBuilder();
                String line;
                boolean titleFound = false;
                while ((line = unpackedBook.getBufferedReader().readLine()) != null) {
                    if (line.startsWith(TITLE)) {
                        title.append(line.replace(TITLE, ""));
                        titleFound = true;
                    } else if (titleFound && !line.trim().isEmpty()) {
                        title.append(line);
                    } else if (titleFound && line.trim().isEmpty()) {
                        break;
                    }
                }
                String content = IOUtils.toString(unpackedBook.getBufferedReader());
                try {
                    content = content.split("\\*\\*\\*")[2];
                } catch (IndexOutOfBoundsException e) {
                    System.out.println(unpackedBook.getFileName());
                }
                addedFiles.add(unpackedBook.getFileName());
                addDoc(title.toString().replace("\n", " "), content);
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }
        }
    }

    private void addDoc(String title, String content) throws IOException {
        Document doc = new Document();
        doc.add(new TextField(TITLE_FIELD, title, Field.Store.YES));
        doc.add(new TextField(CONTENT_FIELD, content, Field.Store.NO));
        indexWriter.addDocument(doc);
    }

    public void query(String queryString, QueryType queryType) throws ParseException, IOException {
        BooleanQuery.Builder query = new BooleanQuery.Builder();
        String field;
        if (queryType.equals(QueryType.TITLE)) {
            field = TITLE_FIELD;
        } else {
            field = CONTENT_FIELD;
        }
        List<Query> queries = getQueriesFromString(queryString, field);
        queries.forEach(innerQuery -> query.add(innerQuery, BooleanClause.Occur.MUST));

        int hitsPerPage = 200;
        try (IndexReader reader = DirectoryReader.open(index)) {
            IndexSearcher searcher = new IndexSearcher(reader);
            TopDocs docs = searcher.search(query.build(), hitsPerPage);
            ScoreDoc[] hits = docs.scoreDocs;
            System.out.println("Total hits number: " + docs.totalHits);
            System.out.println("First " + hitsPerPage + " records");
            for (int i = 0; i < hits.length; ++i) {
                int docId = hits[i].doc;
                Document d = searcher.doc(docId);
                System.out.println(d.get(TITLE_FIELD));
            }
        }
    }

    void printSize() {
        System.out.println("Indexed files: " + addedFiles.size());
    }

    private List<Query> getQueriesFromString(String queryString, String queryType) {
        if (queryString.contains(" ")) {
            return Arrays.asList(queryString.split(" "))
                    .stream()
                    .map(word -> converStringToQuery(word, queryType))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } else {
            return Collections.singletonList(converStringToQuery(queryString, queryType));
        }
    }

    private Query converStringToQuery(String word, String queryType) {
        try {
            return new QueryParser(queryType, analyzer).parse(word);
        } catch (ParseException ex) {
            return null;
        }
    }

}
