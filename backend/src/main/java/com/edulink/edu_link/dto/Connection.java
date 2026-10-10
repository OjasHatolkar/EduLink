package com.edulink.edu_link.dto;

// Connection.java
import java.util.List;

public record Connection(String concept, String explanation,
                         List<String> queries, List<Paper> papers) {}