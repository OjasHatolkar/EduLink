package com.edulink.edu_link;

import com.edulink.edu_link.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ConnectService {

    private static final Logger log = LoggerFactory.getLogger(ConnectService.class);
    private static final int PAPERS_PER_QUERY = 5;

    private final LlmService llmService;
    private final ArxivService arxivService;

    public ConnectService(LlmService llmService, ArxivService arxivService) {
        this.llmService = llmService;
        this.arxivService = arxivService;
    }

    public ConnectResponse connect(String nameA, String notesA,
                                   String nameB, String notesB) {
        List<LlmConcept> concepts = llmService.findConcepts(nameA, notesA, nameB, notesB);

        List<Connection> connections = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        Set<String> seenUrls = new HashSet<>();

        for (LlmConcept concept : concepts) {
            List<Paper> papers = new ArrayList<>();
            for (String query : concept.queries()) {
                try {
                    for (Paper paper : arxivService.search(query, PAPERS_PER_QUERY)) {
                        if (seenUrls.add(paper.url())) {
                            papers.add(paper);
                        }
                    }
                } catch (RuntimeException e) {
                    log.warn("arXiv search failed for \"{}\": {}", query, e.toString());
                    warnings.add("The paper search failed for \"" + query + "\".");
                }
            }
            connections.add(new Connection(
                    concept.concept(), concept.explanation(), concept.queries(), papers));
        }

        return new ConnectResponse(connections, warnings);
    }
}