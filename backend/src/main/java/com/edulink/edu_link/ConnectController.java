package com.edulink.edu_link;

import com.edulink.edu_link.dto.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ConnectController {

    private static final int MAX_NOTES_LENGTH = 8000;

    private final ConnectService connectService;

    public ConnectController(ConnectService connectService) {
        this.connectService = connectService;
    }

    @PostMapping("/connect")
    public ResponseEntity<?> connect(@RequestBody ConnectRequest request) {
        String errorA = checkNotes(request.subjectA(), "Subject A");
        if (errorA != null) {
            return ResponseEntity.badRequest().body(new ErrorResponse(errorA));
        }
        String errorB = checkNotes(request.subjectB(), "Subject B");
        if (errorB != null) {
            return ResponseEntity.badRequest().body(new ErrorResponse(errorB));
        }

        String nameA = displayName(request.subjectA(), "Subject A");
        String nameB = displayName(request.subjectB(), "Subject B");

        ConnectResponse response = connectService.connect(
                nameA, request.subjectA().notes().trim(),
                nameB, request.subjectB().notes().trim());
        return ResponseEntity.ok(response);
    }

    // Returns an error message, or null if the subject is fine.
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

    // Uses the given name, or a fallback if it's missing or blank.
    private String displayName(Subject subject, String fallback) {
        if (subject.name() == null || subject.name().isBlank()) {
            return fallback;
        }
        return subject.name().trim();
    }
}