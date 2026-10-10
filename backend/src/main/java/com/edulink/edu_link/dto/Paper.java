package com.edulink.edu_link.dto;

// Paper.java
import java.util.List;

public record Paper(String title, List<String> authors, String summary,
                    String published, String url) {}