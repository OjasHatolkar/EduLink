package com.edulink.edu_link.dto;

import java.util.List;

public record ProjectIdea(String title, String summary, List<String> techStack,
                          String gettingStarted, List<String> steps,
                          String stretchGoal) {}