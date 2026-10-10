package com.edulink.edu_link;

import com.edulink.edu_link.dto.Paper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class ArxivService {

    private static final Logger log = LoggerFactory.getLogger(ArxivService.class);
    private static final String API_URL = "https://export.arxiv.org/api/query";
    private static final long MIN_GAP_MILLIS = 3000;
    private static final Set<String> STOP_WORDS =
            Set.of("a", "an", "and", "the", "of", "in", "on", "for", "to", "with", "via", "by", "at");

    private final RestClient client;
    private long lastRequestAt = 0;

    public ArxivService() {
        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory();
        requestFactory.setReadTimeout(Duration.ofSeconds(20));

        this.client = RestClient.builder()
                .requestFactory(requestFactory)
                .defaultHeader("User-Agent", "EduLink/0.1 (hackathon project)")
                .build();
    }

    public List<Paper> search(String query, int maxResults) {
        String searchQuery = buildSearchQuery(query);
        if (searchQuery.isEmpty()) {
            return List.of();
        }
        log.info("arXiv search_query: {}", searchQuery);

        URI uri = UriComponentsBuilder.fromUriString(API_URL)
                .queryParam("search_query", searchQuery)
                .queryParam("start", 0)
                .queryParam("max_results", maxResults)
                .queryParam("sortBy", "relevance")
                .queryParam("sortOrder", "descending")
                .build()
                .encode()
                .toUri();

        awaitTurn();
        String xml = client.get().uri(uri).retrieve().body(String.class);
        return parsePapers(xml);
    }

    // Turns "prime numbers cryptography" into "all:prime AND all:numbers AND all:cryptography".
    private String buildSearchQuery(String query) {
        List<String> terms = new ArrayList<>();
        String cleaned = query.toLowerCase().replaceAll("[^\\p{L}\\p{N}]+", " ").trim();
        for (String word : cleaned.split("\\s+")) {
            if (!word.isEmpty() && !STOP_WORDS.contains(word)) {
                terms.add("all:" + word);
            }
        }
        return String.join(" AND ", terms);
    }

    // Makes sure at least 3 seconds pass between any two arXiv requests, app-wide.
    private synchronized void awaitTurn() {
        long waitMillis = lastRequestAt + MIN_GAP_MILLIS - System.currentTimeMillis();
        if (waitMillis > 0) {
            try {
                Thread.sleep(waitMillis);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Interrupted while waiting to call arXiv", e);
            }
        }
        lastRequestAt = System.currentTimeMillis();
    }

    private List<Paper> parsePapers(String xml) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            Document doc = factory.newDocumentBuilder().parse(new InputSource(new StringReader(xml)));

            NodeList entries = doc.getElementsByTagName("entry");
            List<Paper> papers = new ArrayList<>();
            for (int i = 0; i < entries.getLength(); i++) {
                Element entry = (Element) entries.item(i);

                String id = text(entry, "id");
                if (id.contains("/api/errors")) {
                    throw new IllegalStateException("arXiv returned an error: " + text(entry, "summary"));
                }

                List<String> authors = new ArrayList<>();
                NodeList authorNodes = entry.getElementsByTagName("author");
                for (int j = 0; j < authorNodes.getLength(); j++) {
                    String name = text((Element) authorNodes.item(j), "name");
                    if (!name.isEmpty()) {
                        authors.add(name);
                    }
                }

                String published = text(entry, "published");
                if (published.length() >= 10) {
                    published = published.substring(0, 10);
                }

                papers.add(new Paper(
                        text(entry, "title"),
                        authors,
                        text(entry, "summary"),
                        published,
                        id.replace("http://", "https://")));
            }
            return papers;
        } catch (IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException("Could not read the arXiv response", e);
        }
    }

    // Text of the first <tag> inside parent, with line breaks and extra spaces collapsed.
    private String text(Element parent, String tag) {
        NodeList nodes = parent.getElementsByTagName(tag);
        if (nodes.getLength() == 0) {
            return "";
        }
        return nodes.item(0).getTextContent().replaceAll("\\s+", " ").trim();
    }
}