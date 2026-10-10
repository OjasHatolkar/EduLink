package com.edulink.edu_link;

import com.edulink.edu_link.dto.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ConnectService {

    private final LlmService llmService;

    public ConnectService(LlmService llmService) {
        this.llmService = llmService;
    }

    public ConnectResponse connect(String nameA, String notesA,
                                   String nameB, String notesB) {
        List<LlmConcept> concepts = llmService.findConcepts(nameA, notesA, nameB, notesB);

        List<Connection> connections = new ArrayList<>();
        for (LlmConcept c : concepts) {
            // TODO: search arXiv here and fill in the papers.
            connections.add(new Connection(c.concept(), c.explanation(), c.queries(), List.of()));
        }

        return new ConnectResponse(connections,
                List.of("Paper search isn't connected yet, so no papers are shown."));
    }
}