package com.realestate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AIQueryRequest {
    
    @NotBlank(message = "Question is required")
    @Size(max = 1000, message = "Question must be less than 1000 characters")
    private String question;

    // Constructors
    public AIQueryRequest() {}

    public AIQueryRequest(String question) {
        this.question = question;
    }

    // Getters and Setters
    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }
}