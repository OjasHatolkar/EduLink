package com.edulink.edu_link.dto;

// ConnectResponse.java
import java.util.List;

public record ConnectResponse(List<Connection> connections, List<String> warnings) {}