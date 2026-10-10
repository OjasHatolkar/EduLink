package com.edulink.edu_link;

import com.edulink.edu_link.dto.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConnectService {

    public ConnectResponse connect(String nameA, String notesA,
                                   String nameB, String notesB) {
        // TODO: call Featherless, then arXiv. For now, return fake data.
        Paper fakePaper = new Paper(
                "Placeholder paper title",
                List.of("Ada Example", "Grace Sample"),
                "This is fake data. Real papers will come from arXiv.",
                "2024-03-15",
                "https://arxiv.org/abs/0000.00000");

        Connection fakeConnection = new Connection(
                "Placeholder concept linking " + nameA + " and " + nameB,
                "Fake explanation. The real one will come from the LLM.",
                List.of("placeholder query"),
                List.of(fakePaper));

        return new ConnectResponse(List.of(fakeConnection), List.of());
    }
}