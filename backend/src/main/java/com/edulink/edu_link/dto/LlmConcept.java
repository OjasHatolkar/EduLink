package com.edulink.edu_link.dto;

import java.util.List;

public record LlmConcept(String concept, String explanation, List<String> queries) {}