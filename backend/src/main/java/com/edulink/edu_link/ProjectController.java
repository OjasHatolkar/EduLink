package com.edulink.edu_link;

import com.edulink.edu_link.dto.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ProjectController {

    private static final int MAX_CONCEPTS = 8;

    private final LlmService llmService;

    public ProjectController(LlmService llmService) {
        this.llmService = llmService;
    }

    @PostMapping("/project-idea")
    public ResponseEntity<?> projectIdea(@RequestBody ProjectIdeaRequest request) {
        List<ConceptSummary> concepts = new ArrayList<>();
        if (request.concepts() != null) {
            for (ConceptSummary c : request.concepts()) {
                if (c == null || c.concept() == null || c.concept().isBlank()) {
                    continue;
                }
                concepts.add(new ConceptSummary(
                        clean(c.concept(), 100, ""),
                        clean(c.explanation(), 500, "")));
                if (concepts.size() == MAX_CONCEPTS) {
                    break;
                }
            }
        }
        if (concepts.isEmpty()) {
            return ResponseEntity.badRequest().body(new ErrorResponse(
                    "Find connections first, so there are concepts to base a project on."));
        }

        ProjectConstraints given = request.constraints() != null
                ? request.constraints()
                : new ProjectConstraints(null, null, null, null);
        ProjectConstraints constraints = new ProjectConstraints(
                clean(given.skillLevel(), 50, "Not specified"),
                clean(given.timeAvailable(), 50, "Not specified"),
                clean(given.teamSize(), 50, "Not specified"),
                clean(given.extra(), 500, "None"));

        ProjectIdea idea = llmService.generateProjectIdea(
                clean(request.subjectAName(), 100, "Subject A"),
                clean(request.subjectBName(), 100, "Subject B"),
                concepts,
                constraints);
        return ResponseEntity.ok(idea);
    }

    // Trims the value, cuts it to maxLength, and uses the fallback if it's missing or blank.
    private String clean(String value, int maxLength, String fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        String trimmed = value.trim();
        return trimmed.length() > maxLength ? trimmed.substring(0, maxLength) : trimmed;
    }
}