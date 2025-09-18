package com.realestate.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class OpenAIRequest {
    
    private String model;
    private List<Message> messages;
    
    @JsonProperty("max_tokens")
    private Integer maxTokens;
    
    private Double temperature;

    public static class Message {
        private String role;
        private String content;

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }

        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }

        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
    }

    // Constructors
    public OpenAIRequest() {}

    public OpenAIRequest(String model, List<Message> messages, Integer maxTokens, Double temperature) {
        this.model = model;
        this.messages = messages;
        this.maxTokens = maxTokens;
        this.temperature = temperature;
    }

    // Getters and Setters
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public List<Message> getMessages() { return messages; }
    public void setMessages(List<Message> messages) { this.messages = messages; }

    public Integer getMaxTokens() { return maxTokens; }
    public void setMaxTokens(Integer maxTokens) { this.maxTokens = maxTokens; }

    public Double getTemperature() { return temperature; }
    public void setTemperature(Double temperature) { this.temperature = temperature; }
}