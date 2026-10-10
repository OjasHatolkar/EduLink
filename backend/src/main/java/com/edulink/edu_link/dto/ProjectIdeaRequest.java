package com.edulink.edu_link.dto;

// ProjectIdeaRequest.java
import java.util.List;

public record ProjectIdeaRequest(String subjectAName, String subjectBName,
                                 List<ConceptSummary> concepts,
                                 ProjectConstraints constraints) {}