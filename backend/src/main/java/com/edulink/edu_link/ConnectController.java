package com.edulink.edu_link;

import com.edulink.edu_link.dto.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ConnectController {

    private static final int MAX_NOTES_LENGTH = 8000;
    private static final int MAX_NAME_LENGTH = 100;

    private final ConnectService connectService;
    private final LlmService llmService;

    public ConnectController(ConnectService connectService, LlmService llmService) {
        this.connectService = connectService;
        this.llmService = llmService;
    }

    @PostMapping("/connect")
    public ResponseEntity<?> connect(@RequestBody ConnectRequest request) {
        String problem = validate(request);
        if (problem != null) {
            return ResponseEntity.badRequest().body(new ErrorResponse(problem));
        }

        ConnectResponse response = connectService.connect(
                displayName(request.subjectA(), "Subject A"), request.subjectA().notes().trim(),
                displayName(request.subjectB(), "Subject B"), request.subjectB().notes().trim());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/summary")
    public ResponseEntity<?> summary(@RequestBody ConnectRequest request) {
        String problem = validate(request);
        if (problem != null) {
            return ResponseEntity.badRequest().body(new ErrorResponse(problem));
        }

        String summary = llmService.summarizeAsMascot(
                displayName(request.subjectA(), "Subject A"), request.subjectA().notes().trim(),
                displayName(request.subjectB(), "Subject B"), request.subjectB().notes().trim());
        return ResponseEntity.ok(new SummaryResponse(summary));
    }

    // Returns an error message, or null if the request is fine.
    private String validate(ConnectRequest request) {
        String errorA = checkNotes(request.subjectA(), "Subject A");
        if (errorA != null) {
            return errorA;
        }
        return checkNotes(request.subjectB(), "Subject B");
    }

    private String checkNotes(Subject subject, String label) {
        if (subject == null || subject.notes() == null || subject.notes().isBlank()) {
            return "Notes for " + label + " are empty.";
        }
        if (subject.notes().length() > MAX_NOTES_LENGTH) {
            return "Notes for " + label + " are too long (max "
                    + MAX_NOTES_LENGTH + " characters).";
        }
        return null;
    }

    // Uses the given name (cut to a sane length), or a fallback if it's missing or blank.
    private String displayName(Subject subject, String fallback) {
        if (subject.name() == null || subject.name().isBlank()) {
            return fallback;
        }
        String name = subject.name().trim();
        return name.length() > MAX_NAME_LENGTH ? name.substring(0, MAX_NAME_LENGTH) : name;
    }
}